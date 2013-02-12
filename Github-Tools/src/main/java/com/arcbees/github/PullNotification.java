package com.arcbees.github;

import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.egit.github.core.CommitComment;
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

    private static PullNotification newInstance(String[] args) {
        return new PullNotification(args);
    }

    private GitHubClient client;

    /**
     * Parameter -ro=branflake2267 Repository Owner [user or organization]
     */
    private String repoOwner = "branflake2267";

    /**
     * Parameter -rn=Sandbox Repository Name - like [organization/name] or [user/name] - only include the name
     */
    private String repoName = "Sandbox";

    /**
     * Parameter -sha=c9dc29c60df9bf8af8248e77db0e2eca1d7b7e4d Ref Sha which is Team City parameter %build.vcs.number%
     */
    private String commitShaRef = "dba3fdc6c35a9ec42b92314ace0d1662adf021e9";

    /**
     * Parameter -settings=~/.m2/settings.xml Override the default maven settings url.
     */
    private String mavenSettingsPath = "~/.m2/settings.xml";

    /**
     * Parameter -github=github Store the github credentials as a server. <server> <id>github</id>
     * <username>branflake2267</username> <password>xxxxxxx</password> </server>
     */
    private String mavenSettingsGithubServerId = "github";

    /**
     * Parameter -teamcity=teamcity-gonevertical Store the Team City credentials as a server. <server>
     * <id>teamcity-gonevertical</id> <username>branflake2267</username> <password>xxxxxxx</password>
     * <url>http://teamcity.gonevertical.org</url> </server>
     */
    private String mavenSettingsTeamcityServerId = "teamcity-gonevertical";

    /**
     * Parameter -returnurl=http://teamcity.gonevertical.org Provide a return link for build server investigation
     */
    private String buildServerReturnUrl = "http://teamcity.gonevertical.org";

    /**
     * Parameter -status=pending Provide a manual status update
     */
    private String status;

    /**
     * Parameter -buildid=299 Provide the buildId
     */
    private int buildId = 299;
    
    /**
     * Parameter -skipcomment=[false|true]
     */
    private boolean skipGitHubHComment = false;

    private MavenProperties properties;

    private PullNotification() {
    }

    private PullNotification(String[] args) {
        parameterParser(args);
    }

    private void parameterParser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].matches("^-ro.*")) {
                repoOwner = parameterParser(args[i]);
            } else if (args[i].matches("^-rn.*")) {
                repoName = parameterParser(args[i]);
            } else if (args[i].matches("^-sha.*")) {
                commitShaRef = parameterParser(args[i]);
            } else if (args[i].matches("^-settings.*")) {
                String path = parameterParser(args[i]);
                if (path != null) {
                    mavenSettingsPath = path;
                }
            } else if (args[i].matches("^-github.*")) {
                mavenSettingsGithubServerId = parameterParser(args[i]);
            } else if (args[i].matches("^-teamcity.*")) {
                mavenSettingsTeamcityServerId = parameterParser(args[i]);
            } else if (args[i].matches("^-returnurl.*")) {
                buildServerReturnUrl = parameterParser(args[i]);
            } else if (args[i].matches("^-status.*")) {
                status = parameterParser(args[i]);
            } else if (args[i].matches("^-buildid.*")) {
                String id = parameterParser(args[i]);
                try {
                    buildId = Integer.valueOf(id);
                } catch (NumberFormatException e) {
                    buildId = 0;
                }
            } else if (args[i].matches("^-status.*")) {
                status = parameterParser(args[i]);
            } else if (args[i].matches("^-help.*")) {
                displayHelp();
            } else if (args[i].matches("^-skipcomment.*")) {
                skipGitHubHComment = Boolean.valueOf(parameterParser(args[i]));
            }
        }

        boolean mandatoryRequired = false;
        if (repoOwner == null) {
            mandatoryRequired = true;
            System.out.println("arg misssing -ro=RepositoryOwner[user|organization]");
        }

        if (repoName == null) {
            mandatoryRequired = true;
            System.out.println("arg missing -rn=RepositoryName");
        }

        if (commitShaRef == null) {
            mandatoryRequired = true;
            System.out.println("arg missing -sha=[%build.vcs.number%] sha commit reference");
        }

        if (mavenSettingsGithubServerId == null) {
            mandatoryRequired = true;
            System.out.println("arg missing -github=MavenGithubServerId");
        }

        if (mavenSettingsTeamcityServerId == null) {
            mandatoryRequired = true;
            System.out.println("arg missing -teamcity=MavenTeamCityServerId");
        }

        if (buildServerReturnUrl == null) {
            mandatoryRequired = true;
            System.out.println("arg missing -returnurl=BuildServerReturnUrl");
        }

        // if the status is provided no need to check the teamcity build number using rest api
        if (buildId == 0 && status == null) {
            mandatoryRequired = true;
            System.out.println("arg missing -buildid=[%build.number%]");
        }

        if (mandatoryRequired == true) {
            System.out.println("Mandatory argurment is required. Exiting");
            System.exit(0);
        }
    }

    private void displayHelp() {
        String s = "Mandatory arguments:\n";
        s += "-ro=RepositoryOwner[user|organization]\n";
        s += "-rn=RepositoryName\n";
        s += "-sha=[%build.vcs.number%] sha commit reference\n";
        s += "-github=MavenGithubServerId\n";
        s += "-teamcity=MavenTeamCityServerId";
        s += "-returnurl=BuildServerReturnUrl\n";
        s += "-buildid=[%build.number%]";

        s += "\n\nOptional arguments:\n";
        s += "-settings=[~/.m2/settings.xml]\n";
        s += "-status=[fail|pending|success|error]\n";
        s += "-skipcomment=[true|false]\n";
        s += "-help\n";

        System.out.println(s);
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

        if (status != null) {
            try {
                changeStatus(status);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                autoCheckAndChangeGitPullStatus(buildId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!skipGitHubHComment) {
            try {
                addCommitMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        System.exit(0);
    }

    private void autoCheckAndChangeGitPullStatus(int buildId) throws IOException {
        // wait for a few seconds for last build to save to db
        // see if this fixes the build failures that are getting through. 
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        MavenTeamCity teamcitySettings = properties.getTeamCityCredentials(mavenSettingsTeamcityServerId);

        String buildServerUrl = teamcitySettings.getUrl();
        String buildServerUsername = teamcitySettings.getUsername();
        String buildServerPassword = teamcitySettings.getPassword();

        TeamCityRestRequest restRequest = new TeamCityRestRequest(buildServerUrl, buildServerUsername,
                buildServerPassword);

        Build build = new Build();
        build = restRequest.fetchBuildStatus(buildId);

        status = build.getStatus();
        
        System.out.println("autoCheckAndChangeGitPullStatus(): status=" + status);
        
        changeStatus(status);
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
            status.setDescription("The build is in progress...");
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

    private void addCommitMessage() throws IOException {
        String message = "Build server update... " + status.toLowerCase() + ".";

        CommitComment comment = new CommitComment();
        comment.setBody(message);
        comment.setUrl(buildServerReturnUrl);

        CommitService service = new CommitService(client);
        service.addComment(getRepository(), commitShaRef, comment);
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
