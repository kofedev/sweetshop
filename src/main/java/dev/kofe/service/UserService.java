package dev.kofe.service;

import dev.kofe.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {

    User loadUserByUsername(String username);

    User findUserById(Long userId);

    User findUserByUsernameIgnoreCase(String username);

    List<User> allUsers();

    void saveUserForUpdate(User user);

    boolean saveAdminUser(User user);

    void reWriteUser (User user);

    boolean reWriteUserWithNewPassword(User user);

    boolean saveNewUser(User user);

    User getUserByUsername(String username);

    boolean deleteUserById(Long userId);

    User getCurrentUser();

}
