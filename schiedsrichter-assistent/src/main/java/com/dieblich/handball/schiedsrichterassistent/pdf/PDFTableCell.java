package com.dieblich.handball.schiedsrichterassistent.pdf;

import java.util.List;

public class PDFTableCell {
    public List<String> text;
    public Alignment alignment;
    public int colspan;
    public boolean bold = false;

    public PDFTableCell(String text, int colspan, Alignment alignment, boolean bold){
        this.text = List.of(text.split("\n"));
        this.colspan = colspan;
        this.alignment = alignment;
        this.bold = bold;
    }
    public PDFTableCell(String text, int colspan, Alignment alignment){
        this(text, colspan, alignment, false);
    }
    public PDFTableCell(String text, int colspan){
        this(text, colspan, Alignment.LEFT);
    }
    public PDFTableCell(String text){
        this(text, 1);
    }

    public int rowspan() {
        return text.size();
    }

    public enum Alignment{
        LEFT, CENTER, RIGHT;
    }
}
