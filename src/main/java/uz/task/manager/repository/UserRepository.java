package uz.task.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.task.manager.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    Boolean existsByUsernameIgnoreCase(String username);
}
