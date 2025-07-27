package ttl.larku.dependendobjects;

import java.time.LocalDate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ttl.larku.dao.repository.ClassRepo;
import ttl.larku.dao.repository.CourseRepo;
import ttl.larku.domain.Course;
import ttl.larku.domain.ScheduledClass;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Disabled
public class TestDependantObjects {

   @Autowired
   private CourseRepo courseRepo;

   @Autowired
   private ClassRepo classRepo;

   /**
    * Test to show Transient/Dependant Object issues.
    * Case 1:  No cascade value set for the Course relationship in
    * the Scheduled class:
    *      Scenario 1: You do not Persist the Course Object before
    *      persisting the ScheduledClass. In this case, it will fail with:
    *
    *        "org.hibernate.TransientPropertyValueException: object references an unsaved transient instance"
    *
    *      because you are trying to save a class which has a reference to a Course object that does not
    *      exist in the database (aka the Transient Object).
    *      Scenario 2: If you save the Course, then both saves for the
    *      Scheduled classes will work fine.
    * Case 2:  You have a cascade persist on the Course relationship in the
    * ScheduledClass.  In this case, the first save for the scheduled class
    * will succeed, but the second one will fail with:
    *
    * "...org.hibernate.PersistentObjectException: detached entity passed to persist"
    *
    * because the course will have become a detached object after the first save.
    * To prevent that from happening, make the test @Transactional.
    *
    */

   @Test
   public void testInsertWithPositiveCourseId_GivesDependentException() {
      Course course = new Course("KNIT-101", "Intro to Knitting");
//      courseRepo.save(course);

      ScheduledClass sc = new ScheduledClass(course, LocalDate.of(2025, 10, 10), LocalDate.of(2026, 10, 10));

      //This will blow up if the course is not already managed, and you don't
      //have @Cascade set for the course relationship in the Scheduled Class
      sc = classRepo.save(sc);

      assertTrue(sc.getId() > 0);
      assertTrue(course.getId() > 0);

      ScheduledClass sc2 = new ScheduledClass(course, LocalDate.of(2025, 10, 10), LocalDate.of(2026, 10, 10));

      classRepo.save(sc2);
   }
}
