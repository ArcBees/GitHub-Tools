package com.arcbees.maven;

public class MavenGithub {
    private String username;
    private String password;
    
    public MavenGithub(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }    
}
