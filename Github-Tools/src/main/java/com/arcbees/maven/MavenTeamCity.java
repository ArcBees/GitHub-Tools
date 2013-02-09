package com.arcbees.maven;

public class MavenTeamCity {
    private String username;
    private String password;
    private String url;
    
    public MavenTeamCity(String username, String password, String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }    
    
    public String getUrl() {
        return url;
    }
}
