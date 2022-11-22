package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.LinkedList;

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
     * Say if this command does its own text parsing, and tell the message listener if it should automatically
     * split all arguments in separate entries of an array, or pass everything as the first entry of that array.
     *
     * This is better instead of getting the message contents from the event, because the message listener will
     * still strip the bot prefix and command name from the args, but leave the rest untouched.
     *
     * @return the boolean being true if no parsing should be made by the command handler.
     */
    boolean passRawArgs();

    /**
     * Run the command logic by parsing the event and replying accordingly.
     *
     *
     * @param event the received message event. It should not be used for parsing message contents data as
     *              the arguments already account for it in a better way.
     *
     * @param label the command label that was used, taken from all available command aliases.
     *
     * @param args a pre-formatted list of arguments, excluding the bot prefix and the command name.
     *             This is useful because command logic won't have to change in case the bot prefix is changed,
     *             removed, or we switch to another method of triggering commands (ping, trigger words, ...).
     */
    void runCommand(MessageReceivedEvent event, String label, String[] args);
}
