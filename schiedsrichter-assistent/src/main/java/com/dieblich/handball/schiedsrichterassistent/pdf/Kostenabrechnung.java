package com.dieblich.handball.schiedsrichterassistent.pdf;

import java.io.IOException;

public class Kostenabrechnung {

    public Kostenabrechnung(){

    }

    public void exportToPDF(String filename) throws IOException {
        try(PDFFile pdfFile = new PDFFile()){
            seitenTitel(pdfFile);
            spielinformationen(pdfFile);
            halleninformationen(pdfFile);
            schiedsrichterInformationen(pdfFile);
            fahrtkostenInformationen(pdfFile);
            richtigkeitsErklaerung(pdfFile);
            anmerkungenAmEnde(pdfFile);

            pdfFile.exportToFile(filename);
        }
    }


    private void seitenTitel(PDFFile pdfFile) throws IOException {
        pdfFile.text("Handball Nordrhein e.V.", 30,762, 16, true);
        pdfFile.text("Reisekostenabrechnung Schiedsrichter", 30, 745);
    }
    private void spielinformationen(PDFFile pdfFile) throws IOException {
        PDFTable table = new PDFTable(
                tableHeader("Spielinformationen"),
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
        pdfFile.table(table, 29, 637);
    }

    private PDFTableRow tableHeader(String text){
        return new PDFTableRow(
                new PDFTableCell(text, 1, PDFTableCell.Alignment.CENTER)
        );
    }

    private void halleninformationen(PDFFile pdfFile) throws IOException {
        PDFTable table = new PDFTable(
                tableHeader("Halleninformationen"),
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
        pdfFile.table(table, 29, 570);
    }

    private void schiedsrichterInformationen(PDFFile pdfFile) throws IOException {
        PDFTable table = new PDFTable(
                tableHeader("Schiedsrichter-Informationen"),
                new PDFTableRow("SR A", "SR B"),
                new PDFTableRow("Nachname", "", "Nachname", ""),
                new PDFTableRow("Vorname", "", "Vorname", ""),
                new PDFTableRow("Straße", "", "Straße", ""),
                new PDFTableRow("Ort", "", "Ort", ""),
                new PDFTableRow(""),
                new PDFTableRow("Abfahrtdatum", "Abfahrtszeit", "Abfahrtdatum", "Abfahrtszeit"),
                new PDFTableRow("13.04.2024", "13:45", "13.04.2024", "13:45"),
                new PDFTableRow("vorraus. Rückkehrdatum", "Rückkehrzeit", "vorraus. Rückkehrdatum", "Rückkehrzeit"),
                new PDFTableRow("13.04.2024", "18:15", "13.04.2024", "18:00")
        );
        pdfFile.table(table, 29, 370);
    }
    private void fahrtkostenInformationen(PDFFile pdfFile) throws IOException {
        PDFTable table = new PDFTable(
                tableHeader("Fahrtkosteninformationen"),
                fahrtkostenRow("157 km x 0,30 €", "47,10 €", "  0 km x 0,30 €", "€"),
                fahrtkostenRow("  0 km x 0,30 €", "€"),
                fahrtkostenRow("Nahverkehrskosten (Belege beifügen)", "€"),
                fahrtkostenRow("Tagegeld für ___ Stunden", "€"),
                fahrtkostenRow("Teilnahmeentschädigung", "30,00 €"),
                fahrtkostenRow("Übernachtung (Belege beifügen)", "€"),
                fahrtkostenRow("Sonstige Auslagen (Belege beifügen)", "€"),
                fahrtkostenRow("Summe:", "77,10 €", "Summe:", "30,00"),
                new PDFTableRow(
                        new PDFTableCell("Gesamtsumme", 5, PDFTableCell.Alignment.LEFT, true),
                        new PDFTableCell("107,10 €",    1, PDFTableCell.Alignment.RIGHT, true)
                )
        );
        pdfFile.table(table, 29, 188);
    }

    private void richtigkeitsErklaerung(PDFFile pdfFile) throws IOException {
        PDFTable table = new PDFTable(
                new PDFTableRow(
                        "Wir versichern die Richtigkeit der vorgenannten Angaben und erklären, " +
                                "dass wir die erforderliche Steuererklärung selbst veranlassen.\nDie notwendigen " +
                                "Belege sind beigefügt bzw. lagen dem Verein zur Einsichtnahme vor."),
                new PDFTableRow("Datum", "Datum"),
                new PDFTableRow("Unterschrift", "Unterschrift")
        );
        pdfFile.table(table, 29, 91);
    }
    private void anmerkungenAmEnde(PDFFile pdfFile) throws IOException {
        pdfFile.text("Das Reisekostenabrechnungsformular verbleibt als Quittung beim Verein. " +
                "Bei Zweifeln an der Richtigkeit der Abrechnung ist eine", 30,66);
        pdfFile.text("Kopie zwecks Überprüfung an die spielleitende Stelle zu übersenden", 30,50);
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
}
