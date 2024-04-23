package com.dieblich.handball.schiedsrichterassistent.pdf;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class PDFFileTest {

    @Test
    public void createTestFile() throws IOException {
        PDFFile file = new PDFFile();
        file.exportToFile("martinpdf.pdf");
    }
}