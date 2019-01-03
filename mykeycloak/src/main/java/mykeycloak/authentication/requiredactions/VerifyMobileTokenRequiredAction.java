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
package mykeycloak.authentication.requiredactions;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.UserCredentialModel;
import mykeycloak.credential.MobileTokenCredentialProvider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VerifyMobileTokenRequiredAction implements RequiredActionProvider {

	public static final String ID = "Verify Mobile Token";
	private static final Logger logger = Logger.getLogger(VerifyMobileTokenRequiredAction.class);

	@Override
	public void evaluateTriggers(RequiredActionContext context) {

	}

	@Override
	public void requiredActionChallenge(RequiredActionContext context) {
		String theToken = "" + System.currentTimeMillis() % 1000;
		UserCredentialModel input = new UserCredentialModel();
		input.setType(MobileTokenCredentialProvider.MOBILE_TOKEN);
		input.setValue(theToken);
		context.getSession().userCredentialManager().updateCredential(context.getRealm(), context.getUser(), input);
		logger.info("ho generato il token " + theToken);

		Response challenge = context.form()
				.setAttribute("username", context.getAuthenticationSession().getAuthenticatedUser().getUsername())
				.createForm("verify-mobile-token.ftl");
		context.challenge(challenge);
	}

	@Override
	public void processAction(RequiredActionContext context) {
		try {
			String theToken = (context.getHttpRequest().getDecodedFormParameters().getFirst("token"));
			UserCredentialModel input = new UserCredentialModel();
			input.setType(MobileTokenCredentialProvider.MOBILE_TOKEN);
			input.setValue(theToken);
			if (!context.getSession().userCredentialManager().isValid(context.getRealm(), context.getUser(), input)) {				
				throw new Exception("mobile token errato!!!");
			}
			context.success();
		} catch (Exception e) {
			logger.error(e.getMessage());
			Response challenge = context.form()
					.setError(e.getMessage())
					.setAttribute("username", context.getAuthenticationSession().getAuthenticatedUser().getUsername())
					.createForm("verify-mobile-token.ftl");
			context.challenge(challenge);
		}
	}

	protected boolean validateAnswer(AuthenticationFlowContext context) {
		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
		String theToken = formData.getFirst("secret_answer");
		UserCredentialModel input = new UserCredentialModel();
		input.setType(MobileTokenCredentialProvider.MOBILE_TOKEN);
		input.setValue(theToken);
		return context.getSession().userCredentialManager().isValid(context.getRealm(), context.getUser(), input);
	}

	@Override
	public void close() {

	}
}
