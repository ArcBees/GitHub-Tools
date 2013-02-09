package com.arcbees.maven;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class MavenPropertiesTest {
    private MavenProperties properties;

    @Before
    public void setup() {
        properties = new MavenProperties("/Users/branflake2267/.m2/settings.xml");
        try {
            properties.fetchProperties();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testGitHubProperties() {
        MavenGithub github = properties.getGithubCredentials("github");
        
        Assert.assertEquals("branflake2267", github.getUsername());
        Assert.assertNotNull(github.getPassword());
    }
}
