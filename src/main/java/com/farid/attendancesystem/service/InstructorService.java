package com.farid.attendancesystem.service;


import com.farid.attendancesystem.dto.CourseDTO;
import com.farid.attendancesystem.dto.InstructorDTO;
import com.farid.attendancesystem.entity.Course;
import com.farid.attendancesystem.entity.Instructor;
import com.farid.attendancesystem.repository.CourseRepository;
import com.farid.attendancesystem.repository.InstructorRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class InstructorService {
    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void removeInstructor(UUID uuid){
        instructorRepository.deleteById(uuid);
    }

    public List<InstructorDTO> getAllInstructors(){
        List<InstructorDTO> instructorDTOS = new ArrayList<>();
        for(Instructor instructor: instructorRepository.findAll()){
           instructorDTOS.add(InstructorDTO.builder().id(instructor.getId()).name(instructor.getName())
                   .email(instructor.getEmail())
                   .password(instructor.getPassword()).build());
        }
        return instructorDTOS;
    }

    public InstructorDTO updateInstructor(UUID uuid, InstructorDTO updatedInstructor){
        if (!instructorRepository.existsById(uuid)) {
            throw new RuntimeException("Instructor not found with ID: " + uuid);
        }

        Instructor existingInstructor = instructorRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Instructor not found with ID: " + uuid));

        existingInstructor.setName(updatedInstructor.getName());
        existingInstructor.setEmail(updatedInstructor.getEmail());
        existingInstructor.setPassword(passwordEncoder.encode(updatedInstructor.getPassword()));

        instructorRepository.save(existingInstructor);
        return InstructorDTO.builder().id(uuid).name(existingInstructor.getName())
                .email(existingInstructor.getEmail())
                .password(existingInstructor.getPassword()).build();
    }

    public InstructorDTO saveInstructor(String name, String email, String password){
        Instructor tempInstructor = Instructor.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();
        Instructor instructor = instructorRepository.save(tempInstructor);
        return InstructorDTO.builder().id(instructor.getId())
                .name(instructor.getName())
                .email(instructor.getEmail())
                .password(instructor.getPassword()).build();
    }

    @Transactional(readOnly = true)
    public InstructorDTO getInstructor(UUID uuid){
        Instructor instructor = instructorRepository.findById(uuid).orElseThrow(() -> new RuntimeException("instructor not found with id: " + uuid));
        return InstructorDTO.builder().id(instructor.getId())
                .name(instructor.getName())
                .email(instructor.getEmail())
                .password(instructor.getPassword()).build();
    }

    public InstructorDTO addCourseToInstructor(UUID uuid, UUID courseId) {
        Instructor instructor = instructorRepository.findById(uuid).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        instructor.getCourses().add(course);
        course.setInstructor(instructor);


        courseRepository.save(course);
        instructor = instructorRepository.save(instructor);
        return InstructorDTO.builder()
                .id(instructor.getId())
                .name(instructor.getName())
                .email(instructor.getEmail())
                .password(instructor.getPassword())
                .courseDTOS(getInstructorCoursesDTOS(instructor)).build();
    }

    private List<CourseDTO> getInstructorCoursesDTOS(Instructor instructor) {
        List<CourseDTO> courseDTOS = new ArrayList<>();
        if(instructor.getCourses() != null)
            for(Course course: instructor.getCourses()){
                courseDTOS.add(
                        CourseDTO.builder()
                                .id(course.getId())
                                .name(course.getName())
                                .description(course.getDescription())
                                .build()
                );
            }
        return courseDTOS;
    }

    public List<CourseDTO> getInstructorCourses(UUID uuid) {
        List<CourseDTO> courseDTOS = new ArrayList<>();
        Instructor instructor = instructorRepository.findById(uuid).orElseThrow();
        if(instructor.getCourses() != null)
            for(Course course: instructor.getCourses()){
                courseDTOS.add(
                        CourseDTO.builder()
                                .id(course.getId())
                                .name(course.getName())
                                .description(course.getDescription())
                                .build()
                );
            }
        return courseDTOS;
    }
}
