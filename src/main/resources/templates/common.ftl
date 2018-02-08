
<#macro common_head title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${staticServerPath}/css/style.css">
    <link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <#nested>
    <style type="text/css">

    </style>
    <title>${title}</title>
</#macro>

<#macro common_body>
    <#nested>
</#macro>

<#macro common_bottom>
    <#nested>
<script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
</#macro>

