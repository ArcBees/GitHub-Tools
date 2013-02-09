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

    /**
     * Parameter -ro=branflake2267
     * Repository Owner [user or organization]
     */
    private String repoOwner = "branflake2267";

    /**
     * Parameter -rn=Sandbox
     * Repository Name - like [organization/name] or [user/name] - only include the name
     */
    private String repoName = "Sandbox";

    /**
     * Parameter -sha=c9dc29c60df9bf8af8248e77db0e2eca1d7b7e4d
     * Ref Sha which is Team City parameter %build.vcs.number%
     */
    private String commitShaRef = "c9dc29c60df9bf8af8248e77db0e2eca1d7b7e4d";

    /**
     * Parameter -settings=~/.m2/settings.xml 
     * Override the default maven settings url.
     */
    private String mavenSettingsPath = "~/.m2/settings.xml";

    /**
     * Parameter -github=github
     * Store the github credentials as a server. <server> <id>github</id> <username>branflake2267</username>
     * <password>xxxxxxx</password> </server>
     */
    private String mavenSettingsGithubServerId = "github";

    /**
     * Parameter -teamcity=teamcity-gonevertical
     * Store the Team City credentials as a server. <server> <id>teamcity-gonevertical</id>
     * <username>branflake2267</username> <password>xxxxxxx</password> <url>http://teamcity.gonevertical.org</url>
     * </server>
     */
    private String mavenSettingsTeamcityServerId = "teamcity-gonevertical";

    /**
     * Parameter -returnurl=http://teamcity.gonevertical.org
     * Provide a return link for build server investigation
     */
    private String buildServerReturnUrl = "http://teamcity.gonevertical.org";

    /**
     * Parameter -status=pending 
     * Provide a manual status update
     */
    private String status = "error";
    
    /**
     * Parameter -buildid=299
     * Provide the buildId
     */
    private int buildId = 299;

    private MavenProperties properties;

    public PullNotification(String[] args) {
        parameterParser(args);

        // TODO validate params and output missing
        // TODO manual status override when status is provided
    }

    private void parameterParser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-ro")) {
                repoOwner = parameterParser(args[i]);
            } else if (args[i].equals("-rn")) {
                repoName = parameterParser(args[i]);
            } else if (args[i].equals("-sha")) {
                commitShaRef = parameterParser(args[i]);
            } else if (args[i].equals("-settings")) {
                String path = parameterParser(args[i]);
                if (path != null) {
                    mavenSettingsPath = path;
                }
            } else if (args[i].equals("-github")) {
                mavenSettingsGithubServerId = parameterParser(args[i]);
            } else if (args[i].equals("-teamcity")) {
                mavenSettingsTeamcityServerId = parameterParser(args[i]);
            } else if (args[i].equals("-returnurl")) {
                buildServerReturnUrl = parameterParser(args[i]);
            } else if (args[i].equals("-status")) {
                status = parameterParser(args[i]);
            } else if (args[i].equals("-buildid")) {
                String id = parameterParser(args[i]);
                buildId = Integer.valueOf(id);
            }
        }
    }

    private String parameterParser(String param) {
        if (param == null || param.trim().length() == 0 || param.contains("=") == false) {
            return null;
        }

        String[] values = param.split("=");
        return values[1];
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
            autoChangeGitPullStatus(buildId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autoChangeGitPullStatus(int buildId) throws IOException {
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
        return repoService.getRepository(repoOwner, repoName);
    }

    private void loginToGitHub() {
        MavenGithub githubCredentials = properties.getGithubCredentials(mavenSettingsGithubServerId);

        client = new GitHubClient();
        client.setCredentials(githubCredentials.getUsername(), githubCredentials.getPassword());
    }
}
