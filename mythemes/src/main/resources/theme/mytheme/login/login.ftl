<#import "mytemplate.ftl" as layout>
<@layout.registrationLayout ; section>
    <#if section = "form">
        <div class="container">
            <div class="header">
                <img src="${url.resourcesPath}/img/tim-logo.svg">
                <h2>TIM Business Account</h2>
            </div>

            <div class="login-container">
                ${msg("loginMessage")?no_esc}
                <form onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                    <div class="form-group">
                        <input tabindex="1" id="username" class="form-control" name="username" value="${(login.username!'')}"  type="text" autofocus autocomplete="off" placeholder="${msg("usernameOrEmail")}" />
                    </div>
                    
                    <div class="form-group">
                        <input tabindex="2" id="password" class="form-control" name="password" type="password" autocomplete="off" placeholder="${msg("password")}" />
                    </div>

                    <input tabindex="3" class="btn btn-primary btn-block btn-login" name="login" id="kc-login" type="submit" value="ACCEDI"/>
                </form>

                <a class="helper" tabindex="5" href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a>            
                <a class="helper" tabindex="5" href="${url.registrationUrl}">${msg("doRegister2")?no_esc}</a>            
                <div class="strikethrough"><span>oppure</span></div>
            
                <#if social.providers??>
                    <#list social.providers as p>
                        <#if "${p.alias}"="google">
                            <div class="row">
                                <div class="col-xs-12 col-md-8 col-md-offset-2">
                                    <a href="${p.loginUrl}"><div class="btn btn-social btn-block gplus-login"><i class="fa fa-google-plus"></i>Accedi con Google+</div></a>
                                </div>
                            </div>
                         <#elseif "${p.alias}"="facebook">
                            <div class="row">
                                <div class="col-xs-12 col-md-8 col-md-offset-2">
                                    <a href="${p.loginUrl}"><div class="btn btn-social btn-block fb-login"><i class="fa fa-facebook"></i>Accedi con Facebook</div></a>
                                </div>
                            </div>
                        </#if>
                    </#list>
                </#if>
            </div>
        </div>
    </#if>

</@layout.registrationLayout>
