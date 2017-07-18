package trzcina.pokaz2;

public class Rozne {

    public static void czekaj(int milisekundy) {
        try {
            Thread.sleep(milisekundy);
        } catch (InterruptedException e) {
        }
    }

    public static int zaokraglijWGoreDo10(int liczba) {
        if(liczba < 0) {
            return 0;
        }
        if(liczba == 0) {
            return 10;
        }
        for(int i = 1; i < 1000; i++) {
            if(liczba < i * 10) {
                return i * 10;
            }
        }
        return 10000;
    }
}
