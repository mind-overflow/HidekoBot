package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class KickCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList("kick"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return new ArrayList<Permission>(Collections.singletonList(Permission.KICK_MEMBERS));
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @NotNull
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MODERATION;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Kick the mentioned user.";
    }

    @Nullable
    @Override
    public String getUsage() {
        return "<mentioned user> [reason]";
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        if(!(event.getChannel() instanceof TextChannel))
        {
            // todo nicer looking with emojis
            event.getMessage().reply("Sorry! I can't kick people in DMs.").queue();
            return;
        }

        Mentions msgMentions = event.getMessage().getMentions();
        List<IMentionable> mentions = msgMentions.getMentions();

        if(args.length == 0 ||mentions.isEmpty())
        {
            // todo nicer looking with emojis
            event.getMessage().reply("You have to tell me who to kick!").queue();
            return;
        }

        String mentionedId = mentions.get(0).getId();
        User mentioned = null;

        try {
             mentioned = HidekoBot.getAPI().retrieveUserById(mentionedId).complete();
        } catch (Exception e)
        {
            // todo nicer looking with emojis
            event.getMessage().reply("I can't kick that user!").queue();
            return;
        }

        StringBuilder reasonBuilder = new StringBuilder();;
        String reason = "";
        if(args.length > 1)
        {
            for(int i = 1; i < args.length; i++)
            {
                String arg = args[i];
                reasonBuilder.append(arg);

                if(i + 1 != arg.length())
                    reasonBuilder.append(" "); // separate args with a space except on last iteration.
            }

            reason = reasonBuilder.toString();
        }

        if(mentioned == null)
        {
            // todo nicer looking with emojis
            event.getMessage().reply("I can't kick that user!").queue();
            return;
        }

        AuditableRestAction<Void> kickAction = event.getGuild().kick(mentioned);
        if(!reason.isEmpty() && !reasonBuilder.isEmpty()) kickAction.reason(reason);

        final String finalReason = reason;
        final IMentionable finalMentioned = mentioned;

        kickAction.queue(success -> {
            String embedReason = finalReason;

            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setAuthor(event.getAuthor().getAsTag(), null,event.getAuthor().getAvatarUrl());
            embedBuilder.setColor(Cache.getBotColor());
            embedBuilder.setTitle("User kicked");

            embedBuilder.addField("\uD83D\uDC64 User", finalMentioned.getAsMention(), true);
            embedBuilder.addField("✂️ By", event.getAuthor().getAsMention(), true);

            if(embedReason.isEmpty())
                embedReason = "*No reason specified*";

            embedBuilder.addField("\uD83D\uDCD6 Reason", embedReason, false);


            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        }, throwable -> {
            // todo nicer looking with emojis
            event.getMessage().reply("Sorry, I couldn't kick " + finalMentioned.getAsMention() + "!").queue();
        });

    }
}
