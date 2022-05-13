package dev.kofe.repo;

import dev.kofe.model.ConfirmationToken;
import dev.kofe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, String> {

    ConfirmationToken findByConfirmationToken(String confirmationToken);
    void deleteAllByUser(User user);
    List<ConfirmationToken> findAllByUser(User user);

}
