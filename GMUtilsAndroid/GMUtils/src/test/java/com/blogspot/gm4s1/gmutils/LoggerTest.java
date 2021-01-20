package com.blogspot.gm4s1.gmutils;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoggerTest {

    @Test
    public void divideMsg1() {
        int msgLen = Logger.MAX_LOG_LENGTH + 3999;
        int offset = 0;
        int expectedParts = 2; //(int) Math.ceil(msgLen / ((float) Logger.MAX_LOG_LENGTH));

        String msg = "";
        for (int i = 0; i < msgLen; i++) msg += "x";

        String[] strings = Logger.divideMsg(msg, offset);

        assertEquals(expectedParts, strings.length);
    }

    @Test
    public void divideMsg2() {
        int msgLen = Logger.MAX_LOG_LENGTH + 3999;
        int offset = 0;

        String msg = "";
        for (int i = 0; i < msgLen; i++) msg += "x";

        String[] strings = Logger.divideMsg(msg, offset);

        assertEquals(Logger.MAX_LOG_LENGTH, strings[0].length());
        assertEquals(3999, strings[1].length());
    }

    @Test
    public void divideMsg3() {
        int msgLen = Logger.MAX_LOG_LENGTH + 3999;
        int offset = 100;
        int expectedParts = 3;

        String msg = "";
        for (int i = 0; i < msgLen; i++) msg += "x";

        String[] strings = Logger.divideMsg(msg, offset);

        assertEquals(expectedParts, strings.length);
        //assertEquals(Logger.MAX_LOG_LENGTH, strings[0].length());
        //assertEquals(3999, strings[1].length());
    }

    @Test
    public void divideMsg4() {
        int msgLen = Logger.MAX_LOG_LENGTH + 3999;
        int offset = 100;
        int partLen = Logger.MAX_LOG_LENGTH - offset - 2;
        int remPartLen = msgLen - (2 * partLen);

        String msg = "";
        for (int i = 0; i < msgLen; i++) msg += "x";

        String[] strings = Logger.divideMsg(msg, offset);

        assertEquals(partLen, strings[0].length());
        assertEquals(partLen, strings[1].length());
        assertEquals(remPartLen, strings[2].length());
    }

}