package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.entity.Quarter;
import com.ozzo.habit_tracker.entity.SubGoal;
import com.ozzo.habit_tracker.repository.SubGoalRepository;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubGoalService {

    private final SubGoalRepository subGoalRepository;

    public SubGoalService(SubGoalRepository subGoalRepository) {
        this.subGoalRepository = subGoalRepository;
    }

    public List<SubGoal> findByYear(Integer year) {
        if (year == null) {
            return subGoalRepository.findAllWithGoal();
        }
        return subGoalRepository.findByYear(year);
    }

    public Map<Quarter, List<SubGoal>> groupByQuarter(Integer year) {
        List<SubGoal> subGoals = findByYear(year);
        Map<Quarter, List<SubGoal>> grouped = new EnumMap<>(Quarter.class);
        for (Quarter q : Quarter.values()) {
            grouped.put(q, subGoals.stream()
                    .filter(sg -> sg.getQuarter() == q)
                    .collect(Collectors.toList()));
        }
        return grouped;
    }

    public Map<Month, List<SubGoal>> groupByMonth(Integer year) {
        List<SubGoal> subGoals = findByYear(year);
        Map<Month, List<SubGoal>> grouped = new EnumMap<>(Month.class);
        for (Month month : Month.values()) {
            grouped.put(month, subGoals.stream()
                    .filter(sg -> month.equals(sg.getMonth()))
                    .collect(Collectors.toList()));
        }
        return grouped;
    }

    public Set<Integer> collectAvailableYears() {
        List<SubGoal> all = subGoalRepository.findAllWithGoal();
        return all.stream()
                .map(SubGoal::getYear)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public int resolveYear(Integer requestedYear) {
        if (requestedYear != null) {
            return requestedYear;
        }
        return Year.now().getValue();
    }

    public SubGoal save(SubGoal sg){
        return subGoalRepository.save(sg);
    }

    public Optional<SubGoal> findById(Long id){
        return subGoalRepository.findById(id);
    }

    public void deleteById(Long id){
        subGoalRepository.deleteById(id);
    }
}
