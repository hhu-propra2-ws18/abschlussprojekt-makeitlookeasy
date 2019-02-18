package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	@Override
	List<User> findAll();

	Optional<User> getById(Long id);

	Optional<User> findByUsername(String username);
}
