package com.dieblich.handball.schiedsrichterassistent.pdf;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class PDFTableTest {

    @Test
    public void limitCellWidth(){
        PDFTable table = new PDFTable(new PDFTableRow(
                "Martin", "Michael Thomas", "Anne Pit Viktor"
        ));
        table.width = 30;
        Function<String, Float> calculateStringWidth = text -> (float)text.length();

        table.limitCellWidth(calculateStringWidth);

        assertEquals(List.of("Martin"), table.rows.get(0).cells.get(0).text);
        assertEquals(List.of("Michael", "Thomas"), table.rows.get(0).cells.get(1).text);
        assertEquals(List.of("Anne Pit", "Viktor"), table.rows.get(0).cells.get(2).text);

    }
}