package com.dieblich.handball.schiedsrichterassistent.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;

public class PDFFile implements AutoCloseable {

    private final PDDocument doc;
    private PDPage currentPage;
    private PDPageContentStream currentStream;

    private final static PDFont DEFAULT_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final int DEFAULT_FONT_SIZE = 8;

    public PDFFile() throws IOException {
        doc = new PDDocument();
        currentPage = new PDPage();
        doc.addPage(currentPage);
        currentStream = new PDPageContentStream(doc, currentPage);
    }
    public File exportToFile(String filename) throws IOException {

        seitenTitel();
        spielinformationen();
        halleninformationen();
        schiedsrichterInformationen();
        fahrtkostenInformationen();
        richtigkeitsErklaerung();
        anmerkungenAmEnde();

        currentStream.close();
        doc.save(filename);
        return new File(filename);
    }


    private void seitenTitel() throws IOException {
        text("Handball Nordrhein e.V.", 30,762);
        text("Reisekostenabrechnung Schiedsrichter", 30, 745);
    }
    private void spielinformationen() throws IOException {
        PDFTable table = new PDFTable(
                new PDFTableRow("Spielinformationen"),
                new PDFTableRow(
                        "Spiel-Nr.",
                        "Datum",
                        "Liga",
                        "Heimmannschaft",
                        "Gastmannschaft"
                ),
                new PDFTableRow(
                        "13156",
                        "13.04.2024 15:30",
                        "Mittelrhein Landesliga\nMänner",
                        "BTB Aachen III",
                        "SG Olheim-Straßfeld")
        );
        table(table, 29, 637);
    }

    private void halleninformationen() throws IOException {
        PDFTable table = new PDFTable(
                new PDFTableRow("Halleninformationen"),
                new PDFTableRow(
                        "Name",
                        "Straße",
                        "Ort"
                ),
                new PDFTableRow(
                        "AC2 Aachen Gillesbachtal",
                        "Branderhofer Weg 15",
                        "52066 Aachen")
        );
        table(table, 29, 570);
    }

    private void schiedsrichterInformationen() throws IOException {
        PDFTable table = new PDFTable(
                new PDFTableRow("Schiedsrichter-Informationen"),
                new PDFTableRow("SR A", "SR B"),
                new PDFTableRow("Nachname", "Fritz", "Nachname", "Huthwelker"),
                new PDFTableRow("Vorname", "Martin", "Vorname", "Marcus"),
                new PDFTableRow("Straße", "Arnimstr. 108", "Straße", "Antoniusstraße"),
                new PDFTableRow("Ort", "50825 Köln", "Ort", "Kerpen"),
                new PDFTableRow(""),
                new PDFTableRow("Abfahrtdatum", "Abfahrtszeit", "Abfahrtdatum", "Abfahrtszeit"),
                new PDFTableRow("13.04.2024", "13:45", "13.04.2024", "13:45"),
                new PDFTableRow("vorraus. Rückkehrdatum", "Rückkehrzeit", "vorraus. Rückkehrdatum", "Rückkehrzeit"),
                new PDFTableRow("13.04.2024", "18:15", "13.04.2024", "18:00")
        );
        table(table, 29, 370);
    }
    private void fahrtkostenInformationen() throws IOException {
        PDFTable table = new PDFTable(
                new PDFTableRow("Fahrtkosteninformationen"),
                fahrtkostenRow("157 km x 0,30 €", "47,10 €", "  0 km x 0,30 €", "€"),
                fahrtkostenRow("  0 km x 0,30 €", "€"),
                fahrtkostenRow("Nahverkehrskosten (Belege beifügen)", "€"),
                fahrtkostenRow("Tagegeld für ___ Stunden", "€"),
                fahrtkostenRow("Teilnahmeentschädigung", "30,00 €"),
                fahrtkostenRow("Übernachtung (Belege beifügen)", "€"),
                fahrtkostenRow("Sonstige Auslagen (Belege beifügen)", "€"),
                fahrtkostenRow("Summe:", "77,10 €", "Summe:", "30,00"),
                new PDFTableRow(
                        new PDFTableCell("Gesamtsumme", 5, PDFTableCell.Alignment.LEFT),
                        new PDFTableCell("107,10 €",    1, PDFTableCell.Alignment.RIGHT)
                )
        );
        table(table, 29, 188);
    }

