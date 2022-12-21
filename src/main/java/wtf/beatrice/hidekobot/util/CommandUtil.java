package wtf.beatrice.hidekobot.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.RestAction;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.datasources.DatabaseSource;
import wtf.beatrice.hidekobot.objects.commands.SlashCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandUtil
{

    private static final Logger logger = new Logger(CommandUtil.class);

    /**
     * Function to delete a message when a user clicks the "delete" button attached to that message.
     * This will check in the database if that user ran the command originally.
     *
     * @param event the button interaction event.
     */
    public static void delete(ButtonInteractionEvent event)
    {
        // check if the user interacting is the same one who ran the command
        if (!(Cache.getDatabaseSource().isUserTrackedFor(event.getUser().getId(), event.getMessageId()))) {
            event.reply("❌ You did not run this command!").setEphemeral(true).queue();
            return;
        }

        // delete the message
        event.getInteraction().getMessage().delete().queue();
        // no need to manually untrack it from database, it will be purged on the next planned check.
    }


    /**
     * Method to update slash commands registered on Discord's side.
     * It runs automatically every time the bot starts, but only updates the commands in case differences
     * are found, unless forced.
     *
     * @param force a boolean specifying if the update should be forced even if no differences were found.
     */
    public static void updateSlashCommands(boolean force)
    {

        // populate commands list from registered commands
        List<CommandData> allCommands = new ArrayList<>();
        for(SlashCommand cmd : Cache.getSlashCommandListener().getRegisteredCommands())
        { allCommands.add(cmd.getSlashCommandData()); }

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
    }


    /**
     * Method to disable all buttons from an expired message.
     *
     * @param messageId the message id to disable.
     */
    public static void disableExpired(String messageId)
    {
        DatabaseSource databaseSource = Cache.getDatabaseSource();

        String channelId = databaseSource.getQueuedExpiringMessageChannel(messageId);

        // todo: warning, the following method + related if check are thread-locking.
        // todo: we should probably merge the two tables somehow, since they have redundant information.
        ChannelType msgChannelType = databaseSource.getTrackedMessageChannelType(messageId);

        MessageChannel textChannel = null;

        // this should never happen, but only message channels are supported.
        if(!msgChannelType.isMessage())
        {
            databaseSource.untrackExpiredMessage(messageId);
            return;
        }

        // if this is a DM
        if(!(msgChannelType.isGuild()))
        {
            String userId = databaseSource.getTrackedReplyUserId(messageId);
            User user = userId == null ? null : HidekoBot.getAPI().retrieveUserById(userId).complete();
            if(user == null)
            {
                // if user is not found, consider it expired
                // (deleted profile, or blocked the bot)
                databaseSource.untrackExpiredMessage(messageId);
                return;
            }

            textChannel = user.openPrivateChannel().complete();
        }
        else
        {
            String guildId = databaseSource.getQueuedExpiringMessageGuild(messageId);
            Guild guild = guildId == null ? null : HidekoBot.getAPI().getGuildById(guildId);
            if(guild == null)
            {
                // if guild is not found, consider it expired
                // (server was deleted or bot was kicked)
                databaseSource.untrackExpiredMessage(messageId);
                return;
            }
            textChannel = guild.getTextChannelById(channelId);
        }

        if(textChannel == null)
        {
            // if channel is not found, count it as expired
            // (channel was deleted or bot permissions restricted)
            databaseSource.untrackExpiredMessage(messageId);
            return;
        }

        RestAction<Message> retrieveAction = textChannel.retrieveMessageById(messageId);


        if(Cache.isVerbose()) logger.log("cleaning up: " + messageId);

        retrieveAction.queue(
                message -> {
                    if(message == null)
                    {
                        databaseSource.untrackExpiredMessage(messageId);
                        return;
                    }

                    List<LayoutComponent> components = message.getComponents();
                    List<LayoutComponent> newComponents = new ArrayList<>();
                    for (LayoutComponent component : components)
                    {
                        component = component.asDisabled();
                        newComponents.add(component);
                    }

                    message.editMessageComponents(newComponents).queue();
                    databaseSource.untrackExpiredMessage(messageId);
                },

                (error) -> {
                    databaseSource.untrackExpiredMessage(messageId);
                });
    }
}
