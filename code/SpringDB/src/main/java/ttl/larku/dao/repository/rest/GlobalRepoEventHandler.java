package ttl.larku.dao.repository.rest;

import org.springframework.data.rest.core.annotation.*;
import ttl.larku.domain.Student;

@RepositoryEventHandler
public class GlobalRepoEventHandler {

    /**
     * Hdndle Before POST.
     * The argument type is checked
     * against a repository Entity type to match to a particular
     * method.
     * @param student
     */
    @HandleBeforeCreate
    public void handleStudentCreate(Student student) {
        //Throwing this Exception here would stop the creation
        //attempt.
//        if(student.getName().startsWith("Y")) {
//            throw new RuntimeException("Name can't start with Y: " + student);
//        }
        System.out.println("About to Create a Student with the Student Repo");
    }

    /**
     * Handle Before PUT.
     * The argument type is checked
     * against a repository Entity type to match to a particular
     * method.
     * @param student
     */
    @HandleBeforeSave
    public void handleStudentUpdate(Student student) {
        System.out.println("About to Update a Student with the Student Repo");
    }

    /**
     * Handle Before Delete
     * The argument type is checked
     * against a repository Entity type to match to a particular
     * method.
     * @param student
     */
    @HandleBeforeDelete
    public void handleStudentDelete(Student student) {
        System.out.println("About to Delete a Student with the Student Repo");
    }

    @HandleAfterCreate
    @HandleAfterSave
    @HandleAfterDelete
    public void handleAfterThings(Student student) {
        System.out.println("Finished Doing either a Create, Save or Delete on a Student: " + student);
    }
}
