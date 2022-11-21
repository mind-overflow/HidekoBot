package wtf.beatrice.hidekobot.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import wtf.beatrice.hidekobot.Configuration;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.listeners.MessageListener;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandsUtil
{

    private static final Logger logger = new Logger(MessageListener.class);

    static List<CommandData> allCommands = new ArrayList<>()
    {{
        add(Commands.slash("avatar", "Get someone's profile picture.")
                .addOption(OptionType.USER, "user", "User you want to grab the avatar of.")
                .addOption(OptionType.INTEGER, "size", "The size of the returned image.", false, true));
        add(Commands.slash("botinfo", "Get info about the bot."));
        add(Commands.slash("die", "Stop the bot's process.")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED));
        add(Commands.slash("clear", "Clear the current channel's chat.")
                .addOption(OptionType.INTEGER, "amount", "The amount of messages to delete.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)));
        add(Commands.slash("coinflip", "Flip a coin and get head or tails."));
        add(Commands.slash("invite", "Get an invite link for the bot."));
        add(Commands.slash("ping", "Test if the bot is responsive."));
    }};

    public static void updateSlashCommands(boolean force)
    {
        JDA jdaInstance = HidekoBot.getAPI();

        // get all the already registered commands
        List<Command> registeredCommands = jdaInstance.retrieveCommands().complete();

        boolean update = false;

        if(force)
        {
            update = true;
        } else
        {

            // for each command that we have already registered...
            for(Command currRegCmd : registeredCommands)
            {
                boolean found = false;

                // iterate through all "recognized" commands
                for(CommandData cmdData : allCommands)
                {
                    // if we find the same command...
                    if(cmdData.getName().equals(currRegCmd.getName()))
                    {
                        // quit the loop since we found it.
                        found = true;
                        break;
                    }
                }

                // if no match was found, we need to send an updated command list because
                // an old command was probably removed.
                if(!found)
                {
                    update = true;

                    // quit the loop since we only need to trigger this once.
                    break;
                }
            }

            // if an update is not already queued...
            if(!update)
            {
                // for each "recognized" valid command
                for(CommandData currCmdData : allCommands)
                {
                    boolean found = false;

                    // iterate through all already registered commands.
                    for(Command cmd : registeredCommands)
                    {
                        // if this command was already registered...
                        if(cmd.getName().equals(currCmdData.getName()))
                        {
                            // quit the loop since we found a match.
                            found = true;
                            break;
                        }
                    }

                    // if no match was found, we need to send an updated command list because
                    // a new command was probably added.
                    if(!found)
                    {
                        update = true;

                        // quit the loop since we only need to trigger this once.
                        break;
                    }
                }
            }
        }

        logger.log("Found " + registeredCommands.size() + " commands.");

        if(update)
        {
            // send updated command list.
            jdaInstance.updateCommands().addCommands(allCommands).queue();
            logger.log("Commands updated. New total: " + allCommands.size() + ".");
        }

        // cache the registered commands.
        Configuration.setRegisteredCommands(jdaInstance.retrieveCommands().complete());
    }
}
