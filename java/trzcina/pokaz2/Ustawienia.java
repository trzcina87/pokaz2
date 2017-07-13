package trzcina.pokaz2;

import android.content.Context;
import android.content.SharedPreferences;

public class Ustawienia {

    public static String wczytajUstawienie(String klucz) {
        SharedPreferences sharedPref = MainActivity.activity.getSharedPreferences("UST", Context.MODE_PRIVATE);
        return sharedPref.getString(klucz, null);
    }

    public static int wczytajUstawienieInt(String klucz) {
        SharedPreferences sharedPref = MainActivity.activity.getSharedPreferences("UST", Context.MODE_PRIVATE);
        return sharedPref.getInt(klucz, -1);
    }

    public static void zapiszUstawienie(String klucz, String wartosc) {
        SharedPreferences sharedPref = MainActivity.activity.getSharedPreferences("UST", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(klucz, wartosc);
        editor.commit();
    }

    public static void zapiszUstawienie(String klucz, int wartosc) {
        SharedPreferences sharedPref = MainActivity.activity.getSharedPreferences("UST", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(klucz, wartosc);
        editor.commit();
    }

}
