package ttl.larku.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Course {

    private int id;
    private String title;
    private String code;
    private float credits = 2.5f;

    @JsonIgnore
    private String name;

    @JsonIgnore
    private float[] creditList = {1, 1.5f, 2, 2.5f, 3, 3.5f, 4};

    public Course() {
    }

    public Course(String code, String title) {
        super();
        this.title = title;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getCredits() {
        return credits;
    }

    public void setCredits(float credits) {
        this.credits = credits;
    }

    public float[] getCreditList() {
        return creditList;
    }

    public void setCreditList(float[] creditList) {
        this.creditList = creditList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Course other = (Course) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Course [id=" + id + ", title=" + title + ", code=" + code + ", credits=" + credits + "]";
    }


}
