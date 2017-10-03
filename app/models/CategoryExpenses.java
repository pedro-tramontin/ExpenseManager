package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.ebean.Model;
import auth.AuthUtil;

import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.Sql;

@Entity
@Sql
public class CategoryExpenses extends Model {

	private static final long serialVersionUID = -3304084800566376857L;

	@OneToOne
	public Category category;

	public Double totalExpense;

	public static Finder<Category, CategoryExpenses> find = new Finder<Category, CategoryExpenses>(
			Category.class, CategoryExpenses.class);

	public static List<CategoryExpenses> all() {
		String sql = "select c.id, sum(e.value) as totalExpense from category c inner join expense e on c.id = e.category_id inner join user u on e.user_id = u.id where u.login = :userLogin group by c.id order by c.name";
		RawSql rawSql = RawSqlBuilder.parse(sql)
				.columnMapping("c.id", "category.id").create();

		Query<CategoryExpenses> query = find.query();
		query.setRawSql(rawSql);
		query.setParameter("userLogin", AuthUtil.getSessionLogin());

		return (query.findList());
	}
}
