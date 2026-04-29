package com.example.habittracker.service;

import com.example.habittracker.dto.request.HabitCreateRequest;
import com.example.habittracker.dto.request.HabitUpdateRequest;
import com.example.habittracker.dto.response.HabitResponse;
import com.example.habittracker.dto.response.HabitStatsResponse;
import com.example.habittracker.entity.Habit;
import com.example.habittracker.entity.HabitCheckIn;
import com.example.habittracker.exception.HabitNotFoundException;
import com.example.habittracker.repository.HabitCheckInRepository;
import com.example.habittracker.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitCheckInRepository habitCheckInRepository;

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

    public HabitResponse getHabitById(Long id) {
        Habit habit = habitRepository.findById(id)
        .orElseThrow(() -> new HabitNotFoundException(id));

        return toResponse(habit);
    }

    public HabitResponse updateHabit(Long id, HabitUpdateRequest request) {
        Habit habit = habitRepository.findById(id)
        .orElseThrow(() -> new HabitNotFoundException(id));

        habit.setTitle(request.getTitle());
        habit.setDescription(request.getDescription());

        Habit updatedHabit = habitRepository.save(habit);

        return toResponse(updatedHabit);
    }

    public void archiveHabit(Long id) {
        Habit habit = habitRepository.findById(id)
        .orElseThrow(() -> new HabitNotFoundException(id));

        habit.setArchived(true);

        habitRepository.save(habit);
    }

    public HabitStatsResponse getHabitStats(Long habitId) {
        if (!habitRepository.existsById(habitId)) {
            throw new HabitNotFoundException(habitId);
        }

        long totalCheckIns = habitCheckInRepository.countByHabitId(habitId);

        int currentStreak = calculateCurrentStreak(habitId);

        int longestStreak = calculateLongestStreak(habitId);

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        long completedDaysLast7Days = habitCheckInRepository.countByHabitIdAndCheckInDateBetween(
        habitId,
        sevenDaysAgo,
        today
        );

        double completionRateLast7Days = completedDaysLast7Days * 100.0 / 7;
        completionRateLast7Days = roundToTwoDecimals(completionRateLast7Days);

        return new HabitStatsResponse(
        habitId,
        totalCheckIns,
        currentStreak,
        longestStreak,
        completedDaysLast7Days,
        completionRateLast7Days
        );
    }

    private int calculateCurrentStreak(Long habitId) {
        List<HabitCheckIn> checkIns = habitCheckInRepository.findByHabitIdOrderByCheckInDateDesc(habitId);

        int streak = 0;
        LocalDate expectedDate = LocalDate.now();

        for (HabitCheckIn checkIn : checkIns) {
            if (checkIn.getCheckInDate().equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else if (checkIn.getCheckInDate().isBefore(expectedDate)) {
                break;
            }
        }

        return streak;
    }

    private int calculateLongestStreak(Long habitId) {
        List<HabitCheckIn> checkIns =
        habitCheckInRepository.findByHabitIdOrderByCheckInDateAsc(habitId);

        if (checkIns.isEmpty()) {
            return 0;
        }

        int longestStreak = 1;
        int currentStreak = 1;

        LocalDate previousDate = checkIns.get(0).getCheckInDate();

        for (int i = 1; i < checkIns.size(); i++) {
            LocalDate currentDate = checkIns.get(i).getCheckInDate();

            if (currentDate.equals(previousDate.plusDays(1))) {
                currentStreak++;
            } else {
                currentStreak = 1;
            }

            if (currentStreak > longestStreak) {
                longestStreak = currentStreak;
            }

            previousDate = currentDate;
        }

        return longestStreak;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
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