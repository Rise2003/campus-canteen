package com.xsq.base.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

	private Integer code;
	private String message;
	private T data;

	public static <T> Result<T> success(T data) {
		return Result.<T>builder()
				.code(0)
				.message("success")
				.data(data)
				.build();
	}

	public static <T> Result<T> error(String message) {
		return Result.<T>builder()
				.code(-1)
				.message(message)
				.data(null)
				.build();
	}
}
