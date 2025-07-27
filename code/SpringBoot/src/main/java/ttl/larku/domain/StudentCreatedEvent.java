package ttl.larku.domain;

import org.springframework.context.ApplicationEvent;

public class StudentCreatedEvent extends ApplicationEvent {

    private final Student student;

    public StudentCreatedEvent(Object source, Student student) {
        super(source);
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    @Override
    public String toString() {
        return "StudentCreatedEvent{" +
                "student=" + student +
                '}';
    }
}

