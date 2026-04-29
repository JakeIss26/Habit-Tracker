package com.example.habittracker.service;

import com.example.habittracker.dto.response.HabitCheckInResponse;
import com.example.habittracker.entity.HabitCheckIn;
import com.example.habittracker.exception.CheckInNotFoundException;
import com.example.habittracker.exception.CheckInOwnershipException;
import com.example.habittracker.exception.HabitAlreadyCheckedInException;
import com.example.habittracker.exception.HabitNotFoundException;
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
            throw new HabitNotFoundException(habitId);
        }

        LocalDate today = LocalDate.now();

        boolean alreadyCheckedIn = habitCheckInRepository.existsByHabitIdAndCheckInDate(habitId, today);

        if (alreadyCheckedIn) {
            throw new HabitAlreadyCheckedInException(habitId);
        }

        HabitCheckIn checkIn = new HabitCheckIn();

        checkIn.setHabitId(habitId);
        checkIn.setCheckInDate(today);

        HabitCheckIn savedCheckIn = habitCheckInRepository.save(checkIn);

        return toResponse(savedCheckIn);
    }

    public List<HabitCheckInResponse> getCheckInsByHabitId(Long habitId) {
        if (!habitRepository.existsById(habitId)) {
            throw new HabitNotFoundException(habitId);
        }

        return habitCheckInRepository.findByHabitIdOrderByCheckInDateDesc(habitId)
        .stream()
        .map(this::toResponse)
        .toList();
    }
    public void deleteCheckIn(Long habitId, Long checkInId) {
        HabitCheckIn checkIn = habitCheckInRepository.findById(checkInId)
        .orElseThrow(() -> new CheckInNotFoundException(checkInId));

        if (!checkIn.getHabitId().equals(habitId)) {
            throw new CheckInOwnershipException(checkInId, habitId);
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