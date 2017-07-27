package trzcina.pokaz2;

import java.io.File;
import java.util.Random;

public class WatekWczytaj extends Thread {

    public volatile boolean zakoncz;
    public volatile boolean przeladuj;
    public PlikJPG[] pliki;
    public int iloscplikow;
    public String zacznijod;

    public WatekWczytaj() {
        pliki = null;
        przeladuj = false;
        iloscplikow = 0;
        zakoncz = false;
        zacznijod = null;
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

    private void zamienWTab(File[] tab, int n1, int n2) {
        File tmp = tab[n1];
        tab[n1] = tab[n2];
        tab[n2] = tmp;
    }

    private void przelosujTablice(File[] plikiwkatalogu) {
        Random rn = new Random();
        if(plikiwkatalogu != null) {
            if(plikiwkatalogu.length > 0) {
                for(int j = 0; j < 2; j++) {
                    for (int i = 0; i < plikiwkatalogu.length; i++) {
                        int n1 = rn.nextInt(plikiwkatalogu.length);
                        zamienWTab(plikiwkatalogu, i, n1);
                    }
                }
            }
        }
    }

    private void znajdzKlikniety(File[] plikiwkatalogu) {
        int klikniety = 0;
        if(zacznijod != null) {
            File[] tabtymczasowa = new File[plikiwkatalogu.length];
            for(int i = 0; i < plikiwkatalogu.length; i++) {
                if(plikiwkatalogu[i].getAbsolutePath().equals(zacznijod)) {
                    klikniety = i;
                }
            }
            for(int i = 0; i < plikiwkatalogu.length; i++) {
                tabtymczasowa[i] = plikiwkatalogu[dodaji(i, plikiwkatalogu.length, klikniety)];
            }
            for(int i = 0; i < plikiwkatalogu.length; i++) {
                plikiwkatalogu[i] = tabtymczasowa[i];
            }
        }
        zacznijod = null;
    }

    private void wczytajNowaListe() {
        File katalog = new File(MainActivity.folderroboczy);
        File[] plikiwkatalogu = katalog.listFiles();
        if(plikiwkatalogu != null) {
            WatekListujPliki.sortujPliki(plikiwkatalogu);
        }
        if(OpcjeProgramu.losuj == 1) {
            przelosujTablice(plikiwkatalogu);
        }
        znajdzKlikniety(plikiwkatalogu);
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

    private int dodaj1(int liczba, int ilosc) {
        int temp = liczba + 1;
        if(temp >= ilosc) {
            temp = 0;
        }
        return temp;
    }

    private int dodaji(int liczba, int ilosc, int i) {
        int temp = liczba + i;
        if(temp >= ilosc) {
            temp = temp % ilosc;
        }
        return temp;
    }

    private int odejmij1(int liczba, int ilosc) {
        int temp = liczba - 1;
        if(temp < 0) {
            temp = ilosc - 1;
        }
        return temp;
    }

    private boolean czyIntWTablicy(int liczba, int[] tablica) {
        for(int i = 0; i < tablica.length; i++) {
            if(liczba == tablica[i]) {
                return true;
            }
        }
        return false;
    }

    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(10);
            if(przeladuj) {
                wczytajPliki();
                MainActivity.ktoryplik = 0;
                AppService.service.watekrysuj.odswiez = true;
            }
            if(iloscplikow > 0) {
                int ktoryplikwczytac = MainActivity.ktoryplik;
                int nastepny1 = dodaj1(ktoryplikwczytac, iloscplikow);
                int nastepny2 = dodaj1(nastepny1, iloscplikow);
                int nastepny3 = dodaj1(nastepny2, iloscplikow);
                int poprzedni1 = odejmij1(ktoryplikwczytac, iloscplikow);
                int poprzedni2 = odejmij1(poprzedni1, iloscplikow);
                int[] potrzebne = {ktoryplikwczytac, nastepny1, nastepny2, nastepny3, poprzedni1, poprzedni2};
                if((ktoryplikwczytac < iloscplikow) && (ktoryplikwczytac >= 0)) {
                    for(int i = 0; i < iloscplikow; i++) {
                        if(czyIntWTablicy(i, potrzebne) == false) {
                            if(pliki[i].bitmapa != null) {
                                pliki[i].wyczysc();
                            }
                        }
                    }
                    if ((pliki[ktoryplikwczytac].bitmapa == null) && (przeladuj == false) && (zakoncz == false)){
                        boolean czyzaladowano = pliki[ktoryplikwczytac].zaladuj();
                        if(czyzaladowano == false) {
                            wyczyscObecnePliki();
                        }
                    }
                    if ((pliki[ktoryplikwczytac].bitmapa == null) && (przeladuj == false) && (zakoncz == false)) {
                        pliki[ktoryplikwczytac].zaladuj();
                    }
                    if ((pliki[nastepny1].bitmapa == null) && (przeladuj == false) && (zakoncz == false)) {
                        pliki[nastepny1].zaladuj();
                    }
                    if ((pliki[nastepny2].bitmapa == null) && (przeladuj == false) && (zakoncz == false)) {
                        pliki[nastepny2].zaladuj();
                    }
                    if ((pliki[poprzedni1].bitmapa == null) && (przeladuj == false) && (zakoncz == false)) {
                        pliki[poprzedni1].zaladuj();
                    }
                    if ((pliki[nastepny3].bitmapa == null) && (przeladuj == false) && (zakoncz == false)) {
                        pliki[nastepny3].zaladuj();
                    }
                    if ((pliki[poprzedni2].bitmapa == null) && (przeladuj == false) && (zakoncz == false)) {
                        pliki[poprzedni2].zaladuj();
                    }
                }
            }
        }
    }
}