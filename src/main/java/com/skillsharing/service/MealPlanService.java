package com.skillsharing.service;

import com.skillsharing.dto.MealPlanDTO;
import com.skillsharing.dto.ResourceDTO;
import com.skillsharing.dto.WeekDTO;
import com.skillsharing.model.MealPlan;
import com.skillsharing.model.Resource;
import com.skillsharing.model.Week;
import com.skillsharing.repository.MealPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        
        // Map personal details
        mealPlan.setAge(mealPlanDTO.getAge());
        mealPlan.setGender(mealPlanDTO.getGender());
        mealPlan.setHeight(mealPlanDTO.getHeight());
        mealPlan.setWeight(mealPlanDTO.getWeight());
        mealPlan.setActivityLevel(mealPlanDTO.getActivityLevel());
        mealPlan.setDietaryPreferences(mealPlanDTO.getDietaryPreferences());
        mealPlan.setAllergies(mealPlanDTO.getAllergies());
        mealPlan.setMealPlanType(mealPlanDTO.getMealPlanType());

        // Map resources
        List<Resource> resources = mealPlanDTO.getResources().stream()
            .map(resourceDTO -> {
                Resource resource = new Resource();
                resource.setTitle(resourceDTO.getTitle());
                resource.setUrl(resourceDTO.getUrl());
                resource.setType(resourceDTO.getType());
                return resource;
            })
            .toList();
        mealPlan.setResources(resources);

        // Map weeks
        List<Week> weeks = mealPlanDTO.getWeeks().stream()
            .map(weekDTO -> {
                Week week = new Week();
                week.setTitle(weekDTO.getTitle());
                week.setDescription(weekDTO.getDescription());
                return week;
            })
            .toList();
        mealPlan.setWeeks(weeks);

        mealPlan.setSourcePlanId(mealPlanDTO.getSourcePlanId());
        mealPlan.setCreatedAt(LocalDateTime.now());
        mealPlan.setUpdatedAt(LocalDateTime.now());
        
        return mealPlanRepository.save(mealPlan);
    }

    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public List<MealPlan> getMealPlansByUserId(String userId) {
        return mealPlanRepository.findByUserId(userId);
    }

    public MealPlan updateMealPlan(String planId, MealPlanDTO mealPlanDTO) {
        MealPlan existingPlan = mealPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Meal plan not found"));
        
        existingPlan.setTitle(mealPlanDTO.getTitle());
        existingPlan.setDescription(mealPlanDTO.getDescription());
        
        // Update personal details
        existingPlan.setAge(mealPlanDTO.getAge());
        existingPlan.setGender(mealPlanDTO.getGender());
        existingPlan.setHeight(mealPlanDTO.getHeight());
        existingPlan.setWeight(mealPlanDTO.getWeight());
        existingPlan.setActivityLevel(mealPlanDTO.getActivityLevel());
        existingPlan.setDietaryPreferences(mealPlanDTO.getDietaryPreferences());
        existingPlan.setAllergies(mealPlanDTO.getAllergies());
        existingPlan.setMealPlanType(mealPlanDTO.getMealPlanType());

        // Update resources
        List<Resource> resources = mealPlanDTO.getResources().stream()
            .map(resourceDTO -> {
                Resource resource = new Resource();
                resource.setTitle(resourceDTO.getTitle());
                resource.setUrl(resourceDTO.getUrl());
                resource.setType(resourceDTO.getType());
                return resource;
            })
            .toList();
        existingPlan.setResources(resources);

        // Update weeks
        List<Week> weeks = mealPlanDTO.getWeeks().stream()
            .map(weekDTO -> {
                Week week = new Week();
                week.setTitle(weekDTO.getTitle());
                week.setDescription(weekDTO.getDescription());
                return week;
            })
            .toList();
        existingPlan.setWeeks(weeks);
        
        existingPlan.setUpdatedAt(LocalDateTime.now());
        
        return mealPlanRepository.save(existingPlan);
    }

    public void deleteMealPlan(String planId) {
        mealPlanRepository.deleteById(planId);
    }

    public Optional<MealPlan> getMealPlanById(String planId) {
        return mealPlanRepository.findById(planId);
    }
}