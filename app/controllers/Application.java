package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import auth.Secured;

import com.google.inject.Inject;

import dao.PeriodDao;

public class Application extends Controller {

	@Inject
	private PeriodDao periodDao;

	@Security.Authenticated(Secured.class)
	public static Result index() {
		return ok(views.html.index.render());
	}

	public Result balance() {
		return (ok(views.html.balance.render(periodDao.all())));
	}

	public static Result login() {
		return ok(views.html.login.render());
	}

	public static Result authenticate() {
		Map<String, String[]> postedData = request().body().asFormUrlEncoded();

		session().clear();
		session("login", postedData.get("login")[0]);

		return redirect(routes.Application.index());
	}

	@Security.Authenticated(Secured.class)
	public static Result executeQuery() {
		DataSource ds = DB.getDataSource();
		StringBuilder sBuilder = new StringBuilder();

		try {
			Connection connection = ds.getConnection();
			Statement statement = connection.createStatement();
			statement
					.execute(request().body().asFormUrlEncoded().get("sql")[0]);

			ResultSet resultSet = statement.getResultSet();
			if (resultSet.isBeforeFirst()) {
				sBuilder.append("<table border='1'><tr>");

				for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
					sBuilder.append("<th>");
					sBuilder.append(resultSet.getMetaData().getColumnName(i));
					sBuilder.append("</th>");
				}

				sBuilder.append("</tr>");

				while (resultSet.next()) {
					sBuilder.append("<tr>");

					for (int i = 1; i <= resultSet.getMetaData()
							.getColumnCount(); i++) {
						sBuilder.append("<td>");
						sBuilder.append(resultSet.getString(i));
						sBuilder.append("</td>");
					}

					sBuilder.append("</tr>");
				}
				sBuilder.append("</table>");
			}

			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ok(sBuilder.toString()).as("text/html");
	}

	public static Result executeUpdate() {
		DataSource ds = DB.getDataSource();
		StringBuilder sBuilder = new StringBuilder();

		try {
			Connection connection = ds.getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate(request().body().asFormUrlEncoded()
					.get("sql")[0]);

			sBuilder.append("SQL executado");

			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ok(sBuilder.toString()).as("text/html");
	}
}
