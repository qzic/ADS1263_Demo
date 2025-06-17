/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.qzic.ads1263test_py;

/**
 *
 * @author Quentin
 */
import static ca.qzic.ads1263test_py.ADS1263.Delay.DELAY_35us;
import static ca.qzic.ads1263test_py.RaspberryPiConfig.*;
import static java.lang.System.out;

public class ADS1263 {

    // Constants (gains, rates, commands)
    public enum Gain {
        GAIN1(0), GAIN2(1), GAIN4(2), GAIN8(3), GAIN16(4), GAIN32(5), GAIN64(6);
        private final int code;

        Gain(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public enum Drate {
        SPS38400(0xF), SPS19200(0xE), SPS14400(0xD), /*
         * ...
         */ SPS2_5(0x0);
        private final int code;

        Drate(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public enum ADC2Drate {
        SPS10(0), SPS100(1), SPS400(2), SPS800(3);
        private final int code;

        ADC2Drate(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public enum Delay {
        DELAY_0(0), DELAY_8d7us(1), DELAY_17us(2), DELAY_35us(3), DELAY_8d8ms(11);
        private final int code;

        Delay(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    // Register addresses
    private static final int REG_ID = 0, REG_MODE0 = 3, REG_MODE1 = 4, REG_MODE2 = 5;
    private static final int REG_INPMUX = 6, REG_REFMUX = 15, REG_ADC2CFG = 21;

    // Command bytes
    private static final byte CMD_RESET = 0x06, CMD_START1 = 0x08, CMD_STOP1 = 0x0A;
    private static final byte CMD_START2 = 0x0C, CMD_RDATA1 = 0x12, CMD_RDATA2 = 0x14;
    private static final byte CMD_RREG = 0x20, CMD_WREG = 0x40;

    private int ScanMode = 0;

    public static RaspberryPiConfig rpi = new RaspberryPiConfig();

    public ADS1263() {

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

    public void writeCmd(byte cmd) {
        csPin.off();
        rpi.spiWriteBytes(new byte[]{cmd});
        csPin.on();
    }

    public void writeReg(int reg, byte data) {
        csPin.off();
        rpi.spiWriteBytes(new byte[]{(byte) (CMD_WREG | reg), 0x00, data});
        csPin.on();
    }

    public byte readReg(int reg) {
        csPin.off();
        rpi.spiWriteBytes(new byte[]{(byte) (CMD_RREG | reg), 0x00});
        rpi.delayMs(2);
        byte[] resp = rpi.spiReadBytes(1);
        csPin.on();
        return resp[0];
    }

    public byte checksum(long val, byte b) {
        int sum = 0, mask = -1;
        while (val != 0) {
            sum += val & mask;
            val >>= 8;
        }
        sum += 0x9B;
        return (byte) ((sum ^ b) & 0xff);
    }

    public void waitDRDY() {
        long count = 0;
        while (rpi.digitalRead(DRDY_PIN) != false) {
            if (++count > 400_000) {
                System.err.println("waitDRDY(): Time Out ...");
                break;
            }
        }
    }

    public int readChipID() {
        byte id = readReg(REG_ID);
//        out.println("reg_id = " + id);
        return (id >> 5) & 0x07;
    }

    public void configADC1(Gain gain, Drate drate, Delay delay) {
        csPin.off();
        byte mode2 = (byte) ((gain.ordinal() << 4) | drate.ordinal());
        writeReg(REG_MODE2, mode2);
        rpi.delayMs(1);
        assert readReg(REG_MODE2) == mode2;

        byte refmux = 0x24;
        writeReg(REG_REFMUX, refmux);
        rpi.delayMs(1);
        assert readReg(REG_REFMUX) == refmux;

        byte mode0 = (byte) delay.ordinal();
        writeReg(REG_MODE0, mode0);
        rpi.delayMs(1);
        assert readReg(REG_MODE0) == mode0;

        byte mode1 = (byte) 0x84;
        writeReg(REG_MODE1, mode1);
        rpi.delayMs(1);
        assert readReg(REG_MODE1) == mode1;
        csPin.on();
    }

    public void initADC1(Drate rate) {
        ADS1263_reset();
        rpi.moduleInit();
//        rpi.delayMs(100);
//        int chipID = readChipID();
//        if (readChipID() != 1) throw new IllegalStateException("ID Read failed, ID = " + chipID);
        writeCmd(CMD_STOP1);
        configADC1(Gain.GAIN1, rate, DELAY_35us);
        writeCmd(CMD_START1);
    }

    public long readADC1Data() {
        final int TIMEOUT = 40;
        int timeout = 0;
        byte[] status;
        csPin.off();
        rpi.delayMs(10);
//        do {
//            writeCmd(CMD_RDATA1);
//            rpi.delayMs(10);
//            status = rpi.spiReadBytes(1);
//        } while((status[0] & 0x40) == 0 && timeout++ < TIMEOUT);
//        
//        if(timeout >= TIMEOUT) {
//            out.println("readADC1Data; timed out");
//        }
        byte[] buf = rpi.spiReadBytes(5);  // 4 data bytes plus crc
        csPin.on();
        long raw = ((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16)
            | ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);
        if (checksum(raw, buf[4]) != 0)
            System.err.println("ADC1 data read error!  crc = " + buf[4]);
        return raw;
    }

    public long getChannel(int ch) {
        ScanMode = 0; // KLUDGE because somehow this gets set to 1 ????
        if (ScanMode == 0) {
            // single-ended; channels 0–10
            writeReg(REG_INPMUX, (byte) ((ch << 4) | 0x0a));
        } else {
            // differential; 0–4
            writeReg(REG_INPMUX, (byte) (((ch * 2) << 4) | ((ch * 2) + 1)));
        }
//        waitDRDY();
        return readADC1Data();
    }

    public void setMode(byte Mode) {
        if (Mode == 0) {
            ScanMode = 0;
        } else {
            ScanMode = 1;
        }
    }

    public void exit() {
        rpi.moduleExit();
    }
}
