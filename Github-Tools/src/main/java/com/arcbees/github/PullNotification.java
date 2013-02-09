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
    private String commitShaRef = "c9dc29c60df9bf8af8248e77db0e2eca1d7b7e4d";

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
        try {
            properties.fetchProperties();
        } catch (SAXException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

        try {
            autoChangeStatus(299);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autoChangeStatus(int buildId) throws IOException {
        MavenTeamCity teamcitySettings = properties.getTeamCityCredentials(mavenSettingsTeamcityServerId);

        String buildServerUrl = teamcitySettings.getUrl();
        String buildServerUsername = teamcitySettings.getUsername();
        String buildServerPassword = teamcitySettings.getPassword();

        TeamCityRestRequest restRequest = new TeamCityRestRequest(buildServerUrl, buildServerUsername,
                buildServerPassword);

        Build build = new Build();
        build = restRequest.fetchBuildStatus(buildId);

        changeStatus(build.getStatus());
    }

    private void changeStatus(String buildStatus) throws IOException {
        if (buildStatus != null && buildStatus.trim().length() == 0) {
            buildStatus = "error";
        } else {
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
        status.setCreatedAt(new Date());
        status.setUpdatedAt(new Date());
        
        changeStatus(status);
    }

    private void changeStatus(CommitStatus status) throws IOException {
        loginToGitHub();

        CommitService service = new CommitService(client);
        service.createStatus(getRepository(), commitShaRef, status);
    }

    private Repository getRepository() throws IOException {
        RepositoryService repoService = new RepositoryService(client);
        Repository  repo = repoService.getRepository(repoOwner, repoName);
        return repo;
    }

    /**
     * Login to git.
     */
    private void loginToGitHub() {
        MavenGithub githubCredentials = properties.getGithubCredentials(mavenSettingsGithubServerId);

        client = new GitHubClient();
        client.setCredentials(githubCredentials.getUsername(), githubCredentials.getUsername());
    }
}
