<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <link rel="stylesheet" href="${staticServerPath}/css/style.css" />
  <title>${data.book.name}</title>
  
<link rel="stylesheet" href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css">  
<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  
</head>
<body>

<div class="container">
	<div class="row">
		<div class="col-sm-1">
		</div>
		<div class="col-sm-10">
			<#list data.section.content as c>
		    	<p>
		    	${c}
		    	</p>
		    </#list>
		</div>
		<div class="col-sm-1">
		</div>
	</div>
	
	<hr/>
	
	<div class="row">
	<div class="col-sm-4">
		<#if data.section.prev??>
		<a href="${data.section.prev.index}" alt="${data.section.prev.message}"><<上一章</a>
		</#if>
	</div>
	<div class="col-sm-4">
		<a href="../">目录</a>
	</div>
	<div class="col-sm-4">
		<#if data.section.next??>
		<a href="${data.section.next.index}" alt="${data.section.next.message}">下一章>></a>
		</#if>
	</div>
	</div>
</div>


</body>
</html>
