package com.example.habittracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.habittracker.entity.Habit;
import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findByUserIdAndArchivedFalseOrderByCreatedAtDesc(Long userId);

    List<Habit> findByUserIdAndArchivedTrueOrderByCreatedAtDesc(Long userId);

    Optional<Habit> findByIdAndUserId(Long id, Long userId);

}
