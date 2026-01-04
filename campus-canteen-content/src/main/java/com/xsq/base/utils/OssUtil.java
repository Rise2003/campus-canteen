package com.xsq.base.utils;

import com.xsq.base.config.OssProperties;
import com.xsq.base.exception.BusinessException;
import com.xsq.base.exception.CommonError;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

/**
 * 七牛云 OSS 工具类（纯 HTTP 实现，避免引入 qiniu-java-sdk 依赖）。
 * <p>
 * 需要在 application.yaml 配置：
 * <pre>
 * oss:
 *   accessKey: xxx
 *   secretKey: xxx
 *   bucket: xxx
 *   domain: https://xxx.xxx.com   # 可选，用于拼接公开访问 URL / 私有下载签名
 * </pre>
 */
@Component
public class OssUtil {

    private static final MediaType MEDIA_TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream");

    // 七牛上传域名（华东/华北/华南等可不同；这里用通用的 upload.qiniup.com，七牛会自动调度）
    private static final String QINIU_UPLOAD_URL = "https://upload.qiniup.com";

    // 七牛资源管理 API 域名（region 有差异；rs.qiniu.com 属于通用入口）
    private static final String QINIU_RS_URL = "https://rs.qiniu.com";

    private final OssProperties ossProperties;
    private final OkHttpClient httpClient;

    public OssUtil(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
        this.httpClient = new OkHttpClient();
    }

