#GitHub-Tools
Team City build server GitHub pull notification on build status application.

##Reference
* [Shell Script Templates](https://github.com/ArcBees/GitHub-Tools/tree/master/Github-Tools/src/main/resources/com/arcbees/run)

##Directions
* [Directions and Screenshots](http://c.gwt-examples.com/home/maven/build-server/team-city/github-pull-notification-app)

##Command Line Options
* `-ro=RepositoryOwner` - Owner is either a user or organization.
* `-rn=RepostioryName` - Repository name.
* `-sha=2e84e6446df300cd572930869c5ed2be8ee1f614` - Latest commit sha reference in pull. 
  * `-sha=%build.vcs.number%` - Using the Team City variable filter.  
* `-github=github` - Maven ~/.m2/settings.xml GitHub server id. 
```xml
   <server>
      <id>github</id>
      <username>branflake2267</username>
      <password>xxxxxx</password>
   </server>
```
