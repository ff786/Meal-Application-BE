package com.skillsharing.controller;

import com.skillsharing.model.MealPlan;
import com.skillsharing.model.User;
import com.skillsharing.model.Week; // Assuming you have a Week class for the weeks field
import com.skillsharing.repository.MealPlanRepository;
import com.skillsharing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/meal-plan")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;

    // Add a new mealing plan
    @PostMapping
    public ResponseEntity<?> createMealPlan(@RequestBody MealPlan plan) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        plan.setUserId(currentUser.getId());
        MealPlan savedPlan = mealPlanRepository.save(plan);
        return ResponseEntity.ok(savedPlan);
    }

    // Get all mealing plans (admin or for viewing/testing)
    @GetMapping
    public ResponseEntity<List<MealPlan>> getAllMealPlans() {
        List<MealPlan> allPlans = mealPlanRepository.findAll();
        return ResponseEntity.ok(allPlans);
    }

    // Get all plans for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MealPlan>> getPlansForUser(@PathVariable String userId) {
        List<MealPlan> plans = mealPlanRepository.findByUserId(userId);
        return ResponseEntity.ok(plans);
    }

    // Get a specific plan by ID
    @GetMapping("/{planId}")
    public ResponseEntity<?> getPlanById(@PathVariable String planId) {
        Optional<MealPlan> optionalPlan = mealPlanRepository.findById(planId);
        return optionalPlan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a mealing plan
    @PutMapping("/{planId}")
    public ResponseEntity<?> updateMealPlan(@PathVariable String planId, @RequestBody MealPlan updatedPlan) {
        Optional<MealPlan> optionalPlan = mealPlanRepository.findById(planId);
        if (optionalPlan.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MealPlan existingPlan = optionalPlan.get();
        existingPlan.setTitle(updatedPlan.getTitle());
        existingPlan.setDescription(updatedPlan.getDescription());

        existingPlan.setAge(updatedPlan.getAge());
        existingPlan.setGender(updatedPlan.getGender());
        existingPlan.setHeight(updatedPlan.getHeight());
        existingPlan.setWeight(updatedPlan.getWeight());

        existingPlan.setWeeks(updatedPlan.getWeeks());

        MealPlan savedPlan = mealPlanRepository.save(existingPlan);
        return ResponseEntity.ok(savedPlan);
    }

    // Delete a mealing plan
    @DeleteMapping("/{planId}")
    public ResponseEntity<?> deleteMealPlan(@PathVariable String planId) {
        if (!mealPlanRepository.existsById(planId)) {
            return ResponseEntity.notFound().build();
        }

        mealPlanRepository.deleteById(planId);
        return ResponseEntity.ok(Map.of("message", "Mealing plan deleted successfully"));
    }

    @PostMapping("/follow/{planId}")
    public ResponseEntity<?> followMealPlan(@PathVariable String planId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
    
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        Optional<MealPlan> optionalPlan = mealPlanRepository.findById(planId);
        if (optionalPlan.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        MealPlan originalPlan = optionalPlan.get();
    
        if (originalPlan.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot follow your own mealing plan"));
        }
    
        // Check if already followed
        boolean alreadyFollowed = mealPlanRepository.existsByUserIdAndSourcePlanId(currentUser.getId(), planId);
        if (alreadyFollowed) {
            return ResponseEntity.badRequest().body(Map.of("error", "You have already followed this mealing plan"));
        }
    
        MealPlan newPlan = new MealPlan();
        newPlan.setUserId(currentUser.getId());
        newPlan.setTitle(originalPlan.getTitle());
        newPlan.setDescription(originalPlan.getDescription());

        newPlan.setAge(originalPlan.getAge());
        newPlan.setGender(originalPlan.getGender());
        newPlan.setHeight(originalPlan.getHeight());
        newPlan.setWeight(originalPlan.getWeight());

        newPlan.setResources(originalPlan.getResources());
        newPlan.setWeeks(copyWeeksWithResetStatus(originalPlan.getWeeks()));
        newPlan.setSourcePlanId(planId);
    
        MealPlan savedPlan = mealPlanRepository.save(newPlan);
        return ResponseEntity.ok(Map.of("message", "Mealing plan followed successfully", "planId", savedPlan.getId()));
    }
    
    private List<Week> copyWeeksWithResetStatus(List<Week> originalWeeks) {
        if (originalWeeks == null) {
            return new ArrayList<>();
        }
        List<Week> newWeeks = new ArrayList<>();
        for (Week week : originalWeeks) {
            Week newWeek = new Week();
            newWeek.setTitle(week.getTitle());
            newWeek.setDescription(week.getDescription());

            newWeek.setStatus("Not Started");
            newWeeks.add(newWeek);
        }
        return newWeeks;
    }
}