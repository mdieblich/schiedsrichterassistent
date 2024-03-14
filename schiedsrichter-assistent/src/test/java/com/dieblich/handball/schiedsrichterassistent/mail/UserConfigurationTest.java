package com.dieblich.handball.schiedsrichterassistent.mail;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserConfigurationTest {

    @Test
    public void create_empty() throws IOException {
        UserConfiguration config = new UserConfiguration("", "");
        assertExactContent(Map.of(), config);
    }
    @Test
    public void create_WithOneEntry() throws IOException {
        UserConfiguration config = new UserConfiguration("", "Adresse=FFF");
        assertExactContent(Map.of("Adresse", "FFF"), config);
    }

    @Test
    public void updateWith_createSingleEntry() throws IOException {
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("Adresse=ABCD");
        assertExactContent(Map.of("Adresse", "ABCD"), config);
    }
    @Test
    public void updateWith_trims() throws IOException {
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("  Adresse  =\tABCD   ");
        assertExactContent(Map.of("Adresse", "ABCD"), config);
    }

    @Test
    public void updateWith_createsTwoEntries() throws IOException {
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("""
        Adresse=ABCD
        Umziehen.DauerInMinuten=35
        """);
        assertExactContent(Map.of(
                "Adresse", "ABCD",
                "Umziehen.DauerInMinuten","35"
                ), config);
    }

    @Test
    public void updateWith_updatesEntry() throws IOException {
        UserConfiguration config = new UserConfiguration("", "Adresse=FFF");
        config.updateWith("Adresse=ABCD");
        assertExactContent(Map.of("Adresse", "ABCD"), config);
    }

    @Test
    public void updateWith_refusesUnknownEntries() throws IOException{
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("blabla=ABCD");
        assertExactContent(Map.of(), config);
    }
    @Test
    public void updateWith_ignoresTripleAssignments() throws IOException{
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("Adresse=ABCD=EFG");
        assertExactContent(Map.of(), config);
    }

    @Test
    public void updateWith_ignoresNewlines() throws IOException{
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("""
        
        Adresse=ABCD
        
        
        Umziehen.DauerInMinuten=35
        
        """);
        assertExactContent(Map.of(
                "Adresse", "ABCD",
                "Umziehen.DauerInMinuten","35"
        ), config);
    }

    @Test
    public void updateWith_stopsAtGibberish() throws IOException{
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("""
        
        Adresse=ABCD
        
        heyho!
        
        Umziehen.DauerInMinuten=35
        
        """);
        assertExactContent(Map.of(
                "Adresse", "ABCD"
        ), config);
    }

    @Test
    public void addressIsNew_false_ifNotPresent() throws IOException {
        UserConfiguration config = new UserConfiguration("", "");
        Map<String, String> update = Map.of();
        assertFalse(config.addressIsNew(update));
    }
    @Test
    public void addressIsNew_true_ifItWasNotThereBefore() throws IOException {
        UserConfiguration config = new UserConfiguration("", "");
        Map<String, String> update = Map.of(UserConfiguration.ADRESSE, "Musterstr. 5");
        assertTrue(config.addressIsNew(update));
    }
    @Test
    public void addressIsNew_false_ifTheSame() throws IOException {
        UserConfiguration config = new UserConfiguration("", UserConfiguration.ADRESSE+"=Musterstr. 5");
        Map<String, String> update = Map.of(UserConfiguration.ADRESSE, "Musterstr. 5");
        assertFalse(config.addressIsNew(update));
    }
    @Test
    public void addressIsNew_true_ifDiffers() throws IOException {
        UserConfiguration config = new UserConfiguration("", UserConfiguration.ADRESSE+"=Musterstr. 5");
        Map<String, String> update = Map.of(UserConfiguration.ADRESSE, "Musterstr. 6");
        assertTrue(config.addressIsNew(update));
    }

    private void assertExactContent(Map<String, String> expectedContent, UserConfiguration actualConfig){
        for(Map.Entry<String, String> entry:expectedContent.entrySet()){
            assertHas(entry.getKey(), entry.getValue(), actualConfig);
        }
        assertSize(expectedContent.size(), actualConfig);
    }

    private void assertHas(String key, String value, UserConfiguration actualConfig){
        Optional<String> optionalValue = actualConfig.get(key);
        assertTrue(optionalValue.isPresent(), key + ": not present");
        assertEquals(value, optionalValue.get(), key + ": wrong value");
    }

    private void assertSize(int size, UserConfiguration actualConfig){
        assertEquals(size, actualConfig.size(), "Wrong size");
    }

}