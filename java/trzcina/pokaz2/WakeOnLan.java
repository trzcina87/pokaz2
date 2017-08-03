package trzcina.pokaz2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WakeOnLan {

    public static String TEMAC = "f4:ce:46:fc:b9:5c";
    public static String TEIP = "192.168.0.101";
    public static String TEBROADCAST = "192.168.0.255";

    private static byte[] zamienMAC(String mac)  {
        byte[] bajty = new byte[6];
        try {
            String[] tablicahex = mac.split(":");
            for (int i = 0; i < 6; i++) {
                bajty[i] = (byte)Integer.parseInt(tablicahex[i], 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bajty;
    }

    private static DatagramPacket stworzPakiet(String mac, String broadcast) {
        byte[] bajtymac = zamienMAC(mac);
        if(bajtymac != null) {
            try {
                byte[] bytes = new byte[6 + 16 * bajtymac.length];
                for (int i = 0; i < 6; i++) {
                    bytes[i] = (byte) 0xff;
                }
                for (int i = 6; i < bytes.length; i += bajtymac.length) {
                    System.arraycopy(bajtymac, 0, bytes, i, bajtymac.length);
                }
                InetAddress address = InetAddress.getByName(broadcast);
                return new DatagramPacket(bytes, bytes.length, address, 9);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private static boolean wyslijPakiet(DatagramPacket pakiet) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.send(pakiet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean wyslijWOL(String mac, String broadcast) {
        try {
            DatagramPacket pakiet = stworzPakiet(mac, broadcast);
            if(pakiet != null) {
                boolean wyslane = wyslijPakiet(pakiet);
                return wyslane;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
