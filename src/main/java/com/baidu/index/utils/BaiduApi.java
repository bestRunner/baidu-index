package com.baidu.index.utils;

/**
 * BAIDU API
 *
 * @author buhuaqi
 * @date 2020-05-10 20:58
 */
public interface BaiduApi {

    /**
     * 百度指数搜索指数相关
     */
    String INDEX_API = "http://index.baidu.com/api/SearchApi/index?word=%s";
    /**
     * 加密参数相关
     */
    String INDEX_PTBK_API = "http://index.baidu.com/Interface/ptbk?uniqid=";
    /**
     * 地域
     */
    String REGION_API = "http://index.baidu.com/api/SearchApi/region?region=%s&word=%s";
}
