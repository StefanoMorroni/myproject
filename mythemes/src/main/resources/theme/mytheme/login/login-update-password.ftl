<#import "mytemplate.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "form">
        <div class="container">
            <div class="header">
                <img src="${url.resourcesPath}/img/tim-logo.svg">
                <h2>TIM Business Account</h2>
            </div>
            <div class="login-container">
                <form id="kc-passwd-update-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
                    <#if TDB??>
                    <input type="password" id="password" name="password" autocomplete="current-password" style="display:none;"/>

                    ${msg("loginUpdateMessage")?no_esc}

                    <div class="form-group">
                        <input tabindex="1" id="username" class="form-control" name="username" type="text" autocomplete="username" readonly="readonly" />
                    </div>
                    </#if>

                    ${msg("loginUpdateMessage2")?no_esc}

                    <div class="form-group">
                        <input tabindex="2" id="password-new" class="form-control" name="password-new" type="password" autocomplete="off" placeholder="${msg("password")}" />
                    </div>

                    <div class="form-group">
                        <input tabindex="3" id="password-confirm" class="form-control" name="password-confirm" type="password" autocomplete="off" placeholder="${msg("passwordConfirm")}" />
                    </div>

                    <input tabindex="4" class="btn btn-primary btn-block btn-login" name="login" id="kc-login" type="submit" value="Conferma Registrazione"/>

                </form>
            </div>
        </div>
    </#if>
</@layout.registrationLayout>
