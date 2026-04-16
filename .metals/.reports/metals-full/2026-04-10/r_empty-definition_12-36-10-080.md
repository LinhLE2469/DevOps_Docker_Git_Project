error id: file://<WORKSPACE>/backend/src/main/java/fr/takima/training/simpleapi/model/Department.java:com/fasterxml/jackson/annotation/JsonIgnore#
file://<WORKSPACE>/backend/src/main/java/fr/takima/training/simpleapi/model/Department.java
empty definition using pc, found symbol in pc: com/fasterxml/jackson/annotation/JsonIgnore#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 115
uri: file://<WORKSPACE>/backend/src/main/java/fr/takima/training/simpleapi/model/Department.java
text:
```scala
package fr.takima.training.simpleapi.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.@@JsonIgnore;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Student> students;

    public Department() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: com/fasterxml/jackson/annotation/JsonIgnore#