package com.arcbees.teamcity;

import java.io.IOException;


import com.arcbees.teamcity.model.Build;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Team City Rest Request
 * 
 * @See http://confluence.jetbrains.com/display/TW/REST+API+Plugin#RESTAPIPlugin-Developmentlinks
 */
public class TeamCityRestRequest {
    private static String PATH_BUILD = "/httpAuth/app/rest/builds/id:";
    
    private final String buildServerUrl;
    private final String username;
    private final String password;

    @SuppressWarnings("unused")
    private TeamCityRestRequest() {
        buildServerUrl = null;
        username = null;
        password = null;
    }
    
    /**
     * Fetch the build progress status
     * 
     * @param buildServerUrl - 'http://domain.tld:port'
     */
    public TeamCityRestRequest(String buildServerUrl, String buildServerUsername, String buildServerPassword) {
        this.buildServerUrl = buildServerUrl;
        this.username = buildServerUsername;
        this.password = buildServerPassword;
    }
    
    public Build fetchBuildStatus(int buildId) throws IOException {
        String urlEndpoint = buildServerUrl + PATH_BUILD + Integer.toString(buildId);
        
        JsonElement json = RestRequest.fetchJson(username, password, urlEndpoint);
        Gson gson = new Gson();
        
        return gson.fromJson(json, Build.class);
    }
}
