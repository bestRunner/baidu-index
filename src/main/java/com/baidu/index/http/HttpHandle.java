package com.baidu.index.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpHandle {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpHandle.class);

    public static String doGet(String url, Map<String, String> requestHeaders, String cookie) {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setConnectionManagerTimeout(1 * 1000);
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(2 * 1000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(4 * 1000);
        httpClient.getHttpConnectionManager().getParams().setTcpNoDelay(true);
        HttpMethod method = new GetMethod(url);
        method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36");

        if (requestHeaders != null ) {
            for (String requestHeader : requestHeaders.keySet()) {
                method.setRequestHeader(requestHeader, requestHeaders.get(requestHeader));
            }
        }
        if (cookie == null) {
            method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            method.setRequestHeader("Cookie", "special-cookie=value");
        } else {
            method.getParams().setCookiePolicy(CookiePolicy.DEFAULT);
            method.setRequestHeader("Cookie", cookie);
        }
        method.setRequestHeader("Connection", "close");
        int retry = 0;
        HttpHandleException exception = new HttpHandleException("request fail");
        do {
            try {
                httpClient.executeMethod(method);

                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));) {
                    String lineString = null;
                    while ((lineString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(lineString);
                    }
                }
                if (0 != retry) {
                    LOGGER.info("httphandle retry success, retry={}, info={}", retry,
                            exception.getMessage().replace("Exception", ""));
                }
                // 返回
                return stringBuilder.toString();
            } catch (URIException e) {
                exception = new HttpHandleException(e);
            } catch (IOException e) {
                exception = new HttpHandleException(e);
            } catch (Exception e) {
                exception = new HttpHandleException(e);
            } finally {
                // 关闭响应
                method.releaseConnection();
            }
        } while (++retry < 3);
        throw exception;
    }
}
