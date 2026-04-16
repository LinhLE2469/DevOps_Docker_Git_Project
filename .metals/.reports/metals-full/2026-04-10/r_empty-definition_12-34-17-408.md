error id: file://<WORKSPACE>/backend/src/main/java/fr/takima/training/simpleapi/model/Student.java:com/fasterxml/jackson/annotation/JsonBackReference#
file://<WORKSPACE>/backend/src/main/java/fr/takima/training/simpleapi/model/Student.java
empty definition using pc, found symbol in pc: com/fasterxml/jackson/annotation/JsonBackReference#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 115
uri: file://<WORKSPACE>/backend/src/main/java/fr/takima/training/simpleapi/model/Student.java
text:
```scala
package fr.takima.training.simpleapi.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.@@JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    @JsonBackReference
    private Department department;

    @Column(name = "first_name", nullable = false)
    @JsonProperty("firstname")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @JsonProperty("lastname")
    private String lastName;

    public Student() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: com/fasterxml/jackson/annotation/JsonBackReference#