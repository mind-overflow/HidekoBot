package wtf.beatrice.hidekobot.util;

import wtf.beatrice.hidekobot.services.CommandService;
import wtf.beatrice.hidekobot.services.DatabaseService;

public record Services(CommandService commandService, DatabaseService databaseService)
{
}
