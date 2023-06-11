package ttl.larku.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Address {

    private String street;
    private String city;
    private String state;
    private String zip;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	
	/*
	@JsonBackReference
	@ManyToMany(fetch=FetchType.EAGER)
	private List<Student> students = new ArrayList<Student>();
	*/

    private String startDate;
    private String endDate;


    private Course course;


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

}
