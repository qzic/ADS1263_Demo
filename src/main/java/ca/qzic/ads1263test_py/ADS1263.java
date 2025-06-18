/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.qzic.ads1263test_py;

import ca.qzic.ads1263test_py.ADS1263_Constants.*;
import static ca.qzic.ads1263test_py.RaspberryPiConfig.*;
import static java.lang.System.out;

public class ADS1263 {

    private int ScanMode = 0;

    public static RaspberryPiConfig rpi = new RaspberryPiConfig();

    public ADS1263() {
        ADS1263_reset();
        setMode(1);
    }

    /**
     * Resets via RST pin toggles
     */
    static public void ADS1263_reset() {
        rstPin.on();
        rpi.delayMs(300);
        rstPin.off();
        rpi.delayMs(300);
        rstPin.on();
        rpi.delayMs(300);
    }

    public void writeCmd(ADS1263_CMD cmd) {
        csPin.off();
        rpi.delayMs(1);
        rpi.spiWriteBytes(new byte[]{(byte) cmd.ordinal()});
        csPin.on();
    }

    public void writeReg(ADS1263_REG reg, byte data) {
        csPin.off();
        rpi.delayMs(1);
        rpi.spiWriteBytes(new byte[]{(byte) (ADS1263_CMD.CMD_WREG.ordinal() | reg.ordinal()), 0x00, data});
        csPin.on();
    }


    static public byte readReg(ADS1263_REG reg) {
        int regCmd;
        csPin.off();
        regCmd = ADS1263_CMD.CMD_RREG.ordinal() | reg.ordinal();
        rpi.spiWriteBytes(new byte[]{(byte) regCmd , 0x00});
        byte[] resp = rpi.spiReadBytes(1);
        csPin.on();
        return resp[0];
    }

    public byte checksum(int val, byte b) {
        int sum = 0, mask = 0xff;
        while (val != 0) {
            sum += (val & mask);
            val >>= 8;
        }
        sum += 0x9B;
        return (byte) ((sum ^ b) & 0xff);
    }

    public void waitDRDY() {
        long count = 0;
        while (drdyPin.getValue() != false) {
            if (++count > 400_000) {
                System.err.println("waitDRDY(): Time Out ...");
                break;
            }
        }
    }

    public int readChipID() {
        byte id = readReg(ADS1263_REG.REG_ID);
//        out.println("reg_id = " + id);
        return (id >> 5) & 0x07;
    }

    public void configADC1(ADS1263_GAIN gain, ADS1263_DRATE drate, ADS1263_DELAY delay) {
        csPin.off();
        byte mode2 = (byte) ((gain.ordinal() << 4) | drate.ordinal());
        writeReg(ADS1263_REG.REG_MODE2, mode2);
        rpi.delayMs(1);
        assert readReg(ADS1263_REG.REG_MODE2) == mode2;

        byte refmux = 0x24;
        writeReg(ADS1263_REG.REG_REFMUX, refmux);
        rpi.delayMs(1);
        assert readReg(ADS1263_REG.REG_REFMUX) == refmux;

        byte mode0 = (byte) delay.ordinal();
        writeReg(ADS1263_REG.REG_MODE0, mode0);
        rpi.delayMs(1);
        assert readReg(ADS1263_REG.REG_MODE0) == mode0;

        byte mode1 = (byte) 0x84;
        writeReg(ADS1263_REG.REG_MODE1, mode1);
        rpi.delayMs(1);
        assert readReg(ADS1263_REG.REG_MODE1) == mode1;
        csPin.on();
    }

    public void initADC1(ADS1263_DRATE rate) {
        rpi.spiDeviceInit();
        int chipID = readChipID();
        if (chipID != 1)
            throw new IllegalStateException("ID Read failed, ID = " + chipID);
        writeCmd(ADS1263_CMD.CMD_STOP1);
        configADC1(ADS1263_GAIN.ADS1263_GAIN_1, rate, ADS1263_DELAY.ADS1263_DELAY_35us);
        writeCmd(ADS1263_CMD.CMD_START1);
    }

    public long readADC1Data() {
        final int TIMEOUT = 50;
        int timeout = 0;
        byte[] status;
        csPin.off();
        rpi.delayMs(10);
        do {
            writeCmd(ADS1263_CMD.CMD_RDATA1);
//            rpi.delayMs(100);
            status = rpi.spiReadBytes(1);
            out.printf("status = %d, ", status[0]);
        } while ((status[0] & 0x40) == 0 && timeout++ < TIMEOUT);

        if (timeout >= TIMEOUT) {
            out.println("readADC1Data; timed out");
        }
        byte[] buf = rpi.spiReadBytes(5);  // 4 data bytes plus crc
        csPin.on();
        int raw = ((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16)
            | ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);
        if (checksum(raw, buf[4]) != 0)
            System.err.println("ADC1 data read error!  crc = " + buf[4]);
        return raw;
    }

    public long getChannel(int ch) {
        ScanMode = 0; // KLUDGE because somehow this gets set to 1 ????
        if (ScanMode == 0) {
            // single-ended; channels 0–10
            writeReg(ADS1263_REG.REG_INPMUX, (byte) ((ch << 4) | 0x0a));
        } else {
            // differential; 0–4
            writeReg(ADS1263_REG.REG_INPMUX, (byte) (((ch * 2) << 4) | ((ch * 2) + 1)));
        }
        waitDRDY();
        return readADC1Data();
    }

    public void setMode(int Mode) {
        if (Mode == 0) {
            ScanMode = 0;
        } else {
            ScanMode = 1;
        }
    }

    public void exit() {
        rpi.spiDeviceClose();
    }
}
