package com.example.slagalicaprojekat.Model;

public class Korisnik {
    private String username;
    private String email;
    private String password;
    private int tokeni;
    private int zvezde;
    private int odigranePartije;

    public Korisnik() {
    }

    public Korisnik(String username, int zvezde, int odigranePartije, String email) {
        this.username = username;
        this.zvezde = zvezde;
        this.odigranePartije = odigranePartije;
        this.email = email;
    }

    public Korisnik(String username, String email, String password, int tokeni, int zvezde, int odigranePartije) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.tokeni = tokeni;
        this.zvezde = zvezde;
        this.odigranePartije = odigranePartije;
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

    public int getTokeni() {
        return tokeni;
    }

    public void setTokeni(int tokeni) {
        this.tokeni = tokeni;
    }

    public int getZvezde() {
        return zvezde;
    }

    public void setZvezde(int zvezde) {
        this.zvezde = zvezde;
    }

    public int getOdigranePartije() {
        return odigranePartije;
    }

    public void setOdigranePartije(int odigranePartije) {
        this.odigranePartije = odigranePartije;
    }

    @Override
    public String toString() {
        return "Username: " + username + ", Zvezde: " + zvezde;
    }
}