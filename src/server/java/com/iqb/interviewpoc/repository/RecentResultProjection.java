package com.iqb.interviewpoc.repository;

public interface RecentResultProjection {
    Long getId();
    String getFullName();
    String getName();
    Integer getScore();
    String getCreatedAt();
}
