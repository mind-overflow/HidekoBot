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
import org.apache.commons.text.WordUtils;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.objects.MessageResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserPunishment
{
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
            return new MessageResponse("I can't " + punishmentTypeName + " that user!", null);
        }

        Guild guild = ((TextChannel) channel).getGuild();

        AuditableRestAction<Void> punishmentAction = null;

        try {
            switch (punishmentType) {
                case BAN -> punishmentAction = guild.ban(mentioned, 0, TimeUnit.SECONDS);
                case KICK -> punishmentAction = guild.kick(mentioned);
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

        embedBuilder.addField("\uD83D\uDC64 User", mentioned.getAsMention(), true);
        embedBuilder.addField("✂️ By", author.getAsMention(), true);

        if(reason.isEmpty())
            reason = "*No reason specified*";

        embedBuilder.addField("\uD83D\uDCD6 Reason", reason, false);


        return new MessageResponse(null, embedBuilder.build());

    }

    public enum PunishmentType {
        KICK("kicked"),
        BAN("banned"),

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
