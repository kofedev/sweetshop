package dev.kofe.repo;

import dev.kofe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByUsernameIgnoreCase(String username);

}
