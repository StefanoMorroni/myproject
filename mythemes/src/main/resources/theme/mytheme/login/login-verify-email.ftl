<#import "mytemplate.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "form">
        <div class="container">
            <div class="header">
                <img src="${url.resourcesPath}/img/tim-logo.svg">
                <h2>TIM Business Account</h2>
            </div>
            <div class="login-container">
                    ${msg("emailVerifyInstruction1",username)?no_esc}
                    <div class="text-muted" style="text-align:center">
                            ${msg("emailVerifyInstruction3")?no_esc}
                            <a class="text-muted" href="${url.loginRestartFlowUrl}">${msg("emailVerifyInstruction4")?no_esc}</a>
                    </div>
            </div>
        </div>
    </#if>
</@layout.registrationLayout>