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
package mykeycloak.authentication.forms;

import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventType;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.resources.AttributeFormDataProcessor;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;
import mykeycloak.authentication.requiredactions.VerifyEmailRequiredAction;
import mykeycloak.authentication.requiredactions.VerifyMobileTokenRequiredAction;
import org.jboss.logging.Logger;
import org.keycloak.authentication.authenticators.broker.AbstractIdpAuthenticator;
import org.keycloak.authentication.authenticators.broker.util.SerializedBrokeredIdentityContext;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.models.FederatedIdentityModel;
import mykeycloak.utils.KeycloakModelUtils;
import mykeycloak.utils.ValidatorUtil;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegistrationUserCreation implements FormAction, FormActionFactory {

	public static final String PROVIDER_ID = "my-registration-user-creation";
	private static final Logger logger = Logger.getLogger(RegistrationUserCreation.class);

	@Override
	public String getHelpText() {
		return "This action must always be first! Validates the username of the user in validation phase.  In success phase, this will create the user in the database.";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return null;
	}

	@Override
	public void validate(ValidationContext context) {
		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
		List<FormMessage> errors = new ArrayList<>();
		context.getEvent().detail(Details.REGISTER_METHOD, "form");

		String username = formData.getFirst(RegistrationPage.FIELD_USERNAME);
		String email = null; //formData.getFirst(Validation.FIELD_EMAIL);
		String mobile = null;
		if (ValidatorUtil.validateMobile(username)) {
			username = ValidatorUtil.normalizeMobile(username);
			mobile = username;
		} else if (ValidatorUtil.validateEmail(username)) {
			email = username;
		} else {
			context.error(Errors.INVALID_REGISTRATION);
			errors.add(new FormMessage(RegistrationPage.FIELD_USERNAME, "invalidUserMessage"));
			context.validationError(formData, errors);
			return;
		}
		if (KeycloakModelUtils.findUserByUsernameEmailOrMobile(context.getSession(), context.getRealm(), username) != null) {
			errors.add(new FormMessage(RegistrationPage.FIELD_USERNAME, Messages.USERNAME_EXISTS));
			context.error(Errors.INVALID_REGISTRATION);
			context.validationError(formData, errors);
			return;
		}

		context.getEvent().detail(Details.USERNAME, username);
		context.getEvent().detail(Details.EMAIL, email);
		context.success();
	}

	@Override
	public void buildPage(FormContext context, LoginFormsProvider form) {

	}

	@Override
	public void success(FormContext context) {
		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
		String username = formData.getFirst(RegistrationPage.FIELD_USERNAME);
		String email = null;
		String mobile = null;
		if (ValidatorUtil.validateMobile(username)) {
			username = ValidatorUtil.normalizeMobile(username);
			mobile = username;
		} else if (ValidatorUtil.validateEmail(username)) {
			email = username;
		}
		context.getEvent().detail(Details.USERNAME, username)
				.detail(Details.REGISTER_METHOD, "form")
				.detail(Details.EMAIL, email);
		UserModel user = context.getSession().users().addUser(context.getRealm(), username);
		logger.info("creo l'utente " + user.getUsername());
		user.setEnabled(true);
		user.setEmail(email);
		List<String> attributes = new ArrayList();
		attributes.add(mobile);
		user.setAttribute("mobile", attributes);
		user.addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
		if (email != null) {
			user.addRequiredAction(VerifyEmailRequiredAction.ID);
		} else if (mobile != null) {
			user.addRequiredAction(VerifyMobileTokenRequiredAction.ID);
		}

		context.getAuthenticationSession().setClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM, username);
		AttributeFormDataProcessor.process(formData, context.getRealm(), user);
		context.setUser(user);
		context.getEvent().user(user);
		context.getEvent().success();
		context.newEvent().event(EventType.LOGIN);
		context.getEvent().client(context.getAuthenticationSession().getClient().getClientId())
				.detail(Details.REDIRECT_URI, context.getAuthenticationSession().getRedirectUri())
				.detail(Details.AUTH_METHOD, context.getAuthenticationSession().getProtocol());
		String authType = context.getAuthenticationSession().getAuthNote(Details.AUTH_TYPE);
		if (authType != null) {
			context.getEvent().detail(Details.AUTH_TYPE, authType);
		}

		try {
			SerializedBrokeredIdentityContext serializedCtx = SerializedBrokeredIdentityContext.readFromAuthenticationSession(context.getAuthenticationSession(), AbstractIdpAuthenticator.BROKERED_CONTEXT_NOTE);
			BrokeredIdentityContext brokeredIdentityContext = serializedCtx.deserialize(context.getSession(), context.getAuthenticationSession());
			FederatedIdentityModel federatedIdentityModel = new FederatedIdentityModel(brokeredIdentityContext.getIdpConfig().getAlias(), brokeredIdentityContext.getId(),
					brokeredIdentityContext.getUsername(), brokeredIdentityContext.getToken());
			context.getSession().users().addFederatedIdentity(context.getRealm(), user, federatedIdentityModel);
			logger.info("all'utente " + user.getUsername() + " ho aggiunto l'identità federata -> " + federatedIdentityModel.getUserName());
		} catch (Exception e) {
		}
	}

	@Override
	public boolean requiresUser() {
		return false;
	}

	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		return true;
	}

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

	}

	@Override
	public boolean isUserSetupAllowed() {
		return false;
	}

	@Override
	public void close() {

	}

	@Override
	public String getDisplayType() {
		return "My Registration User Creation";
	}

	@Override
	public String getReferenceCategory() {
		return null;
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}

	private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
		AuthenticationExecutionModel.Requirement.REQUIRED,
		AuthenticationExecutionModel.Requirement.DISABLED
	};

	@Override
	public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
		return REQUIREMENT_CHOICES;
	}

	@Override
	public FormAction create(KeycloakSession session) {
		return this;
	}

	@Override
	public void init(Config.Scope config) {

	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {

	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}
}
