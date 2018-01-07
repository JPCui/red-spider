# red-spider
:spider:

[![HitCount](http://hits.dwyl.io/jpcui/red-spider.svg)](http://hits.dwyl.io/jpcui/red-spider)


# TODO

- 优化、完善解析器

	- 解析数组
	
- 实现URL分发器

	- 发现URL交由分发器分发到不同队列 

- 重写 downloader，失败要归还到爬取队列

- 失败监听，记录失败URL，告警

# tip

在Jsoup使用 :eq() 查找元素时，使用的是 siblings 方法

而在jQuery中，是在兄弟节点中，符合前缀表达式的兄弟节点的下标

> Integer org.jsoup.nodes.Element.elementSiblingIndex()

比如在下面html中查找 `h4:eq(2)`：

```
<body>
	<h2>1</h2>
	<h4>2</h4>
	<h4>3</h4>
	<h4>4</h4>
</body>
```

jsoup 是从 h2, h4, h4, h4中取下标为2的元素: `<h4>3</h4>`

jQuery 是从 h4, h4, h4中取下标为2的元素: `<h4>4</h4>`
