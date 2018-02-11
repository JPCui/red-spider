<#include "../common.ftl">
<!DOCTYPE html>
<html lang="en">
<head>
<@common_head title="404">
    <link rel="stylesheet" href="${staticServerPath}/css/style.css">
</@common_head>
</head>

<body>
<@common_body>
<#if message??>
    message
<#else>
    <img src="https://static.hacpai.com/images/404/${.now?long % 10}.gif" />
</#if>
</@common_body>
</body>
</html>
