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
public class PeriodExpenses extends Model {

	private static final long serialVersionUID = -3961917076917581707L;

	@OneToOne
	public Period period;

	public Double totalExpense;

	public static Finder<Period, PeriodExpenses> find = new Finder<Period, PeriodExpenses>(
			Period.class, PeriodExpenses.class);

	public static List<PeriodExpenses> all() {
		String sql = "select p.id, sum(e.value) as totalExpense from period p inner join expense e on p.id = e.period_id inner join user u on e.user_id = u.id where u.login = :userLogin group by p.id order by p.value";
		RawSql rawSql = RawSqlBuilder.parse(sql)
				.columnMapping("p.id", "period.id").create();

		Query<PeriodExpenses> query = find.query();
		query.setRawSql(rawSql);
		query.setParameter("userLogin", AuthUtil.getSessionLogin());

		return (query.findList());
	}
}
