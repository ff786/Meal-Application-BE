package com.skillsharing.dto;

import javax.validation.constraints.NotBlank;

public class ResourceDTO {
    private String id;
    
    @NotBlank(message = "Resource title is required")
    private String title;

    @NotBlank(message = "URL is required")
    private String url;

    @NotBlank(message = "Type is required")
    private String type;

    // Default constructor
    public ResourceDTO() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}