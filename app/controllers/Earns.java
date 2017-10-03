package controllers;

import models.Earn;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Result;

import com.google.inject.Inject;

import dao.CategoryDao;
import dao.Dao;
import dao.EarnDao;
import dao.PeriodDao;
import dao.UserDao;

public class Earns extends BaseCRUD<Long, Earn> {

	private Form<Earn> form = Form.form(Earn.class);

	@Inject
	private CategoryDao categoryDao;

	@Inject
	private PeriodDao periodDao;

	@Inject
	private EarnDao earnDao;

	@Inject
	private UserDao userDao;

	@Override
	protected Form<Earn> getForm() {
		return form;
	}

	@Override
	protected Dao<Long, Earn> getDao() {
		return earnDao;
	}

	@Override
	protected Call getListRoute() {
		return routes.Earns.list();
	}

	@Override
	protected Long getId(Earn entity) {
		return entity.id;
	}

	public Result byCategory() {
		return (ok(views.html.earn.byCategory.render(categoryDao.all())));
	}

	public Result byPeriod() {
		return (ok(views.html.earn.byPeriod.render(periodDao.all())));
	}

	@Override
	protected void setAuth(Earn entity, String login) {
		entity.user = userDao.findUser(login);
	}
}
