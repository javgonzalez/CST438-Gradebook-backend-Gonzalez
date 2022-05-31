package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

@SpringBootTest
public class EndToEndTestAddAssignment {

   public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/javig/Desktop/chromedriver";

   public static final String URL = "http://localhost:3000";
   public static final String TEST_USER_EMAIL = "test@csumb.edu";
   public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
   public static final Integer TEST_COURSE_ID = 123456;
   public static final int SLEEP_DURATION = 1000; // 1 second.
   public static final String TEST_ASSIGNMENT_NAME = "TEST";
   
   @Autowired
   EnrollmentRepository enrollmentRepository;

   @Autowired
   CourseRepository courseRepository;

   @Autowired
   AssignmentGradeRepository assignnmentGradeRepository;

   @Autowired
   AssignmentRepository assignmentRepository;
   
   @Test
   public void addAssignmentTest() throws Exception {
      
      // test assignment grade
      AssignmentGrade ag = null;
      
      /*
       * initialize the WebDriver and get the home page. 
       */

      System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
      WebDriver driver = new ChromeDriver();
      // Puts an Implicit wait for 10 seconds before throwing exception
      driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

      driver.get(URL);
      Thread.sleep(SLEEP_DURATION);
      
      try {
         // locate add assignment button
         driver.findElement(By.xpath("//button[@id='AddAssignment']")).click();
         Thread.sleep(SLEEP_DURATION);
         
         // locate input for assignment name, due date, and course id 
         driver.findElement(By.xpath("//input[@name='assignmentName']")).sendKeys(TEST_ASSIGNMENT_NAME);
         driver.findElement(By.xpath("//input[@name='dueDate']")).sendKeys("2021-09-05");
         driver.findElement(By.xpath("//input[@name='courseId']")).sendKeys("123456");
         Thread.sleep(SLEEP_DURATION);
         
         // locate submission button to add assignment
         driver.findElement(By.xpath("//button[@id='Add']")).click();
         Thread.sleep(SLEEP_DURATION);
         
         
         // check to see that assignment was added
         List<WebElement> elements  = driver.findElements(By.xpath("//div[@data-field='assignmentName']/div"));
         boolean found = false;
         for (WebElement we : elements) {
            System.out.println(we.getText()); // for debug
            if (we.getText().equals(TEST_ASSIGNMENT_NAME)) {
               found = true;
               we.findElement(By.xpath("descendant::input")).click();
               break;
            }
         }
         
         assertTrue( found, "Unable to locate Test Assignment in list of assignments to be graded.");
         
      } catch (Exception e) {
         throw e;
      } finally {
         /*
          *  clean up database so the test is repeatable.
          */
            List<Assignment> assignments = assignmentRepository.findNeedGradingByEmail(TEST_INSTRUCTOR_EMAIL);
            for (Assignment assignment : assignments) {
               ag = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(assignment.getId(), TEST_USER_EMAIL);
               if (ag!=null) assignnmentGradeRepository.delete(ag);
               assignmentRepository.delete(assignment);
            }
      
            driver.quit();
        }
         
    }
    
}
