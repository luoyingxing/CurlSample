====================================================================================================

           ┌─┐       ┌─┐
        ┌──┘ ┴───────┘ ┴──┐
        │                 │
        │       ───       │
        │  ─┬┘       └┬─  │
        │                 │
        │       ─┴─       │
        │                 │
        └───┐         ┌───┘
            │         │
            │         │
            │         │
            │         └──────────────┐
            │                        │
            │                        ├─┐
            │                        ┌─┘
            │                        │
            └─┐  ┐  ┌───────┬──┐  ┌──┘
              │ ─┤ ─┤       │ ─┤ ─┤
              └──┴──┘       └──┴──┘

## 警云·数据采集接口API说明文档·HTTPS

#### 1 奔溃日志采集
##### 1.1 接口描述
请求URL：[POST] https://cos.conwin.cn:8443/log/crash
请求参数：json对象，对象字段可以自定义，eg:
{
 "width":"1080",
 "height":"1920",
 "verName":"1.0",
 "verCode":"1",
 "package":"con.conwin.test",
 "phoneModel":"test",
 "phoneBrand":"test",
 "systemVer":"6.0",
 "cpuModel":"aarch64",
 "cpuInstruction":"armeabi-v7a",
 "cpuInstruction2":"armeabi-v7a",
 "phoneIMEI":"test",
 "root:":"false",
 "time":"2020-10-19 12:00:01",
 "exception":"TestException ...",
}
请求响应：code=200  body=


#### 2 用户使用情况采集
##### 2.1 接口描述
请求URL：[POST] https://cos.conwin.cn:8443/log/usage
请求参数：
ref   String    额外的字段，可以存储任何值，直接跟在url后面
{}    json对象  对象字段可以自定义

请求响应：code=200   body=


#### 3 奔溃日志获取
##### 3.1 接口描述
请求URL：http://cos.conwin.cn:9999