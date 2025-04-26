package com.skillsharing.repository;

import com.skillsharing.model.MealPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MealPlanRepository extends MongoRepository<MealPlan, String> {
    List<MealPlan> findByUserId(String userId);

    // Find all meal plans for a user ordered by creation
    List<MealPlan> findByUserIdOrderByIdDesc(String userId);

    // Search meal plans by title (case-insensitive)
    List<MealPlan> findByUserIdAndTitleContainingIgnoreCase(String userId, String title);

    // Count total meal plans for a user
    long countByUserId(String userId);

    // Find meal plans that include a specific resource title
    @Query("{ 'userId': ?0, 'resources.title': { $regex: ?1, $options: 'i' } }")
    List<MealPlan> findByUserIdAndResourceTitleLike(String userId, String resourceTitle);

    // Find meal plans that have weeks with a specific status
    @Query("{ 'userId': ?0, 'weeks.status': ?1 }")
    List<MealPlan> findByUserIdAndWeekStatus(String userId, String status);

    // Get most recent N meal plans for dashboard
    List<MealPlan> findTop5ByUserIdOrderByIdDesc(String userId);

    // Check if meal plan exists by userId and sourcePlanId
    boolean existsByUserIdAndSourcePlanId(String id, String planId);
}
