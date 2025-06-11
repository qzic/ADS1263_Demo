/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.qzic.ads1263test_py;

/**
 *
 * @author Quentin
 */
import com.diozero.api.DigitalOutputDevice;
import com.diozero.api.DigitalInputDevice;
import com.diozero.api.SpiDevice;
import com.diozero.util.SleepUtil;
//import com.diozero.internal.provider.DeviceFactoryHelper;
import com.diozero.api.*;
import com.diozero.sbc.*;
import com.diozero.api.PinInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiozeroHardwareInterface {

    private SpiDevice spi;
    private final Map<Integer, DigitalOutputDevice> outputDevices = new HashMap<>();
    private final Map<Integer, DigitalInputDevice> inputDevices = new HashMap<>();

    public int moduleInit() {
        try {
            spi = (SpiDevice) DeviceFactoryHelper.getNativeDeviceFactory()
                .createSpiDevice("", 0, 0, 2_000_000, SpiClockMode.MODE_1, false);
            return 0; // success
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // failure
        }
    }

    public void moduleExit() {
        try {
            if (spi != null) spi.close();
            outputDevices.values().forEach(DigitalOutputDevice::close);
            inputDevices.values().forEach(DigitalInputDevice::close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void digitalWrite(int pin, boolean value) {
        DigitalOutputDevice device = outputDevices.computeIfAbsent(pin, p
            -> new DigitalOutputDevice(p));
        device.setValue(value);
    }

    public boolean digitalRead(int pin) {
        DigitalInputDevice device = inputDevices.computeIfAbsent(pin, p
            -> new DigitalInputDevice(p));
        return device.getValue();
    }

    public void delayMs(int delaytime) {
        SleepUtil.sleepMillis(delaytime);
    }

    public void spiWriteBytes(byte[] data) {
        spi.write(data);
    }

    public byte[] spiReadBytes(int length) {
        byte[] dummy = new byte[length];
        // Fill with dummy data (commonly 0x00 or 0xFF)
        for (int i = 0; i < length; i++) {
            dummy[i] = (byte) 0x00;
        }
        return spi.writeAndRead(dummy);

    }
}
