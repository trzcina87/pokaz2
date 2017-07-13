package trzcina.pokaz2;

public class Rozne {

    public static void czekaj(int milisekundy) {
        try {
            Thread.sleep(milisekundy);
        } catch (InterruptedException e) {
        }
    }
}
