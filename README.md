# red-spider
:spider:
[![HitCount](http://hits.dwyl.io/jpcui/red-spider.svg)](http://hits.dwyl.io/jpcui/red-spider)
[![Build Status](https://travis-ci.org/JPCui/red-spider.svg?branch=master)](https://travis-ci.org/JPCui/red-spider)

# 总体架构

- 抓取
- 解析
- 发现

# TODO

- [20%]爬取类型
  - html
  - json
- 种子发现策略
  - [0%] 从html中提取a标签
  - 分页
    - [x][100%]从url上匹配pageNum，然后+1
    - [0%]指针，从页面结果中提取指针作为下次抓取的url指针参数
    - [0%]

- [0%] 静态数据 VS 动态数据
  - 动态数据允许 定时、重复 抓取
    
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

# 待跟进项目

- 爬取和解析是否可以分开
  - 好处：
    - 各司其职
    - 单独部署
    - 分开监控

