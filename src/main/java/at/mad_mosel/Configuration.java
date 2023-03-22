package at.mad_mosel;

import java.util.*;

public class Configuration {
    protected static List<Configuration> configurations = new LinkedList<>();
    protected static HashMap<String, Configuration> configurationMap = new HashMap<>();

    private String key;
    private String value;
    private Set<String> allowedRegexes = new HashSet<>();

    public Configuration(String key, String value, String[] allowedRegexes) {
        this.value = value;
        this.allowedRegexes.addAll(Arrays.stream(allowedRegexes).toList());
        setKey(key);
        configurations.add(this);
        configurationMap.put(key,this);
    }

    public void setKey(String key) throws IllegalArgumentException {
        if (key == null || key.equals("")) throw new IllegalArgumentException("key null or empty");
        for (Configuration conf : configurations) {
            if (key.equals(conf.key)) throw new IllegalArgumentException("Duplicated key : " + key);
        }
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }


    /**
     * Sets the value if it is contained in
     * allowed Values
     */
    public void setValue(String value) throws IllegalArgumentException {
        boolean found = false;
        for (String regex : allowedRegexes) {
            if (value.matches(regex)) {
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Value not allowed!");
        this.value = value;
    }

    public boolean addAllowedValue(String value) {
        if (this.allowedRegexes.contains(value)) return false;
        this.allowedRegexes.add(value);
        return true;
    }

    public boolean removeAllowedValue(String value) {
        return this.allowedRegexes.remove(value);
    }


    public String allowedValuesToString() {
        if (allowedRegexes == null || allowedRegexes.size() == 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (String val : allowedRegexes) stringBuilder.append(val).append(';');
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        if (key == null) key = "";
        if (value == null) value = "";
        return key + ";" + value + ";{" + allowedValuesToString() + "}";
    }
}
