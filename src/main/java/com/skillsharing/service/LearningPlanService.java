package com.skillsharing.service;

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
public class LearningPlanService {

    private final MealPlanRepository learningPlanRepository;

    public List<MealPlan> getAllLearningPlans() {
        return learningPlanRepository.findAll();
    }

    public Optional<MealPlan> getLearningPlanById(String id) {
        return learningPlanRepository.findById(id);
    }

    public MealPlan createLearningPlan(MealPlanDTO learningPlanDTO) {
        MealPlan learningPlan = new MealPlan();
        learningPlan.setTitle(learningPlanDTO.getTitle());
        learningPlan.setDescription(learningPlanDTO.getDescription());

        learningPlan.setAge(learningPlanDTO.getAge());
        learningPlan.setGender(learningPlanDTO.getGender());
        learningPlan.setHeight(learningPlanDTO.getHeight());
        learningPlan.setWeight(learningPlanDTO.getWeight());

        learningPlan.setResources(
            learningPlanDTO.getResources().stream()
                .map(resourceDTO -> {
                    Resource resource = new Resource();
                    resource.setTitle(resourceDTO.getTitle());
                    resource.setUrl(resourceDTO.getUrl());
                    return resource;
                })
                .toList()
        );
        learningPlan.setWeeks(
            learningPlanDTO.getWeeks().stream()
                .map(weekDTO -> {
                    Week week = new Week();
                    week.setTitle(weekDTO.getTitle());
                    week.setDescription(weekDTO.getDescription());

                    return week;
                })
                .toList()
        );
        return learningPlanRepository.save(learningPlan);
    }

    public MealPlan updateLearningPlan(String id, MealPlanDTO learningPlanDTO) {
        return learningPlanRepository.findById(id)
                .map(existingPlan -> {
                    existingPlan.setResources(
                        learningPlanDTO.getResources().stream()
                            .map(resourceDTO -> {
                                Resource resource = new Resource();
                                resource.setTitle(resourceDTO.getTitle());
                                resource.setUrl(resourceDTO.getUrl());
                                return resource;
                            })
                            .toList()
                    );
                    existingPlan.setDescription(learningPlanDTO.getDescription());
                    existingPlan.setResources(
                        learningPlanDTO.getResources().stream()
                            .map(resourceDTO -> {
                                Resource resource = new Resource();
                                resource.setTitle(resourceDTO.getTitle());
                                resource.setUrl(resourceDTO.getUrl());
                                return resource;
                            })
                            .toList()
                    );
                    existingPlan.setWeeks(
                        learningPlanDTO.getWeeks().stream()
                            .map(weekDTO -> {
                                Week week = new Week();
                                week.setTitle(weekDTO.getTitle());
                                week.setDescription(weekDTO.getDescription());

                                return week;
                            })
                            .toList()
                    );
                    return learningPlanRepository.save(existingPlan);
                })
                .orElseThrow(() -> new RuntimeException("LearningPlan not found with id: " + id));
    }

    public void deleteLearningPlan(String id) {
        learningPlanRepository.deleteById(id);
    }
}