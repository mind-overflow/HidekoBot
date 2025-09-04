package wtf.beatrice.hidekobot.commands.base;

import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.LinkedList;

public class Alias
{
    private Alias()
    {
        throw new IllegalStateException("Utility class");
    }

    public static String generateNiceAliases(MessageCommand command)
    {
        LinkedList<String> aliases = command.getCommandLabels();
        StringBuilder aliasesStringBuilder = new StringBuilder();
        for (int i = 0; i < aliases.size(); i++)
        {
            aliasesStringBuilder.append("`").append(aliases.get(i)).append("`");

            if (i + 1 != aliases.size())
                aliasesStringBuilder.append(", "); // separate with comma except on last iteration
        }

        return aliasesStringBuilder.toString();
    }
}
