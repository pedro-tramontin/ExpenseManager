package auth;

import play.mvc.Http;

public class AuthUtil {
	public static final String LOGGED_USER_ATTR = "loggedUser";

	public static String getSessionLogin() {
		return Http.Context.current().session().get(LOGGED_USER_ATTR);
	}
}
