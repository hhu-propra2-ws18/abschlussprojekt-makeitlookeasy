package de.propra2.ausleiherino24.Respositories;

import de.propra2.ausleiherino24.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface UserRespository extends CrudRepository<User, Long> {
	ArrayList<User> findAll();
}
