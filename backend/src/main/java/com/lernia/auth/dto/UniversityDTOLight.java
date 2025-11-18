package com.lernia.auth.dto;

public class UniversityDTOLight {
    private Long id;
    private String name;
    private String description;
    private LocationDTO location;

    public UniversityDTOLight() {}

    public UniversityDTOLight(Long id, String name, String description, LocationDTO location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }
}
