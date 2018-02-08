<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="${staticServerPath}/css/style.css"/>
    <title>${data.book.name}</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <style>
    </style>

</head>
<body id="body">
<header>
    <script src="${staticServerPath}/js/canvas-nest.js" count="200" zindex="-2" opacity="0.8" color="47,135,193" type="text/javascript"></script>
</header>

<div class="box">
    <ul class="list-group">
        <li class="list-group-item">
            <a href="${serverPath}/books">首页</a>
        </li>
		<#list data.chaptors as cha>
        <li class="list-group-item">
        	<#if cha.viewId??>
            <a href="${serverPath}/book/${data.book._id}/${cha.viewId}">${cha.chaptorName}</a>
        	<#else>
            ${cha.chaptorName}
        	</#if>
        </li>
    	</#list>
    </ul>
</div>


</body>
</html>
