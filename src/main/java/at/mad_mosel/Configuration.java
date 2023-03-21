package at.mad_mosel;

import java.util.*;

public class Configuration {
    protected static List<Configuration> configurations = new LinkedList<>();

    private String key;
    private String value;
    private Set<String> allowedValues = new HashSet<>();

    public Configuration(String key, String value, String[] allowedValues) {
        this.value = value;
        this.allowedValues.addAll(Arrays.stream(allowedValues).toList());
        setKey(key);
        configurations.add(this);
    }

    public void setKey(String key) throws IllegalArgumentException {
        if(key == null || key.equals("")) throw new IllegalArgumentException("key null or empty");
        for (Configuration conf : configurations) {
            if (key.equals(conf.key)) throw new IllegalArgumentException("Duplicated key : " + key);
        }
        this.key = key;
    }

    public String getKey() {
        return key;
    }


    /**
     * Sets the value if it is contained in
     * allowed Values
     */
    public void setValue(String value) throws IllegalArgumentException{
        if (!allowedValues.contains(value)) throw new IllegalArgumentException("Value not allowed!");
        this.value = value;
    }

    public boolean addAllowedValue (String value) {
        if (this.allowedValues.contains(value)) return false;
        this.allowedValues.add(value);
        return true;
    }

    public boolean removeAllowedValue (String value) {
        return this.allowedValues.remove(value);
    }


    public String allowedValuesToString () {
        if (allowedValues == null || allowedValues.size() == 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (String val : allowedValues) stringBuilder.append(val).append(';');
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        if (key == null) key = "";
        if (value == null) value = "";
        return key + ";" + value + ";{" + allowedValuesToString() +"}";
    }
}
