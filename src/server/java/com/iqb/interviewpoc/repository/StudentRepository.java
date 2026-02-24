package com.iqb.interviewpoc.repository;

import com.iqb.interviewpoc.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "CAST(s.number AS string) LIKE CONCAT('%', :term, '%') OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(s.gsmNumber) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<Student> search(@Param("term") String term, Pageable pageable);
}
