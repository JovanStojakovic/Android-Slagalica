package com.example.slagalicaprojekat.Model;

public class Korisnik {
    private String username;
    private String email;
    private String password;

    public Korisnik() {
    }

    public Korisnik( String username, String email, String password) {

        this.password = password;
        this.username = username;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
