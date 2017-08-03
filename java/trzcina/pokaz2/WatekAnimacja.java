package trzcina.pokaz2;

import java.util.Random;

@SuppressWarnings("PointlessBooleanExpression")
public class WatekAnimacja extends Thread {

    public static volatile boolean zakoncz;
    public static volatile int startx;
    public static volatile int starty;
    public static volatile int x;
    public static volatile int y;
    public static volatile long startanimacji;
    public static volatile boolean zacznij;
    public static volatile int czaszdjecia;

    public WatekAnimacja() {
        zakoncz = false;
        zacznij = false;
        startanimacji = 0;
        czaszdjecia = 2000;
        startx = 0;
        starty = 0;
        x = 0;
        y = 0;
    }

    public static void zacznijAnimacjeJesliTrzeba() {
        if(WatekRysuj.czyAnimowac()) {
            Random random = new Random();
            int znakx = Rozne.znak(random.nextInt(2));
            int znaky = Rozne.znak(random.nextInt(2));
            startx = (10 + random.nextInt(91)) * znakx;
            starty = (10 + random.nextInt(91)) * znaky;
            zacznij = true;
        }
    }

    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(1);
            if(WatekRysuj.czyAnimowac()) {
                if(zacznij == true) {
                    zacznij = false;
                    x = startx;
                    y = starty;
                    startanimacji = System.currentTimeMillis();
                    czaszdjecia = OpcjeProgramu.czaszdjecia * 1000;
                }
                double procentanimacji = (double)(System.currentTimeMillis() - startanimacji) / (double)czaszdjecia;
                if(startx < 0) {
                    x = (int)(startx + procentanimacji * (Math.abs(2 * startx)));
                } else {
                    x = (int)(startx - procentanimacji * (Math.abs(2 * startx)));
                }
                if(starty < 0) {
                    y = (int)(starty + procentanimacji * (Math.abs(2 * starty)));
                } else {
                    y = (int)(starty - procentanimacji * (Math.abs(2 * starty)));
                }
                AppService.watekrysuj.odswiez = true;
            }
        }
    }
}
