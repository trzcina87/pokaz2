package trzcina.pokaz2;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class PlikLogu {

    private static FileWriter pliklogu;

    public static void otworzLog() {
        try {
            pliklogu = new FileWriter("/sdcard/pokaz2.log");
        } catch (IOException e) {
            e.printStackTrace();
            pliklogu = null;
        }
    }

    public static void zamknijLog() {
        if(pliklogu != null) {
            try {
                pliklogu.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void zapiszDoLogu(String komunikat) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss.SSS");
        dateFormat.setTimeZone(TimeZone.getDefault());
        String data = dateFormat.format(System.currentTimeMillis());
        synchronized (AppService.service) {
            if(pliklogu != null) {
                try {
                    pliklogu.write(data + " " + komunikat + "\n");
                    pliklogu.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
