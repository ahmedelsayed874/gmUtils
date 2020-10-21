package com.blogspot.gm4s1.gmutils.dataStructure;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class RingQueue {
    private Object[] cells;
    private int lastCell = 0;
    private int startCell = 0;

    public RingQueue(int size) {
        cells = new Object[size];
    }

    public void push(Object obj) {
        cells[lastCell] = obj;
        lastCell++;
        lastCell %= cells.length;

        if (lastCell == startCell) {
            startCell++;
            startCell %= cells.length;
        }
    }

    public Object pull() {
        Object cell = cells[startCell];
        cells[startCell] = null;

        if (cell != null) {
            startCell++;
            startCell %= cells.length;
        }

        return cell;
    }

    public boolean hasNext() {
        Object cell = cells[startCell];
        return cell != null;
    }

}