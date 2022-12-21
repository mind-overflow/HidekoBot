package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;
import wtf.beatrice.hidekobot.objects.comparators.TriviaCategoryComparator;
import wtf.beatrice.hidekobot.objects.fun.TriviaCategory;
import wtf.beatrice.hidekobot.runnables.TriviaTask;
import wtf.beatrice.hidekobot.util.TriviaUtil;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TriviaCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList("trivia"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null;
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @NotNull
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Start a Trivia session and play with others!";
    }

    @Nullable
    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        MessageChannel channel = event.getChannel();

        if(!(channel instanceof TextChannel))
        {
            channel.sendMessage("\uD83D\uDE22 Sorry! Trivia doesn't work in DMs.").queue();
            return;
        }

        if(TriviaUtil.channelsRunningTrivia.contains(channel.getId()))
        {
            // todo nicer looking
            // todo: also what if the bot stops (database...?)
            // todo: also what if the message is already deleted
            Message err = event.getMessage().reply("Trivia is already running here!").complete();
            Cache.getTaskScheduler().schedule(() -> err.delete().queue(), 10, TimeUnit.SECONDS);
            return;
        }

        // todo null checks
        JSONObject categoriesJson = TriviaUtil.fetchJson(TriviaUtil.getCategoriesLink());
        List<TriviaCategory> categories = TriviaUtil.parseCategories(categoriesJson);
        categories.sort(new TriviaCategoryComparator());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("\uD83C\uDFB2 Trivia");
        embedBuilder.addField("\uD83D\uDCD6 Begin here",
                "Select a category from the dropdown menu to start a match!",
                false);
        embedBuilder.addField("❓ How to play",
                "A new question gets posted every few seconds." +
                        "\nIf you get it right, you earn points!" +
                        "\nIf you choose a wrong answer, you lose points." +
                        "\nIf you are unsure, you can wait without answering and your score won't change!",
                false);

        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("trivia_categories");

        for(TriviaCategory category : categories)
        {
            String name = category.categoryName();
            int id = category.categoryId();
            menuBuilder.addOption(name, String.valueOf(id));
        }

        event.getMessage().replyEmbeds(embedBuilder.build()).addActionRow(menuBuilder.build()).queue(message ->
        {
            Cache.getDatabaseSource().trackRanCommandReply(message, event.getAuthor());
            Cache.getDatabaseSource().queueDisabling(message);
        });


    }
}
