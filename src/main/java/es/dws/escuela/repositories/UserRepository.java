package es.dws.escuela.repositories;

import es.dws.escuela.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByNameAndSurname(String name, String surname);
}
