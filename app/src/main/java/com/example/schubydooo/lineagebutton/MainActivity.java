package com.example.schubydooo.lineagebutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Process;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("onCreate", "Starting shenanigans");
        try {
            initGpio();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initGpio() throws  IOException {
        Runtime r = Runtime.getRuntime();
        Process p = null;

        //gpio assignment
        String redButton = "21";
        String blueButton = "20";
        String redLED = "6";
        String blueLED = "5";

        //Initialize red button
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  %s > /sys/class/gpio/export", redButton)});
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  in > /sys/class/gpio/gpio%s/direction", redButton)});

        //Initialize blue button
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  %s > /sys/class/gpio/export", blueButton)});
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  in > /sys/class/gpio/gpio%s/direction", blueButton)});

        //Initialize blue LED
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  %s > /sys/class/gpio/export", blueLED)});
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  out > /sys/class/gpio/gpio%s/direction", blueLED)});
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  1 > /sys/class/gpio/gpio%s/value", blueLED)});

        //Initialize red LED
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  %s > /sys/class/gpio/export", redLED)});
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  out > /sys/class/gpio/gpio%s/direction", redLED)});
        p = r.exec(new String[]{"/system/bin/su", "-c", String.format("echo  1 > /sys/class/gpio/gpio%s/value", redLED)});
    }


    public void turnOnLED() throws IOException {
        Runtime r = Runtime.getRuntime();
        Process p = null;
        while(true) {
            try {
                p = r.exec("cat /sys/class/gpio/gpio21/value");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";

            line = b.readLine();

            if (line.equals("0"))
            {
                Process p2 = Runtime.getRuntime().exec("su -c 'echo 1 > /sys/class/gpio/gpio5/value'");
                Log.i("scotttt", "line is 0");
                try {
                    p2.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BufferedReader c = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                line = c.readLine();
                Log.i("aoeu", "--: " + line);
            }
            else if (line.equals("1"))
            {
                p = r.exec("su -c 'echo 0 > /sys/class/gpio/gpio5/value'");
            }

            b.close();
        }

    }

}

// /sys/class/gpio/gpioXX

// echo XX > 935