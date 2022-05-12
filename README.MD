## 百度指数爬虫核心类

### Class BaiduIndexCore

* 构造方法
> ***BaiduIndexCore***(String cookie,String... keyWords)
>> 构造一个BaiduIndexCore对象，keyWord为爬取百度指数的关键词(可传递字符串或字符串数组)，cookie为登陆百度指数的cookie，注意：必须是登陆cookie，游客cookie无效！！

> ***BaiduIndexCore***(String startDate, String endDate, String cookie, String... keyWords)
>> 构造一个BaiduIndexCore对象，keyWord为爬取百度指数的关键词(可传递字符串或字符串数组)，startDate为开始时间，endDate为结束时间，cookie为登陆百度指数的cookie，注意：必须是登陆cookie，游客cookie无效！！

> ***BaiduIndexCore***(String startDate, String endDate, int area, String cookie, String... keyWords)
>> 构造一个BaiduIndexCore对象，keyWord为爬取百度指数的关键词(可传递字符串或字符串数组)，startDate为开始时间，endDate为结束时间，area为地区代码（详见资源目录各地市对应代码），cookie为登陆百度指数的cookie，注意：必须是登陆cookie，游客cookie无效！！

* 方法摘要
> **searchIndexRun()**
> > 从当前BaiduIndexCore对象获取百度搜索指数官方json数据。
>
> **regionIndexRun()**
> > 从当前BaiduIndexCore对象获取百度地域分布指数官方json数据。

* 关于返回参数
> **all**:pc端+移动端数据
> **pc**:pc端数据
> **wise**:移动端数据
> 涉及到的参数比较多，不一一列出，建议对照百度指数官方页面查看。

* 更新
> 1. *20191231*
>    - 原先构造函数只支持传递一个String类型的keyWord，先支持传递单个String及关键词数组
>    - 添加regionIndexRun()地域分布指数方法，原先run()方法更名为searchIndexRun()

* 开发者
>  **邮箱**：287510038@qq.com  
>  **QQ**: 287510038
>  **WECHAT**: b287510038
