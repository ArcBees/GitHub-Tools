package com.arcbees.teamcity;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.arcbees.maven.MavenGithub;
import com.arcbees.maven.MavenProperties;
import com.arcbees.teamcity.model.Build;

public class TeamCityRestRequestTest {
    private MavenGithub github;

    @Before
    public void setup() throws SAXException, IOException, ParserConfigurationException {
        MavenProperties properties = new MavenProperties("~/.m2/settings.xml");
        properties.fetchProperties();
        
        github = properties.getGithubCredentials("github");
    }

    @Test
    public void testRestRequestSuccess() throws IOException {
        String serverUrl = "http://teamcity.gonevertical.org";
        String username = github.getUsername();
        String password = github.getPassword();
        int buildId = 299;
        
        TeamCityRestRequest rest = new TeamCityRestRequest(serverUrl, username, password);
        Build build = rest.fetchBuildStatus(buildId);
        
        Assert.assertNotNull(build);
        Assert.assertTrue(build.getStatus().contains("SUCC"));
    }
    
    @Test
    public void testRestRequestFailure() throws IOException {
        String serverUrl = "http://teamcity.gonevertical.org";
        String username = github.getUsername();
        String password = github.getPassword();
        int buildId = 466;
        
        TeamCityRestRequest rest = new TeamCityRestRequest(serverUrl, username, password);
        Build build = rest.fetchBuildStatus(buildId);
        
        Assert.assertNotNull(build);
        Assert.assertTrue(build.getStatus().contains("FAIL"));
    }
}
