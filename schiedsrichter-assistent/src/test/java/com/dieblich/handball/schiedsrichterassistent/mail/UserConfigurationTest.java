package com.dieblich.handball.schiedsrichterassistent.mail;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
        config.updateWith("Umziehen.DauerInMinuten=30", doNotFindGeoLocation);
        assertExactContent(Map.of("Umziehen.DauerInMinuten", "30"), config);
    }
    @Test
    public void updateWith_trims() throws IOException {
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("  Umziehen.DauerInMinuten  =\t30   ", doNotFindGeoLocation);
        assertExactContent(Map.of("Umziehen.DauerInMinuten", "30"), config);
    }

    @Test
    public void updateWith_createsTwoEntries() throws IOException {
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("""
        TechnischeBesprechung.Oberliga.DauerInMinuten=100
        Umziehen.DauerInMinuten=35
        """, doNotFindGeoLocation);
        assertExactContent(Map.of(
                "TechnischeBesprechung.Oberliga.DauerInMinuten", "100",
                "Umziehen.DauerInMinuten","35"
                ), config);
    }

    @Test
    public void updateWith_updatesEntry() throws IOException {
        UserConfiguration config = new UserConfiguration("", "Umziehen.DauerInMinuten=20");
        config.updateWith("Umziehen.DauerInMinuten=30", doNotFindGeoLocation);
        assertExactContent(Map.of("Umziehen.DauerInMinuten", "30"), config);
    }

    @Test
    public void updateWith_refusesUnknownEntries() throws IOException{
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("blabla=ABCD", doNotFindGeoLocation);
        assertExactContent(Map.of(), config);
    }
    @Test
    public void updateWith_ignoresTripleAssignments() throws IOException{
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("Umziehen.DauerInMinuten=30=45",doNotFindGeoLocation);
        assertExactContent(Map.of(), config);
    }

    @Test
    public void updateWith_ignoresNewlines() throws IOException{
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("""
        
        TechnischeBesprechung.Oberliga.DauerInMinuten=100
        
        
        Umziehen.DauerInMinuten=35
        
        """, doNotFindGeoLocation);
        assertExactContent(Map.of(
                "TechnischeBesprechung.Oberliga.DauerInMinuten", "100",
                "Umziehen.DauerInMinuten","35"
        ), config);
    }

    @Test
    public void updateWith_stopsAtGibberish() throws IOException{
        UserConfiguration config = new UserConfiguration("", "");
        config.updateWith("""
        
        TechnischeBesprechung.Oberliga.DauerInMinuten=100
        
        heyho!
        
        Umziehen.DauerInMinuten=35
        
        """, doNotFindGeoLocation);
        assertExactContent(Map.of(
                "TechnischeBesprechung.Oberliga.DauerInMinuten", "100"
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

    private final Function<String, Optional<String>> doNotFindGeoLocation = (String o) -> Optional.empty();

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