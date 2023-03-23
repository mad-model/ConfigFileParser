package at.mad_mosel;

import java.util.*;

public class Configuration {
    static List<Object> configurations = new LinkedList<>();
    static HashMap<String, Configuration> configurationMap = new HashMap<>();

    private String key;
    private String value;
    private Set<String> allowedRegexes = new HashSet<>();
    String comment = "";

    Configuration(String key, String value, String comment, String... allowedRegexes) {
        if (comment != null) this.comment = comment;
        if (allowedRegexes == null || allowedRegexes.length == 0) {
            this.allowedRegexes.add(".*");
        } else {
            this.allowedRegexes.addAll(Arrays.stream(allowedRegexes).toList());
        }
        setKey(key);
        setValue(value);
        configurations.add(this);
        configurationMap.put(key, this);
    }

    public void setKey(String key) throws IllegalArgumentException {
        if (key == null || key.equals("")) throw new IllegalArgumentException("key null or empty");
        for (Object conf : configurations) {
            if (!(conf instanceof Configuration)) continue;
            if (key.equals(((Configuration) conf).key)) throw new IllegalArgumentException("Duplicated key : " + key);
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
        if (value == null) value = "";
        for (String regex : allowedRegexes) {
            if (value.matches(regex)) {
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Value not allowed!");
        this.value = value;
    }

    /**
     * Sets value and allowedRegex. If no allowedRegexes are specified,
     * the regex [^;]* is set.
     * @param value current value of configuration
     * @return true on success
     */
    public boolean setAllowedValues(String value, String... allowedRegex) {
        if (allowedRegex == null || allowedRegex.length == 0) {
            this.value = value;
            this.allowedRegexes = new HashSet<>();
            this.allowedRegexes.add(".*");
        }
        boolean found = false;
        for (String v : allowedRegex) {
            if(value.matches(v)) {
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Value violates constraints!");
        this.value = value;
        this.allowedRegexes = new HashSet<>();
        this.allowedRegexes.addAll(allowedRegexes);
        return false;
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
