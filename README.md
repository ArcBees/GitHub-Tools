#GitHub-Tools
Team City build server GitHub pull notification on build status application.

##Reference
* [Shell Script Templates](https://github.com/ArcBees/GitHub-Tools/tree/master/Github-Tools/src/main/resources/com/arcbees/run)

##Directions
* [Directions and Screenshots](http://c.gwt-examples.com/home/maven/build-server/team-city/github-pull-notification-app)

##Command Line Argument Options

###Mandatory
* `-ro=RepositoryOwner` - Owner is either a user or organization.
* `-rn=RepostioryName` - Repository name.
* `-sha=2e84e6446df300cd572930869c5ed2be8ee1f614` - Latest commit sha reference in pull. 
 * `-sha=%build.vcs.number%` - Using the Team City variable parameter.    
* `-github=github` - Maven ~/.m2/settings.xml GitHub server id.
* `-teamcity=teamcity-gonevertical` - Maven ~/.m2/settings.xml Team City server id.
* `-returnurl=http://teamcity.gonevertical.org` - Return url link for the the status link. 
 * `-returnurl=%teamcity.serverUrl%` - Using the Team City variable parameter.

###Mandatory OR
Use either `-buildid` OR `-status`:

* `-buildid=299` - Team City Build Id. This calls the rest api to check the status of the build. 
 * `-buildid=%teamcity.build.id%` - Using the Team City variable parameter. 
* `-status=pending`
 * `-status=error`
 * `-status=failed`
 * `-status=success`

###Optional
* `-help` - display the argument options.
* `-skipcomment=true` - Adding a commit message is on by default. Turn it off and on with this.

###Maven Settings
Two servers have to be added to use this applicaiton. Change the server id to your desire as long 
as it is matched in the command line options. 

* `-github=github` example:

  ```xml
  <server>
      <id>github</id>
      <username>branflake2267</username>
      <password>xxxxxxx</password>
  </server>
  ```
  
* `-teamcity=teamcity-gonevertical` example:

  ```xml
  <server>
      <id>teamcity-gonevertical</id>
      <username>branflake2267</username>
      <password>xxxxxxx</password>
      <configuration>
          <url>http://teamcity.gonevertical.org</url>
      </configuration>
  </server>
  ```

##Thanks to
[![Arcbees.com](http://arcbees-ads.appspot.com/ad.png)](http://arcbees.com)

[![IntelliJ](https://lh6.googleusercontent.com/--QIIJfKrjSk/UJJ6X-UohII/AAAAAAAAAVM/cOW7EjnH778/s800/banner_IDEA.png)](http://www.jetbrains.com/idea/index.html)
