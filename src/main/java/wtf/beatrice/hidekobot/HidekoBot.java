package wtf.beatrice.hidekobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class HidekoBot
{

    public static void main(String[] args)
    {
        try
        {
            JDA jda = JDABuilder.createDefault("token").build();
        } catch (LoginException e)
        {
            throw new RuntimeException(e);
        }
    }
}
