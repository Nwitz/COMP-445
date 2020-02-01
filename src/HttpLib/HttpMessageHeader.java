package HttpLib;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpMessageHeader {

    private HashMap<String, String> _entries = new HashMap<>();

    void ParseLine(String headerLine) throws HttpFormatException {
        Pattern headerReg = Pattern.compile("(\\w+)\\s*:\\s*(\\w+)");
        Matcher regMatcher = headerReg.matcher(headerLine);

        if (regMatcher.find()) {
            String key = regMatcher.group(0);
            if(!_entries.containsKey(key))
                _entries.remove(key);

            _entries.put(key, regMatcher.group(1));
        }else {
            throw new HttpFormatException("Header entry not well formatted.");
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

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        _entries.forEach((key, value) -> stringBuilder.append(key).append(": ").append(value).append("\r\n"));

        return stringBuilder.toString();
    }

}
