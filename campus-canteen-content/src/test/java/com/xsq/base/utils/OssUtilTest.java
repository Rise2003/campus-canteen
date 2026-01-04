package com.xsq.base.utils;

import com.xsq.base.config.OssProperties;
import com.xsq.base.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;

class OssUtilTest {

    @Test
    void validateConfig_missingRequired_shouldThrow() {
        OssProperties props = new OssProperties();
        OssUtil util = new OssUtil(props);
        assertThrows(BusinessException.class, util::validateConfig);
    }

    @Test
    void publicUrl_shouldJoinAndEncodeKey() throws UnsupportedEncodingException {
        OssProperties props = new OssProperties();
        props.setAccessKey("ak");
        props.setSecretKey("sk");
        props.setBucket("b");
        props.setDomain("https://cdn.example.com/");

        OssUtil util = new OssUtil(props);
        String url = util.publicUrl("images/中文 空格.png");

        assertEquals("https://cdn.example.com/images/%E4%B8%AD%E6%96%87+%E7%A9%BA%E6%A0%BC.png", url);
    }

    @Test
    void privateUrl_requiresConfigAndReturnsSignedUrl() throws UnsupportedEncodingException {
        OssProperties props = new OssProperties();
        props.setAccessKey("ak");
        props.setSecretKey("sk");
        props.setBucket("b");
        props.setDomain("https://cdn.example.com");

        OssUtil util = new OssUtil(props);
        String url = util.privateUrl("a.png", 60);

        assertNotNull(url);
        assertTrue(url.startsWith("https://cdn.example.com/a.png"));
        assertTrue(url.contains("e="));
        assertTrue(url.contains("token="));
    }
}

