package com.arcbees.github;

import java.io.IOException;
import java.util.Date;

import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.arcbees.maven.MavenProperties;

public class PullNotification {
    public static void main(String[] args) {
        PullNotification.newInstance(args);
    }
    
    public static PullNotification newInstance(String[] args) {
        return new PullNotification(args);
    }
    
    private GitHubClient client;
    
    // TODO params
    private String repoOwner = "branflake2267";
    private String repoName = "Sandbox";
    private String settingsPath = "~/.m2/settings.xml";
    
    public PullNotification(String[] args) {
        // TODO args
    }

    private void run() {
        loginToGitHub();

    }

    private void loginToGitHub() {
        MavenProperties properties = new MavenProperties(settingsPath);
        
        client = new GitHubClient();
        client.setCredentials("branflake2267", "xxx");
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
        Repository repository = getRepository();

        String sha = "33dfb4b52e63bf0b97e86704fdc0bf432635176c";

        CommitStatus status = new CommitStatus();
        status.setCreatedAt(new Date());
        // status.setDescription("Build server is checking build...");
        status.setDescription("Wahoo it everything is ready to merge, I pomise...");

        String state = CommitStatus.STATE_SUCCESS;
        status.setState(state);
        status.setTargetUrl("http://teamcity-private.arcbees.com");
        status.setUpdatedAt(new Date());

        CommitService service = new CommitService(client);
        try {
            service.createStatus(repository, sha, status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
