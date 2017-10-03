package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import json.serializer.CurrencyDeserializer;
import json.serializer.CurrencySerializer;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class Expense extends Model {

	private static final long serialVersionUID = 1352802383096212540L;

	@Id
	public Long id;

	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public Date datetime;

	@Required
	public String bankDescription;

	@Required
	public String description;

	@Required
	@JsonSerialize(using = CurrencySerializer.class)
	@JsonDeserialize(using = CurrencyDeserializer.class)
	public Double value;

	@Required
	@ManyToOne
	public Category category;

	@Required
	@ManyToOne
	public Period period;

	public static Finder<Long, Expense> find = new Finder<Long, Expense>(
			Long.class, Expense.class);

	public static List<Expense> filterPeriod(Period period) {
		return (find.where("period.id = " + period.id).findList());
	}

	@ManyToOne
	public User user;
	
	@Override
	public String toString() {
		return String
				.format("id: %d; datetime: %tF; bankDescription: %s; description: %s, value: %.2f; category.id: %d; period.id: %d",
						id, datetime, bankDescription, description, value,
						category.id, period.id);
	}
}
