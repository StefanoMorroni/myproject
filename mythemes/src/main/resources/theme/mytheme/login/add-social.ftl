<#import "mytemplate.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "form">
        <div class="container">
            <div class="header">
                <img src="${url.resourcesPath}/img/tim-logo.svg">
                <h2>TIM Business Account</h2>
            </div>

            <div class="login-container">
                ${msg("addSocialMessage")?no_esc}

                <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">

                    <div class="form-group">
                        <input tabindex="1" id="username" class="form-control" name="username" value="${(username!'')}"  type="text" autofocus autocomplete="off" placeholder="${msg("usernameOrEmail")}" />
                    </div>

                    <div class="form-group">
                        <input tabindex="2" id="password" class="form-control" name="password" type="password" autocomplete="off" placeholder="${msg("password")}" />
                    </div>

                    <input tabindex="3" class="btn btn-primary btn-block btn-login" name="login" id="kc-login" type="submit" value="${msg("doConfirmSocialAccount")}"/>

                    <a class="helper" href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a>

                    <div class="strikethrough">${msg("addSocialMessage2")?no_esc}</div>

                    <a class="btn btn-primary btn-block btn-login" href="${url.registrationUrl}">${msg("doRegister")}</a>
                </form>
            </div>

        </div>
    </#if>

</@layout.registrationLayout>