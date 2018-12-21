<#import "mytemplate.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "form">
        <div class="container">
            <div class="header">
                <img src="${url.resourcesPath}/img/tim-logo.svg">
                <h2>TIM Business Account</h2>
            </div>
            <div class="login-container">
                <form id="kc-register-form" action="${url.registrationAction}" method="post">
                    <div class="form-group">
                        <input tabindex="1" id="username" class="form-control" name="username" value=""  type="text" autofocus autocomplete="off" placeholder="${msg("usernameOrEmail")}" />
                    </div>

                    <#if firstNameRequired??>
                    <div class="form-group">
                        <input tabindex="1" id="firstName" class="form-control" name="firstName" value=""  type="text" autofocus autocomplete="off" placeholder="${msg("firstName")}" />
                    </div>
                    </#if>

                    <#if lastNameRequired??>
                    <div class="form-group">
                        <input tabindex="1" id="lastName" class="form-control" name="lastName" value=""  type="text" autofocus autocomplete="off" placeholder="${msg("lastName")}" />
                    </div>
                    </#if>

                    <#if emailRequired??>
                    <div class="form-group">
                        <input tabindex="1" id="email" class="form-control" name="email" value=""  type="text" autofocus autocomplete="off" placeholder="${msg("email")}" />
                    </div>
                    </#if>

                    <#if passwordRequired??>
                    <div class="form-group">
                        <input tabindex="2" id="password" class="form-control" name="password" type="password" autocomplete="off" placeholder="${msg("password")}" />
                    </div>

                    <div class="form-group">
                        <input tabindex="3" id="password-confirm" class="form-control" name="password-confirm" type="password" autocomplete="off" placeholder="${msg("passwordConfirm")}" />
                    </div>
                    </#if>

                    <div cssclass="form-control ng-pristine ng-valid ng-empty ng-valid-email ng-touched">
                        <label for="" id="termsLabel" style="display: block; text-align: center;">Cliccando su "Registrati" dichiari di aver preso visione della&nbsp;<a href="${url.resourcesPath}/privacy.pdf" target="_blank">informativa privacy</a>&nbsp;&nbsp;&nbsp;
                        </label>
                    </div>

                    <#if recaptchaRequired??>
                    <div class="form-group">
                        <div class="${properties.kcInputWrapperClass!}">
                            <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                        </div>
                    </div>
                    </#if>



                    <input tabindex="4" class="btn btn-primary btn-block btn-login" name="login" id="kc-login" type="submit" value="${msg("doRegister")}"/>

                    <div class="text-muted" style="text-align:center">
                        <a class="text-muted" href="${url.loginUrl}"><ins>Clicca qui per  Accedere o Recuperare la Password</ins></a>
                    </div>
                </form>
            </div>
        </div>

    </#if>
</@layout.registrationLayout>
