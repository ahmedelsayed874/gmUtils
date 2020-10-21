package com.blogspot.gm4s1.gmutils.listeners;


public interface ActionListener<Input, Return> {
    Return invoke(Input input);
}
