package com.skillsharing.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "meal-plans")
public class MealPlan {
    
    @Id
    private String id;
    private String userId;
    private String title;
    private String description;
    
    // Personal Details
    private int age;
    private String gender; // Male, Female, Other
    private int height; // in cm
    private int weight; // in kg
    private String activityLevel; // Sedentary, Lightly Active, etc.
    private Set<String> dietaryPreferences; // Vegetarian, Vegan, etc.
    private String allergies;
    
    // Meal Plan Type (Weight Loss, Muscle Gain, etc.)
    private String mealPlanType;
    
    // Existing Resource class (unchanged)
    private List<Resource> resources;
    
    // Existing Week class (unchanged)
    private List<Week> weeks;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Reference to original plan if cloned
    private String sourcePlanId;

    // Constructors
    public MealPlan() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }     
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Set<String> getDietaryPreferences() {
        return dietaryPreferences;
    }

    public void setDietaryPreferences(Set<String> dietaryPreferences) {
        this.dietaryPreferences = dietaryPreferences;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMealPlanType() {
        return mealPlanType;
    }

    public void setMealPlanType(String mealPlanType) {
        this.mealPlanType = mealPlanType;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSourcePlanId() {
        return sourcePlanId;
    }

    public void setSourcePlanId(String sourcePlanId) {
        this.sourcePlanId = sourcePlanId;
    }
}