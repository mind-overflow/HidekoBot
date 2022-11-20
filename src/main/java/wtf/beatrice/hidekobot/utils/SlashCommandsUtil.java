package wtf.beatrice.hidekobot.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.listeners.MessageListener;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandsUtil
{

    private static final Logger logger = new Logger(MessageListener.class);

    static List<CommandData> allCommands = new ArrayList<>()
    {{
        add(Commands.slash("ping", "Test if the bot is responsive."));
        add(Commands.slash("coinflip", "Flip a coin and get head or tails."));
        add(Commands.slash("clear", "Clear the current channel's chat.")
                .addOption(OptionType.INTEGER, "amount", "The amount of messages to delete.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)));
    }};

    public static void updateSlashCommands()
    {
        JDA jdaInstance = HidekoBot.getAPI();

        // get all the already registered commands
        List<Command> registeredCommands = jdaInstance.retrieveCommands().complete();

        boolean update = false;

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

        logger.log("Found " + registeredCommands.size() + " commands.");

        if(update)
        {
            // send updated command list.
            jdaInstance.updateCommands().addCommands(allCommands).queue();
            logger.log("Commands updated. New total: " + allCommands.size() + ".");
        }



        /*
        List<CommandData> toAdd = new ArrayList<>();
        List<Command> toDelete = new ArrayList<>();

        List<Command> registeredCommands = jdaInstance.retrieveCommands().complete();

        // for each command that we have already registered...
        for(Command currRegCmd : registeredCommands)
        {
            // queue it for removal.
            boolean toRemove = true;

            // iterate through all "recognized" commands
            for(CommandData cmdData : allCommands)
            {
                // if we find the same command...
                if(cmdData.getName().equals(currRegCmd.getName()))
                {
                    // then don't remove it
                    toRemove = false;
                    // and quit the loop since we found it.
                    break;
                }
            }

            // if no match was found, queue this command for removal.
            if(toRemove) toDelete.add(currRegCmd);

        }

        // for each "recognized" valid command
        for(CommandData currCmdData : allCommands)
        {
            // queue it for registration.
            boolean toRegister = true;

            // iterate through all already registered commands.
            for(Command cmd : registeredCommands)
            {
                // if this command was already registered...
                if(cmd.getName().equals(currCmdData.getName()))
                {
                    // flag that we don't need to register it
                    toRegister = false;
                    // and quit the loop since we found a match.
                    break;
                }
            }

            // if no match was found, queue this command for registration.
            if(toRegister) toAdd.add(currCmdData);
        }

        logger.log("Found " + registeredCommands.size() + " commands.");

        // remove all commands queued for removal.
        for(Command cmd : toDelete)
        {
            jdaInstance.deleteCommandById(cmd.getId()).queue();
        }

        logger.log("Deleted " + toDelete.size() + " commands.");

        // register all new commands.
        jdaInstance.updateCommands().addCommands(toAdd).queue();
        logger.log("Registered " + toAdd.size() + " new commands.");


         */
    }
}
