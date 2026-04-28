package com.example.habittracker.repository;

import com.example.habittracker.entity.HabitCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HabitCheckInRepository extends JpaRepository<HabitCheckIn, Long> {

    List<HabitCheckIn> findByHabitId(Long habitId);

    boolean existsByHabitIdAndCheckInDate(Long habitId, LocalDate checkInDate);

    long countByHabitId(Long habitId);
}