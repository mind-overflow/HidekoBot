package wtf.beatrice.hidekobot.objects;

import wtf.beatrice.hidekobot.util.RandomUtil;

import java.util.UUID;

public class Dice
{
    private final int sides;
    private int value = 0;
    private final UUID uuid;

    public Dice(int sides)
    {
        this.sides = sides;
        this.uuid = UUID.randomUUID();
    }

    public Dice(Dice old)
    {
        this.sides = old.sides;
        this.value = old.value;
        this.uuid = UUID.randomUUID();
    }

    public int getValue()
    {
        return value;
    }

    public int getSides()
    {
        return sides;
    }

    public void roll()
    {
        value = RandomUtil.getRandomNumber(1, sides);
    }

    public UUID getUUID()
    {
        return uuid;
    }
}
