package com.arcbees.github;

import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.xml.sax.SAXException;

import com.arcbees.maven.MavenGithub;
import com.arcbees.maven.MavenProperties;

public class PullNotification {
    public static void main(String[] args) {
        PullNotification.newInstance(args).run();
    }
    
    public static PullNotification newInstance(String[] args) {
        return new PullNotification(args);
    }
    
    private GitHubClient client;
    
    // TODO params
    private String repoOwner = "branflake2267";
    private String repoName = "Sandbox";
    private String mavenSettingsPath = "~/.m2/settings.xml";
    private String mavenSettingsGithubServerId = "github";
    private String shaRef = "33dfb4b52e63bf0b97e86704fdc0bf432635176c";
    private String returnUrl = "http://teamcity-private.arcbees.com";
    /**
     * override status
     */
    private String status = "";
    
    public PullNotification(String[] args) {
        // TODO args
        
        // TODO if status is not null, change to it. 
    }

    private void run() {
        loginToGitHub();
        
    }

    private void loginToGitHub() {
        MavenProperties properties = new MavenProperties(mavenSettingsPath);
        try {
            properties.fetchProperties();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        
        MavenGithub githubCredentials = properties.getGithubCredentials(mavenSettingsGithubServerId);
        
        client = new GitHubClient();
        client.setCredentials(githubCredentials.getUsername(), githubCredentials.getUsername());
    }

    private Repository getRepository() {
        RepositoryService repoService = new RepositoryService(client);
        Repository repo = null;
        try {
            repo = repoService.getRepository(repoOwner, repoName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repo;
    }
    
    private void changeStatus() {
        CommitStatus status = new CommitStatus();
        if (1==1) { 
            status.setDescription("The build is errored...");
            status.setState(CommitStatus.STATE_ERROR);
        } else if (2==2) { 
            status.setDescription("The build failed...");
            status.setState(CommitStatus.STATE_FAILURE);
        } else if (3==3) { 
            status.setDescription("The build pending...");
            status.setState(CommitStatus.STATE_PENDING);
        } else if (4==4) { 
            status.setDescription("The build succeeded...");
            status.setState(CommitStatus.STATE_SUCCESS);
        }
        
        status.setTargetUrl(returnUrl);
        status.setUpdatedAt(new Date());
        changeStatus(status);
    }

    private void changeStatus(CommitStatus status) {
        CommitService service = new CommitService(client);
        try {
            service.createStatus(getRepository(), shaRef, status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
