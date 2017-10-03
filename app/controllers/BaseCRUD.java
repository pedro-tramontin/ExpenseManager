package controllers;

import java.io.IOException;
import java.util.List;

import play.data.Form;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dao.Dao;

public abstract class BaseCRUD<ID, T extends Model> extends Controller {

	private ObjectMapper mapper = new ObjectMapper();

	public Result show(ID id) {
		T entity = getDao().byId(id);
		if (entity != null) {
			return ok(Json.toJson(entity));
		} else {
			return notFound();
		}
	}

	public Result list() throws JsonGenerationException, JsonMappingException,
			IOException {
		List<T> entityList = getDao().all();

		return ok(mapper.writeValueAsString(entityList));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Result add() {
		Form<T> filledForm = getForm().bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(filledForm.errorsAsJson());
		} else {
			T postedEntity = filledForm.get();
			setAuth(postedEntity, session("loggedUser"));

			getDao().create(postedEntity);

			return ok(Json.toJson(getDao().byId(getId(postedEntity))));
		}
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Result update(ID id) {
		Form<T> filledForm = getForm().fill(getDao().byId(id))
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(filledForm.errorsAsJson());
		} else {
			T postedEntity = filledForm.get();

			postedEntity.update(id);

			return ok(Json.toJson(getDao().byId(getId(postedEntity))));
		}
	}

	public Result delete(ID id) {
		getDao().delete(id);
		return redirect(getListRoute());
	}

	protected abstract Form<T> getForm();

	protected abstract Dao<ID, T> getDao();

	protected abstract Call getListRoute();

	protected abstract ID getId(T entity);

	protected abstract void setAuth(T entity, String login);
}
