package com.dieblich.handball.schiedsrichterassistent.pdf;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class KostenabrechnungTest {

    @Test
    public void createTestFile() throws IOException {
        Kostenabrechnung abr = new Kostenabrechnung();
        abr.exportToPDF("martinpdf.pdf");
    }
}