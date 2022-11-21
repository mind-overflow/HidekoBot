package wtf.beatrice.hidekobot.utils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import wtf.beatrice.hidekobot.HidekoBot;

import java.io.*;
import java.util.LinkedHashMap;

public class ConfigurationUtil
{

    private final Logger logger;
    private final String configFilePath;

    public ConfigurationUtil(String configFilePath)
    {
        this.configFilePath = configFilePath;
        logger = new Logger(getClass());
    }

    public void initConfig()
    {
        // load the YAML file from the archive's resources folder
        Yaml internalConfigYaml = new Yaml();
        LinkedHashMap<String, Object> internalConfigContents = null; // map holding all file entries
        try (InputStream internalConfigStream = getClass()
                .getClassLoader()
                .getResourceAsStream("config.yml"))
        { internalConfigContents = internalConfigYaml.load(internalConfigStream); }
        catch (IOException e) { logger.log(e.getMessage()); }

        if(internalConfigContents == null)
        {
            logger.log("Error reading internal configuration!");
            HidekoBot.shutdown();
            return;
        }

        // check if config files exists in filesystem
        File fsConfigFile = new File(configFilePath);
        if(!fsConfigFile.exists())
        {
            // try to create config file
            try { fsConfigFile.createNewFile(); }
            catch (IOException e) {
                logger.log("Error creating configuration file!");
                logger.log(e.getMessage());
                HidekoBot.shutdown();
                return;
            }
        }
        // load the YAML file from the filesystem
        Yaml fsConfigYaml = new Yaml();
        LinkedHashMap<String, Object> fsConfigContents = null; // map holding all file entries
        try (InputStream fsConfigStream = new FileInputStream(fsConfigFile))
        { fsConfigContents = fsConfigYaml.load(fsConfigStream); }
        catch (IOException e) { logger.log(e.getMessage()); }


        if(fsConfigContents == null) // if file contents are empty or corrupted...
        {
            // "clean" them (this effectively forces a config file reset)
            fsConfigContents = new LinkedHashMap<>();
        }

        // check for missing keys
        boolean missingKeys = false;
        for(String key : internalConfigContents.keySet())
        {
            // if key is missing
            if(!fsConfigContents.containsKey(key))
            {
                // quit and flag it, as we need to complete the file with the missing ones
                missingKeys = true;
                break;
            }
        }

        // if keys are missing
        if(missingKeys)
        {
            // create a new mixed map that will take existing values from the non-missing keys
            // and fill everything else with the default values
            LinkedHashMap<String, Object> filledEntries = new LinkedHashMap<>();
            for(String key : internalConfigContents.keySet())
            {
                if(fsConfigContents.containsKey(key))
                {
                    // if the key already exists, copy the original value
                    filledEntries.put(key, fsConfigContents.get(key));
                } else {
                    // else, copy the value from the example config file
                    filledEntries.put(key, internalConfigContents.get(key));
                }
            }

            try {
                // new writer to actually write the contents to the file
                PrintWriter missingKeysWriter = new PrintWriter(fsConfigFile);

                // set yaml options to make the output prettier
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setIndent(2);
                dumperOptions.setPrettyFlow(true);
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

                // create the yaml object and dump the values to filesystem
                Yaml yaml = new Yaml(dumperOptions);
                yaml.dump(filledEntries, missingKeysWriter);
            } catch (FileNotFoundException e) {
                logger.log(e.getMessage());
                HidekoBot.shutdown();
                return;
            }

        }

    }
}
