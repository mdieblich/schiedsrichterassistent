package com.dieblich.handball.schiedsrichterassistent.calendar;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpielTerminTest {
    protected void assertEntryIs(String entry, String expected, String calendarEvent) {
        Optional<String> actualValue = findValueOf(entry, calendarEvent);
        assertTrue(actualValue.isPresent(), "Entry <" + entry + "> not found in <" + calendarEvent + ">");
        assertEquals(expected, actualValue.get(), "Full Event: " + calendarEvent);
    }

    private Optional<String> findValueOf(String keyWord, String calendarEvent) {
        String[] lines = calendarEvent.split(System.lineSeparator());
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith(keyWord + ":")) {
                StringBuilder value = new StringBuilder(line.substring(keyWord.length() + 1));
                while (i + 1 < lines.length && lines[i + 1].startsWith(" ")) {
                    // A value con continue in the next lines
                    i++;
                    // but remove the preceding space
                    value.append(lines[i].substring(1));
                }
                return Optional.of(value.toString());
            }
        }
        return Optional.empty();
    }
}
