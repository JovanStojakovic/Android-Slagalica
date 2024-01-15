package com.example.slagalicaprojekat.Model;

public class KoZnaZna {
    private String Pitanje;
    private String Resenje;
    private String Opcija1;
    private String Opcija2;
    private String Opcija3;

    public KoZnaZna() {
    }

    public KoZnaZna(String pitanje, String resenje, String opcija1, String opcija2, String opcija3) {
        Pitanje = pitanje;
        Resenje = resenje;
        Opcija1 = opcija1;
        Opcija2 = opcija2;
        Opcija3 = opcija3;
    }

    public String getPitanje() {
        return Pitanje;
    }

    public void setPitanje(String pitanje) {
        Pitanje = pitanje;
    }

    public String getResenje() {
        return Resenje;
    }

    public void setResenje(String resenje) {
        Resenje = resenje;
    }

    public String getOpcija1() {
        return Opcija1;
    }

    public void setOpcija1(String opcija1) {
        Opcija1 = opcija1;
    }

    public String getOpcija2() {
        return Opcija2;
    }

    public void setOpcija2(String opcija2) {
        Opcija2 = opcija2;
    }

    public String getOpcija3() {
        return Opcija3;
    }

    public void setOpcija3(String opcija3) {
        Opcija3 = opcija3;
    }
}
