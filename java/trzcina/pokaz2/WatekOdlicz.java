package trzcina.pokaz2;

import java.util.Random;

@SuppressWarnings("PointlessBooleanExpression")
public class WatekOdlicz extends Thread {

    public volatile boolean zakoncz;
    public volatile long ostatniczas;

    public WatekOdlicz() {
        zakoncz = false;
        ostatniczas = System.currentTimeMillis() * 2;
    }

    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(50);
            if(OpcjeProgramu.pokazslidow == 1) {
                if(System.currentTimeMillis() > ostatniczas + OpcjeProgramu.czaszdjecia * 1000) {
                    if (MainActivity.powiekszenie == 0) {
                        int iloscplikow = AppService.watekwczytaj.iloscplikow;
                        if (iloscplikow > 0) {
                            MainActivity.ktoryplik = MainActivity.ktoryplik + 1;
                            if (MainActivity.ktoryplik >= iloscplikow) {
                                MainActivity.ktoryplik = 0;
                            }
                            MainActivity.activity.resetujPrzesuj();
                            ostatniczas = 2 * System.currentTimeMillis();
                            AppService.watekrysuj.odswiez = true;
                        }
                        if(WatekRysuj.czyAnimowac()) {
                            Random random = new Random();
                            int znakx = Rozne.znak(random.nextInt(2));
                            int znaky = Rozne.znak(random.nextInt(2));
                            AppService.watekanimacja.startx = (40 + random.nextInt(61)) * znakx;
                            AppService.watekanimacja.starty = (40 + random.nextInt(61)) * znaky;
                            AppService.watekanimacja.zacznij = true;
                        }
                    }
                }
            }
        }
    }
}
