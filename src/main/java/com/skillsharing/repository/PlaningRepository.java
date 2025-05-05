package com.skillsharing.repository;

import com.skillsharing.model.PlaningUpdate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface PlaningRepository extends MongoRepository<PlaningUpdate, String> {
    // Find all updates for a user ordered by completion date (most recent first)
    List<PlaningUpdate> findByUserIdOrderByCompletedAtDesc(String userId);
    
    // Find updates by category (TUTORIAL, COURSE, PROJECT)
    List<PlaningUpdate> findByUserIdAndCategoryOrderByCompletedAtDesc(String userId, String category);
    
    // Find updates by difficulty level
    List<PlaningUpdate> findByUserIdAndDifficultyOrderByCompletedAtDesc(String userId, String difficulty);
    
    // Find updates completed within a date range
    List<PlaningUpdate> findByUserIdAndCompletedAtBetweenOrderByCompletedAtDesc(
        String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find updates that include a specific skill
    @Query("{ 'userId': ?0, 'skillsLearned': { $in: [?1] } }")
    List<PlaningUpdate> findByUserIdAndSkill(String userId, String skill);
    
    // Count updates by category
    long countByUserIdAndCategory(String userId, String category);
    
    // Get total hours spent on planning
    @Query(value = "{ 'userId': ?0 }", 
           fields = "{ 'hoursSpent': 1 }")
    List<PlaningUpdate> findHoursSpentByUserId(String userId);
    
    // Find most recent updates (for dashboard feed)
    List<PlaningUpdate> findTop10ByUserIdInOrderByCompletedAtDesc(List<String> userIds);
    
    // Find updates by resource name (for search)
    List<PlaningUpdate> findByUserIdAndResourceNameContainingIgnoreCaseOrderByCompletedAtDesc(
        String userId, String resourceName);
}
