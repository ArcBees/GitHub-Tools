package org.arcbees.github;

import java.io.IOException;
import java.util.Date;

import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

public class PullNotification {
  
  public static void main(String[] args) {
    new PullNotification().run();
  }
  
  private GitHubClient client;
  
  private String repoOwner = "branflake2267";
  private String repoName = "Sandbox";
  
  private void run() {
    login();

    Repository repository = getRepository();
    
    
    
    String sha = "53ca4c75f32792f0bd48885b4ac9ba3bd2b1cfd8";
    
    CommitStatus status = new CommitStatus();
    status.setCreatedAt(new Date());
    //status.setDescription("Build server is checking build...");
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
  
  private void login() {
    client = new GitHubClient();
    client.setCredentials("branflake2267", "xxx");
  }

  private Repository getRepository() {
    RepositoryService repoService = new RepositoryService(client);
    Repository repo = null;
    try {
      repo  = repoService.getRepository(repoOwner, repoName);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return repo;
  }
  
}
