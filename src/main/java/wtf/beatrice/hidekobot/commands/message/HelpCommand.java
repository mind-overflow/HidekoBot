package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.commands.base.Alias;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.*;

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

    @NotNull
    @Override
    public String getDescription() {
        return "Get general help on the bot. Specify a command if you want specific help about that command.";
    }

    @Nullable
    @Override
    public String getUsage() {
        return "[command]";
    }

    @NotNull
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

            for(Map.Entry<CommandCategory, LinkedList<MessageCommand>> entry : commandCategories.entrySet())
            {
                StringBuilder commandsList = new StringBuilder();
                CommandCategory category = entry.getKey();
                LinkedList<MessageCommand> commandsOfThisCategory = entry.getValue();

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
        } else {

            String commandLabel = args[0].toLowerCase();
            MessageCommand command = Cache.getMessageCommandListener().getRegisteredCommand(commandLabel);
            if(command == null)
            {
                event.getMessage().reply("Unrecognized command: `" + commandLabel + "`!").queue(); // todo prettier
                return;
            }

            commandLabel = command.getCommandLabels().get(0);
            String usage = "`" + Cache.getBotPrefix() + " " + commandLabel;
            String internalUsage = command.getUsage();
            if(internalUsage != null) usage += " " + internalUsage;
            usage += "`";

            String aliases = Alias.generateNiceAliases(command);

            List<Permission> permissions = command.getPermissions();
            StringBuilder permissionsStringBuilder = new StringBuilder();
            if(permissions == null)
            {
                permissionsStringBuilder = new StringBuilder("Available to everyone");
            } else {
                for(int i = 0; i < permissions.size(); i++)
                {
                    Permission permission = permissions.get(i);
                    permissionsStringBuilder.append("**").append(permission.getName()).append("**");

                    if(i + 1 != permissions.size())
                        permissionsStringBuilder.append(", "); // separate with comma expect on last iteration
                }
            }

            String title = command.getCategory().getEmoji() +
                    " \"" + WordUtils.capitalizeFully(commandLabel + "\" help");

            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setColor(Cache.getBotColor());
            embedBuilder.setTitle(title);

            embedBuilder.addField("Description", command.getDescription(), false);
            embedBuilder.addField("Usage", usage, false);
            embedBuilder.addField("Aliases", aliases, false);
            embedBuilder.addField("Permissions", permissionsStringBuilder.toString(), false);

            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        }
    }
}
