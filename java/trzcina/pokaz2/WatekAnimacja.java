package trzcina.pokaz2;

public class WatekAnimacja extends Thread {

    public volatile boolean zakoncz;
    public volatile int startx;
    public volatile int starty;
    public volatile int x;
    public volatile int y;
    public volatile int dx;
    public volatile int dy;
    public volatile boolean zacznij;
    public volatile int opoznienie;

    public WatekAnimacja() {
        zakoncz = false;
        zacznij = false;
    }

    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(1);
            if((MainActivity.animacja) && (OpcjeProgramu.pokazslidow == 1)) {
                if(zacznij == true) {
                    zacznij = false;
                    opoznienie = Math.abs((int) (OpcjeProgramu.czaszdjecia * 1000 / (float)(2 * starty)));
                    x = startx;
                    y = starty;
                    if(startx < 0) {
                        dx = 1;
                    } else {
                        dx = -1;
                    }
                    if(starty < 0) {
                        dy = 1;
                    } else {
                        dy = -1;
                    }
                }
                Rozne.czekaj(opoznienie);
                x = x + dx;
                y = y + dy;
                AppService.service.watekrysuj.odswiez = true;
            }
        }
    }
}
