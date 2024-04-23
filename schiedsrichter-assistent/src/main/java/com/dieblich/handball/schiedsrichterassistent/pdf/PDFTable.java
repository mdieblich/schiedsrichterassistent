package com.dieblich.handball.schiedsrichterassistent.pdf;

import java.util.List;

public class PDFTable {
    public List<PDFTableRow> rows;
    public double lineHeight = 16.63;
    public int width = 543;

    public PDFTable(PDFTableRow... rows){
        this.rows = List.of(rows);
    }

    public int height() {
        int height = 0;
        for(PDFTableRow row:rows){
            height += (int) (lineHeight*row.rowspan());
        }
        return height;
    }
}
