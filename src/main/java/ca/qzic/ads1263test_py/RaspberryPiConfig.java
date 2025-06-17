/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.qzic.ads1263test_py;

/**
 *
 * @author Quentin
 */
import com.diozero.api.*;
import static com.diozero.api.SpiConstants.DEFAULT_SPI_CLOCK_MODE;
import com.diozero.util.SleepUtil;
import static java.lang.System.out;
import java.util.*;

public class RaspberryPiConfig {
    public static final int RST_PIN = 18;
    public static final int CS_PIN = 22;
    public static final int DRDY_PIN = 17;
    public static final int CONTROLLER = 0;
    public static final int PORT_NUMBER = 0;
    static public DigitalOutputDevice rstPin;
    private DigitalOutputDevice csPin;
    private DigitalInputDevice drdyPin;
    private SpiDeviceInterface spi;


    public int moduleInit() {
        
        rstPin = new DigitalOutputDevice(RST_PIN, true, false);
        csPin = new DigitalOutputDevice(CS_PIN, true, false);
        drdyPin = new DigitalInputDevice(DRDY_PIN);
        
        spi = SpiDevice.builder(CONTROLLER)
            .setChipSelect(PORT_NUMBER)
            .setFrequency(2_000_000)
            .setClockMode(SpiClockMode.MODE_0)
            .build();
        return 0;
    }


    public void moduleExit() {
        if (spi != null) spi.close();
        if (rstPin != null) rstPin.setValue(false);
        if (csPin != null) csPin.setValue(false);
        if (rstPin != null) rstPin.close();
        if (csPin != null) csPin.close();
        if (drdyPin != null) drdyPin.close();
    }


    public void digitalWrite(int pin, boolean value) {
        if (pin == RST_PIN) rstPin.setValue(value);
        else if (pin == CS_PIN) csPin.setValue(value);
    }


    public boolean digitalRead(int pin) {
        if (pin == DRDY_PIN) return drdyPin.getValue();
        return false;
    }


    public void delayMs(int delaytime) {
        SleepUtil.sleepMillis(delaytime);
    }


    public void spiWriteBytes(byte[] data) {
        spi.write(data);
    }


    public byte[] spiReadBytes(int length) {
        byte[] dummy = new byte[length];
        return spi.writeAndRead(dummy);
    }
}
