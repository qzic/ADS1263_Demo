/*# /*****************************************************************************
# * | File        :   ADS1263_Constants.java
# * | Author      :   Quentin Meek - original in python by Waveshare team
# * | Function    :   ADS1263 constants
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

public class ADS1263_Constants {

    enum ADS1263_GAIN {
        ADS1263_GAIN_1,
        ADS1263_GAIN_2,
        ADS1263_GAIN_4, 
        ADS1263_GAIN_8, 
        ADS1263_GAIN_16, 
        ADS1263_GAIN_32, 
        ADS1263_GAIN_64,
    }

    enum ADS1263_ADC2_GAIN {
        ADS1263_ADC2_GAIN_1,
        ADS1263_ADC2_GAIN_2,
        ADS1263_ADC2_GAIN_4,
        ADS1263_ADC2_GAIN_8,
        ADS1263_ADC2_GAIN_16,
        ADS1263_ADC2_GAIN_32,
        ADS1263_ADC2_GAIN_64,
        ADS1263_ADC2_GAIN_128,
    };
    
    enum ADS1263_DRATE {
        ADS1263_2d5SPS,
        ADS1263_5SPS,
        ADS1263_10SPS,
        ADS1263_16d6SPS,
        ADS1263_20SPS,
        ADS1263_50SPS,
        ADS1263_60SPS,
        ADS1263_100SPS,
        ADS1263_400SPS,
        ADS1263_1200SPS,
        ADS1263_2400SPS,
        ADS1263_4800SPS,
        ADS1263_7200SPS,
        ADS1263_14400SPS,
        ADS1263_19200SPS,
        ADS1263_38400SPS,
    };
    
    enum ADS1263_ADC2_DRATE {
    ADS1263_ADC2_10SPS,
    ADS1263_ADC2_100SPS,
    ADS1263_ADC2_400SPS,
    ADS1263_ADC2_800SPS;
}

    enum ADS1263_DELAY {
        ADS1263_DELAY_0s,
        ADS1263_DELAY_8d7us,
        ADS1263_DELAY_17us,
        ADS1263_DELAY_35us,
        ADS1263_DELAY_169us,
        ADS1263_DELAY_139us,
        ADS1263_DELAY_278us,
        ADS1263_DELAY_555us,
        ADS1263_DELAY_1d1ms,
        ADS1263_DELAY_2d2ms,
        ADS1263_DELAY_4d4ms,
        ADS1263_DELAY_8d8ms,
    };


    enum ADS1263_DAC_VOLT {
        ADS1263_DAC_VLOT_4_5(0b01001),
        ADS1263_DAC_VLOT_3_5(0b01000),
        ADS1263_DAC_VLOT_3(0b00111),
        ADS1263_DAC_VLOT_2_75(0b00110),
        ADS1263_DAC_VLOT_2_625(0b00101),
        ADS1263_DAC_VLOT_2_5625(0b00100),
        ADS1263_DAC_VLOT_2_53125(0b00011),
        ADS1263_DAC_VLOT_2_515625(0b00010),
        ADS1263_DAC_VLOT_2_5078125(0b00001),
        ADS1263_DAC_VLOT_2_5(0b00000),
        ADS1263_DAC_VLOT_2_4921875(0b10001),
        ADS1263_DAC_VLOT_2_484375(0b10010),
        ADS1263_DAC_VLOT_2_46875(0b10011),
        ADS1263_DAC_VLOT_2_4375(0b10100),
        ADS1263_DAC_VLOT_2_375(0b10101),
        ADS1263_DAC_VLOT_2_25(0b10110),
        ADS1263_DAC_VLOT_2(0b10111),
        ADS1263_DAC_VLOT_1_5(0b11000),
        ADS1263_DAC_VLOT_0_5(0b11001);

        private final int value;

        ADS1263_DAC_VOLT(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    };

    enum ADS1263_REG {
        /*
         * Register address, followed by reset the default values
         */
        REG_ID(0x00),
        REG_POWER(0x01),
        REG_INTERFACE(0x02),
        REG_MODE0(0x03),
        REG_MODE1(0x04),
        REG_MODE2(0x05),
        REG_INPMUX(0x06),
        REG_OFCAL0(0x07),
        REG_OFCAL1(0x08),
        REG_OFCAL2(0x09),
        REG_FSCAL0(0x0a),
        REG_FSCAL1(0x0b),
        REG_FSCAL2(0x0c),
        REG_IDACMUX(0x0d),
        REG_IDACMAG(0x0e),
        REG_REFMUX(0x0f),
        REG_TDACP(0x10),
        REG_TDACN(0x11),
        REG_GPIOCON(0x12),
        REG_GPIODIR(0x13),
        REG_GPIODAT(0x14),
        REG_ADC2CFG(0x15),
        REG_ADC2MUX(0x16),
        REG_ADC2OFC0(0x17),
        REG_ADC2OFC1(0x18),
        REG_ADC2FSC0(0x19),
        REG_ADC2FSC1(0x1a);

        private final int value;

        ADS1263_REG(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    };

    enum ADS1263_CMD {
        CMD_RESET(0x06), // Reset the ADC, 0000 011x (06h or 07h)
        CMD_START1(0x08), // Start ADC1 conversions, 0000 100x (08h or 09h)
        CMD_STOP1(0x0A), // Stop ADC1 conversions, 0000 101x (0Ah or 0Bh)
        CMD_START2(0x0C), // Start ADC2 conversions, 0000 110x (0Ch or 0Dh)
        CMD_STOP2(0x0E), // Stop ADC2 conversions, 0000 111x (0Eh or 0Fh)
        CMD_RDATA1(0x12), // Read ADC1 data, 0001 001x (12h or 13h)
        CMD_RDATA2(0x14), // Read ADC2 data, 0001 010x (14h or 15h)
        CMD_SYOCAL1(0x16), // ADC1 system offset calibration, 0001 0110 (16h)
        CMD_SYGCAL1(0x17), // ADC1 system gain calibration, 0001 0111 (17h)
        CMD_SFOCAL1(0x19), // ADC1 self offset calibration, 0001 1001 (19h)
        CMD_SYOCAL2(0x1B), // ADC2 system offset calibration, 0001 1011 (1Bh)
        CMD_SYGCAL2(0x1C), // ADC2 system gain calibration, 0001 1100 (1Ch)
        CMD_SFOCAL2(0x1E), // ADC2 self offset calibration, 0001 1110 (1Eh)
        CMD_RREG(0x20), // Read registers 001r rrrr (20h+000r rrrr)
        CMD_RREG2(0x00), // number of registers to read minus 1, 000n nnnn
        CMD_WREG(0x40), // Write registers 010r rrrr (40h+000r rrrr)
        CMD_WREG2(0x00);

        private final int value;

        ADS1263_CMD(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    };

}
