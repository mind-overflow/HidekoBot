package wtf.beatrice.hidekobot.commands.base;

import net.dv8tion.jda.api.Permission;

public class Say
{

    private Say() {
        throw new IllegalStateException("Utility class");
    }

    public static Permission getPermission() {
        return Permission.MESSAGE_MANAGE;
    }
}
