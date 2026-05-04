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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCheckInRepository habitCheckInRepository;

    @Mock
    private UserService currentUserService;

    @InjectMocks
    private HabitService habitService;

    @Test
    void shouldCreateHabitForCurrentUser() {
        Long userId = 10L;

        HabitCreateRequest request = new HabitCreateRequest();
        request.setTitle("Read 20 pages");
        request.setDescription("Read every evening");

        Habit savedHabit = buildHabit(1L, userId);
        savedHabit.setTitle(request.getTitle());
        savedHabit.setDescription(request.getDescription());

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.save(any(Habit.class))).thenReturn(savedHabit);

        HabitResponse response = habitService.createHabit(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Read 20 pages");
        assertThat(response.getDescription()).isEqualTo("Read every evening");

        ArgumentCaptor<Habit> habitCaptor = ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(habitCaptor.capture());

        Habit habitToSave = habitCaptor.getValue();

        assertThat(habitToSave.getTitle()).isEqualTo("Read 20 pages");
        assertThat(habitToSave.getDescription()).isEqualTo("Read every evening");
        assertThat(habitToSave.getUserId()).isEqualTo(userId);
        assertThat(habitToSave.isArchived()).isFalse();
    }

    @Test
    void shouldReturnActiveHabitsForCurrentUser() {
        Long userId = 10L;

        Habit habit1 = buildHabit(1L, userId);
        Habit habit2 = buildHabit(2L, userId);

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByUserIdAndArchivedFalseOrderByCreatedAtDesc(userId))
        .thenReturn(List.of(habit1, habit2));

        List<HabitResponse> response = habitService.getActiveHabits();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(1).getId()).isEqualTo(2L);

        verify(habitRepository).findByUserIdAndArchivedFalseOrderByCreatedAtDesc(userId);
    }

    @Test
    void shouldReturnHabitByIdForCurrentUser() {
        Long habitId = 1L;
        Long userId = 10L;

        Habit habit = buildHabit(habitId, userId);

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.of(habit));

        HabitResponse response = habitService.getHabitById(habitId);

        assertThat(response.getId()).isEqualTo(habitId);
        assertThat(response.getTitle()).isEqualTo("Read 20 pages");
        assertThat(response.getDescription()).isEqualTo("Read every evening");

        verify(habitRepository).findByIdAndUserId(habitId, userId);
    }

    @Test
    void shouldThrowExceptionWhenHabitByIdNotFound() {
        Long habitId = 999L;
        Long userId = 10L;

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.empty());

        assertThrows(
        HabitNotFoundException.class,
        () -> habitService.getHabitById(habitId)
        );
    }

    @Test
    void shouldUpdateHabitForCurrentUser() {
        Long habitId = 1L;
        Long userId = 10L;

        Habit habit = buildHabit(habitId, userId);

        HabitUpdateRequest request = new HabitUpdateRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");

        Habit updatedHabit = buildHabit(habitId, userId);
        updatedHabit.setTitle(request.getTitle());
        updatedHabit.setDescription(request.getDescription());

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.of(habit));
        when(habitRepository.save(habit)).thenReturn(updatedHabit);

        HabitResponse response = habitService.updateHabit(habitId, request);

        assertThat(response.getId()).isEqualTo(habitId);
        assertThat(response.getTitle()).isEqualTo("Updated title");
        assertThat(response.getDescription()).isEqualTo("Updated description");

        assertThat(habit.getTitle()).isEqualTo("Updated title");
        assertThat(habit.getDescription()).isEqualTo("Updated description");

        verify(habitRepository).save(habit);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingHabitNotFound() {
        Long habitId = 999L;
        Long userId = 10L;

        HabitUpdateRequest request = new HabitUpdateRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.empty());

        assertThrows(
        HabitNotFoundException.class,
        () -> habitService.updateHabit(habitId, request)
        );
    }

    @Test
    void shouldArchiveHabitForCurrentUser() {
        Long habitId = 1L;
        Long userId = 10L;

        Habit habit = buildHabit(habitId, userId);
        habit.setArchived(false);

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.of(habit));

        habitService.archiveHabit(habitId);

        assertThat(habit.isArchived()).isTrue();

        verify(habitRepository).save(habit);
    }

    @Test
    void shouldThrowExceptionWhenArchivingHabitNotFound() {
        Long habitId = 999L;
        Long userId = 10L;

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.empty());

        assertThrows(
        HabitNotFoundException.class,
        () -> habitService.archiveHabit(habitId)
        );
    }

    @Test
    void shouldReturnArchivedHabitsForCurrentUser() {
        Long userId = 10L;

        Habit habit = buildHabit(1L, userId);
        habit.setArchived(true);

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByUserIdAndArchivedTrueOrderByCreatedAtDesc(userId))
        .thenReturn(List.of(habit));

        List<HabitResponse> response = habitService.getArchivedHabits();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(0).isArchived()).isTrue();

        verify(habitRepository).findByUserIdAndArchivedTrueOrderByCreatedAtDesc(userId);
    }

    @Test
    void shouldRestoreHabitForCurrentUser() {
        Long habitId = 1L;
        Long userId = 10L;

        Habit habit = buildHabit(habitId, userId);
        habit.setArchived(true);

        Habit restoredHabit = buildHabit(habitId, userId);
        restoredHabit.setArchived(false);

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.of(habit));
        when(habitRepository.save(habit)).thenReturn(restoredHabit);

        HabitResponse response = habitService.restoreHabit(habitId);

        assertThat(response.getId()).isEqualTo(habitId);
        assertThat(response.isArchived()).isFalse();

        assertThat(habit.isArchived()).isFalse();

        verify(habitRepository).save(habit);
    }

    @Test
    void shouldReturnHabitStatsWithCurrentStreakAndCompletedToday() {
        Long habitId = 1L;
        Long userId = 10L;

        LocalDate today = LocalDate.now();

        Habit habit = buildHabit(habitId, userId);

        List<HabitCheckIn> checkInsDesc = List.of(
        buildCheckIn(today),
        buildCheckIn(today.minusDays(1)),
        buildCheckIn(today.minusDays(2))
        );

        List<HabitCheckIn> checkInsAsc = List.of(
        buildCheckIn(today.minusDays(2)),
        buildCheckIn(today.minusDays(1)),
        buildCheckIn(today)
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.of(habit));

        when(habitCheckInRepository.countByHabitId(habitId))
        .thenReturn(3L);

        when(habitCheckInRepository.findByHabitIdOrderByCheckInDateDesc(habitId))
        .thenReturn(checkInsDesc);

        when(habitCheckInRepository.findByHabitIdOrderByCheckInDateAsc(habitId))
        .thenReturn(checkInsAsc);

        when(habitCheckInRepository.countByHabitIdAndCheckInDateBetween(
        habitId,
        today.minusDays(6),
        today
        )).thenReturn(3L);

        when(habitCheckInRepository.existsByHabitIdAndCheckInDate(habitId, today))
        .thenReturn(true);

        HabitStatsResponse response = habitService.getHabitStats(habitId);

        assertThat(response.getHabitId()).isEqualTo(habitId);
        assertThat(response.getTotalCheckIns()).isEqualTo(3L);
        assertThat(response.getCurrentStreak()).isEqualTo(3);
        assertThat(response.getLongestStreak()).isEqualTo(3);
        assertThat(response.getCompletedDaysLast7Days()).isEqualTo(3L);
        assertThat(response.getCompletionRateLast7Days()).isEqualTo(42.86);
        assertThat(response.isCompletedToday()).isTrue();
    }

    @Test
    void shouldReturnZeroCurrentStreakWhenHabitWasNotCompletedToday() {
        Long habitId = 1L;
        Long userId = 10L;

        LocalDate today = LocalDate.now();

        Habit habit = buildHabit(habitId, userId);

        List<HabitCheckIn> checkInsDesc = List.of(
        buildCheckIn(today.minusDays(1)),
        buildCheckIn(today.minusDays(2))
        );

        List<HabitCheckIn> checkInsAsc = List.of(
        buildCheckIn(today.minusDays(2)),
        buildCheckIn(today.minusDays(1))
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.of(habit));

        when(habitCheckInRepository.countByHabitId(habitId))
        .thenReturn(2L);

        when(habitCheckInRepository.findByHabitIdOrderByCheckInDateDesc(habitId))
        .thenReturn(checkInsDesc);

        when(habitCheckInRepository.findByHabitIdOrderByCheckInDateAsc(habitId))
        .thenReturn(checkInsAsc);

        when(habitCheckInRepository.countByHabitIdAndCheckInDateBetween(
        habitId,
        today.minusDays(6),
        today
        )).thenReturn(2L);

        when(habitCheckInRepository.existsByHabitIdAndCheckInDate(habitId, today))
        .thenReturn(false);

        HabitStatsResponse response = habitService.getHabitStats(habitId);

        assertThat(response.getTotalCheckIns()).isEqualTo(2L);
        assertThat(response.getCurrentStreak()).isEqualTo(0);
        assertThat(response.getLongestStreak()).isEqualTo(2);
        assertThat(response.getCompletedDaysLast7Days()).isEqualTo(2L);
        assertThat(response.getCompletionRateLast7Days()).isEqualTo(28.57);
        assertThat(response.isCompletedToday()).isFalse();
    }

    @Test
    void shouldCalculateLongestStreakWithGapBetweenCheckIns() {
        Long habitId = 1L;
        Long userId = 10L;

        LocalDate today = LocalDate.now();

        Habit habit = buildHabit(habitId, userId);

        List<HabitCheckIn> checkInsDesc = List.of(
        buildCheckIn(today),
        buildCheckIn(today.minusDays(1)),
        buildCheckIn(today.minusDays(3)),
        buildCheckIn(today.minusDays(4)),
        buildCheckIn(today.minusDays(5))
        );

        List<HabitCheckIn> checkInsAsc = List.of(
        buildCheckIn(today.minusDays(5)),
        buildCheckIn(today.minusDays(4)),
        buildCheckIn(today.minusDays(3)),
        buildCheckIn(today.minusDays(1)),
        buildCheckIn(today)
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.of(habit));

        when(habitCheckInRepository.countByHabitId(habitId))
        .thenReturn(5L);

        when(habitCheckInRepository.findByHabitIdOrderByCheckInDateDesc(habitId))
        .thenReturn(checkInsDesc);

        when(habitCheckInRepository.findByHabitIdOrderByCheckInDateAsc(habitId))
        .thenReturn(checkInsAsc);

        when(habitCheckInRepository.countByHabitIdAndCheckInDateBetween(
        habitId,
        today.minusDays(6),
        today
        )).thenReturn(5L);

        when(habitCheckInRepository.existsByHabitIdAndCheckInDate(habitId, today))
        .thenReturn(true);

        HabitStatsResponse response = habitService.getHabitStats(habitId);

        assertThat(response.getCurrentStreak()).isEqualTo(2);
        assertThat(response.getLongestStreak()).isEqualTo(3);
        assertThat(response.getCompletedDaysLast7Days()).isEqualTo(5L);
        assertThat(response.getCompletionRateLast7Days()).isEqualTo(71.43);
    }

    @Test
    void shouldThrowExceptionWhenGettingStatsForHabitNotFound() {
        Long habitId = 999L;
        Long userId = 10L;

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByIdAndUserId(habitId, userId))
        .thenReturn(Optional.empty());

        assertThrows(
        HabitNotFoundException.class,
        () -> habitService.getHabitStats(habitId)
        );
    }

    @Test
    void shouldReturnHabitSummariesForDashboard() {
        Long habitId = 1L;
        Long userId = 10L;

        LocalDate today = LocalDate.now();

        Habit habit = buildHabit(habitId, userId);

        List<HabitCheckIn> checkInsDesc = List.of(
        buildCheckIn(today),
        buildCheckIn(today.minusDays(1))
        );

        List<HabitCheckIn> checkInsAsc = List.of(
        buildCheckIn(today.minusDays(1)),
        buildCheckIn(today)
        );

        when(currentUserService.getCurrentUserId()).thenReturn(userId);
        when(habitRepository.findByUserIdAndArchivedFalseOrderByCreatedAtDesc(userId))
        .thenReturn(List.of(habit));

        when(habitCheckInRepository.existsByHabitIdAndCheckInDate(habitId, today))
        .thenReturn(true);

        when(habitCheckInRepository.findByHabitIdOrderByCheckInDateDesc(habitId))
        .thenReturn(checkInsDesc);

        when(habitCheckInRepository.findByHabitIdOrderByCheckInDateAsc(habitId))
        .thenReturn(checkInsAsc);

        when(habitCheckInRepository.countByHabitIdAndCheckInDateBetween(
        habitId,
        today.minusDays(6),
        today
        )).thenReturn(2L);

        var response = habitService.getHabitSummaries();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getId()).isEqualTo(habitId);
        assertThat(response.get(0).getTitle()).isEqualTo("Read 20 pages");
        assertThat(response.get(0).getDescription()).isEqualTo("Read every evening");
        assertThat(response.get(0).isCompletedToday()).isTrue();
        assertThat(response.get(0).getCurrentStreak()).isEqualTo(2);
        assertThat(response.get(0).getLongestStreak()).isEqualTo(2);
        assertThat(response.get(0).getCompletedDaysLast7Days()).isEqualTo(2L);
        assertThat(response.get(0).getCompletionRateLast7Days()).isEqualTo(28.57);
    }

    private Habit buildHabit(Long habitId, Long userId) {
        Habit habit = new Habit();

        habit.setId(habitId);
        habit.setUserId(userId);
        habit.setTitle("Read 20 pages");
        habit.setDescription("Read every evening");
        habit.setArchived(false);
        habit.setCreatedAt(LocalDateTime.now());

        return habit;
    }

    private HabitCheckIn buildCheckIn(LocalDate date) {
        HabitCheckIn checkIn = new HabitCheckIn();

        checkIn.setCheckInDate(date);

        return checkIn;
    }
}