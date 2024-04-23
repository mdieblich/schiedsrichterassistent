package com.dieblich.handball.schiedsrichterassistent.pdf;

public class PDFTableCell {
    public String text;
    public Alignment alignment;
    public int colspan;

    public PDFTableCell(String text, int colspan, Alignment alignment){
        this.text = text;
        this.colspan = colspan;
        this.alignment = alignment;
    }
    public PDFTableCell(String text, int colspan){
        this(text, colspan, Alignment.LEFT);
    }
    public PDFTableCell(String text){
        this(text, 1);
    }

    public enum Alignment{
        LEFT, CENTER, RIGHT;
    }
}
