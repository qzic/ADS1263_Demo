/*# /*****************************************************************************
# * | File        :   RaspberryPiConfig.java
# * | Author      :   Quentin Meek - original in python by Waveshare team
# * | Function    :   Raspberry Pi interface
# * | Info        :
# *----------------
# * | This version:   V1.0
# * | Date        :   06/19/2025
# * | Info        :   
# ******************************************************************************
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documnetation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to  whom the Software is
# furished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS OR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
*/
package ca.qzic.ads1263demo;

import static ca.qzic.ads1263demo.Main.logger;
import com.diozero.api.*;
import static com.diozero.api.GpioPullUpDown.PULL_UP;
import com.diozero.util.SleepUtil;

public class RaspberryPiConfig {
    public static final int RST_PIN = 18;
    public static final int CS_PIN = 22;
    public static final int DRDY_PIN = 17;
    public static final int CONTROLLER = 0;
    public static final int SPI_SSS = 0;        // SPI Slave Select 0 or 1
    static public DigitalOutputDevice rstPin = new DigitalOutputDevice(RST_PIN, true, false);
    static public DigitalOutputDevice csPin = new DigitalOutputDevice(CS_PIN, true, false);
    static public DigitalInputDevice drdyPin = new DigitalInputDevice(DRDY_PIN,PULL_UP,GpioEventTrigger.RISING );
    static public SpiDeviceInterface spi;

    public int spiDeviceInit() {
        spi = SpiDevice.builder(CONTROLLER)
            .setChipSelect(SPI_SSS)
            .setFrequency(2_000_000)
            .setClockMode(SpiClockMode.MODE_1)
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
        if (pin == DRDY_PIN)
            return drdyPin.getValue();
        return false;
    }

    public void delayMs(int delaytime) {
        SleepUtil.sleepMillis(delaytime);
    }

    public void spiWriteBytes(byte[] data) {
        printBytes("Write Bytes",data);
        spi.write(data);
    }

    public byte[] spiReadBytes(int length) {
        byte[] dummy = new byte[length];
        byte[] data = spi.writeAndRead(dummy);
        printBytes("Read Bytes",data);
        return data;
    }

    public void printBytes(String label,byte[] data) {
        String outString="";
        outString += label + " ";
        for (int i = 0; i < data.length; i++) {
            outString += String.format("0x%02x, ", data[i]);
        }
        logger.debug(outString);
    }
}
