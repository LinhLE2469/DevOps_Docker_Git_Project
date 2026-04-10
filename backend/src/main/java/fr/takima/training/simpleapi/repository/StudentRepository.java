package fr.takima.training.simpleapi.repository;

import fr.takima.training.simpleapi.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByDepartment_Name(String name);
}
