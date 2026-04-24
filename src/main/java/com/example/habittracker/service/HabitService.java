package com.example.habittracker.service;

import com.example.habittracker.dto.request.HabitCreateRequest;
import com.example.habittracker.dto.response.HabitResponse;
import com.example.habittracker.entity.Habit;
import com.example.habittracker.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;

    public HabitResponse createHabit(HabitCreateRequest request) {
        Habit habit = new Habit();

        habit.setTitle(request.getTitle());
        habit.setDescription(request.getDescription());

        Habit savedHabit = habitRepository.save(habit);

        return toResponse(savedHabit);
    }

    public List<HabitResponse> getActiveHabits() {
        return habitRepository.findByArchivedFalseOrderByCreatedAtDesc()
        .stream()
        .map(this::toResponse)
        .toList();
    }

    private HabitResponse toResponse(Habit habit) {
        return new HabitResponse(
        habit.getId(),
        habit.getTitle(),
        habit.getDescription(),
        habit.isArchived(),
        habit.getCreatedAt()
        );
    }
}