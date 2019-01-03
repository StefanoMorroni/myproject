<#import "mytemplate.ftl" as layout>
<@layout.registrationLayout ; section>
    <#if section = "form">
        <div class="container">
            <div class="header">
                <img src="${url.resourcesPath}/img/tim-logo.svg">
                <h2>TIM Business Account</h2>
            </div>

            <div class="login-container">
                
                <h4>Inserisci qui il codice di conferma che ti abbiamo inviato via sms al numero <i>${(username!'')}</i></h4>

                <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">

                    <div class="form-group">
                        <input id="token" name="token" type="text" class="form-control" value="">
                    </div>

                    <input type="submit" class="btn btn-primary btn-block btn-login" value="Procedi" />

                    <div style="text-align:center">
                        <h4>Ti ricordiamo che il codice che ti abbiamo inviato è utilizzabile entro 15 minuti, dopodiché, se non completi l'operazione, dovrai reinserire il tuo numero di telefono o la tua e-mail.</h4>
                        <a class="text-muted" href="${url.loginRestartFlowUrl}"><ins>Torna indietro</ins></a>
                    </div>

                </form>
            </div>
        </div>
    </#if>

</@layout.registrationLayout>
