package com.skillsharing.controller;

import com.skillsharing.dto.MealPlanDTO;
import com.skillsharing.dto.ResourceDTO;
import com.skillsharing.dto.WeekDTO;
import com.skillsharing.model.MealPlan;
import com.skillsharing.model.User;
import com.skillsharing.service.MealPlanService;
import com.skillsharing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<MealPlan> createMealPlan(@Valid @RequestBody MealPlanDTO mealPlanDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        mealPlanDTO.setUserId(currentUser.getId());
        MealPlan createdPlan = mealPlanService.createMealPlan(mealPlanDTO);
        return ResponseEntity.ok(createdPlan);
    }

    @GetMapping
    public ResponseEntity<List<MealPlan>> getCurrentUserMealPlans() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<MealPlan> plans = mealPlanService.getMealPlansByUserId(currentUser.getId());
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealPlan> getMealPlanById(@PathVariable String id) {
        return mealPlanService.getMealPlanById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealPlan> updateMealPlan(
            @PathVariable String id,
            @Valid @RequestBody MealPlanDTO mealPlanDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        MealPlan plan = mealPlanService.getMealPlanById(id)
            .orElseThrow(() -> new RuntimeException("Meal plan not found"));
        
        if (!plan.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        MealPlan updatedPlan = mealPlanService.updateMealPlan(id, mealPlanDTO);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        MealPlan plan = mealPlanService.getMealPlanById(id)
            .orElseThrow(() -> new RuntimeException("Meal plan not found"));
        
        if (!plan.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        mealPlanService.deleteMealPlan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<MealPlan> cloneMealPlan(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        MealPlan originalPlan = mealPlanService.getMealPlanById(id)
            .orElseThrow(() -> new RuntimeException("Meal plan not found"));
        
        if (originalPlan.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("Cannot clone your own meal plan");
        }
        
        MealPlanDTO cloneDTO = new MealPlanDTO();
        cloneDTO.setUserId(currentUser.getId());
        cloneDTO.setTitle(originalPlan.getTitle() + " (Clone)");
        cloneDTO.setDescription(originalPlan.getDescription());
        
        // Convert Resource to ResourceDTO
        List<ResourceDTO> resourceDTOs = originalPlan.getResources().stream()
            .map(resource -> {
                ResourceDTO dto = new ResourceDTO();
                dto.setTitle(resource.getTitle());
                dto.setUrl(resource.getUrl());
                dto.setType(resource.getType());
                return dto;
            })
            .toList();
        cloneDTO.setResources(resourceDTOs);
        
        // Convert Week to WeekDTO
        List<WeekDTO> weekDTOs = originalPlan.getWeeks().stream()
            .map(week -> {
                WeekDTO dto = new WeekDTO();
                dto.setTitle(week.getTitle());
                dto.setDescription(week.getDescription());
                return dto;
            })
            .toList();
        cloneDTO.setWeeks(weekDTOs);
        
        cloneDTO.setSourcePlanId(id);
        
        MealPlan clonedPlan = mealPlanService.createMealPlan(cloneDTO);
        return ResponseEntity.ok(clonedPlan);
    }
}