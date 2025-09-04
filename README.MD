# HidekoBot    
[![Reliability Rating](https://sonar.beatrice.wtf/api/project_badges/measure?project=HidekoBot&metric=reliability_rating&token=0a63c149148555d6d2ee40665af1afae8f67cc3f)](https://sonar.beatrice.wtf/dashboard?id=HidekoBot)
[![Maintainability Rating](https://sonar.beatrice.wtf/api/project_badges/measure?project=HidekoBot&metric=sqale_rating&token=0a63c149148555d6d2ee40665af1afae8f67cc3f)](https://sonar.beatrice.wtf/dashboard?id=HidekoBot) 
[![Security Rating](https://sonar.beatrice.wtf/api/project_badges/measure?project=HidekoBot&metric=security_rating&token=0a63c149148555d6d2ee40665af1afae8f67cc3f)](https://sonar.beatrice.wtf/dashboard?id=HidekoBot)
[![Build Status](https://drone.beatrice.wtf/api/badges/bea/HidekoBot/status.svg)](https://drone.beatrice.wtf/bea/HidekoBot)
[![Lines of Code](https://sonar.beatrice.wtf/api/project_badges/measure?project=HidekoBot&metric=ncloc&token=0a63c149148555d6d2ee40665af1afae8f67cc3f)](https://sonar.beatrice.wtf/dashboard?id=HidekoBot)
  
Hideko is a general-purpose Discord bot.  
  
## Download  
The latest stable version is always uploaded automatically to the [Maven repository](https://nexus.beatrice.wtf/#browse/browse:maven-releases:wtf%2Fbeatrice%2Fhidekobot%2FHidekoBot).  
You can download the JAR directly by clicking [here](https://nexus.beatrice.wtf/service/rest/v1/search/assets/download?sort=version&repository=maven-releases&maven.groupId=wtf.beatrice.hidekobot&maven.artifactId=HidekoBot&maven.extension=jar).    
  
## Startup
Download a prebuilt JAR file or build it from source, then run it with:
```bash
java -jar HidekoBot.jar [additional parameters]
```
Where `HidekoBot.jar` is the executable archive and `[additional parameters]` are arguments that you can add to
make the bot change its behavior.
  
Additionally available parameters are:
  - **verbose**: log every message that the bot receives, plus additional debugging messages. Very spammy and performance heavy.
  - **refresh**: force refresh the slash commands. This is useful in case there was a simple update to a command that did not drastically change it, so no changes are found at bootup (eg: fixing a typo in the command description).

*Note: Java 21 or later is required.*
  
## Initial setup  
  
Run the startup command once. The bot will generate a `config.yml` file in your current directory (`$PWD` on GNU/Linux).  
  
Edit the configuration file and set all values according to your needs.  
  
Save the file and start the bot again. If there are no issues, everything will load and it will print an
invite-link in your console. Click on the link to add your bot to any server with the correct permissions
already set-up. The bot supports both slash commands and message commands, with prefix `hideko`. Most
commands support both systems, but some of them are limited in one way or another.  
  
The bot currently supports SQLite as a database backend. A database file will be created after the first boot
in your current directory. Do not delete the database file to avoid corruption and unpredictable
behavior.

# Development
## Versioning
This project uses the `x.y.z-releaseType` schema for releases.  
Development builds are tagged as `x.y.z-SNAPSHOT` and sometimes pushed to the snapshots Maven repository.  
Stable builds are tagged as `x.y.z` and always pushed to the releases Maven repository, by promoting the build on
[Drone](https://drone.beatrice.wtf/). Currently, promoting stable builds is a manual process.
