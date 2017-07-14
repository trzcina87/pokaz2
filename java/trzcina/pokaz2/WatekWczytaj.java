package trzcina.pokaz2;

import java.io.File;

public class WatekWczytaj extends Thread {

    public volatile boolean zakoncz;
    public volatile boolean przeladuj;
    public PlikJPG[] pliki;
    public int iloscplikow;

    public WatekWczytaj() {
        pliki = null;
        przeladuj = false;
        iloscplikow = 0;
        zakoncz = false;
    }

    private void wyczyscObecnePliki() {
        if(pliki != null) {
            for(int i = 0; i < pliki.length; i++) {
                if(pliki[i] != null) {
                    pliki[i].wyczysc();
                }
            }
        }
    }

    private boolean czyPlikJPG(File plik) {
        if(plik.isFile()) {
            if(plik.getName().toLowerCase().endsWith(".jpg")) {
                return true;
            }
        }
        return false;
    }

    private int policzPlikiJPG(File[] plikiwkatalogu) {
        int ilosc = 0;
        if(plikiwkatalogu != null) {
            for(int i = 0; i < plikiwkatalogu.length; i++) {
                if(czyPlikJPG(plikiwkatalogu[i])) {
                    ilosc = ilosc + 1;
                }
            }
        }
        return ilosc;
    }

    private void wczytajNowaListe() {
        File katalog = new File(MainActivity.folderroboczy);
        File[] plikiwkatalogu = katalog.listFiles();
        iloscplikow = policzPlikiJPG(plikiwkatalogu);
        pliki = new PlikJPG[iloscplikow];
        int aktualnyplik = 0;
        if(plikiwkatalogu != null) {
            for(int i = 0; i < plikiwkatalogu.length; i++) {
                if(czyPlikJPG(plikiwkatalogu[i])) {
                    pliki[aktualnyplik] = new PlikJPG();
                    pliki[aktualnyplik].sciezka = plikiwkatalogu[i].getAbsolutePath();
                    aktualnyplik = aktualnyplik + 1;
                }
            }
        }
        przeladuj = false;
    }

    private void wczytajPliki() {
        wyczyscObecnePliki();
        wczytajNowaListe();
    }



    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(1);
            if(przeladuj) {
                wczytajPliki();
                MainActivity.ktoryplik = 0;
                AppService.service.watekrysuj.odswiez = true;
            }
            if(iloscplikow > 0) {
                int ktoryplikwczytac = MainActivity.ktoryplik;
                if((ktoryplikwczytac < iloscplikow) && (ktoryplikwczytac >= 0)) {
                    if (pliki[ktoryplikwczytac].bitmapa == null) {
                        boolean czyzaladowano = pliki[ktoryplikwczytac].zaladuj();
                        if(czyzaladowano == false) {
                            wyczyscObecnePliki();
                        }
                    }
                }
            }
        }
    }
}
