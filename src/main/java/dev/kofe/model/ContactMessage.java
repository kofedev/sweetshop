package dev.kofe.model;

import lombok.Data;

@Data
public class ContactMessage {
    private String name;
    private String email;
    private String text;
    private String userName;

    public void clear() {
        name = "";
        //email = "";
        text = "";
        userName = "";
    }
}
