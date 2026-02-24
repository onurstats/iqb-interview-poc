package com.iqb.interviewpoc.repository;

import com.iqb.interviewpoc.entity.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    @Query("SELECT AVG(er.score) FROM ExamResult er")
    Double averageScore();

    @Query(value = "SELECT COUNT(*) FROM (SELECT student_id, course_id FROM exam_result GROUP BY student_id, course_id HAVING COUNT(*) = 3)", nativeQuery = true)
    long countCompletedPairs();

    @Query(value = "SELECT COUNT(*) FROM (SELECT student_id, course_id FROM exam_result GROUP BY student_id, course_id HAVING COUNT(*) < 3)", nativeQuery = true)
    long countInProgressPairs();

    @Query(value = "SELECT s.id, s.full_name, AVG(er.score) as avg_score " +
           "FROM exam_result er JOIN student s ON er.student_id = s.id " +
           "GROUP BY s.id, s.full_name ORDER BY avg_score DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopStudentsByAvgScore();

    @Query(value = "SELECT er.id, s.full_name, c.name, er.score, er.created_at " +
           "FROM exam_result er JOIN student s ON er.student_id = s.id JOIN course c ON er.course_id = c.id " +
           "ORDER BY er.created_at DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findRecentResults();

    @Query("SELECT er.score FROM ExamResult er")
    List<Integer> findAllScores();

    List<ExamResult> findByStudentIdOrderByCourseIdAscIdAsc(Long studentId);

    @Query(value = "SELECT er FROM ExamResult er JOIN FETCH er.student JOIN FETCH er.course ORDER BY er.id DESC",
           countQuery = "SELECT COUNT(er) FROM ExamResult er")
    Page<ExamResult> findAllWithDetails(Pageable pageable);

    @Query(value = "SELECT er FROM ExamResult er JOIN FETCH er.student JOIN FETCH er.course " +
           "WHERE LOWER(er.student.fullName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(er.course.name) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "CAST(er.score AS string) LIKE CONCAT('%', :term, '%') " +
           "ORDER BY er.id DESC",
           countQuery = "SELECT COUNT(er) FROM ExamResult er " +
           "WHERE LOWER(er.student.fullName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(er.course.name) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "CAST(er.score AS string) LIKE CONCAT('%', :term, '%')")
    Page<ExamResult> searchWithDetails(@Param("term") String term, Pageable pageable);
}
