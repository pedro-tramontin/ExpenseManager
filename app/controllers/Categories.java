package controllers;

import models.Category;
import play.data.Form;
import play.mvc.Call;

import com.google.inject.Inject;

import dao.CategoryDao;
import dao.Dao;

public class Categories extends BaseCRUD<Long, Category> {

	private Form<Category> form = Form.form(Category.class);

	@Inject
	private CategoryDao categoryDao;

	@Override
	protected Dao<Long, Category> getDao() {
		return categoryDao;
	}

	@Override
	protected Form<Category> getForm() {
		return form;
	}

	@Override
	protected Call getListRoute() {
		return routes.Categories.list();
	}

	@Override
	protected Long getId(Category entity) {
		return entity.id;
	}

	@Override
	protected void setAuth(Category entity, String login) {
	}
}
