package ttl.larku.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Student {

    public enum Status {
        FULL_TIME,
        PART_TIME,
        HIBERNATING
    };

    private int id;

    private String name;

    private String phoneNumber;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dob;

    private Status status = Status.FULL_TIME;

    private List<ScheduledClass> classes;

    private static int nextId = 0;

    public Student() {
//        this("Unknown");
        this(null);
    }

    public Student(String name) {
        this(name, null, null, Status.FULL_TIME, new ArrayList<ScheduledClass>());
    }

    public Student(String name, String phoneNumber, Status status) {
        this(name, phoneNumber, null, status, new ArrayList<ScheduledClass>());
    }

    public Student(String name, String phoneNumber, LocalDate dob, Status status) {
        this(name, phoneNumber, dob, status, new ArrayList<ScheduledClass>());
    }

    public Student(String name, String phoneNumber, LocalDate dob, Status status, List<ScheduledClass> classes) {
        super();
        this.name = name;
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.dob = dob;
        this.classes = classes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public Status[] getStatusList() {
        return Status.values();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDob() {
        return dob;
    }


    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public List<ScheduledClass> getClasses() {
        return classes;
    }

    public void setClasses(List<ScheduledClass> classes) {
        this.classes = classes;
    }


    public void addClass(ScheduledClass sClass) {
        classes.add(sClass);
    }

    public void dropClass(ScheduledClass sClass) {
        classes.remove(sClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id && name.equals(student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dob=" + dob +
                ", status=" + status +
                ", classes=" + classes +
                '}';
    }
}
