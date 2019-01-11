<#import "mytemplate.ftl" as layout>
<@layout.mainLayout; section>

<div class="container">
    <div class="header">
            <img src="${url.resourcesPath}/img/tim-logo.svg">
            <h2>TIM Business Account</h2>
    </div>
    <div class="login-container">
            <h4><strong>Modifica credenziali di accesso</strong></h4>
            <form action="${url.accountUrl}" method="POST">     
                    <input type="hidden" id="stateChecker" name="stateChecker" value="${stateChecker}" />
                    <input type="hidden" id="username" name="username" value="${(account.username!'')}" />
                    <input type="hidden" id="firstName" name="firstName" value="N/A" />
                    <input type="hidden" id="lastName" name="lastName" value="N/A" />
                    <div class="form-group">
                            <h5>Modifica l'indirizzo e-mail</h5>
                            <input id="email" name="email" placeholder="Indirizzo e-mail non specificato" type="text" class="form-control" value="${(account.email!'')}">
                    </div>

                    <button type="submit" id="submit" class="btn btn-primary btn-block btn-login" name="submitAction" value="Save">Conferma la modifica della e-mail</button>
            </form>
            <form action="${url.accountUrl}" method="POST">
                    <input type="hidden" id="stateChecker" name="stateChecker" value="${stateChecker}">
                    <input type="hidden" id="username" name="username" value="${(account.username!'')}">
                    <input type="hidden" id="firstName" name="firstName" value="N/A" />
                    <input type="hidden" id="lastName" name="lastName" value="N/A" />
                    <input type="hidden" id="email" name="email" value="${(account.email!'')}">
                    <div class="form-group">
                            <h5>Modifica il numero di cellulare</h5>
                            <input id="user.attributes.mobile" name="user.attributes.mobile" class="form-control" placeholder="Numero di cellulare non specificato" type="text" value="${(account.attributes.mobile!'')}">
                    </div>

                    <button type="submit" id="submit" class="btn btn-primary btn-block btn-login" name="submitAction" value="Save">Conferma la modifica del cellulare</button>
            </form>
            <form action="${url.passwordUrl}" method="POST">
                    <input type="hidden" id="stateChecker" name="stateChecker" value="${stateChecker}">
                    <div class="form-group">
                            <h5>Modifica la password</h5>
                            <div class="form-group">
                                    <input id="password" name="password" class="form-control" placeholder="Password attuale" type="password" value="">
                            </div>
                            <div class="form-group">
                                    <input id="password-new" name="password-new" class="form-control" placeholder="Nuova Password" type="password" value="">
                            </div>
                            <div class="form-group">
                                    <input id="password-confirm" name="password-confirm" class="form-control" placeholder="Ripeti nuova Password" type="password" value="">
                            </div>
                    </div>

                    <button type="submit" id="submit" class="btn btn-primary btn-block btn-login" name="submitAction" value="Save">Conferma la modifica della password</button>
            </form>

            <div style="text-align:center">
                    <#if referrer?has_content && referrer.url?has_content><a href="${referrer.url}" id="referrer"><ins>Clicca qui per tornare alla pagina di provenienza</ins></a></#if>
            </div>

    </div>
</div>

</@layout.mainLayout>