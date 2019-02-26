package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface UserRepository extends CrudRepository<User, Long> {

    @NonNull
    List<User> findAll();

    Optional<User> getById(Long userId);

    Optional<User> findByUsername(String username);
}
