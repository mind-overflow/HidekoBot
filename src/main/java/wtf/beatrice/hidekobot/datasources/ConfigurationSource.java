package wtf.beatrice.hidekobot.datasources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import wtf.beatrice.hidekobot.HidekoBot;

import java.io.*;
import java.util.LinkedHashMap;

public class ConfigurationSource
{
    private final LinkedHashMap<String, Object> configurationEntries = new LinkedHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSource.class);
    private final String configFilePath;

    public ConfigurationSource(String configFilePath)
    {
        this.configFilePath = configFilePath;
    }

    public void initConfig()
    {
        // load the YAML file from the archive's resources folder
        /*
         * note: this is no longer technically a YAML file, but we are using a very similar structure
         * to what SnakeYaml does, so that it can map all entries directly to a YAML file itself.
         * we used to have a config.yml file in the "resources" folder, but that is no longer necessary.
         */
        LinkedHashMap<String, Object> internalConfigContents = new LinkedHashMap<>(); // map holding all file entries
        for(ConfigurationEntry entry : ConfigurationEntry.values())
        {
            internalConfigContents.put(entry.getPath(), entry.getDefaultValue());
        }

        if(internalConfigContents.isEmpty())
        {
            LOGGER.error("Error reading internal configuration!");
            HidekoBot.shutdown();
            return;
        }

        // check if config files exists in filesystem
        File fsConfigFile = new File(configFilePath);
        if(!fsConfigFile.exists())
        {
            // try to create config file
            try {
                if(!fsConfigFile.createNewFile())
                {
                    LOGGER.error("We tried creating a file that already exists!");
                    HidekoBot.shutdown();
                    return;
                }
            }
            catch (IOException e) {
                LOGGER.error("Error creating configuration file!", e);
                HidekoBot.shutdown();
                return;
            }
        }
        // load the YAML file from the filesystem
        Yaml fsConfigYaml = new Yaml(new SafeConstructor());
        LinkedHashMap<String, Object> fsConfigContents = null; // map holding all file entries
        try (InputStream fsConfigStream = new FileInputStream(fsConfigFile))
        { fsConfigContents = fsConfigYaml.load(fsConfigStream); }
        catch (IOException e) { LOGGER.error(e.getMessage()); }


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
                LOGGER.error(e.getMessage());
                HidekoBot.shutdown();
                return;
            }

            // finally, dump all entries to cache.
            loadConfig(filledEntries);
        } else {
            // if no key is missing, just cache all entries and values from filesystem.
            loadConfig(fsConfigContents);
        }
    }


    private void loadConfig(LinkedHashMap<String, Object> configurationEntries)
    {
        this.configurationEntries.putAll(configurationEntries);
    }
    public Object getConfigValue(ConfigurationEntry key)
    {
        return configurationEntries.get(key.getPath());
    }
}
