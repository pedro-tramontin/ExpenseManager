package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class User extends Model {

	private static final long serialVersionUID = 2182173519347294709L;

	@Id
	public Long id;

	@Required
	public String login;

	@Required
	public String pass;
}
