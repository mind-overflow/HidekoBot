package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.objects.MessageResponse;
import wtf.beatrice.hidekobot.util.FormatUtil;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserPunishment
{

    private final static Duration maxTimeoutDuration = Duration.of(28, ChronoUnit.DAYS);
    private final static Duration minTimeoutDuration = Duration.of(30, ChronoUnit.SECONDS);

    public static void handle(MessageReceivedEvent event, String[] args, PunishmentType punishmentType)
    {
        Mentions msgMentions = event.getMessage().getMentions();
        List<IMentionable> mentions = msgMentions.getMentions();

        MessageResponse response = getResponse(event.getAuthor(),
                punishmentType,
                event.getChannel(),
                mentions,
                args);

        if(response.embed() != null)
            event.getMessage().replyEmbeds(response.embed()).queue();
        else if(response.content() != null)
            event.getMessage().reply(response.content()).queue();
    }

    public static MessageResponse getResponse(User author,
                                              PunishmentType punishmentType,
                                              MessageChannelUnion channel,
                                              List<IMentionable> mentions,
                                              String[] args)
    {
        String punishmentTypeName = punishmentType.name().toLowerCase();


        if(!(channel instanceof TextChannel))
        {
            // todo nicer looking with emojis
            return new MessageResponse("Sorry! I can't " + punishmentTypeName + " people in DMs.", null);
        }

        if(mentions.isEmpty())
        {
            // todo nicer looking with emojis
            return new MessageResponse("You have to tell me who to " + punishmentTypeName + "!", null);
        }

        String mentionedId = mentions.get(0).getId();
        User mentioned = null;

        try {
            mentioned = HidekoBot.getAPI().retrieveUserById(mentionedId).complete();
        } catch (RuntimeException ignored)
        {
            // todo nicer looking with emojis
            return new MessageResponse("I can't " + punishmentTypeName + " that user!", null);
        }

        StringBuilder reasonBuilder = new StringBuilder();
        String reason = "";

        // some commands require an additional parameter before the reason, so in that case, we should start at 2.
        int startingPoint = punishmentType == PunishmentType.TIMEOUT ? 2 : 1;

        if(args.length > startingPoint)
        {
            for(int i = startingPoint; i < args.length; i++)
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
            return new MessageResponse("I can't " + punishmentTypeName + " that user!", null);
        }

        Guild guild = ((TextChannel) channel).getGuild();
        Duration duration = null;

        AuditableRestAction<Void> punishmentAction = null;

        try {
            switch (punishmentType) {
                case BAN -> punishmentAction = guild.ban(mentioned, 0, TimeUnit.SECONDS);
                case KICK -> punishmentAction = guild.kick(mentioned);
                case TIMEOUT -> {
                    String durationStr = args[1];
                    duration = FormatUtil.parseDuration(durationStr);

                    boolean isDurationValid = true;

                    if(duration == null) isDurationValid = false;
                    else
                    {
                        if(duration.compareTo(maxTimeoutDuration) > 0) isDurationValid = false;
                        if(minTimeoutDuration.compareTo(duration) > 0) isDurationValid = false;
                    }

                    if(duration == null || !isDurationValid)
                    {
                        // todo nicer looking with emojis
                        return new MessageResponse("Sorry, but the specified duration is invalid!", null);
                    }

                    punishmentAction = guild.timeoutFor(mentioned, duration);
                }
            }
        } catch (RuntimeException ignored) {
            // todo nicer looking with emojis
            return new MessageResponse("Sorry, I couldn't " + punishmentTypeName + " " + mentioned.getAsMention() + "!",
                    null);
        }

        if(!reason.isEmpty() && !reasonBuilder.isEmpty()) punishmentAction.reason(reason);

        try {
            punishmentAction.complete();
        } catch (RuntimeException ignored)
        {
            // todo nicer looking with emojis
            return new MessageResponse("Sorry, I couldn't " + punishmentTypeName + " " + mentioned.getAsMention() + "!",
                    null);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor(author.getAsTag(), null, author.getAvatarUrl());
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("User " + punishmentType.getPastTense());

        embedBuilder.addField("\uD83D\uDC64 User", mentioned.getAsMention(), false);
        embedBuilder.addField("✂️ By", author.getAsMention(), false);
        if(duration != null)
            embedBuilder.addField("⏱️ Duration", FormatUtil.getNiceDuration(duration), false);

        if(reason.isEmpty())
            reason = "*No reason specified*";

        embedBuilder.addField("\uD83D\uDCD6 Reason", reason, false);


        return new MessageResponse(null, embedBuilder.build());

    }

    public enum PunishmentType {
        KICK("kicked"),
        BAN("banned"),
        TIMEOUT("timed out"),

        ;

        private final String pastTense;

        PunishmentType(String pastTense)
        {
            this.pastTense = pastTense;
        }

        public String getPastTense()
        {
            return pastTense;
        }

    }
}
