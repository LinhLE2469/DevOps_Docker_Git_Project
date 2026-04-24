package fr.takima.training.simpleapi.controller;

import fr.takima.training.simpleapi.model.Department;
import fr.takima.training.simpleapi.model.Student;
import fr.takima.training.simpleapi.repository.DepartmentRepository;
import fr.takima.training.simpleapi.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;

    public DepartmentController(StudentRepository studentRepository, DepartmentRepository departmentRepository) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<Department>> getDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Department> getDepartmentByName(@PathVariable String name) {
        Optional<Department> department = departmentRepository.findByName(name);
        return department.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{name}/students")
    public ResponseEntity<List<Student>> getStudentsByDepartment(@PathVariable String name) {
        Optional<Department> department = departmentRepository.findByName(name);
        if (department.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Student> students = studentRepository.findByDepartment_Name(name);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{name}/count")
    public ResponseEntity<Long> getStudentCountByDepartment(@PathVariable String name) {
        Optional<Department> department = departmentRepository.findByName(name);
        if (department.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        long count = studentRepository.findByDepartment_Name(name).size();
        return ResponseEntity.ok(count);
    }
}
