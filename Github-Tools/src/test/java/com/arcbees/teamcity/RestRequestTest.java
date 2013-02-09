package com.arcbees.teamcity;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.arcbees.maven.MavenGithub;
import com.arcbees.maven.MavenProperties;
import com.google.gson.JsonElement;

public class RestRequestTest {
    private MavenGithub github;

    @Before
    public void setup() throws SAXException, IOException, ParserConfigurationException {
        MavenProperties properties = new MavenProperties("~/.m2/settings.xml");
        properties.fetchProperties();
        
        github = properties.getGithubCredentials("github");
    }

    @Test
    public void testRestRequest() throws IOException {
        String urlEndpoint = "http://teamcity.gonevertical.org/httpAuth/app/rest/builds/id:299";
        JsonElement json = RestRequest.fetchJson(github.getUsername(), github.getPassword(), urlEndpoint);
        
        Assert.assertNotNull(json);
    }
    
//    @Test
//    public void testRestRequestWithNoCredentials() throws IOException {
//        String urlEndpoint = "http://teamcity.gonevertical.org/httpAuth/app/rest/builds/id:299";
//        JsonElement json = RestRequest.fetchJson(null, null, urlEndpoint);
//        
//        Assert.assertNotNull(json);
//    }
}
