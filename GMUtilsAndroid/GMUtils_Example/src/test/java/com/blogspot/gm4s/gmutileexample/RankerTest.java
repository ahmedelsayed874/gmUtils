package com.blogspot.gm4s.gmutileexample;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RankerTest {

    @Test
    public void testRankWith5Values() {
        Integer[] scores = new Integer[] { 1, 3, 5, 5, 3 };
        Integer[] expectedRanks = new Integer[] { 3, 2, 1, 1, 2 };

        Ranker ranker = new Ranker();
        Integer[] result = ranker.rank(Arrays.asList(scores));

        assertArrayEquals(expectedRanks, result);
    }

    @Test
    public void testRankWith1000Values() {
        Integer[] scores = new Integer[1000];
        Integer[] expectedRanks = new Integer[1000];

        for (int i = 0 ; i < 1000; i++) {
            scores[i] = i;
            expectedRanks[i] = 1000 - i;
        }

        Ranker ranker = new Ranker();
        Integer[] result = ranker.rank(Arrays.asList(scores));

        assertArrayEquals(expectedRanks, result);
    }

}