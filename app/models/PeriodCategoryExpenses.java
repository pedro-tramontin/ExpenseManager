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
public class PeriodCategoryExpenses extends Model {

	private static final long serialVersionUID = -3213696932405725107L;

	@OneToOne
	public Period period;

	@OneToOne
	public Category category;

	public Double totalExpense;

	public static Finder<Period, PeriodCategoryExpenses> find = new Finder<Period, PeriodCategoryExpenses>(
			Period.class, PeriodCategoryExpenses.class);

	public static List<PeriodCategoryExpenses> all() {
		String sql = "select p.id, c.id, sum(e.value) as totalExpense from period p inner join expense e on p.id = e.period_id inner join category c on c.id = e.category_id inner join user u on e.user_id = u.id where u.login = :userLogin group by p.id, c.id order by p.value, c.name";

		RawSql rawSql = RawSqlBuilder.parse(sql)
				.columnMapping("p.id", "period.id")
				.columnMapping("c.id", "category.id").create();

		Query<PeriodCategoryExpenses> query = find.query();
		query.setRawSql(rawSql);
		query.setParameter("userLogin", AuthUtil.getSessionLogin());

		return (query.findList());
	}
}
