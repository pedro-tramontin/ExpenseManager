package models;

import java.util.Date;

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
public class Earn extends Model {

	private static final long serialVersionUID = -3276843523800513528L;

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
