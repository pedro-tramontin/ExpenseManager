package controllers;

import models.Period;
import play.data.Form;
import play.mvc.Call;

import com.google.inject.Inject;

import dao.Dao;
import dao.PeriodDao;

public class Periods extends BaseCRUD<Long, Period> {

	private Form<Period> form = Form.form(Period.class);

	@Inject
	private PeriodDao periodDao;

	@Override
	protected Dao<Long, Period> getDao() {
		return periodDao;
	}

	@Override
	protected Form<Period> getForm() {
		return form;
	}

	@Override
	protected Call getListRoute() {
		return routes.Periods.list();
	}

	@Override
	protected Long getId(Period entity) {
		return entity.id;
	}

	@Override
	protected void setAuth(Period entity, String login) {
	}
}
