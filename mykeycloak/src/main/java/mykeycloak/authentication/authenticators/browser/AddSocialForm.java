/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mykeycloak.authentication.authenticators.browser;

import java.util.LinkedList;
import java.util.List;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import mykeycloak.utils.KeycloakModelUtils;
import mykeycloak.utils.ValidatorUtil;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.broker.AbstractIdpAuthenticator;
import org.keycloak.authentication.authenticators.broker.util.SerializedBrokeredIdentityContext;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.credential.CredentialInput;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.FederatedIdentityModel;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.messages.Messages;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AddSocialForm
		extends AbstractUsernameFormAuthenticator
		implements Authenticator {

	private static final Logger logger = Logger.getLogger(AddSocialForm.class);

	@Override
	public void authenticate(AuthenticationFlowContext context) {
		LoginFormsProvider loginFormsProvider = context.form();

		try {
			SerializedBrokeredIdentityContext serializedCtx = SerializedBrokeredIdentityContext.readFromAuthenticationSession(context.getAuthenticationSession(), AbstractIdpAuthenticator.BROKERED_CONTEXT_NOTE);
			BrokeredIdentityContext brokeredIdentityContext = serializedCtx.deserialize(context.getSession(), context.getAuthenticationSession());
			String _username = brokeredIdentityContext.getEmail();
			UserModel _user = KeycloakModelUtils.findUserByUsernameEmailOrMobile(context.getSession(), context.getRealm(), _username);
			if (_user != null) {
				logger.info("username = "+_username);
				loginFormsProvider.setAttribute(AuthenticationManager.FORM_USERNAME, _username);
			}
		} catch (Exception e) {
		}

		Response challenge = loginFormsProvider.createForm("add-social.ftl");
		context.challenge(challenge);
	}

	@Override
	public void action(AuthenticationFlowContext context) {
		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
		if (formData.containsKey("cancel")) {
			context.cancelLogin();
			return;
		}
		if (!validateForm(context, formData)) {
			return;
		}
		context.success();
	}

	protected boolean validateForm(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
		return validateUserAndPassword(context, formData);
	}

	@Override
	public boolean validateUserAndPassword(AuthenticationFlowContext context, MultivaluedMap<String, String> inputData) {
		String username = inputData.getFirst(AuthenticationManager.FORM_USERNAME);
		if (username == null) {
			context.getEvent().error(Errors.USER_NOT_FOUND);
			//Response challengeResponse = challenge(context, Messages.INVALID_USER);
			Response challengeResponse = context.form()
					.setError(Messages.INVALID_USER)
					.createForm("add-social.ftl");
			context.failureChallenge(AuthenticationFlowError.INVALID_USER, challengeResponse);
			return false;
		}

		// remove leading and trailing whitespace
		username = username.trim();
		if(ValidatorUtil.validateMobile(username)) {
			username = ValidatorUtil.normalizeMobile(username);
		}
		
		context.getEvent().detail(Details.USERNAME, username);
		context.getAuthenticationSession().setAuthNote(AbstractUsernameFormAuthenticator.ATTEMPTED_USERNAME, username);

		UserModel user = null;
		try {
			user = KeycloakModelUtils.findUserByUsernameEmailOrMobile(context.getSession(), context.getRealm(), username);
		} catch (ModelDuplicateException mde) {
			ServicesLogger.LOGGER.modelDuplicateException(mde);

			// Could happen during federation import
			if (mde.getDuplicateFieldName() != null && mde.getDuplicateFieldName().equals(UserModel.EMAIL)) {
				//setDuplicateUserChallenge(context, Errors.EMAIL_IN_USE, Messages.EMAIL_EXISTS, AuthenticationFlowError.INVALID_USER);
				context.getEvent().error(Errors.EMAIL_IN_USE);
				Response challengeResponse = context.form()
						.setError(Messages.EMAIL_EXISTS)
						.createForm("add-social.ftl");
				context.failureChallenge(AuthenticationFlowError.INVALID_USER, challengeResponse);
			} else {
				//setDuplicateUserChallenge(context, Errors.USERNAME_IN_USE, Messages.USERNAME_EXISTS, AuthenticationFlowError.INVALID_USER);
				context.getEvent().error(Errors.USERNAME_IN_USE);
				Response challengeResponse = context.form()
						.setError(Messages.USERNAME_EXISTS)
						.createForm("add-social.ftl");
				context.failureChallenge(AuthenticationFlowError.INVALID_USER, challengeResponse);
			}

			return false;
		}

		if (user == null) {
			context.getEvent().error(Errors.USER_NOT_FOUND);
			//Response challengeResponse = challenge(context, Messages.INVALID_USER);
			Response challengeResponse = context.form()
					.setError(Messages.INVALID_USER)
					.createForm("add-social.ftl");
			context.failureChallenge(AuthenticationFlowError.INVALID_USER, challengeResponse);
			return false;
		}

		if (!validatePassword(context, user, inputData)) {
			return false;
		}

		if (!enabledUser(context, user)) {
			return false;
		}

		// controllo se l'utente ha già un account social
		SerializedBrokeredIdentityContext serializedCtx = SerializedBrokeredIdentityContext.readFromAuthenticationSession(context.getAuthenticationSession(), AbstractIdpAuthenticator.BROKERED_CONTEXT_NOTE);
		BrokeredIdentityContext brokeredIdentityContext = serializedCtx.deserialize(context.getSession(), context.getAuthenticationSession());

		for (FederatedIdentityModel rec : context.getSession().userLocalStorage().getFederatedIdentities(user, context.getRealm())) {
			if (rec.getIdentityProvider().equalsIgnoreCase(brokeredIdentityContext.getIdpConfig().getAlias())) {
				context.getEvent().user(user);
				context.getEvent().error(Errors.FEDERATED_IDENTITY_EXISTS);
				Response challengeResponse = context.form()
						.setError("federatedIdentityExistsMessage", rec.getIdentityProvider())
						.createForm("add-social.ftl");
				context.failureChallenge(AuthenticationFlowError.IDENTITY_PROVIDER_ERROR, challengeResponse);
				context.clearUser();
				return false;
			}
		}

		String rememberMe = inputData.getFirst("rememberMe");
		boolean remember = rememberMe != null && rememberMe.equalsIgnoreCase("on");
		if (remember) {
			context.getAuthenticationSession().setAuthNote(Details.REMEMBER_ME, "true");
			context.getEvent().detail(Details.REMEMBER_ME, "true");
		} else {
			context.getAuthenticationSession().removeAuthNote(Details.REMEMBER_ME);
		}
		context.setUser(user);
		return true;
	}

	@Override
	public boolean validatePassword(AuthenticationFlowContext context, UserModel user, MultivaluedMap<String, String> inputData) {
		List<CredentialInput> credentials = new LinkedList<>();
		String password = inputData.getFirst(CredentialRepresentation.PASSWORD);
		credentials.add(UserCredentialModel.password(password));

		if (isTemporarilyDisabledByBruteForce(context, user)) {
			return false;
		}

		if (password != null && !password.isEmpty() && context.getSession().userCredentialManager().isValid(context.getRealm(), user, credentials)) {
			return true;
		} else {
			context.getEvent().user(user);
			context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
			//Response challengeResponse = challenge(context, Messages.INVALID_USER);
			Response challengeResponse = context.form()
					.setError(Messages.INVALID_USER)
					.createForm("add-social.ftl");
			context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challengeResponse);
			context.clearUser();
			return false;
		}
	}

	@Override
	public boolean enabledUser(AuthenticationFlowContext context, UserModel user) {
		if (!user.isEnabled()) {
			context.getEvent().user(user);
			context.getEvent().error(Errors.USER_DISABLED);
			//Response challengeResponse = challenge(context, Messages.ACCOUNT_DISABLED);
			Response challengeResponse = context.form()
					.setError(Messages.ACCOUNT_DISABLED)
					.createForm("add-social.ftl");
			// this is not a failure so don't call failureChallenge.
			//context.failureChallenge(AuthenticationFlowError.USER_DISABLED, challengeResponse);
			context.forceChallenge(challengeResponse);
			return false;
		}
		if (isTemporarilyDisabledByBruteForce(context, user)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean requiresUser() {
		return false;
	}

	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		// never called
		return true;
	}

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		// never called
	}

	@Override
	public void close() {

	}
}
