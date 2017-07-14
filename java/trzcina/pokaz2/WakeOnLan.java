package trzcina.pokaz2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WakeOnLan {

    public static String TEMAC = "f4:ce:46:fc:b9:5c";
    public static String TEIP = "192.168.0.101";

    private static byte[] zamienMAC(String mac)  {
        byte[] bajty = new byte[6];
        String[] tablicahex = mac.split(":");
        try {
            for (int i = 0; i < 6; i++) {
                bajty[i] = (byte)Integer.parseInt(tablicahex[i], 16);
            }
        } catch (NumberFormatException e) {
        }
        return bajty;
    }

    private static DatagramPacket stworzPakiet(String mac) {
        byte[] bajtymac = zamienMAC(mac);
        byte[] bytes = new byte[6 + 16 * bajtymac.length];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }
        for (int i = 6; i < bytes.length; i += bajtymac.length) {
            System.arraycopy(bajtymac, 0, bytes, i, bajtymac.length);
        }
        InetAddress address = null;
        try {
            address = InetAddress.getByName("192.168.0.255");
        } catch (UnknownHostException e) {
        }
        return new DatagramPacket(bytes, bytes.length, address, 9);
    }

    private static void wyslijPakiet(DatagramPacket pakiet) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.send(pakiet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void wyslijWOL(String mac) {
        try {
            DatagramPacket pakiet = stworzPakiet(mac);
            wyslijPakiet(pakiet);
            MainActivity.wyswietlToast("Serwer " + mac + " obudzony!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
