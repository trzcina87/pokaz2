package trzcina.pokaz2;

public class WatekAnimacja extends Thread {

    public volatile boolean zakoncz;
    public volatile int startx;
    public volatile int starty;
    public volatile int x;
    public volatile int y;
    public volatile long startanimacji;
    public volatile boolean zacznij;
    public volatile int czaszdjecia;

    public WatekAnimacja() {
        zakoncz = false;
        zacznij = false;
        startanimacji = 0;
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
