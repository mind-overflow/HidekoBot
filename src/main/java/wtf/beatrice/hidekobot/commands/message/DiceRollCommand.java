package wtf.beatrice.hidekobot.commands.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;
import wtf.beatrice.hidekobot.objects.Dice;
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

        @Override
        public void runCommand(MessageReceivedEvent event, String label, String[] args)
        {
            LinkedHashMap<Dice, Integer> dicesToRoll = new LinkedHashMap<Dice, Integer>();
            String diceRegex = "d[0-9]+";
            String amountRegex = "[0-9]+";

            Dice currentDice = null;
            int currentAmount;
            UUID lastPushedDice = null;

            for(String arg : args)
            {
                if(arg.matches(amountRegex))
                {
                    currentAmount = Integer.parseInt(arg);

                    if(currentDice == null)
                    {
                        currentDice = new Dice(6);
                    } else {
                        currentDice = new Dice(currentDice);
                    }

                    lastPushedDice = currentDice.getUUID();
                    dicesToRoll.put(currentDice, currentAmount);
                }
                else if(arg.matches(diceRegex))
                {
                    int sides = Integer.parseInt(arg.substring(1));

                    if(args.length == 1)
                    {
                        dicesToRoll.put(new Dice(sides), 1);
                    } else
                    {
                        if(currentDice != null)
                        {
                            if(lastPushedDice == null || !lastPushedDice.equals(currentDice.getUUID()))
                            {
                                dicesToRoll.put(currentDice, 1);
                                lastPushedDice = currentDice.getUUID();
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
                }
            } else
            {
                if(!lastPushedDice.equals(currentDice.getUUID()))
                {
                    dicesToRoll.put(new Dice(currentDice), 1);
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

                message.append(dice.getValue());

                total += dice.getValue();
            }

            message.append("\n\n").append("**Total**: ").append(total);

            event.getMessage().reply(message.toString()).queue();
        }

}
