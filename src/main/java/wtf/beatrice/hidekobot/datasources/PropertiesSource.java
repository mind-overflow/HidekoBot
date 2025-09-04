package wtf.beatrice.hidekobot.datasources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.beatrice.hidekobot.HidekoBot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesSource
{

    private Properties properties = null;
    private final String fileName = "default.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesSource.class);

    public void load()
    {
        properties = new Properties();

        try (InputStream internalPropertiesStream = getClass()
                .getClassLoader()
                .getResourceAsStream(fileName))
        {
            properties.load(internalPropertiesStream);

        } catch (IOException e)
        {
            LOGGER.error(e.getMessage());
            HidekoBot.shutdown();
            return;
        }
    }

    public String getProperty(String property)
    {
        return properties == null ? "" : properties.getProperty(property);
    }
}