    private void richtigkeitsErklaerung() throws IOException {
        PDFTable table = new PDFTable(
                new PDFTableRow(
                        "Wir versichern die Richtigkeit der vorgenannten Angaben und erklären, " +
                                "dass wir die erforderliche Steuererklärung selbst veranlassen.\nDie notwendigen " +
                                "Belege sind beigefügt bzw. lagen dem Verein zur Einsichtnahme vor."),
                new PDFTableRow("Datum", "Datum"),
                new PDFTableRow("Unterschrift", "Unterschrift")
        );
        table(table, 29, 91);
    }
    private void anmerkungenAmEnde() throws IOException {
        text("Das Reisekostenabrechnungsformular verbleibt als Quittung beim Verein. " +
                "Bei Zweifeln an der Richtigkeit der Abrechnung ist eine", 30,66);
        text("Kopie zwecks Überprüfung an die spielleitende Stelle zu übersenden", 30,50);
    }

    public void drawCoordinates() throws IOException {
        for(int y=0; y<800; y+=10) {
            lightGreyLine(0,y,650,y);
        }
        for (int x = 0; x < 650; x += 10) {
            lightGreyLine(x,0,x,800);
        }
        for(int y=0; y<800; y+=50) {
            darkGreyLine(0,y,650,y);
        }
        for (int x = 0; x < 650; x += 50) {
            darkGreyLine(x,0,x,800);
        }
        for(int y=0; y<800; y+=50) {
            for (int x = 0; x < 650; x += 50) {
                text(x+"-"+y, x,y);
            }
        }
    }

    private PDFTableRow fahrtkostenRow(String textA, String textB, String textC, String textD){
        return new PDFTableRow(
                new PDFTableCell(textA, 2, PDFTableCell.Alignment.LEFT),
                new PDFTableCell(textB, 1, PDFTableCell.Alignment.RIGHT),
                new PDFTableCell(textC, 2, PDFTableCell.Alignment.LEFT),
                new PDFTableCell(textD, 1, PDFTableCell.Alignment.RIGHT)
        );
    }
    private PDFTableRow fahrtkostenRow(String textA, String textB){
        return fahrtkostenRow(textA, textB, textA, textB);
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

            int cellX = x1;
            // lines between the columns
            for(int j=0;j<row.cells.size(); j++){
                PDFTableCell cell = row.cells.get(j);
                double singleCellWidth = (double)table.width/row.colCount;
                double cellWidth = singleCellWidth*cell.colspan;
                float stringWidth = DEFAULT_FONT.getStringWidth(cell.text.get(0)) / 1000 * DEFAULT_FONT_SIZE;
                if( stringWidth > cellWidth-3){
                    lightGreyLine(cellX, lineY, (int) (cellX+cellWidth), lineY+lineHeight);
                }

                for(int k=0; k<cell.text.size(); k++){
                    String textLine = cell.text.get(k);
                    text(textLine, cellX+2, (int) (lineY+table.lineHeight*k+2));
                }

                cellX += (int) cellWidth;
                if(j<row.cells.size()-1) {
                    blackLine(cellX, lineY, cellX, lineY+lineHeight);
                }
            }

            lineY += lineHeight;
            if(i<table.rows.size()-1) {
                blackLine(x1, lineY, x2, lineY);
            }
        }
    }

    public void text(String text, int x, int y) throws IOException {
        currentStream.beginText();
        currentStream.setFont(DEFAULT_FONT, DEFAULT_FONT_SIZE);
        currentStream.newLineAtOffset(x, y);
        currentStream.showText(text);
        currentStream.endText();
    }

    public void rect(int x1, int y1, int x2, int y2) throws IOException {
        blackLine(x1, y1, x2, y1);
        blackLine(x2, y1, x2, y2);
        blackLine(x2, y2, x1, y2);
        blackLine(x1, y2, x1, y1);
    }

    public void blackLine(int x1, int y1, int x2, int y2) throws IOException {
        line(x1, y1, x2, y2, 0,0,0);
    }
    public void darkGreyLine(int x1, int y1, int x2, int y2) throws IOException {
        line(x1, y1, x2, y2,0.5f,0.5f,0.5f);
    }
    public void lightGreyLine(int x1, int y1, int x2, int y2) throws IOException {
        line(x1, y1, x2, y2,0.8f,0.8f,0.8f);
    }
    public void line(int x1, int y1, int x2, int y2, float r, float g, float b) throws IOException {
        currentStream.moveTo(x1, y1);
        currentStream.setLineWidth(0.5f);
        currentStream.lineTo(x2, y2);
        currentStream.setStrokingColor(r,g,b);
        currentStream.stroke();
    }

    @Override
    public void close() throws Exception {
        doc.close();
    }
}
