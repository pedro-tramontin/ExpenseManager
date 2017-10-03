package controllers;

import java.util.Map;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import auth.AuthUtil;

import com.google.inject.Inject;

import dao.UserDao;

public class Login extends Controller {

	@Inject
	private UserDao userDao;

	public static Result form() {
		return ok(views.html.login.render());
	}

	public Result authenticate() {
		Map<String, String[]> postedData = request().body().asFormUrlEncoded();

		session().clear();
		String[] postedLoginList = postedData.get("login");
		if (postedLoginList != null) {
			String postedLogin = postedLoginList[0];

			User user = userDao.findUser(postedLogin);
			if (user != null) {
				session(AuthUtil.LOGGED_USER_ATTR, user.login);
			}
		}

		return redirect(routes.Application.index());
	}

	public static Result logout() {
		session().clear();

		return redirect(routes.Application.index());
	}
}
