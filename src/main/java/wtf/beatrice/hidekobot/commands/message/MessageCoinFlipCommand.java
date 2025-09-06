package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wtf.beatrice.hidekobot.commands.base.CoinFlip;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class MessageCoinFlipCommand implements MessageCommand
{

    private final CoinFlip coinFlip;

    public MessageCoinFlipCommand(@Autowired CoinFlip coinFlip)
    {
        this.coinFlip = coinFlip;
    }

    @Override
    public LinkedList<String> getCommandLabels()
    {
        return new LinkedList<>(Arrays.asList("coinflip", "flip", "flipcoin"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions()
    {
        return null; // null because it can be used anywhere
    }

    @Override
    public boolean passRawArgs()
    {
        return false;
    }

    @NotNull
    @Override
    public String getDescription()
    {
        return "Flip a coin.";
    }

    @Nullable
    @Override
    public String getUsage()
    {
        return null;
    }

    @NotNull
    @Override
    public CommandCategory getCategory()
    {
        return CommandCategory.FUN;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {

        // perform coin flip
        event.getMessage().reply(coinFlip.genRandom())
                .addActionRow(coinFlip.getReflipButton())
                .queue((message) ->
                {
                    // set the command as expiring and restrict it to the user who ran it
                    coinFlip.trackAndRestrict(message, event.getAuthor());
                }, (error) -> {
                });
    }
}
