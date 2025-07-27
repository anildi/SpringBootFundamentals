package ttl.larku.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.Version;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Entity
//@NamedQuery(name = "Student.smallSelect", query = "Select s from Student s")
//@NamedQuery(name = "Student.bigSelect", query = "select distinct s from Student s left join fetch s.classes sc left join fetch sc.course")
//@NamedQuery(name = "Student.bigSelectOne", query = "select distinct s from Student s left join fetch s.classes "
//      + "sc left join fetch sc.course where s.id = :id")
//@NamedQuery(name = "Student.getByName", query = "select distinct s from Student s left join fetch s.classes "
//      + "sc left join fetch sc.course where s.name like CONCAT('%',:name,'%')")
//@NamedQuery(name = "Student.exists", query = "select case when count(s) > 0 then true else false end from Student s where s.id = :id")
public class StudentVersioned {

   public enum Status {
      FULL_TIME, PART_TIME, HIBERNATING
   }

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   private String name;

   private String phoneNumber;

//   @Version
   private long version;

   @JsonDeserialize(using = LocalDateDeserializer.class)
   @JsonSerialize(using = LocalDateSerializer.class)
   private LocalDate dob;

   @Enumerated(EnumType.STRING)
   private Status status = Status.FULL_TIME;

   /*
    * Important INFO.  If you use a List in a many-to-many relationship,
    * then Hibernate does awful things when you remove an element from
    * one side - it deletes all the corresponding entries in the link table,
    * and then re-inserts the ones which are still valid.  In our case,
    * if we remove a ScheduledClass, it would remove *all* the records
    * from the link table for all students who had that class.  It would then
    * reinsert records for all those students with their other classes.
    *
    * Awful.  Using a Set makes it behave better.  Now only the link table
    * entries for students for this particular class are removed.
    */
   @JacksonXmlElementWrapper(localName = "classes")
   @JacksonXmlProperty(localName = "class")
   @ManyToMany(fetch = FetchType.LAZY)
   @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
   @JoinTable(name = "Student_ScheduledClass",
         joinColumns = @JoinColumn(name = "students_id", referencedColumnName = "id"),
         inverseJoinColumns = @JoinColumn(name = "classes_id", referencedColumnName = "id"),
         uniqueConstraints = {
               @UniqueConstraint(columnNames = {"students_id", "classes_id"})})
   private Set<ScheduledClass> classes = new HashSet<>();


   public StudentVersioned() {
      this("Unknown");
   }

   public StudentVersioned(String name) {
      this(name, null, null, Status.FULL_TIME, new ArrayList<ScheduledClass>());
   }

//    public Student(String name, String phoneNumber, Status status) {
//        this(name, phoneNumber, null, status, new ArrayList<ScheduledClass>());
//    }

   public StudentVersioned(String name, String phoneNumber, LocalDate dob, Status status) {
      this(name, phoneNumber, dob, status, new ArrayList<ScheduledClass>());
   }

   public StudentVersioned(String name, String phoneNumber, LocalDate dob, Status status, List<ScheduledClass> classes) {
      super();
      this.name = name;
      this.status = status;
      this.dob = dob;
      this.phoneNumber = phoneNumber;
      setClassesCommon(classes);
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

   public Status getStatus() {
      return status;
   }

   public void setStatus(Status status) {
      this.status = status;
   }

   public LocalDate getDob() {
      return dob;
   }

   public void setDob(LocalDate dob) {
      this.dob = dob;
   }

   //    public List<ScheduledClass> getClasses() {
   public Set<ScheduledClass> getClasses() {
      return Set.copyOf(classes);
   }

   @JsonIgnore
   public void setClasses(List<ScheduledClass> classes) {
      setClassesCommon(classes);
   }

   public void setClasses(Set<ScheduledClass> classes) {
      setClassesCommon(classes);
   }

   private void setClassesCommon(Collection<ScheduledClass> classes) {
      if(classes != null) {
         this.classes.clear();
         this.classes.addAll(classes);
      }
   }

   public long getVersion() {
      return version;
   }

   public void setVersion(long version) {
      this.version = version;
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
      StudentVersioned student = (StudentVersioned) o;
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
            ", version=" + version +
//                ", classes=" + classes +
            '}';
   }
}
