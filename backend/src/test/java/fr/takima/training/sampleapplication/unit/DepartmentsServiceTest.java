package fr.takima.training.sampleapplication.unit;

import fr.takima.training.simpleapi.model.Department;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentsServiceTest {

    @Test
    void testDepartmentCreation() {
        Department department = new Department();
        department.setId(1L);
        department.setName("DepartementTest");
        
        assertEquals(1L, department.getId());
        assertEquals("DepartementTest", department.getName());
    }

    @Test
    void testDepartmentWithNullName() {
        Department department = new Department();
        department.setId(1L);
        department.setName(null);
        
        assertNull(department.getName());
    }

    @Test
    void testDepartmentWithEmptyName() {
        Department department = new Department();
        department.setId(1L);
        department.setName("");
        
        assertEquals("", department.getName());
    }
}