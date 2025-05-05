package com.skillsharing.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsharing.model.PlaningUpdate;
import com.skillsharing.model.User;
import com.skillsharing.repository.PlaningRepository;
import com.skillsharing.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/planing")
@RequiredArgsConstructor
public class PlaningController {
    
    private static final Logger logger = LoggerFactory.getLogger(PlaningController.class);
    private final PlaningRepository planingUpdateRepository;
    private final UserRepository userRepository;
    
    // Get planing update templates
    @GetMapping("/templates")
    public ResponseEntity<?> getPlaningTemplates() {
        Map<String, Object> response = new HashMap<>();
        
        // Tutorial completion template
        Map<String, Object> tutorialTemplate = new HashMap<>();
        tutorialTemplate.put("title", "Ate Balanced Meals");
        tutorialTemplate.put("category", "TUTORIAL");
        tutorialTemplate.put("fields", List.of(
            Map.of("name", "resourceName", "label", "Meal Name", "type", "text", "required", true),
            Map.of("name", "description", "label", "What did you eat?", "type", "textarea", "required", false),
            Map.of("name", "skillsLearned", "label", "What do you focus on?", "type", "tags", "required", true),
            Map.of("name", "hoursSpent", "label", "Time Taken to Prepare (hour)", "type", "number", "required", true),
            Map.of("name", "difficulty", "label", "Meal Preparation Level", "type", "select", "options", 
                  List.of("BEGINNER", "INTERMEDIATE", "ADVANCED"), "required", true)
        ));
        
        // Course completion template
        Map<String, Object> courseTemplate = new HashMap<>();
        courseTemplate.put("title", "Tried a New Recipe");
        courseTemplate.put("category", "COURSE");
        courseTemplate.put("fields", List.of(
            Map.of("name", "resourceName", "label", "Recipe Name ", "type", "text", "required", true),
            Map.of("name", "description", "label", "How did you make it?", "type", "textarea", "required", false),
            Map.of("name", "skillsLearned", "label", "What do you focus on?", "type", "tags", "required", true),
            Map.of("name", "hoursSpent", "label", "Time Spent Cooking(hour)", "type", "number", "required", true),
            Map.of("name", "difficulty", "label", "Recipe Difficulty Level", "type", "select", "options", 
                  List.of("BEGINNER", "INTERMEDIATE", "ADVANCED"), "required", true)
        ));
        
        // Project completion template
        Map<String, Object> projectTemplate = new HashMap<>();
        projectTemplate.put("title", "Meal Prepped");
        projectTemplate.put("category", "PROJECT");
        projectTemplate.put("fields", List.of(
            Map.of("name", "resourceName", "label", "Meal Prep Title", "type", "text", "required", true),
            Map.of("name", "description", "label", "What meals did you prep?", "type", "textarea", "required", true),
            Map.of("name", "skillsLearned", "label", "What do you focus on?", "type", "tags", "required", true),
            Map.of("name", "hoursSpent", "label", "Total Prep Time(hour)", "type", "number", "required", true),
            Map.of("name", "difficulty", "label", "Meal Prep Skill Level", "type", "select", "options", 
                  List.of("BEGINNER", "INTERMEDIATE", "ADVANCED"), "required", true)
        ));
        
        response.put("templates", List.of(tutorialTemplate, courseTemplate, projectTemplate));
        
        return ResponseEntity.ok(response);
    }
    
