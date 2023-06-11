package ttl.larku.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NamedQuery(name = "StudentVersioned.smallSelect", query = "Select s from StudentVersioned s")
public class StudentVersioned {

    public enum Status {
        FULL_TIME, PART_TIME, HIBERNATING
    }
    
    
    @Version
    private Long version = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

//    @Column(name = "PHONENUMBER")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Status status = Status.FULL_TIME;

//    @JacksonXmlElementWrapper(localName = "classes")
//    @JacksonXmlProperty(localName = "class")
//    @ManyToMany (fetch = FetchType.LAZY )
//    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinTable(name = "Student_ScheduledClass",
//            joinColumns = @JoinColumn(name = "students_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "classes_id", referencedColumnName = "id"),
//            uniqueConstraints = {
//                    @UniqueConstraint(columnNames = {"students_id", "classes_id"})})
//    private List<ScheduledClass> classes;

    public StudentVersioned() {
        this("Unknown");
    }

    public StudentVersioned(String name) {
        this(name, null, Status.FULL_TIME);
    }


    public StudentVersioned(String name, String phoneNumber, Status status) {
        super();
        this.name = name;
        this.status = status;
        this.phoneNumber = phoneNumber;
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

	@Override
	public String toString() {
		return "StudentVersioned [version=" + version + ", id=" + id + ", name=" + name + ", phoneNumber=" + phoneNumber
				+ ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, phoneNumber, status, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentVersioned other = (StudentVersioned) obj;
		return id == other.id && Objects.equals(name, other.name) && Objects.equals(phoneNumber, other.phoneNumber)
				&& status == other.status && Objects.equals(version, other.version);
	}



}
