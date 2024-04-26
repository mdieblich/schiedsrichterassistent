package com.dieblich.handball.schiedsrichterassistent.pdf;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class PDFTableCellTest {

    @Test
    public void limitCellWidth_NoLineBreak(){
        PDFTableCell cell = new PDFTableCell("Markus und Moritz");
        Function<String, Float> calculateStringWidth = text -> (float)text.length();

        cell.limitCellWidth(20, calculateStringWidth);

        assertEquals(List.of("Markus und Moritz"), cell.text);
    }

    @Test
    public void limitCellWidth_OneLineBreak(){
        PDFTableCell cell = new PDFTableCell("Markus und Moritz");
        Function<String, Float> calculateStringWidth = text -> (float)text.length();

        cell.limitCellWidth(10, calculateStringWidth);

        assertEquals(List.of("Markus und", "Moritz"), cell.text);
    }

    @Test
    public void limitCellWidth_TwoLineBreaks(){
        PDFTableCell cell = new PDFTableCell("Markus und Moritz");
        Function<String, Float> calculateStringWidth = text -> (float)text.length();

        cell.limitCellWidth(7, calculateStringWidth);

        assertEquals(List.of("Markus","und", "Moritz"), cell.text);
    }

    @Test
    public void limitCellWidth_BreaksLongWord(){
        PDFTableCell cell = new PDFTableCell("Heizungsmonteur");
        Function<String, Float> calculateStringWidth = text -> (float)text.length();

        cell.limitCellWidth(10, calculateStringWidth);

        assertEquals(List.of("Heizungsmo","nteur"), cell.text);
    }

    @Test
    public void limitCellWidth_BreaksLongWord_In_Between(){
        PDFTableCell cell = new PDFTableCell("Martin und Heizungsmonteur Max");
        Function<String, Float> calculateStringWidth = text -> (float)text.length();

        cell.limitCellWidth(10, calculateStringWidth);

        assertEquals(List.of("Martin und", "Heizungsmo","nteur Max"), cell.text);
    }

    @Test
    public void limitCellWidth_WithColspan(){
        PDFTableCell cell = new PDFTableCell("Markus und Moritz");
        cell.colspan = 2;
        Function<String, Float> calculateStringWidth = text -> (float)text.length();

        cell.limitCellWidth(5, calculateStringWidth);

        assertEquals(List.of("Markus und", "Moritz"), cell.text);
    }
}