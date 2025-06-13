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
import java.util.Map;

public class ADS1263 {
    // Constants (gains, rates, commands)
    public enum Gain { GAIN1(0), GAIN2(1), GAIN4(2), GAIN8(3), GAIN16(4), GAIN32(5), GAIN64(6); 
    private final int code;
    Gain(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }}
    
    public enum Drate { SPS38400(0xF), SPS19200(0xE), SPS14400(0xD), /*...*/ SPS2_5(0x0); 
    private final int code;
    Drate(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    } }
    
    public enum ADC2Drate { SPS10(0), SPS100(1), SPS400(2), SPS800(3); 
    private final int code;
    ADC2Drate(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }}
    
    public enum Delay { DELAY_0(0), DELAY_8_7us(1), DELAY_17us(2), /*...*/ DELAY_8_8ms(11); 
    private final int code;

    Delay(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }}

    private static final int RST = Config.RST_PIN, CS = Config.CS_PIN, DRDY = Config.DRDY_PIN;

    // Register addresses
    private static final int REG_ID = 0, REG_MODE0 = 3, REG_MODE1 = 4, REG_MODE2 = 5;
    private static final int REG_INPMUX = 6, REG_REFMUX = 15, REG_ADC2CFG = 21;

    // Command bytes
    private static final byte CMD_RESET = 0x06, CMD_START1 = 0x08, CMD_STOP1 = 0x0A;
    private static final byte CMD_START2 = 0x0C, CMD_RDATA1 = 0x12, CMD_RDATA2 = 0x14;
    private static final byte CMD_RREG = 0x20, CMD_WREG = 0x40;

    private int ScanMode = 0;
    private RaspberryPiConfig hw;

    public ADS1263() { 
        hw = new RaspberryPiConfig();
    }

    /** Resets via RST pin toggles */
    public void reset() {
        Config.implementation.digitalWrite(RST, true);
        Config.implementation.delayMs(200);
        Config.implementation.digitalWrite(RST, false);
        Config.implementation.delayMs(200);
        Config.implementation.digitalWrite(RST, true);
        Config.implementation.delayMs(200);
    }

    public void writeCmd(byte cmd) {
        Config.implementation.digitalWrite(CS, false);
        Config.implementation.spiWriteBytes(new byte[]{cmd});
        Config.implementation.digitalWrite(CS, true);
    }

    public void writeReg(int reg, byte data) {
        Config.implementation.digitalWrite(CS, false);
        Config.implementation.spiWriteBytes(new byte[]{(byte)(CMD_WREG | reg), 0x00, data});
        Config.implementation.digitalWrite(CS, true);
    }

    public byte readReg(int reg) {
        Config.implementation.digitalWrite(CS, false);
        Config.implementation.spiWriteBytes(new byte[]{(byte)(CMD_RREG | reg), 0x00});
        byte[] resp = Config.implementation.spiReadBytes(1);
        Config.implementation.digitalWrite(CS, true);
        return resp[0];
    }

    public int checksum(int val, byte b) {
        int sum = 0, mask = 0xff;
        while (val != 0) {
            sum += val & mask;
            val >>= 8;
        }
        sum += 0x9B;
        return ((sum & 0xff) ^ b) & 0xff;
    }

    public void waitDRDY() {
        long count = 0;
        while (Config.implementation.digitalRead(DRDY) != false) {
            if (++count > 400_000) {
                System.err.println("Time Out ...");
                break;
            }
        }
    }

    public int readChipID() {
        byte id = readReg(REG_ID);
        out.println("reg_id = " + id);
        return (id >> 5) & 0x07;
    }

    public void configADC(Gain gain, Drate drate) {
        byte mode2 = (byte)((gain.ordinal() << 4) | drate.ordinal());
        writeReg(REG_MODE2, mode2);
        assert readReg(REG_MODE2) == mode2;

        byte refmux = 0x24;
        writeReg(REG_REFMUX, refmux);
        assert readReg(REG_REFMUX) == refmux;

        byte mode0 = (byte)Delay.DELAY_17us.ordinal();
        writeReg(REG_MODE0, mode0);
        assert readReg(REG_MODE0) == mode0;

        byte mode1 = (byte)0x84;
        writeReg(REG_MODE1, mode1);
        assert readReg(REG_MODE1) == mode1;
    }

    public void initADC1(Drate rate) {
        if (Config.implementation.moduleInit() != 0) throw new IllegalStateException("module_init failed");
        reset();
//        if (readChipID() != 1) throw new IllegalStateException("ID Read failed");
        writeCmd(CMD_STOP1);
        configADC(Gain.GAIN1, rate);
        writeCmd(CMD_START1);
    }

    public int readADC1Data() {
        Config.implementation.digitalWrite(CS, false);
        while (true) {
            writeCmd(CMD_RDATA1);
            byte status = Config.implementation.spiReadBytes(1)[0];
            if ((status & 0x40) != 0) break;
        }
        byte[] buf = Config.implementation.spiReadBytes(5);
        Config.implementation.digitalWrite(CS, true);

        int raw = ((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16)
                | ((buf[2] & 0xff) << 8) | (buf[3] & 0xff);
        if (checksum(raw, buf[4]) != 0) System.err.println("ADC1 data read error!");
        return raw;
    }

    public int getChannel(int ch) {
        ScanMode = 0; // KLUDGE because somehow this gets set to 1 ????
        if (ScanMode == 0) {
            // single-ended; channels 0–10
            writeReg(REG_INPMUX, (byte)((ch << 4) | 0x0a));
        } else {
            // differential; 0–4
            writeReg(REG_INPMUX, (byte)(((ch * 2) << 4) | ((ch * 2) + 1)));
        }
        waitDRDY();
        return readADC1Data();
    }

    public void exit() {
        Config.implementation.moduleExit();
    }
}

