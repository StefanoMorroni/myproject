<#import "mytemplate.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "form">
        <div class="container">
            <div class="header">
                <img src="${url.resourcesPath}/img/tim-logo.svg">
                <h2>TIM Business Account</h2>
            </div>
            <div class="login-container">
                ${msg("emailVerifyInstruction1")?no_esc}
                ${msg("emailVerifyInstruction2")?no_esc}
                ${msg("emailVerifyInstruction3")?no_esc}
                <a class="helper" href="${url.loginAction}">${msg("emailVerifyInstruction4")}</a>
            </div>
        </div>
    </#if>
</@layout.registrationLayout>