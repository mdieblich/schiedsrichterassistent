package com.dieblich.handball.schiedsrichterassistent.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;

public class PDFFile implements AutoCloseable {

    private final PDDocument doc;
    private PDPage currentPage;
    private PDPageContentStream currentStream;

    private final static PDFont DEFAULT_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final static PDFont BOLD_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final int DEFAULT_FONT_SIZE = 9;

    public PDFFile() throws IOException {
        doc = new PDDocument();
        currentPage = new PDPage();
        doc.addPage(currentPage);
        currentStream = new PDPageContentStream(doc, currentPage);
    }

    public void table(PDFTable table, int x1, int y1) throws IOException {
        int x2 = x1+table.width;
        int y2 = y1+table.height();
        rect(x1, y1,x2, y2);

        // lines between the rows
        int lineY = y1;
        for(int i=0; i<table.rows.size(); i++){
            PDFTableRow row = table.rows.get(table.rows.size()-(i+1));  // reverse order
            int lineHeight = (int)(row.rowspan()*table.lineHeight);

            double cellX = x1;
            // lines between the columns
            for(int j=0;j<row.cells.size(); j++){
                PDFTableCell cell = row.cells.get(j);
                double singleCellWidth = (double)table.width/row.colCount;
                double cellWidth = singleCellWidth*cell.colspan;

                for(int k=0; k<cell.text.size(); k++){
                    String textLine = cell.text.get(cell.text.size()-k-1);
                    double textX = cellX+2;
                    int textY = (int) (lineY+table.lineHeight*k+4);
                    switch (cell.alignment){
                        case LEFT:{
                            textX = cellX+2;
                            break;
                        } case RIGHT: {
                            float stringWidth = DEFAULT_FONT.getStringWidth(textLine) / 1000 * DEFAULT_FONT_SIZE;
                            textX = (int) (cellX+cellWidth-stringWidth-2);
                            break;
                        } case CENTER: {
                            float stringWidth = DEFAULT_FONT.getStringWidth(textLine) / 1000 * DEFAULT_FONT_SIZE;
                            textX = (int) (cellX+(cellWidth-stringWidth)/2);
                            break;
                        }
                    }
                    text(textLine, (int) textX, textY, DEFAULT_FONT_SIZE, cell.bold);
                }

                cellX += cellWidth;
                if(j<row.cells.size()-1) {
                    line((int) cellX, lineY, (int) cellX, lineY+lineHeight);
                }
            }

            lineY += lineHeight;
            if(i<table.rows.size()-1) {
                line(x1, lineY, x2, lineY);
            }
        }
    }

    public void text(String text, int x, int y, int font_size, boolean bold) throws IOException {
        currentStream.beginText();
        currentStream.setFont(bold?BOLD_FONT:DEFAULT_FONT, font_size);
        currentStream.newLineAtOffset(x, y);
        currentStream.showText(text);
        currentStream.endText();
    }
    public void text(String text, int x, int y) throws IOException {
        text(text, x, y, DEFAULT_FONT_SIZE, false);
    }

    public void rect(int x1, int y1, int x2, int y2) throws IOException {
        line(x1, y1, x2, y1);
        line(x2, y1, x2, y2);
        line(x2, y2, x1, y2);
        line(x1, y2, x1, y1);
    }

    public void line(int x1, int y1, int x2, int y2) throws IOException {
        currentStream.moveTo(x1, y1);
        currentStream.setLineWidth(0.5f);
        currentStream.lineTo(x2, y2);
        currentStream.stroke();
    }

    public void exportToFile(String filename) throws IOException {
        currentStream.close();
        doc.save(filename);
    }

    @Override
    public void close() throws IOException {
        doc.close();
    }
}
