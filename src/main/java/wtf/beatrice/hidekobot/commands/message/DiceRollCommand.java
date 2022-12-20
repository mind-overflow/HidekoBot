package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.Dice;
import wtf.beatrice.hidekobot.objects.commands.CommandCategory;
import wtf.beatrice.hidekobot.objects.commands.MessageCommand;

import java.util.*;

public class DiceRollCommand implements MessageCommand
{
    @Override
    public LinkedList<String> getCommandLabels() {
        return new LinkedList<>(Arrays.asList("diceroll", "droll", "roll"));
    }

    @Nullable
    @Override
    public List<Permission> getPermissions() {
        return null; // anyone can use it
    }

    @Override
    public boolean passRawArgs() {
        return false;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Roll dice. You can roll multiple dice at the same time." +
                "\nExamples:" +
                "\n - `d8 10` to roll an 8-sided die 10 times." +
                "\n - `d12 3 d5 10` to roll a 12-sided die 3 times, and then a 5-sided die 10 times." +
                "\n - `30` to roll a standard 6-sided die 30 times." +
                "\n - `d10` to roll a 10-sided die once.";
    }

    @Nullable
    @Override
    public String getUsage() {
        return "[dice size] [rolls]";
    }

    @NotNull
    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public void runCommand(MessageReceivedEvent event, String label, String[] args)
    {
        LinkedHashMap<Dice, Integer> dicesToRoll = new LinkedHashMap<>();
        String diceRegex = "d[0-9]+";
        String amountRegex = "[0-9]+";

        Dice currentDice = null;
        int currentAmount;
        UUID lastPushedDice = null;
        int totalRolls = 0;

        for(String arg : args)
        {
            if(totalRolls > 200)
            {
                event.getMessage().reply("Too many total rolls!").queue();
                return;
            }

            if(arg.matches(amountRegex))
            {
                currentAmount = Integer.parseInt(arg);

                if(currentDice == null)
                {
                    currentDice = new Dice(6);
                } else {
                    currentDice = new Dice(currentDice);
                }

                if(currentAmount > 100)
                {
                    event.getMessage().reply("Too many rolls (`" + currentAmount + "`)!").queue();
                    return;
                }

                lastPushedDice = currentDice.getUUID();
                dicesToRoll.put(currentDice, currentAmount);
                totalRolls += currentAmount;
            }
            else if(arg.matches(diceRegex))
            {
                int sides = Integer.parseInt(arg.substring(1));

                if(sides > 10000)
                {
                    event.getMessage().reply("Too many sides (`" + sides + "`)!").queue();
                    return;
                }

                if(args.length == 1)
                {
                    dicesToRoll.put(new Dice(sides), 1);
                    totalRolls++;
                } else
                {
                    if(currentDice != null)
                    {
                        if(lastPushedDice == null || !lastPushedDice.equals(currentDice.getUUID()))
                        {
                            dicesToRoll.put(currentDice, 1);
                            lastPushedDice = currentDice.getUUID();
                            totalRolls++;
                        }
                    }

                    currentDice = new Dice(sides);
                }
            }
        }

        if(lastPushedDice == null)
        {
            if(currentDice != null)
            {
                dicesToRoll.put(currentDice, 1);
                totalRolls++;
            }
        } else
        {
            if(!lastPushedDice.equals(currentDice.getUUID()))
            {
                dicesToRoll.put(new Dice(currentDice), 1);
                totalRolls++;
            }
        }

        LinkedList<Dice> rolledDices = new LinkedList<>();

        for(Dice dice : dicesToRoll.keySet())
        {
            for(int roll = 0; roll < dicesToRoll.get(dice); roll++)
            {
                dice.roll();
                rolledDices.add(new Dice(dice));
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Cache.getBotColor());
        embedBuilder.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        embedBuilder.setTitle("Dice Roll");

        StringBuilder message = new StringBuilder();
        int total = 0;

        int previousDiceSides = 0;
        for (Dice dice : rolledDices) {
            int diceSize = dice.getSides();

            if (previousDiceSides != diceSize) {
                message.append("\nd").append(diceSize).append(": ");
                previousDiceSides = diceSize;
            } else if (previousDiceSides != 0) {
                message.append(", ");
            }

            message.append("`").append(dice.getValue()).append("`");

            total += dice.getValue();
        }

        // discord doesn't allow embed fields to be longer than 1024 and errors out
        if(message.length() > 1024)
        {
            event.getMessage().reply("Too many rolls!").queue();
            return;
        }

        embedBuilder.addField("\uD83C\uDFB2 Rolls", message.toString(), false);

        embedBuilder.addField("✨ Total", totalRolls + " rolls: " + total, false);


        event.getMessage().replyEmbeds(embedBuilder.build()).queue();
    }

}
