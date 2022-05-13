package dev.kofe.model;

import lombok.Data;

@Data
public class PageLink {
    private String number;
    private String link;

    public PageLink(int number, String link) {
        this.number = String.valueOf(number);
        this.link = link;
    }

    public PageLink(String numberAsString, String link) {
        this.number = numberAsString;
        this.link = link;
    }

}
