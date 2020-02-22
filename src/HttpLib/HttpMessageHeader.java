package HttpLib;

import HttpLib.Exceptions.HttpFormatException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The HTTP message header object that allows parsing to retrieves
 * header entry information and stores it in a HashMap.
 */
public class HttpMessageHeader {

    private HashMap<String, String> _entries = new HashMap<>();

    public void parseLine(String headerLine) throws HttpFormatException {
        Pattern headerReg = Pattern.compile("([\\w-]+)\\s*:\\s*(.+)");
        Matcher regMatcher = headerReg.matcher(headerLine);

        if (regMatcher.find()) {
            addEntry(regMatcher.group(1), regMatcher.group(2));
        } else {
            throw new HttpFormatException("Header entry not well formatted.");
        }
    }

    public void addEntry(String key, String value){
        // Will replace if exists
        _entries.remove(key);
        _entries.put(key, value);
    }

    public boolean isValid() {
        // Check for required header field presence (version > 1.0 only)
        // Note: Not required for this assignment.
        return true;
    }

    public HashMap<String, String> GetEntries() {
        return _entries;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        _entries.forEach((key, value) -> stringBuilder.append(key).append(": ").append(value).append("\r\n"));

        return stringBuilder.toString();
    }

}
