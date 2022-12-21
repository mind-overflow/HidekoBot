package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import wtf.beatrice.hidekobot.Cache;
import wtf.beatrice.hidekobot.objects.fun.Dice;
import wtf.beatrice.hidekobot.objects.MessageResponse;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

public class DiceRoll
{
    public static MessageResponse buildResponse(User author, String[] args)
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
                return new MessageResponse("Too many total rolls!", null);
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
                    return new MessageResponse("Too many rolls (`" + currentAmount + "`)!", null);
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
                    return new MessageResponse("Too many sides (`" + sides + "`)!", null);
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

        // in case no dice was specified (or invalid), roll a standard 6-sided dice.
        if(dicesToRoll.isEmpty())
        {
            Dice standardDice = new Dice(6);
            dicesToRoll.put(standardDice, 1);
            totalRolls = 1;
        }

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
        embedBuilder.setAuthor(author.getAsTag(), null, author.getAvatarUrl());
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
            return new MessageResponse("Too many rolls!", null);
        }

        embedBuilder.addField("\uD83C\uDFB2 Rolls", message.toString(), false);

        String rolls = totalRolls == 1 ? "roll" : "rolls";

        embedBuilder.addField("✨ Total", totalRolls + " " + rolls + ": " + total, false);

        return new MessageResponse(null, embedBuilder.build());
    }
}
