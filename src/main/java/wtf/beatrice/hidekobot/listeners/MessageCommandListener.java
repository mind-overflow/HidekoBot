package wtf.beatrice.hidekobot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;
import wtf.beatrice.hidekobot.objects.comparators.MessageCommandAliasesComparator;

import java.util.*;

public class MessageCommandListener extends ListenerAdapter
{

    // map storing command labels and command object alphabetically.
    private final TreeMap<LinkedList<String>, MessageCommand> registeredCommands =
            new TreeMap<>(new MessageCommandAliasesComparator());

    // map commands and their categories.
    // this is not strictly needed but it's better to have it so we avoid looping every time we need to check the cat.
    LinkedHashMap<CommandCategory, LinkedList<MessageCommand>> commandCategories = new LinkedHashMap<>();

    private static final String COMMAND_REGEX = "(?i)^(hideko|hde)\\b";
    // (?i) -> case insensitive flag
    // ^ -> start of string (not in middle of a sentence)
    // \b -> the word has to end here


    public void registerCommand(MessageCommand command)
    {
        registeredCommands.put(command.getCommandLabels(), command);
    }

    public MessageCommand getRegisteredCommand(String label)
    {
        for(Map.Entry<LinkedList<String>, MessageCommand> entry : registeredCommands.entrySet())
        {
            LinkedList<String> aliases = entry.getKey();

            for(String currentAlias : aliases)
            {
                if(label.equals(currentAlias))
                { return entry.getValue(); }
            }
        }

        return null;
    }

    public LinkedList<MessageCommand> getRegisteredCommands()
    { return new LinkedList<>(registeredCommands.values()); }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        // check if a bot is sending this message, and ignore it
        if(event.getAuthor().isBot()) return;

        // warning: we are getting the RAW value of the message content, not the DISPLAY value!
        String eventMessage = event.getMessage().getContentRaw();

        // check if the sent message matches the bot activation regex (prefix, name, ...)
        if(!eventMessage.toLowerCase().matches("(?s)" + COMMAND_REGEX + ".*"))
            return;

        // generate args from the string
        String argsString = eventMessage.replaceAll(COMMAND_REGEX + "\\s*", "");


        // if no args were specified apart from the bot prefix
        // note: we can't check argsRaw's size because String.split returns an array of size 1 if no match is found,
        // and that element is the whole string passed as a single argument, which would be empty in this case
        // (or contain text in other cases like "string split ," if the passed text doesn't contain any comma ->
        // it will be the whole text as a single element.
        if(argsString.isEmpty())
        {
            event.getMessage()
                    .reply("Hello there! ✨ Type `" + Cache.getBotPrefix() + " help` to get started!")
                    .queue();
            return;
        }

        // split all passed arguments
        String[] argsRaw = argsString.split("\\s+");

        // extract the command that the user is trying to run
        String commandLabel = argsRaw[0];
        MessageCommand commandObject = getRegisteredCommand(commandLabel);

        if(commandObject == null)
        {
            /* temporarily disabled because when people talk about the bot, it replies with this spammy message.

            event.getMessage().reply("Unrecognized command: `" + commandLabel + "`!").queue(); // todo prettier

            */
            return;
        }

        ChannelType channelType = event.getChannelType();


        // permissions check
        List<Permission> requiredPermissions = commandObject.getPermissions();
        if(requiredPermissions != null && !requiredPermissions.isEmpty())
        {
            if(channelType.isGuild()) //todo: what about forum post
            {
                Member member = event.getMember();
                GuildChannel channel = event.getGuildChannel(); //todo: what about forum post
                if(member != null && !member.hasPermission(channel, requiredPermissions))
                {
                    event.getMessage()
                            .reply("You do not have permissions to run this command!")
                            .queue(); // todo prettier
                    // todo: queue message deletion in 15 seconds or so
                    return;

                }
            }
        }

        String[] commandArgs;
        if(commandObject.passRawArgs())
        {

            // remove first argument, which is the command label
            argsString = argsString.replaceAll("^[\\S]+\\s*", "");
            // pass all other arguments as a single argument as the first array element
            commandArgs = new String[]{argsString};
        }
        else
        {
            // copy all split arguments to the array, except from the command label
            commandArgs = Arrays.copyOfRange(argsRaw, 1, argsRaw.length);
        }

        // finally run the command, in a new thread to avoid locking.
        new Thread(() -> commandObject.runCommand(event, commandLabel, commandArgs)).start();
    }
}
