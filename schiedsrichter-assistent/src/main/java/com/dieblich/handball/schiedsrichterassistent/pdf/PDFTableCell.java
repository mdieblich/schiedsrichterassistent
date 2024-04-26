package com.dieblich.handball.schiedsrichterassistent.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    public void limitCellWidth(double maxCellWidth, Function<String, Float> calculateStringWidth) {
        List<String> newLines = new ArrayList<>();
        for(String oldLine:text){
            String[] words = oldLine.split(" ");
            String currentLine = "";
            for (String nextWord : words) {
                if (calculateStringWidth.apply(currentLine + nextWord) > maxCellWidth) {
                    if (!currentLine.isEmpty()) {
                        newLines.add(currentLine.trim());
                    }

                    if (calculateStringWidth.apply(nextWord) > maxCellWidth) {
                        // next word is already too long
                        float actualWidth = calculateStringWidth.apply(nextWord);
                        int splitPoint = (int) (nextWord.length() * maxCellWidth / actualWidth);
                        newLines.add(nextWord.substring(0, splitPoint));
                        currentLine = nextWord.substring(splitPoint) + " ";
                    } else {
                        currentLine = nextWord + " ";
                    }

                } else {
                    currentLine += nextWord + " ";
                }
            }
            newLines.add(currentLine.trim());
        }
        text = newLines;
    }

    public enum Alignment{
        LEFT, CENTER, RIGHT;
    }
}
