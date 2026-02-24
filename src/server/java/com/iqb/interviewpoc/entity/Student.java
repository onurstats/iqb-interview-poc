package com.iqb.interviewpoc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "student", indexes = {
    @Index(name = "idx_student_number", columnList = "number"),
    @Index(name = "idx_student_email", columnList = "email"),
    @Index(name = "idx_student_full_name", columnList = "full_name")
})
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @Column(name = "number", nullable = false, unique = true)
    private Integer number;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "gsm_number")
    private String gsmNumber;

    public Student() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGsmNumber() { return gsmNumber; }
    public void setGsmNumber(String gsmNumber) { this.gsmNumber = gsmNumber; }
}
