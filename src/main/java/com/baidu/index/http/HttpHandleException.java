package com.baidu.index.http;

/**
 * HttpHandle异常
 * 
 * @author xuran
 * 
 */
public class HttpHandleException extends RuntimeException {

	private static final long serialVersionUID = -6766000691497502804L;

	public HttpHandleException() {
		super();
	}

	public HttpHandleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpHandleException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpHandleException(String message) {
		super(message);
	}

	public HttpHandleException(Throwable cause) {
		super(cause);
	}

}
