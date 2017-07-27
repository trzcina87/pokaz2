package trzcina.pokaz2;

import java.io.File;

public class OpcjeProgramu {

    public static volatile int pokazslidow;
    public static volatile int czaszdjecia;
    public static volatile int losuj;
    public static volatile String folder;

    private static void wczytajPokazSlidow() {
        int pokazslidowwczytany = Ustawienia.wczytajUstawienieInt("pokazslidow");
        if(pokazslidowwczytany == -1) {
            pokazslidow = 1;
            Ustawienia.zapiszUstawienie("pokazslidow", 1);
        } else {
            pokazslidow = pokazslidowwczytany;
        }
    }

    private static void wczytajCzasZdjecia() {
        int czaszdjeciawczytany = Ustawienia.wczytajUstawienieInt("czaszdjecia");
        if(czaszdjeciawczytany == -1) {
            czaszdjecia = 2;
            Ustawienia.zapiszUstawienie("czaszdjecia", 2);
        } else {
            czaszdjecia = czaszdjeciawczytany;
        }
    }

    private static void wczytajLosuj() {
        int losujwczytany = Ustawienia.wczytajUstawienieInt("losuj");
        if(losujwczytany == -1) {
            losuj = 0;
            Ustawienia.zapiszUstawienie("losuj", 0);
        } else {
            losuj = losujwczytany;
        }
    }

    private static void wczytajFolder() {
        String folderwczytany = Ustawienia.wczytajUstawienie("folder");
        if(folderwczytany == null) {
            folder = "/";
            Ustawienia.zapiszUstawienie("folder", "/");
        } else {
            folder = folderwczytany;
        }
    }

    public static void wczytajOpcje() {
        wczytajPokazSlidow();
        wczytajCzasZdjecia();
        wczytajLosuj();
        wczytajFolder();
    }

    private static void zapiszOpcjePokazSlidow() {
        if(Widoki.checkboxpokazslidow.isChecked()) {
            Ustawienia.zapiszUstawienie("pokazslidow", 1);
            pokazslidow = 1;
        } else {
            Ustawienia.zapiszUstawienie("pokazslidow", 0);
            pokazslidow = 0;
        }
    }

    private static void zapiszOpcjeLosuj() {
        if(Widoki.checkboxlosuj.isChecked()) {
            Ustawienia.zapiszUstawienie("losuj", 1);
            losuj = 1;
        } else {
            Ustawienia.zapiszUstawienie("losuj", 0);
            losuj = 0;
        }
    }

    private static void zapiszOpcjeCzas() {
        if(Widoki.radiobuttonczas2s.isChecked()) {
            Ustawienia.zapiszUstawienie("czaszdjecia", 2);
            czaszdjecia = 2;
        }
        if(Widoki.radiobuttonczas3s.isChecked()) {
            Ustawienia.zapiszUstawienie("czaszdjecia", 3);
            czaszdjecia = 3;
        }
        if(Widoki.radiobuttonczas5s.isChecked()) {
            Ustawienia.zapiszUstawienie("czaszdjecia", 5);
            czaszdjecia = 5;
        }
        if(Widoki.radiobuttonczas7s.isChecked()) {
            Ustawienia.zapiszUstawienie("czaszdjecia", 7);
            czaszdjecia = 7;
        }
        if(Widoki.radiobuttonczas10s.isChecked()) {
            Ustawienia.zapiszUstawienie("czaszdjecia", 10);
            czaszdjecia = 10;
        }
    }

    private static void zapiszOpcjeFolder() {
        Ustawienia.zapiszUstawienie("folder", MainActivity.folderroboczy);
        folder = MainActivity.folderroboczy;
    }

    public static void zapiszOpcje() {
        zapiszOpcjePokazSlidow();
        zapiszOpcjeLosuj();
        zapiszOpcjeCzas();
        zapiszOpcjeFolder();
    }
}
