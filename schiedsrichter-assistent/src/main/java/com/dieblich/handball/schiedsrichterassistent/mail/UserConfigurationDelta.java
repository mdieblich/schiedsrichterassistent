package com.dieblich.handball.schiedsrichterassistent.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UserConfigurationDelta {
    private Map<String, ValueChange> changes = new HashMap<>();

    public static UserConfigurationDelta createDelta(Properties old, Map<String, String> updates){
        UserConfigurationDelta delta = new UserConfigurationDelta();
        for (Map.Entry<String, String> update: updates.entrySet()) {
            String key = update.getKey();
            String oldValue = old.getProperty(key); // Nullable!
            String updatedValue = update.getValue();
            delta.changes.put(key, new ValueChange(oldValue, updatedValue));
        }
        return delta;
    }
}