    // Add a mealing update
    @PostMapping("/updates")
    public ResponseEntity<?> addPlaningUpdate(@RequestBody PlaningUpdate planingUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        planingUpdate.setUserId(currentUser.getId());
        planingUpdate.setCreatedAt(LocalDateTime.now());
        
        if (planingUpdate.getCompletedAt() == null) {
            planingUpdate.setCompletedAt(LocalDateTime.now());
        }
        
        // Initialize skills if null
        if (currentUser.getSkills() == null) {
            currentUser.setSkills(new HashSet<>());
        }
        
        // Update user's skills with newly learned skills
        if (planingUpdate.getSkillsLearned() != null && !planingUpdate.getSkillsLearned().isEmpty()) {
            for (String skill : planingUpdate.getSkillsLearned()) {
                if (!currentUser.getSkills().contains(skill)) {
                    currentUser.getSkills().add(skill);
                }
            }
        }
        
        // Update streak information
        updatePlaningStreak(currentUser, planingUpdate.getCompletedAt().toLocalDate());
        userRepository.save(currentUser);
        
        PlaningUpdate savedUpdate = planingUpdateRepository.save(planingUpdate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("planingUpdate", savedUpdate);
        response.put("user", currentUser); // Return updated user with new skills and streak
        
        return ResponseEntity.ok(response);
    }
    
    // Helper method to update mealing streak
    private void updatePlaningStreak(User user, LocalDate planingDate) {
        // Initialize palning dates set if null
        if (user.getPlaningDates() == null) {
            user.setPlaningDates(new HashSet<>());
        }
        
        // If this date was already recorded, no need to update streak
        if (user.getPlaningDates().contains(planingDate)) {
            return;
        }
        
        // Add this date to mealing dates
        user.getPlaningDates().add(planingDate);
        
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        // If this is the first mealing activity or if the last mealing was more than a day ago
        if (user.getLastPlaningDate() == null) {
            user.setCurrentStreak(1);
            user.setLastPlaningDate(planingDate);
        } else if (user.getLastPlaningDate().equals(yesterday) || 
                   user.getLastPlaningDate().equals(today)) {
            // Increment streak if last activity was yesterday or today
            user.setCurrentStreak(user.getCurrentStreak() + 1);
            user.setLastPlaningDate(planingDate);
        } else if (planingDate.isAfter(user.getLastPlaningDate())) {
            // Reset streak if there's a gap
            user.setCurrentStreak(1);
            user.setLastPlaningDate(planingDate);
        }
        
        // Update longest streak if current streak is longer
        if (user.getCurrentStreak() > user.getLongestStreak()) {
            user.setLongestStreak(user.getCurrentStreak());
        }
    }
    
    // Get mealing updates for a user
    @GetMapping("/updates/user/{userId}")
    public ResponseEntity<List<PlaningUpdate>> getUserPlaningUpdates(@PathVariable String userId) {
        List<PlaningUpdate> updates = planingUpdateRepository.findByUserIdOrderByCompletedAtDesc(userId);
        return ResponseEntity.ok(updates);
    }
    
    // Delete a mealing update
    @DeleteMapping("/updates/{updateId}")
    public ResponseEntity<?> deletePlaningUpdate(@PathVariable String updateId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<PlaningUpdate> updateOpt = planingUpdateRepository.findById(updateId);
        
        if (updateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        PlaningUpdate update = updateOpt.get();
        
        // Only allow the owner to delete their updates
        if (!update.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("You are not authorized to delete this mealing plan update");
        }
        
        planingUpdateRepository.delete(update);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Meal Plan update deleted successfully");
        
        return ResponseEntity.ok(response);
    }
    
    // Update a mealing update
    @PutMapping("/updates/{updateId}")
    public ResponseEntity<?> updatePlaningUpdate(
            @PathVariable String updateId,
            @RequestBody PlaningUpdate updatedData) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<PlaningUpdate> updateOpt = planingUpdateRepository.findById(updateId);
        
        if (updateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        PlaningUpdate existingUpdate = updateOpt.get();
        
        // Verify ownership
        if (!existingUpdate.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("You are not authorized to update this mealing plan update");
        }
        
        // Initialize skills if null
        if (currentUser.getSkills() == null) {
            currentUser.setSkills(new HashSet<>());
        }
        
        // Update fields while preserving the original user ID and creation date
        existingUpdate.setTitle(updatedData.getTitle());
        existingUpdate.setDescription(updatedData.getDescription());
        existingUpdate.setCategory(updatedData.getCategory());
        existingUpdate.setResourceName(updatedData.getResourceName());
        existingUpdate.setDifficulty(updatedData.getDifficulty());
        existingUpdate.setHoursSpent(updatedData.getHoursSpent());
        existingUpdate.setCompletedAt(updatedData.getCompletedAt() != null ? 
                                     updatedData.getCompletedAt() : existingUpdate.getCompletedAt());
        
        // Handle skill updates
        if (updatedData.getSkillsLearned() != null) {
            // Look for new skills added
            List<String> newSkills = new ArrayList<>();
            for (String skill : updatedData.getSkillsLearned()) {
                if (!existingUpdate.getSkillsLearned().contains(skill) && 
                    !currentUser.getSkills().contains(skill)) {
                    newSkills.add(skill);
                }
            }
            
            // Add any new skills to the user
            if (!newSkills.isEmpty()) {
                for (String skill : newSkills) {
                    currentUser.getSkills().add(skill);
                }
                userRepository.save(currentUser);
            }
            
            // Update the meal plan update skills
            existingUpdate.setSkillsLearned(updatedData.getSkillsLearned());
        }
        
        PlaningUpdate savedUpdate = planingUpdateRepository.save(existingUpdate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("planingUpdate", savedUpdate);
        response.put("user", currentUser); // Return updated user with any new skills
        
        return ResponseEntity.ok(response);
    }
    
    // Add new endpoint to get streak information
    @GetMapping("/streak/{userId}")
    public ResponseEntity<?> getUserStreak(@PathVariable String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        
        Map<String, Object> response = new HashMap<>();
        response.put("currentStreak", user.getCurrentStreak());
        response.put("longestStreak", user.getLongestStreak());
        response.put("lastPlaningDate", user.getLastPlaningDate());
        
        // Calculate calendar heatmap data
        Map<String, Integer> planingHeatmap = new HashMap<>();
        if (user.getPlaningDates() != null) {
            // Get dates from the last 6 months
            LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
            
            user.getPlaningDates().stream()
                .filter(date -> !date.isBefore(sixMonthsAgo))
                .forEach(date -> {
                    String dateString = date.toString();
                    planingHeatmap.put(dateString, planingHeatmap.getOrDefault(dateString, 0) + 1);
                });
        }
        
        response.put("heatmapData", planingHeatmap);
        
        return ResponseEntity.ok(response);
    }
}
