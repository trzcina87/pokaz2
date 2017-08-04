package trzcina.pokaz2;

import java.io.File;

@SuppressWarnings("FieldCanBeLocal")
public class Proces {

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

}