    /**
     * 上传字节数组
     *
     * @param bytes       文件内容
     * @param key         OSS 存储 key（例如 images/a.png）；为空则由七牛自动生成
     * @param contentType 可选：文件类型，如 image/png
     * @return 最终存储的 key（若 key 为空则返回七牛生成的 hash）
     */
    public String uploadBytes(byte[] bytes, String key, String contentType) {
        validateConfig();
        if (bytes == null || bytes.length == 0) {
            BusinessException.cast(CommonError.PARAMS_ERROR);
        }

        String upToken = uploadToken(key, 3600);

        String finalContentType = StringUtils.defaultIfBlank(contentType, "application/octet-stream");
        RequestBody fileBody = RequestBody.create(MediaType.parse(finalContentType), bytes);

        MultipartBody.Builder multipart = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("token", upToken)
                .addFormDataPart("file", StringUtils.defaultIfBlank(key, "file"), fileBody);
        if (StringUtils.isNotBlank(key)) {
            multipart.addFormDataPart("key", key);
        }

        Request request = new Request.Builder()
                .url(QINIU_UPLOAD_URL)
                .post(multipart.build())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                BusinessException.cast("七牛上传失败：HTTP " + response.code() + " " + body);
            }
            // 七牛返回 JSON：{"hash":"xxx","key":"xxx"}
            String returnedKey = extractJsonString(body, "key");
            if (StringUtils.isNotBlank(returnedKey)) {
                return returnedKey;
            }
            // key 为空时七牛会只返回 hash（也可能同时返回 key）
            String returnedHash = extractJsonString(body, "hash");
            return StringUtils.defaultIfBlank(returnedHash, key);
        } catch (IOException e) {
            BusinessException.cast("七牛上传异常：" + e.getMessage());
            return null;
        }
    }

    /**
     * 上传本地文件
     */
    public String uploadFile(File file, String key, String contentType) {
        validateConfig();
        if (file == null || !file.exists() || !file.isFile()) {
            BusinessException.cast(CommonError.PARAMS_ERROR);
        }

        MediaType mt = MediaType.parse(StringUtils.defaultIfBlank(contentType, "application/octet-stream"));
        RequestBody fileBody = RequestBody.create(mt, file);

        String upToken = uploadToken(key, 3600);

        MultipartBody.Builder multipart = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("token", upToken)
                .addFormDataPart("file", file.getName(), fileBody);
        if (StringUtils.isNotBlank(key)) {
            multipart.addFormDataPart("key", key);
        }

        Request request = new Request.Builder()
                .url(QINIU_UPLOAD_URL)
                .post(multipart.build())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                BusinessException.cast("七牛上传失败：HTTP " + response.code() + " " + body);
            }
            String returnedKey = extractJsonString(body, "key");
            if (StringUtils.isNotBlank(returnedKey)) {
                return returnedKey;
            }
            String returnedHash = extractJsonString(body, "hash");
            return StringUtils.defaultIfBlank(returnedHash, key);
        } catch (IOException e) {
            BusinessException.cast("七牛上传异常：" + e.getMessage());
            return null;
        }
    }

    /**
     * 上传 Spring MultipartFile
     */
    public String uploadMultipart(MultipartFile file, String key) {
        if (file == null || file.isEmpty()) {
            BusinessException.cast(CommonError.PARAMS_ERROR);
        }
        try {
            return uploadBytes(file.getBytes(), key, file.getContentType());
        } catch (IOException e) {
            BusinessException.cast("读取上传文件失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 删除对象（七牛管理 API）。
     */
    public void delete(String key) {
        validateConfig();
        if (StringUtils.isBlank(key)) {
            BusinessException.cast(CommonError.PARAMS_ERROR);
        }

        String entry = urlSafeBase64(ossProperties.getBucket() + ":" + key);
        String path = "/delete/" + entry;
        String url = QINIU_RS_URL + path;

        String authorization = qiniuManagementAuthorization(path);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authorization)
                .post(RequestBody.create(MEDIA_TYPE_OCTET_STREAM, new byte[0]))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            // 七牛删除成功返回 200 且 body 为空；404 表示对象不存在也可视为“已删除”
            if (response.isSuccessful() || response.code() == 612) {
                return;
            }
            BusinessException.cast("七牛删除失败：HTTP " + response.code() + " " + body);
        } catch (IOException e) {
            BusinessException.cast("七牛删除异常：" + e.getMessage());
        }
    }

    /**
     * 拼接公开访问 URL（要求你的 bucket 绑定了自定义域名，且设置了 oss.domain）。
     */
    public String publicUrl(String key) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(key)) {
            BusinessException.cast(CommonError.PARAMS_ERROR);
        }
        String domain = StringUtils.trimToNull(ossProperties.getDomain());
        if (domain == null) {
            BusinessException.cast("oss.domain 未配置，无法生成公开访问地址");
        }
        return joinUrl(domain, key);
    }

    /**
     * 生成私有空间下载链接（带签名）：
     * baseUrl + "?e=deadline&token=AK:sign"
     */
    public String privateUrl(String key, long expiresInSec) throws UnsupportedEncodingException {
        validateConfig();
        if (StringUtils.isBlank(key)) {
            BusinessException.cast(CommonError.PARAMS_ERROR);
        }
        String domain = StringUtils.trimToNull(ossProperties.getDomain());
        if (domain == null) {
            BusinessException.cast("oss.domain 未配置，无法生成私有下载地址（私有空间需要域名/外链）");
        }

        long deadline = expiresInSec > 0 ? expiresInSec : 3600;
        long e = Instant.now().getEpochSecond() + deadline;

        String base = joinUrl(domain, key);
        String withE = base + (base.contains("?") ? "&" : "?") + "e=" + e;

        String sign = hmacSha1Base64UrlSafe(withE, ossProperties.getSecretKey());
        String token = ossProperties.getAccessKey() + ":" + sign;
        return withE + "&token=" + token;
    }

    /**
     * 基础配置校验
     */
    public void validateConfig() {
        if (ossProperties == null
                || StringUtils.isBlank(ossProperties.getAccessKey())
                || StringUtils.isBlank(ossProperties.getSecretKey())
                || StringUtils.isBlank(ossProperties.getBucket())) {
            BusinessException.cast("OSS 配置缺失，请检查 oss.accessKey/oss.secretKey/oss.bucket");
        }
    }

    /**
     * 生成上传凭证（uploadToken）。
     * scope = bucket 或 bucket:key
     */
    private String uploadToken(String key, long expiresInSec) {
        String scope = ossProperties.getBucket();
        if (StringUtils.isNotBlank(key)) {
            scope = scope + ":" + key;
        }

        long deadline = Instant.now().getEpochSecond() + (expiresInSec > 0 ? expiresInSec : 3600);
        String putPolicyJson = "{\"scope\":\"" + escapeJson(scope) + "\",\"deadline\":" + deadline + "}";
        String encodedPutPolicy = urlSafeBase64(putPolicyJson);

        String sign = hmacSha1Base64UrlSafe(encodedPutPolicy, ossProperties.getSecretKey());
        return ossProperties.getAccessKey() + ":" + sign + ":" + encodedPutPolicy;
    }

    /**
     * 七牛管理 API Authorization: "QBox AK:sign"，其中 sign = urlsafe_base64(hmacsha1(path\n))
     */
    private String qiniuManagementAuthorization(String path) {
        String signingStr = path + "\n";
        String sign = hmacSha1Base64UrlSafe(signingStr, ossProperties.getSecretKey());
        return "QBox " + ossProperties.getAccessKey() + ":" + sign;
    }

    private static String hmacSha1Base64UrlSafe(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return urlSafeBase64(digest);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HmacSHA1 not available", e);
        }
    }

    private static String urlSafeBase64(String s) {
        return urlSafeBase64(s.getBytes(StandardCharsets.UTF_8));
    }

    private static String urlSafeBase64(byte[] bytes) {
        // 七牛使用 URL-safe base64：+ -> - , / -> _ , 去掉 padding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String joinUrl(String domain, String key) throws UnsupportedEncodingException {
        String d = domain.endsWith("/") ? domain.substring(0, domain.length() - 1) : domain;
        String k = key.startsWith("/") ? key.substring(1) : key;
        String encoded = encodeKeyPath(k);
        return d + "/" + encoded;
    }

    private static String encodeKeyPath(String key) throws UnsupportedEncodingException {
        String[] parts = key.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append('/');
            sb.append(URLEncoder.encode(parts[i], String.valueOf(StandardCharsets.UTF_8)));
        }
        return sb.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * 超轻量 JSON 字符串字段提取（只用于解析七牛返回的 {"key":"...","hash":"..."}）。
     */
    private static String extractJsonString(String json, String field) {
        if (StringUtils.isBlank(json) || StringUtils.isBlank(field)) {
            return null;
        }
        String pattern = "\"" + field + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx + pattern.length());
        if (colon < 0) return null;
        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;
        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (secondQuote < 0) return null;
        return json.substring(firstQuote + 1, secondQuote);
    }
}
