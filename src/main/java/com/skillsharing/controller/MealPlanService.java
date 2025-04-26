package com.skillsharing.controller;

import com.skillsharing.dto.MealPlanDTO;
import com.skillsharing.model.MealPlan;
import com.skillsharing.model.Resource;
import com.skillsharing.model.Week;
import com.skillsharing.repository.MealPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;

    public MealPlan createMealPlan(MealPlanDTO mealPlanDTO) {
        MealPlan mealPlan = new MealPlan();
        mealPlan.setUserId(mealPlanDTO.getUserId());
        mealPlan.setTitle(mealPlanDTO.getTitle());
        mealPlan.setDescription(mealPlanDTO.getDescription());
        mealPlan.setResources(mealPlanDTO.getResources());
        mealPlan.setWeeks(mealPlanDTO.getWeeks());
        mealPlan.setSourcePlanId(mealPlanDTO.getSourcePlanId());
        return mealPlanRepository.save(mealPlan);
    }

    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public List<MealPlan> getMealPlansByUserId(String userId) {
        // Implement logic to fetch meal plans by userId
        // Example: return mealPlanRepository.findByUserId(userId);
        return new ArrayList<>(); // Replace with actual implementation
    }

    public MealPlan updateMealPlan(String planId, MealPlanDTO mealPlanDTO) {
        MealPlan existingPlan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));
        existingPlan.setTitle(mealPlanDTO.getTitle());
        existingPlan.setDescription(mealPlanDTO.getDescription());
        List<Resource> resources = mealPlanDTO.getResources().stream()
                .map(resourceDTO -> new Resource(
                        resourceDTO.getId(),
                        resourceDTO.getName(),
                        resourceDTO.getType()
                ))
                .toList();
        existingPlan.setResources(resources);
        List<Week> weeks = mealPlanDTO.getWeeks().stream()
                .map(weekDTO -> new Week(
                        weekDTO.getId(),
                        weekDTO.getName(),
                        weekDTO.getDays()
                ))
                .toList();
        existingPlan.setWeeks(weeks);
        return mealPlanRepository.save(existingPlan);
    }

    public void deleteMealPlan(String planId) {
        mealPlanRepository.deleteById(planId);
    }

    public Optional<MealPlan> getMealPlanById(String planId) {
        return mealPlanRepository.findById(planId);
    }
}