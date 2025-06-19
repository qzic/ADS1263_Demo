# ADS1263_Demo
This maven netbeans project demonstates the ADC1 features of the ADS1263 Raspberry Pi HAT
I have tested on a RPI 4B
1. Import this project into Netbeans (I am using v26)
2. Edit the pom file line 27 -  <platform>Your_RPi_IP_address</platform>
3. Click Run  
4. The project will built, downloaded to the RPi and Run
5. This is a swing project, a window will open on the desktop showing voltage values read
6. The 1st reading of all ten channels is printed to the console.
7. The GUI is continually updated every 200ms
