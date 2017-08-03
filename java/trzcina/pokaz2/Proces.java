package trzcina.pokaz2;

import java.io.File;

@SuppressWarnings("FieldCanBeLocal")
public class Proces {

    private static String SHPATH = "/system/bin/sh";
    private static String MOUNTCOMMAND = "/sdcard/montujpokaz2.sh";
    private static String UNMOUNTCOMMAND = "/sdcard/odmontujpokaz2.sh";

    public static int ping(String address) {
        try {
            Process process = new ProcessBuilder().redirectErrorStream(true).command("ping", "-W", "1", "-c", "1", address).start();
            return process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static boolean dostepnoscIP(String adres) {
        for(int i = 0; i < 3; i++) {
            int ping = Proces.ping(adres);
            if(ping == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean montuj(Long milisekundy) {
        String milisek = String.valueOf(milisekundy);
        try {
            Process process = new ProcessBuilder().redirectErrorStream(true).command(SHPATH, MOUNTCOMMAND, milisek).start();
            int wynik = process.waitFor();
            if(wynik == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean odmontuj(String sciezka) {
        try {
            String milisekundy = new File(sciezka).getName();
            Process process = new ProcessBuilder().redirectErrorStream(true).command(SHPATH, UNMOUNTCOMMAND, milisekundy).start();
            int wynik = process.waitFor();
            if(wynik == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
