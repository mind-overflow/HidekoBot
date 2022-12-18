package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.Permission;

public class Say
{

    public static Permission getPermission() {
        return Permission.MESSAGE_MANAGE;
    }
}
