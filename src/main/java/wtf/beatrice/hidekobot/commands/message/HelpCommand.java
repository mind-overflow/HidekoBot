package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.Say;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class HelpCommand implements MessageCommand
{


    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList("help"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() { return null; }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.TOOLS;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        LinkedHashMap<CommandCategory, LinkedList<MessageCommand>> commandCategories = new LinkedHashMap<>();

        if(args.length == 0)
        {
            for(CommandCategory category : CommandCategory.values())
            {
                LinkedList<MessageCommand> commandsOfThisCategory = new LinkedList<>();
                for (MessageCommand command : Cache.getMessageCommandListener().getRegisteredCommands())
                {
                    if(command.getCategory().equals(category))
                    {
                        commandsOfThisCategory.add(command);
                    }
                }

                commandCategories.put(category, commandsOfThisCategory);
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Cache.getBotColor());
            embedBuilder.setTitle("Bot Help");

            embedBuilder.addField("General Help",
                    "Type `" + Cache.getBotPrefix() + " help [command]` to get help on a specific command." +
                            "\nYou will find a list of commands organized in categories below.",
                    false);

            for(CommandCategory category : commandCategories.keySet())
            {
                StringBuilder commandsList = new StringBuilder();
                LinkedList<MessageCommand> commandsOfThisCategory = commandCategories.get(category);

                for(int pos = 0; pos < commandsOfThisCategory.size(); pos++)
                {
                    MessageCommand command = commandsOfThisCategory.get(pos);
                    commandsList.append("`").append(command.getCommandLabels().get(0)).append("`");

                    if(pos + 1 != commandsOfThisCategory.size())
                        commandsList.append(", "); // separate with comma except on last run
                }

                String niceCategoryName = category.name().replace("_", " ");
                niceCategoryName = WordUtils.capitalizeFully(niceCategoryName);
                niceCategoryName = category.getEmoji() + " " + niceCategoryName;

                embedBuilder.addField(niceCategoryName, commandsList.toString(), false);
            }

            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
