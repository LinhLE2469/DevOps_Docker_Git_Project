package fr.takima.training.sampleapplication.unit;

import fr.takima.training.simpleapi.model.Department;
import fr.takima.training.simpleapi.model.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentsServiceTest {

    @Test
    void testStudentCreation() {
        Department department = new Department();
        department.setId(1L);
        department.setName("DepartementTest");
        
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Firstname");
        student.setLastName("lastname");
        student.setDepartment(department);
        
        assertEquals(1L, student.getId());
        assertEquals("Firstname", student.getFirstName());
        assertEquals("lastname", student.getLastName());
        assertEquals(department, student.getDepartment());
    }

    @Test
    void testStudentWithEmptyLastname() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("abc");
        student.setLastName("");
        
        assertEquals("", student.getLastName());
    }

    @Test
    void testStudentWithoutDepartment() {
        Student student = new Student();
        student.setId(1L);
        student.setLastName("abc");
        
        assertNull(student.getDepartment());
    }
}