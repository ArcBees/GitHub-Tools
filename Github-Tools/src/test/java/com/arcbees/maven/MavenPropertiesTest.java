package com.arcbees.maven;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public class MavenPropertiesTest {
    @Test
    public void testGitHubProperties() {
        MavenProperties properties = new MavenProperties("/Users/branflake2267/.m2/settings.xml");
        try {
            properties.fetchProperties();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        
        MavenGithub github = properties.getGithubCredentials("github");
        
        Assert.assertEquals("branflake2267", github.getUsername());
        Assert.assertNotNull(github.getPassword());
    }
    
    @Test
    public void testGitHubPropertiesHome() {
        MavenProperties properties = new MavenProperties("~/.m2/settings.xml");
        try {
            properties.fetchProperties();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        
        MavenGithub github = properties.getGithubCredentials("github");
        
        Assert.assertEquals("branflake2267", github.getUsername());
        Assert.assertNotNull(github.getPassword());
    }
    
    @Test
    public void testTeamCityProperties() {
        MavenProperties properties = new MavenProperties("~/.m2/settings.xml");
        try {
            properties.fetchProperties();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        
        MavenTeamCity settings = properties.getTeamCityCredentials("teamcity-gonevertical");
        
        Assert.assertEquals("branflake2267", settings.getUsername());
        Assert.assertNotNull(settings.getPassword());
        Assert.assertEquals("http://teamcity.gonevertical.org", settings.getUrl());
    }
}
