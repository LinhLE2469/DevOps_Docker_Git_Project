error id: file://<WORKSPACE>/backend/src/test/java/fr/takima/training/sampleapplication/unit/DepartmentsServiceTest.java:_empty_/Department#builder#
file://<WORKSPACE>/backend/src/test/java/fr/takima/training/sampleapplication/unit/DepartmentsServiceTest.java
empty definition using pc, found symbol in pc: _empty_/Department#builder#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 823
uri: file://<WORKSPACE>/backend/src/test/java/fr/takima/training/sampleapplication/unit/DepartmentsServiceTest.java
text:
```scala
package fr.takima.training.sampleapplication.unit;

import fr.takima.training.simpleapi.dao.DepartmentDAO;
import fr.takima.training.simpleapi.entity.Department;
import fr.takima.training.simpleapi.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentsServiceTest {

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
    private DepartmentDAO departmentDAO;

    private final Department DEPARTMENT = Department.@@builder()
            .id(1L)
            .name("DepartementTest")
            .build();

    @Test
    void testGetDepartmentByName() {
        when(departmentDAO.findDepartmentByName("DepartmentTest")).thenReturn(DEPARTMENT);
        assertEquals(DEPARTMENT, departmentDAO.findDepartmentByName("DepartmentTest"));
    }

    @Test
    void testGetDepartmentByNameWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> departmentService.getDepartmentByName(null));
    }

    @Test
    void testGetDepartmentByNameWithEmptyValue() {
        assertThrows(IllegalArgumentException.class, () -> departmentService.getDepartmentByName(""));
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Department#builder#