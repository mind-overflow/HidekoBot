package wtf.beatrice.hidekobot.objects.commands;

public enum CommandCategory
{
    MODERATION("️\uD83D\uDC40"),
    FUN("\uD83C\uDFB2"),
    TOOLS("\uD83D\uDEE0"),

    ;

    private String emoji;

    CommandCategory(String emoji)
    {
        this.emoji = emoji;
    }

    public String getEmoji()
    {
        return emoji;
    }
}
