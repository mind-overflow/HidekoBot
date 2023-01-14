# HidekoBot   
[![Quality Gate Status](https://sonar.beatrice.wtf/api/project_badges/measure?project=mind-overflow_HidekoBot_AYWyYHsvX-1Ma0D4pJ59&metric=alert_status&token=0a63c149148555d6d2ee40665af1afae8f67cc3f)](https://sonar.beatrice.wtf/dashboard?id=mind-overflow_HidekoBot_AYWyYHsvX-1Ma0D4pJ59) 
[![Maintainability Rating](https://sonar.beatrice.wtf/api/project_badges/measure?project=mind-overflow_HidekoBot_AYWyYHsvX-1Ma0D4pJ59&metric=sqale_rating&token=0a63c149148555d6d2ee40665af1afae8f67cc3f)](https://sonar.beatrice.wtf/dashboard?id=mind-overflow_HidekoBot_AYWyYHsvX-1Ma0D4pJ59) 
[![Security Rating](https://sonar.beatrice.wtf/api/project_badges/measure?project=mind-overflow_HidekoBot_AYWyYHsvX-1Ma0D4pJ59&metric=security_rating&token=0a63c149148555d6d2ee40665af1afae8f67cc3f)](https://sonar.beatrice.wtf/dashboard?id=mind-overflow_HidekoBot_AYWyYHsvX-1Ma0D4pJ59)
  
Hideko is a general-purpose Discord bot.  
  
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

*Note: Java 16 or later is required.*
  
## Initial setup  
  
Run the startup command once. The bot will generate a `config.yml` file in the directory you were when you ran it.  
  
Edit the configuration file and set all values according to your needs.  
  
Save the file and start the bot again. If there are no issues, everything will load and it will print an
invite-link in your console. Click on the link to add your bot to any server with the correct permissions
already set-up. The bot supports both slash commands and message commands, with prefix `hideko`. Most
commands support both systems, but some of them are limited in one way or another.  
  
The bot currently supports SQLite as a database backend. A database file will be created after the first boot
in the same directory that you ran it. Do not delete the database file to avoid corruption and unpredictable
behavior.