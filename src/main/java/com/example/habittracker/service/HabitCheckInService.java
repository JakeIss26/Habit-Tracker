package com.example.habittracker.service;

import com.example.habittracker.dto.response.HabitCheckInResponse;
import com.example.habittracker.entity.HabitCheckIn;
import com.example.habittracker.repository.HabitCheckInRepository;
import com.example.habittracker.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HabitCheckInService {

    private final HabitCheckInRepository habitCheckInRepository;
    private final HabitRepository habitRepository;

    public HabitCheckInResponse createCheckIn(Long habitId) {
        if (!habitRepository.existsById(habitId)) {
            throw new RuntimeException("Habit not found");
        }

        LocalDate today = LocalDate.now();

        boolean alreadyCheckedIn = habitCheckInRepository.existsByHabitIdAndCheckInDate(habitId, today);

        if (alreadyCheckedIn) {
            throw new RuntimeException("Habit already checked in today");
        }

        HabitCheckIn checkIn = new HabitCheckIn();

        checkIn.setHabitId(habitId);
        checkIn.setCheckInDate(today);

        HabitCheckIn savedCheckIn = habitCheckInRepository.save(checkIn);

        return toResponse(savedCheckIn);
    }

    public List<HabitCheckInResponse> getCheckInsByHabitId(Long habitId) {
        if (!habitRepository.existsById(habitId)) {
            throw new RuntimeException("Habit not found");
        }

        return habitCheckInRepository.findByHabitIdOrderByCheckInDateDesc(habitId)
        .stream()
        .map(this::toResponse)
        .toList();
    }
    public void deleteCheckIn(Long habitId, Long checkInId) {
        HabitCheckIn checkIn = habitCheckInRepository.findById(checkInId)
        .orElseThrow(() -> new RuntimeException("Check-in not found"));

        if (!checkIn.getHabitId().equals(habitId)) {
            throw new RuntimeException("Check-in does not belong to this habit");
        }

        habitCheckInRepository.delete(checkIn);
    }

    private HabitCheckInResponse toResponse(HabitCheckIn checkIn) {
        return new HabitCheckInResponse(
        checkIn.getId(),
        checkIn.getHabitId(),
        checkIn.getCheckInDate(),
        checkIn.getCreatedAt()
        );
    }
}