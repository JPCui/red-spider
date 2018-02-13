<#include "../macro/pager.ftl">
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>藏書</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <style>
    </style>

</head>
<body id="body">

<div class="container">
	<br/>
    <div class="row">
        <div class="col-md-12">
            <form action="${serverPath}/books" class="bs-example bs-example-form" data-example-id="simple-input-groups" method="get">
                <div class="input-group">
                    <span class="input-group-addon" id="basic-addon1">书名</span>
                    <input type="text" class="form-control" name="name" value="${name!""}" placeholder="Book Name" aria-describedby="basic-addon1">
	                <span class="input-group-btn">
						<button class="btn btn-default" type="submit">Go!</button>
					</span>
                </div>
                <!-- 
                <br>
                <div class="input-group">
                    <span class="input-group-addon" id="basic-addon1">类型</span>
                    <input type="text" class="form-control" name="type" value="${type!""}" placeholder="Type" aria-describedby="basic-addon1">
                </div>
                <br>
                <div class="input-group">
                    <span class="input-group-addon" id="basic-addon1">作者</span>
                    <input type="text" class="form-control" name="author" value="${author!""}" placeholder="Author" aria-describedby="basic-addon1">
                </div>
                <br>
                <div class="input-group">
                    <span class="input-group-addon" id="basic-addon1">标签</span>
                    <input type="text" class="form-control" name="tags" value="${tags!""}" placeholder="Tags" aria-describedby="basic-addon1">
                </div>
                 -->
            </form>
        </div>
    </div>
	<br/>
    <div class="row">
		<@pager prevPage="${data.prevPage}" prevMsg="上一页" nextPage="${data.nextPage}" nextMsg="下一页" lastPage="${serverPath}/books" lastMsg="目录">
		</@pager>
    </div>
	<br/>
    <div class="row">
        <div class="col-md-12">
	    <#list data.resultList as d>
            <ul class="list-group">
                <li class="list-group-item">
                    <h2><a href="${serverPath}/book/${d._id}">${d.name}</a></h2>
                </li>
                <li class="list-group-item">
                    作者：<a href="${serverPath}/book/author/${d.author}">${d.author}</a>
                </li>
                <li class="list-group-item">
                <#if d.type??>
                    分類：
                    <a href="${serverPath}/book/type/${d.type}">${d.type}</a>
                </#if>
                </li>
                <#if d.tags?? && (d.tags?size > 0)>
                <li class="list-group-item">
					標簽：
                <#list d.tags as tag>
                    <a href="${serverPath}/book/tags/${tag}">${tag}</a> &nbsp;
                </#list>
                </li>
                </#if>
                <li class="list-group-item">
                	${d.summary}
                </li>
            </ul>
        </#list>
        </div>
    </div>
</div>


</body>
</html>
