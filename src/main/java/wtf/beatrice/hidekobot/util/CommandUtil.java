package wtf.beatrice.hidekobot.util;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import wtf.beatrice.hidekobot.Cache;

public class CommandUtil
{

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
}
