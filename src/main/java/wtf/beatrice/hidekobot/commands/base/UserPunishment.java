package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.apache.commons.lang3.ArrayUtils;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.objects.MessageResponse;
import wtf.beatrice.hidekobot.util.FormatUtil;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserPunishment
{

    private UserPunishment()
    {
        throw new IllegalStateException("Utility class");
    }

    private static final Duration maxTimeoutDuration = Duration.of(28, ChronoUnit.DAYS);
    private static final Duration minTimeoutDuration = Duration.of(30, ChronoUnit.SECONDS);

    public static void handle(SlashCommandInteractionEvent event, PunishmentType punishmentType)
    {
        // this might take a sec
        event.deferReply().queue();

        User targetUser = null;

        OptionMapping targetUserArg = event.getOption("target");
        if (targetUserArg != null)
        {
            targetUser = targetUserArg.getAsUser();
        }

        List<IMentionable> mentions = null;
        if (targetUser != null) mentions = new ArrayList<>(Collections.singletonList(targetUser));

        String reason = null;
        OptionMapping reasonArg = event.getOption("reason");
        if (reasonArg != null)
        {
            reason = reasonArg.getAsString();
        }

        String timeDiff = null;
        OptionMapping timeDiffArg = event.getOption("duration");
        if (timeDiffArg != null)
        {
            timeDiff = timeDiffArg.getAsString();
        }

        // todo: the following code is not great, because we are making an array and then
        // we are also recreating the string later in code. this is useless and a bit hacked on,
        // but works for now. this happened because the function was NOT written with slash commands
        // in mind, but with message commands, that send every word as a separate argument.
        // we should probably rework the it so that it works better in both scenarios.
        String[] reasonSplit = null;
        // generate the arguments array by splitting the string
        if (reason != null) reasonSplit = reason.split("\\s+");
        //prepend timediff at index 0
        if (timeDiff != null) reasonSplit = ArrayUtils.insert(0, reasonSplit, timeDiff);
        // in message-commands, the first arg would contain the user mention. since we have no one mentioned here,
        // because it's in its own argument, we just prepend an empty string. note that this makes relying on the
        // first argument BAD, because it is no longer ensured that it contains the user mention.
        if (timeDiff != null) reasonSplit = ArrayUtils.insert(0, reasonSplit, "");

        MessageResponse response = getResponse(event.getUser(),
                punishmentType,
                event.getChannel(),
                mentions,
                reasonSplit);

        if (response.embed() != null)
            event.getHook().editOriginalEmbeds(response.embed()).queue();
        else if (response.content() != null)
            event.getHook().editOriginal(response.content()).queue();
    }

    public static void handle(MessageReceivedEvent event, String[] args, PunishmentType punishmentType)
    {
        Mentions msgMentions = event.getMessage().getMentions();
        List<IMentionable> mentions = msgMentions.getMentions();

        MessageResponse response = getResponse(event.getAuthor(),
                punishmentType,
                event.getChannel(),
                mentions,
                args);

        if (response.embed() != null)
            event.getMessage().replyEmbeds(response.embed()).queue();
        else if (response.content() != null)
            event.getMessage().reply(response.content()).queue();
    }

    public static MessageResponse getResponse(User author,
                                              PunishmentType punishmentType,
                                              MessageChannelUnion channel,
                                              List<IMentionable> mentions,
                                              String[] args)
    {
        String punishmentTypeName = punishmentType.name().toLowerCase();


        if (!(channel instanceof TextChannel))
        {
            // todo nicer looking with emojis
            return new MessageResponse("Sorry! I can't " + punishmentTypeName + " people in DMs.", null);
        }

        if (mentions == null || mentions.isEmpty())
        {
            // todo nicer looking with emojis
            return new MessageResponse("You have to tell me who to " + punishmentTypeName + "!", null);
        }

        String mentionedId = mentions.get(0).getId();
        User mentioned = null;

        try
        {
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

        if (args != null && args.length > startingPoint)
        {
            for (int i = startingPoint; i < args.length; i++)
            {
                String arg = args[i];
                reasonBuilder.append(arg);

                if (i + 1 != arg.length())
                    reasonBuilder.append(" "); // separate args with a space except on last iteration.
            }

            reason = reasonBuilder.toString();
        }

        if (mentioned == null)
        {
            // todo nicer looking with emojis
            return new MessageResponse("I can't " + punishmentTypeName + " that user!", null);
        }

        Guild guild = ((TextChannel) channel).getGuild();
        Duration duration = null;

        AuditableRestAction<Void> punishmentAction = null;
        boolean impossible = false;

        try
        {
            switch (punishmentType)
            {
                case BAN -> punishmentAction = guild.ban(mentioned, 0, TimeUnit.SECONDS);
                case KICK -> punishmentAction = guild.kick(mentioned);
                case TIMEOUT ->
                {
                    // Ensure a duration argument is provided at index 1 (after mention/user)
                    if (args == null || args.length <= 1)
                    {
                        return new MessageResponse("Sorry, but the specified duration is invalid!", null);
                    }

                    String durationStr = args[1];
                    duration = FormatUtil.parseDuration(durationStr);

                    boolean isDurationValid = true;

                    if (duration == null) isDurationValid = false;
                    else
                    {
                        if (duration.compareTo(maxTimeoutDuration) > 0) isDurationValid = false;
                        if (minTimeoutDuration.compareTo(duration) > 0) isDurationValid = false;
                    }

                    if (!isDurationValid)
                    {
                        // todo nicer looking with emojis
                        return new MessageResponse("Sorry, but the specified duration is invalid!", null);
                    }

                    punishmentAction = guild.timeoutFor(mentioned, duration);
                }
            }
        } catch (RuntimeException ignored)
        {
            impossible = true;
        }

        if (punishmentAction == null)
            impossible = true;

        if (impossible)
        {
            // todo nicer looking with emojis
            return new MessageResponse("Sorry, I couldn't " + punishmentTypeName + " " + mentioned.getAsMention() + "!",
                    null);
        }

        if (!reason.isEmpty() && !reasonBuilder.isEmpty())
            punishmentAction.reason("[" + author.getName() + "] " + reason);

        try
        {
            punishmentAction.complete();
        } catch (RuntimeException ignored)
        {
            // todo nicer looking with emojis
            return new MessageResponse("Sorry, I couldn't " + punishmentTypeName + " " + mentioned.getAsMention() + "!",
                    null);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setAuthor(author.getName(), null, author.getAvatarUrl());
        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setTitle("User " + punishmentType.getPastTense());

        embedBuilder.addField("\uD83D\uDC64 User", mentioned.getAsMention(), false);
        embedBuilder.addField("✂️ By", author.getAsMention(), false);
        if (duration != null)
            embedBuilder.addField("⏱️ Duration", FormatUtil.getNiceDuration(duration), false);

        if (reason.isEmpty())
            reason = "*No reason specified*";

        embedBuilder.addField("\uD83D\uDCD6 Reason", reason, false);


        return new MessageResponse(null, embedBuilder.build());

    }

    public enum PunishmentType
    {
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
