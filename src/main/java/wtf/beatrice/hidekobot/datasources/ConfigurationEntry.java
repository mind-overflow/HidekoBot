package wtf.beatrice.hidekobot.datasources;

public enum ConfigurationEntry
{

    BOT_TOKEN("bot-token", "MTAxMjUzNzI5MTMwODI4NjAyMw.GWeNuh.00000000000000000000000000000000000000"),
    BOT_OWNER_ID("bot-owner-id", 100000000000000000L),
    BOT_COLOR("bot-color", "PINK"),
    HEARTBEAT_LINK("heartbeat-link", "https://your-heartbeat-api.com/api/push/apikey?status=up&msg=OK&ping="),
    RANDOM_ORG_API_KEY("random-org-api-key", "00000000-0000-0000-0000-000000000000"),

    ;


    private String path;
    private Object defaultValue;

    ConfigurationEntry(String path, Object defaultValue)
    {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String getPath()
    {
        return path;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }
}
