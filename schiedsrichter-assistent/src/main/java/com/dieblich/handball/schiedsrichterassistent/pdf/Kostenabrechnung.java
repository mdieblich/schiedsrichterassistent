package com.dieblich.handball.schiedsrichterassistent.pdf;

import com.dieblich.handball.schiedsrichterassistent.config.ConfigException;
import com.dieblich.handball.schiedsrichterassistent.config.KostenConfiguration;
import com.dieblich.handball.schiedsrichterassistent.config.SchiriConfiguration;
import com.dieblich.handball.schiedsrichterassistent.SchiriEinsatz;
import com.dieblich.handball.schiedsrichterassistent.calendar.SchirieinsatzAblauf;
import com.dieblich.handball.schiedsrichterassistent.config.Schirikosten;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class Kostenabrechnung {

    private static final DateTimeFormatter FORMAT_DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter FORMAT_TIME = DateTimeFormatter.ofPattern("HH:mm");

    private static final DecimalFormat CURRENCY = new DecimalFormat("0.00 €");
    private static final DecimalFormat DISTANCE = new DecimalFormat("# km");

    private final SchiriEinsatz einsatz;
    private final SchirieinsatzAblauf ablauf;
    private final SchiriConfiguration schiriA;
    @Nullable private final SchiriConfiguration schiriB;

    private Schirikosten schirikosten;

    public Kostenabrechnung(SchiriEinsatz einsatz, SchirieinsatzAblauf ablauf, SchiriConfiguration schiriA, @Nullable SchiriConfiguration schiriB){
        this.einsatz = einsatz;
        this.ablauf = ablauf;
        this.schiriA = schiriA;
        this.schiriB = schiriB;
    }
    public Kostenabrechnung(SchiriEinsatz einsatz, SchirieinsatzAblauf ablauf, SchiriConfiguration schiri){
        this(einsatz, ablauf, schiri, null);
    }

    public Schirikosten getSchirikosten() throws ConfigException {
        if(schirikosten == null){
            schirikosten = KostenConfiguration.calculate(ablauf);
        }
        return schirikosten;
    }

    public void exportToPDF(String filename) throws IOException, ConfigException {
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
                        einsatz.spielNr(),
                        einsatz.anwurf().format(FORMAT_DATE_TIME),
                        einsatz.ligaBezeichnungAusEmail(),
                        einsatz.heimMannschaft(),
                        einsatz.gastMannschaft())
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
                        einsatz.halleName(),
                        einsatz.halleStrasse(),
                        einsatz.hallePLZOrt())
        );
        pdfFile.table(table, 29, 570);
    }

    private void schiedsrichterInformationen(PDFFile pdfFile) throws IOException {
        PDFTable table;
        if(schiriB != null){
            table = new PDFTable(
                    tableHeader("Schiedsrichter-Informationen"),
                    new PDFTableRow("SR A", "SR B"),
                    new PDFTableRow("Nachname", schiriA.Benutzerdaten.Nachname, "Nachname", schiriB.Benutzerdaten.Nachname),
                    new PDFTableRow("Vorname", schiriA.Benutzerdaten.Vorname, "Vorname", schiriB.Benutzerdaten.Vorname),
                    new PDFTableRow("Straße", schiriA.Benutzerdaten.getStrasse(), "Straße", schiriB.Benutzerdaten.getStrasse()),
                    new PDFTableRow("Ort", schiriA.Benutzerdaten.getPLZOrt(), "Ort", schiriB.Benutzerdaten.getPLZOrt()),
                    new PDFTableRow(""),
                    new PDFTableRow("Abfahrtdatum", "Abfahrtszeit", "Abfahrtdatum", "Abfahrtszeit"),
                    new PDFTableRow(ablauf.getAbfahrt().format(FORMAT_DATE), ablauf.getAbfahrt().format(FORMAT_TIME), ablauf.getPartnerAbholen().format(FORMAT_DATE), ablauf.getPartnerAbholen().format(FORMAT_TIME)),
                    new PDFTableRow("vorraus. Rückkehrdatum", "Rückkehrzeit", "vorraus. Rückkehrdatum", "Rückkehrzeit"),
                    new PDFTableRow(ablauf.getHeimkehr().format(FORMAT_DATE), ablauf.getHeimkehr().format(FORMAT_TIME), ablauf.getZurueckbringenPartner().format(FORMAT_DATE), ablauf.getZurueckbringenPartner().format(FORMAT_TIME))
            );
        } else {
            table = new PDFTable(
                    tableHeader("Schiedsrichter-Informationen"),
                    new PDFTableRow("SR A", ""),
                    new PDFTableRow("Nachname", schiriA.Benutzerdaten.Nachname, "Nachname", ""),
                    new PDFTableRow("Vorname", schiriA.Benutzerdaten.Vorname, "Vorname", ""),
                    new PDFTableRow("Straße", schiriA.Benutzerdaten.getStrasse(), "Straße", ""),
                    new PDFTableRow("Ort", schiriA.Benutzerdaten.getPLZOrt(), "Ort", ""),
                    new PDFTableRow(""),
                    new PDFTableRow("Abfahrtdatum", "Abfahrtszeit", "Abfahrtdatum", "Abfahrtszeit"),
                    new PDFTableRow(ablauf.getAbfahrt().format(FORMAT_DATE), ablauf.getAbfahrt().format(FORMAT_TIME), "", ""),
                    new PDFTableRow("vorraus. Rückkehrdatum", "Rückkehrzeit", "vorraus. Rückkehrdatum", "Rückkehrzeit"),
                    new PDFTableRow(ablauf.getHeimkehr().format(FORMAT_DATE), ablauf.getHeimkehr().format(FORMAT_TIME), "", "")
                    );
        }
        pdfFile.table(table, 29, 370);
    }

    private void fahrtkostenInformationen(PDFFile pdfFile) throws IOException, ConfigException {
        PDFTable table;

        double teilnameEntschaedigung = getSchirikosten().getTeilnahmeEntschaedigung();

        int distanzA = getSchirikosten().distanzFahrerInKm();
        double kilometerPauschale = getSchirikosten().ligaKosten().kilometerPauschaleFahrer();
        double fahrtkostenA = getSchirikosten().getFahrtKostenFahrer();
        double summeA = teilnameEntschaedigung+fahrtkostenA;

        if(schiriB != null){
            int distanzB = getSchirikosten().distanzBeifahrerInKm();
            double beifahrerPauschale = getSchirikosten().ligaKosten().kilometerPauschaleBeiFahrer();
            double fahrtkostenB = getSchirikosten().getFahrtKostenBeifahrer();
            double summeB = teilnameEntschaedigung+fahrtkostenB;

            double gesamtSumme = summeA + summeB;

            table = new PDFTable(
                tableHeader("Fahrtkosteninformationen"),
                fahrtkostenRow(
                        DISTANCE.format(distanzA) + " x "+CURRENCY.format(kilometerPauschale),
                        CURRENCY.format(fahrtkostenA),
                        DISTANCE.format(distanzB) + " x "+CURRENCY.format(beifahrerPauschale),
                        CURRENCY.format(fahrtkostenB)
                ),
                fahrtkostenRow("___ km x ___ €", "€"),
                fahrtkostenRow("Nahverkehrskosten (Belege beifügen)", "€"),
                fahrtkostenRow("Tagegeld für ___ Stunden", "€"),
                fahrtkostenRow("Teilnahmeentschädigung", CURRENCY.format(teilnameEntschaedigung)),
                fahrtkostenRow("Übernachtung (Belege beifügen)", "€"),
                fahrtkostenRow("Sonstige Auslagen (Belege beifügen)", "€"),
                fahrtkostenRow("Summe:", CURRENCY.format(summeA), "Summe:", CURRENCY.format(summeB)),
                new PDFTableRow(
                        new PDFTableCell("Gesamtsumme", 5, PDFTableCell.Alignment.LEFT, true),
                        new PDFTableCell(CURRENCY.format(gesamtSumme),    1, PDFTableCell.Alignment.RIGHT, true)
                )
            );
        } else {

            table = new PDFTable(
                    tableHeader("Fahrtkosteninformationen"),
                    fahrtkostenRow(
                            DISTANCE.format(distanzA) + " x "+CURRENCY.format(kilometerPauschale),
                            CURRENCY.format(fahrtkostenA),
                             "___ km x ___ €",
                            "€"
                    ),
                    fahrtkostenRow("___ km x ___ €", "€"),
                    fahrtkostenRow("Nahverkehrskosten (Belege beifügen)", "€"),
                    fahrtkostenRow("Tagegeld für ___ Stunden", "€"),
                    fahrtkostenRow("Teilnahmeentschädigung", CURRENCY.format(teilnameEntschaedigung), "Teilnahmeentschädigung", "€"),
                    fahrtkostenRow("Übernachtung (Belege beifügen)", "€"),
                    fahrtkostenRow("Sonstige Auslagen (Belege beifügen)", "€"),
                    fahrtkostenRow("Summe:", CURRENCY.format(summeA), "Summe:", ""),
                    new PDFTableRow(
                            new PDFTableCell("Gesamtsumme", 5, PDFTableCell.Alignment.LEFT, true),
                            new PDFTableCell(CURRENCY.format(summeA),    1, PDFTableCell.Alignment.RIGHT, true)
                    )
            );

        }
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
