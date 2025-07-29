package ttl.larku.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
	private LocalDate dob;
	private List<String> phoneNumbers = new ArrayList<>();
	
	private Status status;
	
	public Student() {
	}
	
	public Student(String name, LocalDate dob, Status status, String... phoneNums) {
		this.name = name;
		this.dob = dob;
		this.status = status;

		Collections.addAll(phoneNumbers, phoneNums);
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
	
	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
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

	@Override
	public String toString() {
		return "Student{" +
				"id=" + id +
				", name='" + name + '\'' +
				", dob=" + dob +
				", phoneNumbers=" + phoneNumbers +
				", status=" + status +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Student student = (Student) o;
		return id == student.id &&
				Objects.equals(name, student.name) &&
				Objects.equals(dob, student.dob) &&
				Objects.equals(phoneNumbers, student.phoneNumbers) &&
				status == student.status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, dob, phoneNumbers, status);
	}
}
