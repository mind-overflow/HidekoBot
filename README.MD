# HidekoBot  
  
Hideko is a general-purpose Discord bot.  
  
## Startup
Download a prebuilt JAR file or build it from source, then run it with:
```bash
java -jar HidekoBot.jar <botToken> [additional parameters]
```
Where `HidekoBot.jar` is the executable archive and `<botToken>` is your bot token passed as an argument.  
  
Additionally available parameters are:
  - **verbose**: log every message that the bot receives, plus additional debugging messages. Very spammy and performance heavy.
  - **refresh**: force refresh the bot's commands. 

*Note: Java 16 or later is required.*
  
## Initial setup  
  
After successfully starting the bot up, it will print an invite-link in your console. Click on the link to add your bot
to any server with the correct permissions already set-up.