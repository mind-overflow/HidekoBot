package wtf.beatrice.hidekobot.objects;

import net.dv8tion.jda.api.entities.User;

public class TriviaScore
{

    private final User user;
    private int score = 0;

    public TriviaScore(User user)
    {
        this.user = user;
    }

    public void changeScore(int add)
    {
        score += add;
    }

    public int getScore() { return score; }

    public User getUser() { return user; }

    @Override
    public String toString()
    {
        return "[" + user.getAsTag() + "," + score + "]";
    }

}
