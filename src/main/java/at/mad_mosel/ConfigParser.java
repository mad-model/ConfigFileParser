package at.mad_mosel;


import java.io.*;
import java.util.*;

/**
 * Read and Write Configurations to File
 * Format:
 * # comment
 * key;value;{allowed1;...;allowedN} all these characters are ignored
 * default path is user.dir/config.cfg
 */
public class ConfigParser {
    private static final String DEFAULT_PATH = System.getProperty("user.dir") + "/config.conf";

    private File configFile;

    /**
     * reads from config file
     */
    public ConfigParser(String pathConfigFile) {
        configFile = new File(pathConfigFile);
    }

    /**
     * tries to read from ~/config.conf
     *
     * @param create creates file if not found?
     */
    public ConfigParser(boolean create) {
        configFile = new File(DEFAULT_PATH);
    }

    public String[] getKeys() {
        return Configuration.configurationMap.keySet().toArray(new String[0]);
    }

    public Configuration getConfiguration(String key) {
        return Configuration.configurationMap.get(key);
    }

    public String getConfigurationValue(String key) {
        return Configuration.configurationMap.get(key).getValue();
    }

    public void saveConfigs() {
        writeFile();
    }

    public ConfigParser addConfiguration(String key, String value, String... allowed) {
        new Configuration(key, value, null, allowed);
        return this;
    }

    public ConfigParser addConfiguration(String key) {
        new Configuration(key, null, null);
        return this;
    }

    /**
     * Checks if there are Configurations for keys
     * @return true if all the keys are found
     */
    public boolean containsKeys(String... keys) {
        if (keys == null || keys.length == 0) return true;
        for (String key : keys) if (!Configuration.configurationMap.containsKey(key)) return false;
        return true;
    }


    public boolean readFile() {
        try {
            if (!configFile.exists()) return false;

            Scanner fileReader = new Scanner(configFile);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                if (line.matches("#.*")) {
                    Configuration.configurations.add(line);
                    continue;
                }
                if (!line.matches("\\p{Alnum}+;[^;]*;\\{([^;]+;)*([^;]+)*\\}.*"))
                    throw new IllegalStateException("Illegal config file format!");
                String[] cutComment = line.split("#");

                String[] values = new String[3];
                Arrays.fill(values, "");
                boolean stop = false, hitInner = false;
                for (int i = 0, pointTo = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    switch (c) {
                        case ';':
                            if (!hitInner) pointTo++;
                            else values[pointTo] += c;
                            break;
                        case '{':
                            hitInner = true;
                            break;
                        case '}':
                            stop = true;
                            break;
                        default:
                            values[pointTo] += c;
                            break;
                    }
                    if (stop) break;
                }
                String comment = "";
                if (cutComment.length == 2) comment = cutComment[1];
                new Configuration(values[0], values[1], comment, values[2].split(";"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return true;
    }

    private void writeFile() {
        try {
//            if (!configFile.createNewFile()) throw new IOException("File not created!");
            FileWriter fw = new FileWriter(configFile);
            BufferedWriter bw = new BufferedWriter(fw);
            for (Object configuration : Configuration.configurations) {
                bw.write(configuration.toString());
                if (configuration instanceof Configuration && ((Configuration) configuration).comment != null)
                    bw.write(((Configuration) configuration).comment);
                bw.write('\n');
            }
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
