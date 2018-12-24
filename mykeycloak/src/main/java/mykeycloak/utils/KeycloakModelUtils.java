package mykeycloak.utils;

import java.util.List;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public final class KeycloakModelUtils {

    private KeycloakModelUtils() {
    }

    /**
     * Try to find user by username or email for authentication
     *
     * @param realm    realm
     * @param username username or email of user
     * @return found user
     */
    public static UserModel findUserByUsernameEmailOrMobile(KeycloakSession session, RealmModel realm, String username) {
		UserModel user = session.users().getUserByUsername(username, realm);
		if (user != null) return user;
		
		user = session.users().getUserByEmail(username, realm);
		if (user != null) return user;
		
		try {
			String attributeName = "mobile";
			List<UserModel> userModels = session.users().searchForUserByUserAttribute(attributeName, username, realm);
			return userModels.get(0);			
		} catch(Exception e) {			
	        return null;
		}
    }	
}
