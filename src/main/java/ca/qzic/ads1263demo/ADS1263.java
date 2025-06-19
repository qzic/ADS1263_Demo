/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.qzic.ads1263demo;

import ca.qzic.ads1263demo.ADS1263_Constants.*;
import static ca.qzic.ads1263demo.Main.logger;
import static ca.qzic.ads1263demo.RaspberryPiConfig.*;
import static java.lang.System.out;

public class ADS1263 {

    private int ScanMode = 1;
    public static RaspberryPiConfig rpi = new RaspberryPiConfig();

    public ADS1263() {
    }

    /**
     * Resets via RST pin toggles
     */
    public void reset() {
        rstPin.on();
        rpi.delayMs(200);
        rstPin.off();
        rpi.delayMs(200);
        rstPin.on();
        rpi.delayMs(200);
    }

    public void writeCmd(ADS1263_CMD cmd) {
        csPin.off();
        rpi.spiWriteBytes(new byte[]{(byte) cmd.getValue()});
        csPin.on();
    }

    public void writeReg(ADS1263_REG reg, byte data) {
        csPin.off();
        rpi.spiWriteBytes(new byte[]{(byte) (ADS1263_CMD.CMD_WREG.getValue() | reg.getValue()), 0x00, data});
        csPin.on();
    }

    static public byte readData(ADS1263_REG reg) {
        csPin.off();
        rpi.spiWriteBytes(new byte[]{(byte) (ADS1263_CMD.CMD_RREG.getValue() | reg.getValue()), 0x00});
        byte[] data = rpi.spiReadBytes(1);
        csPin.on();
        return data[0];
    }

    public byte checksum(int val, byte b) {
        int sum = 0, mask = 0xff;
        while (val != 0) {
            sum += (val & mask);
            val >>= 8;
        }
        sum += 0x9b;
        return (byte) ((sum & 0xff) ^ b);
    }

    public void waitDRDY() {
        int i = 0;
        while (drdyPin.getValue() != false) {
            if (++i > 400_000) {
                System.err.println("waitDRDY(): Time Out ...");
                break;
            }
        }
    }

    public int readChipID() {   // should be 35
        byte id = readData(ADS1263_REG.REG_ID);
        return (id >> 5);
    }

    public void setMode(int Mode) {
        this.ScanMode = Mode;
    }

    public void configADC1(ADS1263_GAIN gain, ADS1263_DRATE drate, ADS1263_DELAY delay) {
        byte retVal;
        csPin.off();
        byte mode2 = (byte) 0x80;
        mode2 |= ((byte) ((gain.ordinal() << 4) | drate.ordinal()));
        writeReg(ADS1263_REG.REG_MODE2, mode2);
        rpi.delayMs(1);
        retVal = readData(ADS1263_REG.REG_MODE2);
        if (retVal == mode2)
            logger.debug("REG_MODE2 success");
        else
            logger.error("REG_MODE2 unsuccess " + retVal);

        byte refmux = 0x24;
        writeReg(ADS1263_REG.REG_REFMUX, refmux);
        rpi.delayMs(1);
        retVal = readData(ADS1263_REG.REG_REFMUX);
        if (retVal == refmux)
            logger.debug("REG_REFMUX success");
        else
            logger.error("REG_REFMUX unsuccess " + retVal);

        byte mode0 = (byte) delay.ordinal();
        writeReg(ADS1263_REG.REG_MODE0, mode0);
        retVal = readData(ADS1263_REG.REG_MODE0);
        if (retVal == mode0)
            logger.debug("REG_MODE0 success");
        else
            logger.error("REG_MODE0 unsuccess " + retVal);

        byte mode1 = (byte) 0x84;
        writeReg(ADS1263_REG.REG_MODE1, mode1);
        rpi.delayMs(1);
        retVal = readData(ADS1263_REG.REG_MODE1);
        if (retVal == mode1)
            logger.debug("REG_MODE1 success");
        else
            logger.error("REG_MODE1 unsuccess " + retVal);
        csPin.on();
    }

    public void setChannel(int ch) {
        byte ipmux = (byte) ((ch << 4) | 0x0a);
        writeReg(ADS1263_REG.REG_INPMUX, ipmux);
        if ((readData(ADS1263_REG.REG_INPMUX) == ipmux)) {
            logger.debug("setChannel(ch) success");
        } else {
            logger.error("setChannel(ch) failed");
        }
    }

    public void initADC1(ADS1263_DRATE rate) {
        rpi.spiDeviceInit();
        reset();
        int chipID = readChipID();
        if (chipID != 1) out.printf("ID Read failed, ID = %d\n", chipID);
        writeCmd(ADS1263_CMD.CMD_STOP1);
        configADC1(ADS1263_GAIN.ADS1263_GAIN_1, rate, ADS1263_DELAY.ADS1263_DELAY_35us);
        writeCmd(ADS1263_CMD.CMD_START1);
    }

    public int readADC1Data() {
        final int TIMEOUT = 2;
        int timeout = 0;
        byte[] status;
        csPin.off();
        do {
            rpi.spiWriteBytes(new byte[]{(byte) ADS1263_CMD.CMD_RDATA1.getValue()});
            status = rpi.spiReadBytes(1);
        } while ((status[0] & 0x40) == 0 && timeout++ < TIMEOUT);

        if (timeout >= TIMEOUT) {
            logger.error("readADC1Data; timed out");
        }
        byte[] buf = rpi.spiReadBytes(5);  // 4 data bytes plus crc
        csPin.on();
        int read = ((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16)
            | ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);
        if (checksum(read, buf[4]) != 0)
            logger.error("ADC1 data read error!  crc = " + buf[4]);
        return read;
    }

    public int getChannelValue(int ch) {
        setChannel(ch);
        waitDRDY();
        return readADC1Data();
    }

    public int[] getAll(int[] list) {
        int[] ADC_Value = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            ADC_Value[i] = getChannelValue(i);
        }
        return ADC_Value;
    }

    public void exit() {
        rpi.spiDeviceClose();
    }
}
