package wtf.beatrice.hidekobot.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ConfigurationUtil
{
    private final String configFilePath;

    public ConfigurationUtil(String configFilePath)
    {
        this.configFilePath = configFilePath;
    }

    public void initConfig()
    {
        Yaml internalConfigYaml = new Yaml();
        Map<String, Object> internalConfigContents = null;

        try (InputStream internalConfigStream = getClass()
                .getClassLoader()
                .getResourceAsStream("config.yml"))
        {
            internalConfigContents = internalConfigYaml.load(internalConfigStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(internalConfigContents == null) // todo error handling
        {

            return;
        }


    }
}
