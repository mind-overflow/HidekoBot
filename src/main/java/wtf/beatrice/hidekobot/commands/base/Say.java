package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.Permission;
import org.springframework.stereotype.Component;

@Component
public class Say
{
    public Permission getPermission()
    {
        return Permission.MESSAGE_MANAGE;
    }
}
