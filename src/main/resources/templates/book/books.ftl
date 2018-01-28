<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <link rel="stylesheet" href="${staticServerPath}/css/style.css" />
  <title>藏書</title>
  
<style>
    * {
        list-style-type: none;
    }
    div.box {
    }
    ul {
        display: table;
    }
    ul li {
        display: table-row;
        border-bottom: 1px solid silver;
    }
</style>
  
</head>
<body id="body">
<header>
  <script src="${staticServerPath}/js/canvas-nest.js" count="200" zindex="-2" opacity="0.8" color="47,135,193" type="text/javascript"></script>
</header>

<div class="box">
	<#list data as d>
    <ul>
    	<li>
    		<h2><a href="/book/${d._id}">${d.name}</a></h2>
    	</li>
    	<li>
    		作者：<a href="/book/author/${d.author}">${d.author}</a>
    	</li>
    	<li>
	    	<#if d.type??>
    		分類：
    		<a href="/book/type/${d.type}">${d.type}</a>
	    	</#if>
    	</li>
    	<li>
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


</body>
</html>
