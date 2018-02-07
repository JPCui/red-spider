<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="${staticServerPath}/css/style.css"/>
    <title>${data.book.name}</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
    <script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</head>
<body>

<div class="container">
    <div class="row">
		<ul class="pager">
			<#if data.section.prev??>
				<li class="previous">
				<a href="${data.section.prev.index}" aria-label="${data.section.prev.message}">
				<span aria-hidden="true">${data.section.prev.message}</span>
				</a>
				</li>
        	</#if>
			
			<li><a href="./">目录</a></li>
			
			<#if data.section.next??>
				<li class="next">
				<a href="${data.section.next.index}" aria-label="${data.section.next.message}">
				<span aria-hidden="true">${data.section.next.message}</span>
				</a>
				</li>
        	</#if>
		</ul>
	</div>
    <div class="row">
        <div class="col-sm-1"></div>
        <div class="panel panel-default">
			<div class="panel-heading">${data.section.title}</div>
			<#list data.section.content as c>
			<div class="panel-body">
			${c}
			</div>
            </#list>
		</div>
        <div class="col-sm-1"></div>
    </div>

    <div class="row">
		<ul class="pager">
			<#if data.section.prev??>
				<li class="previous">
				<a href="${data.section.prev.index}" aria-label="${data.section.prev.message}">
				<span aria-hidden="true">${data.section.prev.message}</span>
				</a>
				</li>
        	</#if>
			
			<li><a href="./">目录</a></li>
			
			<#if data.section.next??>
				<li class="next">
				<a href="${data.section.next.index}" aria-label="${data.section.next.message}">
				<span aria-hidden="true">${data.section.next.message}</span>
				</a>
				</li>
        	</#if>
		</ul>
	</div>
</div>


</body>
</html>
