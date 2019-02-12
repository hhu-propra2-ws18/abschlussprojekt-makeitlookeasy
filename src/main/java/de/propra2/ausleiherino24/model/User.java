package de.propra2.ausleiherino24.model;

import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

<<<<<<< HEAD
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
=======
import javax.persistence.*;
>>>>>>> frontDev

@Data
@Entity
@Table(name="userDB")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Column(unique = true)
	private String email;

	private String role;

	public User(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.role = user.getRole();
	}

	public User(){

	}
}
