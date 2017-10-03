package controllers;

import models.User;
import play.data.Form;
import play.mvc.Call;

import com.google.inject.Inject;

import dao.Dao;
import dao.UserDao;

public class Users extends BaseCRUD<Long, User> {

	private Form<User> form = Form.form(User.class);

	@Inject
	private UserDao userDao;

	@Override
	protected Dao<Long, User> getDao() {
		return userDao;
	}

	@Override
	protected Form<User> getForm() {
		return form;
	}

	@Override
	protected Call getListRoute() {
		return routes.Users.list();
	}

	@Override
	protected Long getId(User entity) {
		return entity.id;
	}

	@Override
	protected void setAuth(User entity, String login) {
	}
}
