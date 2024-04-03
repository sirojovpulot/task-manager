package uz.task.manager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import uz.task.manager.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void itShouldThrowWhenTwoSameUsernameInserted() {
        User user = User.builder()
                .fullName("fullName")
                .username("username")
                .password("password")
                .build();
        userRepository.save(user);
        User user2 = User.builder()
                .fullName("fullName")
                .username("username")
                .password("password")
                .build();

        assertThatThrownBy(() -> userRepository.save(user2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Unique index or primary key violation");
    }

    @Test
    void findByUsernameIgnoreCase() {
        User user = User.builder()
                .fullName("fullName")
                .username("username")
                .password("password")
                .build();
        userRepository.save(user);
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase("username");
        assertThat(userOptional).isPresent()
                .hasValueSatisfying(u -> assertThat(u).isEqualTo(user));
    }

    @Test
    void existsByUsernameIgnoreCase() {
        User user = User.builder()
                .fullName("fullName")
                .username("username")
                .password("password")
                .build();
        userRepository.save(user);
        Boolean isUserExists = userRepository.existsByUsernameIgnoreCase("username");
        assertThat(isUserExists).isTrue();
    }
}