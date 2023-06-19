package es.dws.escuela.repositories;

import es.dws.escuela.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Teacher, String> {
    Optional<Teacher> findByNameAndSurname(String name, String surname);
}
