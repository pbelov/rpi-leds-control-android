package com.psbelov.rpi.leds.client.android;

import android.app.Activity;
import com.psbelov.rpi.leds.data.Command;
import com.psbelov.rpi.leds.data.CommandsEnum;

public class CommunicationHelper {
    private static Communication c;
    public static void setCommunication(Communication cc) {
        c = cc;
    }

    public static Communication getCommunication() {
        return c;
    }

    public static void setActivity(Activity activity) {
        c.setActivity(activity);
    }

    public static void setActivityIsPaused(boolean isPaused) {
        if (c != null) {
            c.seIsActivityPaused(isPaused);
        }
    }

    public static void sendConnect(String passwd) {
        c.sendCommand(new Command(CommandsEnum.CONNECT, passwd));
    }

    public static void sendDisconnect(String reason) {
        c.sendCommand(new Command(CommandsEnum.DISCONNECT, reason));
    }

    public static void sendGetLength() {
        c.sendCommand(new Command(CommandsEnum.LENGTH, null));
    }

    public static void sendTurnAllOn() {
        c.sendCommand(new Command(CommandsEnum.ON, null));
    }

    public static void sendTurnAllOff() {
        c.sendCommand(new Command(CommandsEnum.OFF, null));
    }

    public static void sendTurnAll(String rgbColor) {
        c.sendCommand(new Command(CommandsEnum.COLOR, rgbColor));
    }

    public static void sendIndex(int index, String rgbColor, boolean fill, boolean reverted) {
        if (reverted == false) {
            c.sendCommand(new Command((fill ? CommandsEnum.FILL : CommandsEnum.INDEX), index + ":" + rgbColor));
        } else {
            c.sendCommand(new Command((fill ? CommandsEnum.FILL_REVERTED : CommandsEnum.INDEX_REVERTED), index + ":" + rgbColor));
        }
    }

    public static void sendCancel() {
        c.sendCommand(new Command(CommandsEnum.CANCEL, null));
    }

    public static void sendRunChase(String rgbColor, boolean cycled) {
        c.sendCommand(new Command((cycled ? CommandsEnum.CHASE_CYCLED : CommandsEnum.CHASE), rgbColor));
    }

    public static void sendRunRandom(boolean isDynamic) {
        c.sendCommand(new Command(isDynamic ? CommandsEnum.RANDOM : CommandsEnum.RANDOM_SOLID, null));
    }

    public static void sendRunStars(String rgbColor) {
        c.sendCommand(new Command(CommandsEnum.STARS, rgbColor));
    }

    public static void sendRunStars2(String rgbColor) {
        c.sendCommand(new Command(CommandsEnum.STARS2, rgbColor));
    }

    public static void sendRainbow() {
        c.sendCommand(new Command(CommandsEnum.RAINBOW2, null));
    }

    public static void sendRainbowDynamic() {
        c.sendCommand(new Command(CommandsEnum.RAINBOW_DYNAMIC, null));
    }
}
