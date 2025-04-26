package com.skillsharing.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillsharing.dto.MealPlanDTO;
import com.skillsharing.model.MealPlan;
import com.skillsharing.model.Resource;
import com.skillsharing.model.Week;
import com.skillsharing.repository.MealPlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;

    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public Optional<MealPlan> getMealPlanById(String id) {
        return mealPlanRepository.findById(id);
    }

    public MealPlan createMealPlan(MealPlanDTO mealPlanDTO) {
        MealPlan mealPlan = new MealPlan();
        
        // Set basic information
        mealPlan.setTitle(mealPlanDTO.getTitle());
        mealPlan.setDescription(mealPlanDTO.getDescription());
        mealPlan.setUserId(mealPlanDTO.getUserId());
        
        // Set personal details
        mealPlan.setAge(mealPlanDTO.getAge());
        mealPlan.setGender(mealPlanDTO.getGender());
        mealPlan.setHeight(mealPlanDTO.getHeight());
        mealPlan.setWeight(mealPlanDTO.getWeight());
        mealPlan.setActivityLevel(mealPlanDTO.getActivityLevel());
        mealPlan.setDietaryPreferences(mealPlanDTO.getDietaryPreferences());
        mealPlan.setAllergies(mealPlanDTO.getAllergies());
        mealPlan.setMealPlanType(mealPlanDTO.getMealPlanType());

        // Set resources
        mealPlan.setResources(
            mealPlanDTO.getResources().stream()
                .map(resourceDTO -> {
                    Resource resource = new Resource();
                    resource.setTitle(resourceDTO.getTitle());
                    resource.setUrl(resourceDTO.getUrl());
                    resource.setType(resourceDTO.getType());
                    return resource;
                })
                .toList()
        );

        // Set weeks
        mealPlan.setWeeks(
            mealPlanDTO.getWeeks().stream()
                .map(weekDTO -> {
                    Week week = new Week();
                    week.setTitle(weekDTO.getTitle());
                    week.setDescription(weekDTO.getDescription());
                    return week;
                })
                .toList()
        );

        // Set timestamps
        mealPlan.setCreatedAt(LocalDateTime.now());
        mealPlan.setUpdatedAt(LocalDateTime.now());

        return mealPlanRepository.save(mealPlan);
    }

    public MealPlan updateMealPlan(String id, MealPlanDTO mealPlanDTO) {
        return mealPlanRepository.findById(id)
                .map(existingPlan -> {
                    // Update basic information
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
                    existingPlan.setResources(
                        mealPlanDTO.getResources().stream()
                            .map(resourceDTO -> {
                                Resource resource = new Resource();
                                resource.setTitle(resourceDTO.getTitle());
                                resource.setUrl(resourceDTO.getUrl());
                                resource.setType(resourceDTO.getType());
                                return resource;
                            })
                            .toList()
                    );

                    // Update weeks
                    existingPlan.setWeeks(
                        mealPlanDTO.getWeeks().stream()
                            .map(weekDTO -> {
                                Week week = new Week();
                                week.setTitle(weekDTO.getTitle());
                                week.setDescription(weekDTO.getDescription());
                                return week;
                            })
                            .toList()
                    );

                    // Update timestamp
                    existingPlan.setUpdatedAt(LocalDateTime.now());
                    
                    return mealPlanRepository.save(existingPlan);
                })
                .orElseThrow(() -> new RuntimeException("MealPlan not found with id: " + id));
    }

    public void deleteMealPlan(String id) {
        mealPlanRepository.deleteById(id);
    }
}