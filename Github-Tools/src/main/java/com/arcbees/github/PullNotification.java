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
import com.arcbees.maven.MavenTeamCity;
import com.arcbees.teamcity.TeamCityRestRequest;
import com.arcbees.teamcity.model.Build;

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
    private String commitShaRef = "33dfb4b52e63bf0b97e86704fdc0bf432635176c";

    private String mavenSettingsPath = "~/.m2/settings.xml";
    private String mavenSettingsGithubServerId = "github";
    private String mavenSettingsTeamcityServerId = "teamcity-gonevertical";
    private String buildServerReturnUrl = "http://teamcity.gonevertical.org";
    private String status = "error";

    private MavenProperties properties;

    public PullNotification(String[] args) {
        // TODO args

        // TODO if status is not null, change to it.
    }

    private void run() {
        properties = new MavenProperties(mavenSettingsPath);
        
        loginToGitHub();

        autoChangeStatus(299);
    }

    private void autoChangeStatus(int buildId) {
        MavenTeamCity teamcitySettings = properties.getTeamCityCredentials(mavenSettingsTeamcityServerId);
        
        String buildServerUrl = teamcitySettings.getUrl();
        String buildServerUsername = teamcitySettings.getUsername();
        String buildServerPassword = teamcitySettings.getPassword();
        
        TeamCityRestRequest restRequest = new TeamCityRestRequest(buildServerUrl, buildServerUsername,
                buildServerPassword);
        
        Build build = new Build();
        try {
            build = restRequest.fetchBuildStatus(buildId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        changeStatus(build.getStatus());
    }

    /**
     * Login to git. 
     */
    private void loginToGitHub() {
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

    private void changeStatus(String buildStatus) {
        if (buildStatus == null || buildStatus.trim().length() == 0) {
            buildStatus = buildStatus.toLowerCase().trim();
        }

        CommitStatus status = new CommitStatus();
        if (buildStatus.contains("fail")) {
            status.setDescription("The build has failed...");
            status.setState(CommitStatus.STATE_FAILURE);

        } else if (buildStatus.contains("pend")) {
            status.setDescription("The build in progress...");
            status.setState(CommitStatus.STATE_PENDING);

        } else if (buildStatus.contains("succ")) {
            status.setDescription("The build has succeeded...");
            status.setState(CommitStatus.STATE_SUCCESS);

        } else {
            status.setDescription("The build has errored...");
            status.setState(CommitStatus.STATE_ERROR);
        }

        status.setTargetUrl(buildServerReturnUrl);
        status.setUpdatedAt(new Date());
        changeStatus(status);
    }

    private void changeStatus(CommitStatus status) {
        CommitService service = new CommitService(client);
        try {
            service.createStatus(getRepository(), commitShaRef, status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
