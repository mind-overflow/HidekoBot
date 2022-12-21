package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.commands.base.DiceRoll;
import wtf.beatrice.hidekobot.objects.MessageResponse;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.*;

public class DiceRollCommand implements MessageCommand
{
    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Arrays.asList("diceroll", "droll", "roll"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null; // anyone can use it
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Roll dice. You can roll multiple dice at the same time." +
                "\nExamples:" +
                "\n - `d8 10` to roll an 8-sided die 10 times." +
                "\n - `d12 3 d5 10` to roll a 12-sided die 3 times, and then a 5-sided die 10 times." +
                "\n - `30` to roll a standard 6-sided die 30 times." +
                "\n - `d10` to roll a 10-sided die once.";
    }

    @Nullable
    @Override
    public String getUsage() {
        return "[dice size] [rolls]";
    }

    @NotNull
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {

        MessageResponse response = DiceRoll.buildResponse(event.getAuthor(), args);

        if(response.content() != null)
        {
            event.getMessage().reply(response.content()).queue();
        } else if(response.embed() != null)
        {
            event.getMessage().replyEmbeds(response.embed()).queue();
        }

    }

}
