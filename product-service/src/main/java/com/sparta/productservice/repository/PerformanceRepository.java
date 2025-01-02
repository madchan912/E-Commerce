package com.sparta.productservice.repository;

import com.sparta.productservice.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    List<Performance> findByNameContaining(String name);
    @Query("SELECT p FROM Performance p WHERE p.date BETWEEN :start AND :end")
    List<Performance> findByDateBetweenWithoutSeats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM Performance p WHERE p.date BETWEEN :start AND :end AND p.name LIKE %:name%")
    List<Performance> findByNameContainingAndDateBetweenWithoutSeats(String name, LocalDateTime start, LocalDateTime end);

}
