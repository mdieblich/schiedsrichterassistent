package com.dieblich.handball.schiedsrichterassistent.pdf;

import java.util.List;

public class PDFTableCell {
    public List<String> text;
    public Alignment alignment;
    public int colspan;

    public PDFTableCell(String text, int colspan, Alignment alignment){
        this.text = List.of(text.split("\n"));
        this.colspan = colspan;
        this.alignment = alignment;
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
