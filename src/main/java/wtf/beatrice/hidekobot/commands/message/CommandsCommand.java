package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.Collections;
import java.util.LinkedList;

public class CommandsCommand implements MessageCommand
{

    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Collections.singletonList("commands"));
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args) {

        StringBuilder commandsList = new StringBuilder();
        commandsList.append("Recognized message commands: ");

        LinkedList<MessageCommand> messageCommands = Cache.getMessageCommandListener().getRegisteredCommands();
        for(int i = 0; i < messageCommands.size(); i++)
        {
            commandsList.append("`")
                    .append(messageCommands.get(i).getCommandLabels().get(0))
                    .append("`");
            if(i+1 != messageCommands.size())
            { commandsList.append(", "); }

        }

        event.getMessage().reply(commandsList.toString()).queue();

    }
}
