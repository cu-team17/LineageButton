package com.example.schubydooo.lineagebutton;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Process;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    int BLUE_LED = 20;
    int RED_LED = 21;
    int BLUE_BUTTON = 5;
    int RED_BUTTON = 6;

    String BUTTON_DOWN = "1";
    String BUTTON_UP = "0";

    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        try { initGpio(); } catch (IOException | InterruptedException e) { e.printStackTrace(); }
        try { watcher(); } catch (IOException | InterruptedException e) { e.printStackTrace(); }
    }

    private String[] genCommand(String command) {
        return new String[]{"/system/bin/su", "-c", command};
    }

    private String formatDecimal(String s, int d) {
        return String.format(Locale.ENGLISH, s, d);
    }

    public void initGpio() throws  IOException, InterruptedException {
        Runtime r = Runtime.getRuntime();
        Process p = null;

        //Initialize red button
        p = r.exec(genCommand(formatDecimal("echo  %d > /sys/class/gpio/export", RED_BUTTON))); p.waitFor();
        p = r.exec(genCommand(formatDecimal("echo  in > /sys/class/gpio/gpio%d/direction", RED_BUTTON))); p.waitFor();

        //Initialize blue button
        p = r.exec(genCommand(formatDecimal("echo  %d > /sys/class/gpio/export", BLUE_BUTTON))); p.waitFor();
        p = r.exec(genCommand(formatDecimal("echo  in > /sys/class/gpio/gpio%d/direction", BLUE_BUTTON))); p.waitFor();

        //Initialize blue LED
        p = r.exec(genCommand(formatDecimal("echo  %d > /sys/class/gpio/export", BLUE_LED))); p.waitFor();
        p = r.exec(genCommand(formatDecimal("echo  out > /sys/class/gpio/gpio%d/direction", BLUE_LED))); p.waitFor();
        p = r.exec(genCommand(formatDecimal("echo  0 > /sys/class/gpio/gpio%d/value", BLUE_LED))); p.waitFor();

        //Initialize red LED
        p = r.exec(genCommand(formatDecimal("echo  %d > /sys/class/gpio/export", RED_LED))); p.waitFor();
        p = r.exec(genCommand(formatDecimal("echo  out > /sys/class/gpio/gpio%d/direction", RED_LED))); p.waitFor();
        p = r.exec(genCommand(formatDecimal("echo  0 > /sys/class/gpio/gpio%d/value", RED_LED))); p.waitFor();
    }

    public void watcher() throws IOException, InterruptedException {
        Runtime r = Runtime.getRuntime();
        Process blueP;
        Process redP;

        String oldRedValue = "";
        String oldBlueValue = "";

        while(true) {
            blueP = r.exec(formatDecimal("cat /sys/class/gpio/gpio%d/value", BLUE_BUTTON));
            redP = r.exec(formatDecimal("cat /sys/class/gpio/gpio%d/value", RED_BUTTON));
            blueP.waitFor();
            redP.waitFor();

            BufferedReader blueB = new BufferedReader(new InputStreamReader(blueP.getInputStream()));
            BufferedReader redB = new BufferedReader(new InputStreamReader(redP.getInputStream()));

            String blueValue = blueB.readLine();
            String redValue = redB.readLine();

            blueB.close();
            redB.close();

            if (blueValue.equals(BUTTON_DOWN))
                changeLED(this.BLUE_LED);
            else if (!blueValue.equals(oldBlueValue))
                blueP = r.exec(genCommand(String.format(Locale.ENGLISH,"echo %s > /sys/class/gpio/gpio%d/value", "0", this.BLUE_LED))); blueP.waitFor();

            if (redValue.equals(BUTTON_DOWN))
                changeLED(this.RED_LED);
            else if (!redValue.equals(oldRedValue))
                redP = r.exec(genCommand(String.format(Locale.ENGLISH,"echo %s > /sys/class/gpio/gpio%d/value", "0", this.RED_LED))); redP.waitFor();

            oldBlueValue = blueValue;
            oldRedValue = redValue;
        }
    }

    public void changeLED(int led) throws IOException, InterruptedException {
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(genCommand(String.format(Locale.ENGLISH,"echo %s > /sys/class/gpio/gpio%d/value", "1", led))); p.waitFor();
        audioManager.adjustVolume(led == RED_LED ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        Thread.sleep(100);
    }

}

// /sys/class/gpio/gpioXX

// echo XX > 935