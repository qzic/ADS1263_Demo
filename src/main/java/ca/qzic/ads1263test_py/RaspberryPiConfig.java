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

public class RaspberryPiConfig extends Config {
    private DigitalOutputDevice rstPin;
    private DigitalOutputDevice csPin;
    private DigitalInputDevice drdyPin;
    private SpiDeviceInterface spi;

    @Override
    public int moduleInit() {
        rstPin = new DigitalOutputDevice(RST_PIN, true, false);
        csPin = new DigitalOutputDevice(CS_PIN, true, false);
        drdyPin = new DigitalInputDevice(DRDY_PIN);

        spi = SpiDevice.builder(0)
            .setChipSelect(CS_PIN)
            .setFrequency(2_000_000)
            .setClockMode(DEFAULT_SPI_CLOCK_MODE)
            .build();
        return 0;
    }

    @Override
    public void moduleExit() {
        if (spi != null) spi.close();
        if (rstPin != null) rstPin.setValue(false);
        if (csPin != null) csPin.setValue(false);
        if (rstPin != null) rstPin.close();
        if (csPin != null) csPin.close();
        if (drdyPin != null) drdyPin.close();
    }

    @Override
    public void digitalWrite(int pin, boolean value) {
        if (pin == RST_PIN) rstPin.setValue(value);
        else if (pin == CS_PIN) csPin.setValue(value);
    }

    @Override
    public boolean digitalRead(int pin) {
        if (pin == DRDY_PIN) return drdyPin.getValue();
        return false;
    }

    @Override
    public void delayMs(int delaytime) {
        SleepUtil.sleepMillis(delaytime);
    }

    @Override
    public void spiWriteBytes(byte[] data) {
        spi.write(data);
    }

    @Override
    public byte[] spiReadBytes(int length) {
        byte[] dummy = new byte[length];
        return spi.writeAndRead(dummy);
    }
}
