package fr.takima.training.simpleapi.controller;

import fr.takima.training.simpleapi.model.Student;
import fr.takima.training.simpleapi.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final StudentRepository studentRepository;

    public DepartmentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping("/{name}/students")
    public List<Student> getStudentsByDepartment(@PathVariable String name) {
        return studentRepository.findByDepartment_Name(name);
    }
}
