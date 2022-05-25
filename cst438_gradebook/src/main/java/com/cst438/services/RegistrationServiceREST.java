package com.cst438.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;

public class RegistrationServiceREST extends RegistrationService {
   
   @Autowired
   CourseRepository courseRepository;
	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	@Override
	public void sendFinalGrades(int course_id , CourseDTOG courseDTO) { 
		
	   // grab course from database to get course name
      Course c = courseRepository.findById(course_id).orElse(null);
      // if not found, throw exception
      if (c == null) {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not find course id " + course_id);
      }
      
	   System.out.printf("Sending final grades for %d: %s%n", course_id, c.getTitle());
	   
	   // update final grades by passing courseDTO to Course Controller in register backend 
	   restTemplate.put(registration_url + "/course/" + course_id, courseDTO);
      
	}
}
