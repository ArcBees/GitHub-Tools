package com.arcbees.teamcity.model;

/**
 * Team City build progress status
 */
public class Build {
    private int id;
    private int number;
    private String status;
    
    public int getId() {
        return id;
    }
    
    public int getNumber() {
        return number;
    }
    
    public String getStatus() {
        return status;
    }
}
