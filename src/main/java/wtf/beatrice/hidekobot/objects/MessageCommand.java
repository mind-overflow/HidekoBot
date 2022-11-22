package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface MessageCommand
{

    /**
     * Get the command's label, which is used when determining if this is the correct command or not.
     *
     * @return the command label.
     */
    String getCommandName();

    /**
     * Run the command logic by parsing the event and replying accordingly.
     *
     * @param event the received message event. It should not be used for parsing message contents data as
     *              the arguments already account for it in a better way.
     * @param args a pre-formatted list of arguments, excluding the bot prefix and the command name.
     *             This is useful because command logic won't have to change in case the bot prefix is changed,
     *             removed, or we switch to another method of triggering commands (ping, trigger words, ...).
     */
    void runCommand(MessageReceivedEvent event, String[] args);
}
