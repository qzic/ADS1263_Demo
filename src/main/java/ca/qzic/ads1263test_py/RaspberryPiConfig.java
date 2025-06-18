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
    public static final int SPI_CS = 0;
    static public DigitalOutputDevice rstPin = new DigitalOutputDevice(RST_PIN, true, false);
    static public DigitalOutputDevice csPin = new DigitalOutputDevice(CS_PIN, true, false);
    static public DigitalInputDevice drdyPin = new DigitalInputDevice(DRDY_PIN);
    static public SpiDeviceInterface spi;

    public int spiDeviceInit() {        
        spi = SpiDevice.builder(CONTROLLER)
            .setChipSelect(SPI_CS)
            .setFrequency(2_000_000)
            .setClockMode(SpiClockMode.MODE_0)
            .build();
        return 0;
    }

    public void spiDeviceClose() {
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
//        if (data.length > 1) out.printf("byte[0] = 0x%x, byte[1] = 0x%x\n", data[0],data[1]);
//        else out.printf("byte[0] = 0x%x\n", data[0]);
        spi.write(data);
    }

    public byte[] spiReadBytes(int length) {
        byte[] dummy = new byte[length];
        return spi.writeAndRead(dummy);
    }
}
