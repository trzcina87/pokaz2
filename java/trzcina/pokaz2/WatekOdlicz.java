package trzcina.pokaz2;

public class WatekOdlicz extends Thread {

    public volatile boolean zakoncz;
    private long ostatniczas;

    public WatekOdlicz() {
        zakoncz = false;
        ostatniczas = System.currentTimeMillis();
    }

    public void run() {
        while(zakoncz == false) {
            Rozne.czekaj(1);
            if(OpcjeProgramu.pokazslidow == 1) {
                if(System.currentTimeMillis() > ostatniczas + OpcjeProgramu.czaszdjecia * 1000) {
                    if (MainActivity.powiekszenie == 0) {
                        int iloscplikow = AppService.service.watekwczytaj.iloscplikow;
                        if (iloscplikow > 0) {
                            MainActivity.ktoryplik = MainActivity.ktoryplik + 1;
                            if (MainActivity.ktoryplik >= iloscplikow) {
                                MainActivity.ktoryplik = 0;
                            }
                            MainActivity.activity.resetujPrzesuj();
                            ostatniczas = System.currentTimeMillis();
                            AppService.service.watekrysuj.odswiez = true;
                        }
                    }
                }
            }
        }
    }
}
