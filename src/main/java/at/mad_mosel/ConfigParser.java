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
    public ConfigParser(String pathConfigFile, boolean create) {
        configFile = new File(pathConfigFile);
        readFile(create);
    }

    /**
     * tries to read from ~/config.conf
     *
     * @param create creates file if not found?
     */
    public ConfigParser(boolean create) {
        configFile = new File(DEFAULT_PATH);
        readFile(create);
    }

    public List<Configuration> getConfigurations() {
        return Configuration.configurations;
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

    public boolean addConfiguration(Configuration configuration) {
        for (Configuration c : configurations) if(c.getKey().equals(configuration.getKey())) return false;
        configurations.add(configuration);
        Configuration.configurationMap.put(configuration.getKey(),configuration);
        return true;
    }


    private void readFile(boolean create) {
        try {
            if (!configFile.exists()) {
                if (!create) return;
                writeFile();
            }
            Scanner fileReader = new Scanner(configFile);

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                if (line.matches("#.*")) continue;
                if (!line.matches("\\p{Alnum}+;[^;]*;\\{(\\p{Alnum}+;)+\\p{Alnum}+\\}.*"))
                    throw new IllegalStateException("Illegal config file format!");
                String[] cutComment = line.split("#");
                String[] helpState = cutComment[0].split("\\{");
                String allowedValuesString = helpState[1].replaceAll("}", "");
                String[] allowedValues = allowedValuesString.split(";");

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
                new Configuration(values[0], values[1], values[2].split(";"));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile() {
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) throw new IOException("File not created!");
                FileWriter fw = new FileWriter(configFile);
                BufferedWriter bw = new BufferedWriter(fw);
                for (Configuration configuration : configurations) {
                    bw.write(configuration.toString() + '\n');
                }
                bw.flush();
                bw.close();
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Configuration> configurations = new ArrayList<>();
}
