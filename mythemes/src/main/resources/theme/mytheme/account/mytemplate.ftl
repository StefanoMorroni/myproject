<#macro mainLayout>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <base href=".">    
    <title>TIM Business Account</title>
    <link href="${url.resourcesPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${url.resourcesPath}/css/styles.css" rel="stylesheet" />
    <script src="https://www.google.com/recaptcha/api.js" async="" defer=""></script>
</head>
<body>
        
    <#if message?has_content>
        <div class="alert alert-danger alert-dismissible">
            <span>${kcSanitize(message.summary)?no_esc}</span>
        </div>
    </#if>
    
    <#nested "content">

</body>
</html>
</#macro>