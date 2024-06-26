package com.dieblich.handball.schiedsrichterassistent.pdf;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PDFTableRow {
    public final List<PDFTableCell> cells;
    public final int colCount;

    public PDFTableRow(PDFTableCell... cells){
        this.cells = List.of(cells);
        int colCount = 0;
        for(PDFTableCell cell:cells){
            colCount += cell.colspan;
        }
        this.colCount = colCount;
    }
    public PDFTableRow(String... cellContent){
        this.cells = Arrays.stream(cellContent)
                .map(PDFTableCell::new)
                .collect(Collectors.toList());
        int colCount = 0;
        for(PDFTableCell cell:cells){
            colCount += cell.colspan;
        }
        this.colCount = colCount;
    }

    public double rowspan() {
        float maxRowSpan = 0;
        for(PDFTableCell cell:cells){
            int rowSpan = cell.rowspan();
            if(maxRowSpan < rowSpan){
                maxRowSpan = rowSpan;
            }
        }
        return maxRowSpan;
    }

    public void limitCellWidth(double tableWidth, Function<String, Float> calculateStringWidth) {
        cells.forEach(cell -> cell.limitCellWidth(tableWidth/colCount, calculateStringWidth));
    }
}
