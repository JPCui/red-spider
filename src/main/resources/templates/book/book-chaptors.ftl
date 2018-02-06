<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <link rel="stylesheet" href="${staticServerPath}/css/style.css"/>
    <title>${data.book.name}</title>

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
    <ul>
	<#list data.chaptors as cha>
        <li>
            <a href="/book/${data.book._id}/${item_index?if_exists+1}">${cha.chaptorName}</a>
        </li>
    </#list>
    </ul>
</div>


</body>
</html>
