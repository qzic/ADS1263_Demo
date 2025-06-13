/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.qzic.ads1263test_py;
/**
 *
 * @author Quentin
 */
import static java.lang.System.out;

public abstract class Config {
    public static final int RST_PIN = 18;
    public static final int CS_PIN = 22;
    public static final int DRDY_PIN = 17;

    public abstract void digitalWrite(int pin, boolean value);
    public abstract boolean digitalRead(int pin);
    public abstract void delayMs(int delaytime);
    public abstract void spiWriteBytes(byte[] data);
    public abstract byte[] spiReadBytes(int length);
    public abstract int moduleInit();
    public abstract void moduleExit();

    public static Config implementation;

    static {
        String hostname = System.getenv("HOSTNAME");
        if (hostname == null || hostname.isEmpty()) {
            hostname = execCommand("uname -n").trim();
            out.println(hostname);
        }

        if (hostname.equals("RPi-3-1")) {
            implementation = new RaspberryPiConfig();
        }
    }

    private static String execCommand(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return new String(p.getInputStream().readAllBytes());
        } catch (Exception e) {
            return "";
        }
    }
}
