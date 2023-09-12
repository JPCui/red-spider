# red-spider
:spider:
[![HitCount](http://hits.dwyl.io/jpcui/red-spider.svg)](http://hits.dwyl.io/jpcui/red-spider)
[![Build Status](https://travis-ci.org/JPCui/red-spider.svg?branch=master)](https://travis-ci.org/JPCui/red-spider)

# 总体架构

- 抓取
- 解析
- 发现

# TODO

- [x] 数据模型
  - [x] 基础配置（站点、版本等）
  - [x] 配置抓取策略（域名、入口、cookie、是否循环抓取等）
    - [x] 配置子模块抓取规则（字段、pipeline）

- [20%]爬取类型
  - html
  - json
- 
- 种子发现策略
  - 从当前url生成 
    - [x] 当前url上匹配pageNum，然后+1
    - [0%] 对于游标分页，一般存在于分页内容中，获取列表中的【游标字段】作为下次抓取的url游标参数
  - 从当前抓取的内容中生成
    - [x] 从html中提取a标签（最通用的策略，符合绝大多数）
    - [] 【URL_TEMPLATE】用给出的url模板生成：1. 从解析出的字段替换；2. 当前url上匹配pageNum，初始值为1

- 种子缓存策略
  - [] 内存；每次重启后销毁内存中的种子，然后根据rootUrl重新爬数据；
  - [x] redis；增量爬；
  - [] 允许用户自己选择使用哪种策略；可以在model中配置；

- [0%] 静态数据 VS 动态数据
  - [0%]动态数据允许 定时、重复 抓取
    
- [0%] 实现URL分发器
  - 发现URL交由分发器分发到不同队列 

- [0%] 重写 downloader，失败要归还到爬取队列

- [0%] 失败监听，记录失败URL，告警

- [0%] 前端-流程监控
  - worker管理 
    - 启动、暂停、停止worker
    - task配置
      - 配置模块、规则
      - 配置完成后是否自动启动/开机自动启动（"autoStartup": true）
    - pipeline配置：每个 task/module 管理各自的pipeline ，可配置默认的pipeline
      - ~~全局：LogPipeline~~
      - 默认：LogPipeline
      - 邮箱
      - 企微
      - 。。。
    - worker配置
      - 配置可用pipeline
      - 
  - 监控每个线程的工作情况
    - 正在解析的url
    - 种子数（待爬取）、已爬取
    - 处理数监控
      - 每个parser实时处理数（维度：worker、module、table、http proxy）
      - 每个discovery实时处理数
      - 每个pipeline实时处理数
      - 每个代理的实时处理数
  - 代理监控
  - 参考：https://github.com/wangweianger/zanePerfor

# 如何增量抓取

关于增量抓取有两个方案：
- ~~定时任务，定时重启抓取任务~~
- [x]定时重置初始队列，相对简单点，不会影响本身的架构

# 待跟进项目

- 爬取和解析是否可以分开
  - 好处：
    - 各司其职
    - 单独部署
    - 分开监控

