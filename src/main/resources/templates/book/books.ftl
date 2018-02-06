<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="${staticServerPath}/css/style.css"/>
    <title>藏書</title>
    <style>
    </style>

</head>
<body id="body">

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <form action="${serverPath}/books" class="bs-example bs-example-form" data-example-id="simple-input-groups" method="get">
                <div class="input-group">
                    <span class="input-group-addon" id="basic-addon1">书名</span>
                    <input type="text" class="form-control" name="name" value="${name}" placeholder="Book Name" aria-describedby="basic-addon1">
                </div>
                <br>
                <div class="input-group">
                    <span class="input-group-addon" id="basic-addon1">类型</span>
                    <input type="text" class="form-control" name="type" value="${type}" placeholder="Type" aria-describedby="basic-addon1">
                </div>
                <br>
                <div class="input-group">
                    <span class="input-group-addon" id="basic-addon1">作者</span>
                    <input type="text" class="form-control" name="author" value="${author}" placeholder="Author" aria-describedby="basic-addon1">
                </div>
                <br>
                <div class="input-group">
                    <span class="input-group-addon" id="basic-addon1">标签</span>
                    <input type="text" class="form-control" name="tags" value="${tags}" placeholder="Tags" aria-describedby="basic-addon1">
                </div>
                <br>
                <button type="submit">Go!</button>
            </form>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
	    <#list data as d>
            <ul class="list-group">
                <li class="list-group-item">
                    <h2><a href="/book/${d._id}">${d.name}</a></h2>
                </li>
                <li class="list-group-item">
                    作者：<a href="/book/author/${d.author}">${d.author}</a>
                </li>
                <li class="list-group-item">
                <#if d.type??>
                    分類：
                    <a href="/book/type/${d.type}">${d.type}</a>
                </#if>
                </li>
                <li class="list-group-item">
                <#if d.tags??>
                    標簽：
                <#list d.tags as tag>
                    <a href="/book/tags/${tag}">${tag}</a> &nbsp;
                </#list>
                </#if>
                </li>
                <li>${d.summary}</li>
            </ul>
        </#list>
        </div>
    </div>
</div>


</body>
</html>
