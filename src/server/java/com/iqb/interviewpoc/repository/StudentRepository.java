package com.iqb.interviewpoc.repository;

import com.iqb.interviewpoc.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "CAST(s.number AS string) LIKE CONCAT('%', :term, '%') OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(s.gsmNumber) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Student> search(@Param("term") String term);
}
