package wtf.beatrice.hidekobot.datasources;

import wtf.beatrice.hidekobot.HidekoBot;
import wtf.beatrice.hidekobot.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesSource
{

    private Properties properties = null;
    private final String fileName = "default.properties";
    private final Logger logger = new Logger(getClass());

    public void load()
    {
        properties = new Properties();

        try (InputStream internalPropertiesStream = getClass()
                .getClassLoader()
                .getResourceAsStream(fileName))
        {
            properties.load(internalPropertiesStream);

        }
        catch (IOException e) {
            logger.log(e.getMessage());
            HidekoBot.shutdown();
            return;
        }
    }

    public String getProperty(String property)
    { return properties == null ? "" : properties.getProperty(property); }
}
