package com.dieblich.handball.schiedsrichterassistent.pdf;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class PDFTableRowTest {
    @Test
    public void limitCellWidth(){
        PDFTableRow row = new PDFTableRow("Markus und Moritz","Michael");
        Function<String, Float> calculateStringWidth = text -> (float)text.length();
        int tableWidth = 20;

        row.limitCellWidth(tableWidth, calculateStringWidth);

        assertEquals(List.of("Markus und", "Moritz"), row.cells.get(0).text);
        assertEquals(List.of("Michael"), row.cells.get(1).text);
    }
}