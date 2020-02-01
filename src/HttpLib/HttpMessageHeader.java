package HttpLib;

import HttpLib.Exceptions.HttpFormatException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: toString to output entire header block well formatted

public class HttpMessageHeader {

    private HashMap<String, String> _entries = new HashMap<>();

    public void parseLine(String headerLine) throws HttpFormatException {
        Pattern headerReg = Pattern.compile("(\\w+)\\s*:\\s*(\\w+)");
        Matcher regMatcher = headerReg.matcher(headerLine);

        if (regMatcher.find()) {
            String key = regMatcher.group(0);
            if(!_entries.containsKey(key))
                _entries.remove(key);

            _entries.put(key, regMatcher.group(1));
        }else {
            throw new HttpFormatException();
        }
    }

    public boolean IsValid(){
        // Check for required header field presence (version > 1.0 only)
        // Note: Not required for this assignment.
        return true;
    }

    public HashMap<String, String> GetEntries() {
        return _entries;
    }

}
