package wtf.beatrice.hidekobot.objects.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public interface MessageCommand
{

    /**
     * Get the command's label(s), which are used when determining if this is the correct command or not.
     * The first label in the collection is considered the main command name. All other labels are considered
     * command aliases.
     *
     * @return the command label.
     */
    LinkedList<String> getCommandLabels();

    /**
     * A list of permissions required to run the command. This is preferred to checking them on your own
     * as the message listener handles it more homogeneously.
     *
     * @return the list of required permissions.
     */
    @Nullable
    List<Permission> getPermissions();

    /**
     * Say if this command does its own text parsing, and tell the message listener if it should automatically
     * split all arguments in separate entries of an array, or pass everything as the first entry of that array.
     * <p>
     * This is better instead of getting the message contents from the event, because the message listener will
     * still strip the bot prefix and command name from the args, but leave the rest untouched.
     *
     * @return the boolean being true if no parsing should be made by the command handler.
     */
    boolean passRawArgs();

    /**
     * Say what category this command belongs to.
     *
     * @return the command category.
     */
    @NotNull
    CommandCategory getCategory();

    /**
     * Say what this command does.
     *
     * @return a String explaining what this command does.
     */
    @NotNull
    String getDescription();

    /**
     * Say how people should use this command.
     *
     * @return a String explaining how to use the command, excluding the bot prefix and command name. Null if no parameter is needed
     */
    @Nullable
    String getUsage();


    /**
     * Run the command logic by parsing the event and replying accordingly.
     *
     * @param event the received message event. It should not be used for parsing message contents data as
     *              the arguments already account for it in a better way.
     * @param label the command label that was used, taken from all available command aliases.
     * @param args  a pre-formatted list of arguments, excluding the bot prefix and the command name.
     *              This is useful because command logic won't have to change in case the bot prefix is changed,
     *              removed, or we switch to another method of triggering commands (ping, trigger words, ...).
     */
    void runCommand(MessageReceivedEvent event, String label, String[] args);
}
