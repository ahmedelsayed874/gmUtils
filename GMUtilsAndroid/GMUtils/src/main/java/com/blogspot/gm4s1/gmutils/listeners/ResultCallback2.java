package com.blogspot.gm4s1.gmutils.listeners;

import com.blogspot.gm4s1.gmutils.R;

public interface ResultCallback2<R1, R2> {
    void invoke(R1 result1, R2 result2);
}