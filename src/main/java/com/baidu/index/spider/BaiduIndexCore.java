package com.baidu.index.spider;

import com.baidu.index.http.HttpHandle;
import com.baidu.index.utils.BaiduApi;
import com.baidu.index.utils.FontUtil;
import com.baidu.index.utils.GsonUtil;
import com.baidu.index.utils.LogbackUtil;
import com.google.common.base.Joiner;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百度指数爬虫核心类
 *
 * @author buhuaqi
 * @date 2019-12-30 11:07
 */
public class BaiduIndexCore {

    private static Logger logger = LoggerFactory.getLogger(BaiduIndexCore.class);

    private final static String KEYWORD_FORMAT = "[{\"name\":\"%s\",\"wordType\":1}]";

    private String keyWord;
    private String startDate;
    private String endDate;
    private String cookie;
    /**
     * 默认为 0 表示全国，地域 ID 与 地域名 关系映射见资源目录下
     */
    private int area = 0;

    /**
     * @param startDate 开始时间，格式为：2020-1-31
     * @param endDate   结束时间，格式为：2020-3-9
     * @param area      地域 id ，详见资源目录下
     * @param cookie    百度指数登陆 cookie ，需要从浏览器下该cookie
     * @param keyWords
     */
    public BaiduIndexCore(String startDate, String endDate, int area, String cookie, String... keyWords) {
        super();
        this.keyWord = Joiner.on(",").join(keyWords);
        this.startDate = startDate;
        this.endDate = endDate;
        this.cookie = cookie;
        this.area = area;
    }

    /**
     * 默认地区为全国
     *
     * @param startDate
     * @param endDate
     * @param cookie
     * @param keyWords
     */
    public BaiduIndexCore(String startDate, String endDate, String cookie, String... keyWords) {
        super();
        this.keyWord = Joiner.on(",").join(keyWords);
        this.startDate = startDate;
        this.endDate = endDate;
        this.cookie = cookie;
    }

    public BaiduIndexCore(String cookie, String... keyWords) {
        super();
        this.keyWord = Joiner.on(",").join(keyWords);
        this.cookie = cookie;
    }

    public static void main(String[] args) {
        // TODO 从百度指数获取cookie
        String cookie = "";
        BaiduIndexCore baiduIndexCore = new BaiduIndexCore("2020-1-31", "2020-3-9", 928, cookie, new String[]{"美国"});
        System.out.println(baiduIndexCore.searchIndexRun());
        System.out.println(baiduIndexCore.regionIndexRun());
    }

    private String getRegionApi() {
        String api = "";
        try {
            api = String.format(BaiduApi.REGION_API, this.area, java.net.URLEncoder.encode(this.keyWord, "UTF-8"));
            if (this.area != 0) {
                api = api + "&area=" + this.area;
            }
        } catch (Exception e) {
            logger.info("error:{}", LogbackUtil.expection2Str(e));
        }
        if (StringUtils.isNotEmpty(this.startDate)) {
            api = api + "&startDate=" + this.startDate;
        }
        if (StringUtils.isNotEmpty(this.endDate)) {
            api = api + "&endDate=" + this.endDate;
        }
        return api;
    }

    /**
     * 关键词格式化处理，与 2020 年 5 月 7 日百度指数官方更新
     *
     * @param keyWord
     * @return
     */
    private String getKeyWordFormat(String keyWord) {
        return String.format(KEYWORD_FORMAT, keyWord);
    }

    private String getIndexApi() {
        String api = "";
        try {
            List<String> results = new ArrayList<>();
            String[] keyWords = this.keyWord.split(",");
            for (String word : keyWords) {
                results.add(getKeyWordFormat(word));
            }
            String keyWordStr = "[" + Joiner.on(",").join(results) + "]";
            api = String.format(BaiduApi.INDEX_API, java.net.URLEncoder.encode(keyWordStr, "UTF-8")) + "&area=" + this.area;
        } catch (Exception e) {
            logger.info("error:{}", LogbackUtil.expection2Str(e));
        }
        if (StringUtils.isNotEmpty(this.startDate)) {
            api = api + "&startDate=" + this.startDate;
        }
        if (StringUtils.isNotEmpty(this.endDate)) {
            api = api + "&endDate=" + this.endDate;
        }
        return api;
    }

    /**
     * 搜索指数 核心方法
     *
     * @return
     */
    public String searchIndexRun() {
        String data = "", ptbk;
        try {
            data = HttpHandle.doGet(getIndexApi(), null, cookie);
            ptbk = JsonPath.read(HttpHandle.doGet(BaiduApi.INDEX_PTBK_API +
                    JsonPath.read(data, "data.uniqid").toString(), null, cookie), "data").toString();
            JSONArray userIndexes = JsonPath.read(data, "$..userIndexes[*]");
            for (Object userIndex : userIndexes) {
                String userIndexStr = FontUtil.chinaToUnicode(GsonUtil.gsonString(userIndex));
                data = data.replace(userIndexStr, parseInfo(userIndexStr, ptbk));
            }
        } catch (Exception e) {
            logger.error("baidu search index crawler error:{}", LogbackUtil.expection2Str(e));
        }

        return data;
    }

    /**
     * 地域分布指数 核心方法
     *
     * @return
     */
    public String regionIndexRun() {
        String data = "";
        try {
            data = HttpHandle.doGet(getRegionApi(), null, cookie);
        } catch (Exception e) {
            logger.error("baidu region index crawler error:{}", LogbackUtil.expection2Str(e));
        }

        return data;
    }

    private String parseInfo(String userIndex, String ptbk) throws Exception {
        String all, pc, wise;
        all = JsonPath.read(userIndex, "all.data").toString();
        pc = JsonPath.read(userIndex, "pc.data").toString();
        wise = JsonPath.read(userIndex, "wise.data").toString();
        return userIndex.replace(all, decrypt(ptbk, all)).replace(pc, decrypt(ptbk, pc)).replace(wise, decrypt(ptbk, wise));
    }

    /**
     * 百度指数 data 解密算法，百度指数 js decrypt()方法解密逻辑复现
     *
     * @param ptbk
     * @param data
     * @return
     * @throws Exception
     */
    private String decrypt(String ptbk, String data) throws Exception {
        StringBuilder result = new StringBuilder();
        try {
            List<String> ptbkSet = flatMap(ptbk);
            List<String> dataSet = flatMap(data);
            int ln = ptbkSet.size() / 2;
            List<String> startSet = ptbkSet.subList(0, ln);
            List<String> endSet = ptbkSet.subList(ln, ptbkSet.size());
            Map<String, String> map = new HashMap<>(ln);
            for (int index = 0; index < ln; index++) {
                map.put(startSet.get(index), endSet.get(index));
            }
            for (String dataChar : dataSet) {
                result.append(map.get(dataChar));
            }
        } catch (Exception e) {
            throw new RuntimeException("decrypt 算法解密失败！！");
        }
        return result.toString();
    }

    /**
     * 扁平化处理
     *
     * @param ptbk
     * @return
     */
    private List<String> flatMap(String ptbk) {
        List<String> list = new ArrayList<>();
        for (char aChar : ptbk.toCharArray()) {
            list.add(String.valueOf(aChar));
        }

        return list;
    }

}
