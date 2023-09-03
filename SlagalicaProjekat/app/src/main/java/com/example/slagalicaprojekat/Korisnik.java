package com.example.slagalicaprojekat;

public class Korisnik {
    private String username;
    private String password;

    public Korisnik() {
    }

    public Korisnik( String username, String password) {

        this.password = password;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
