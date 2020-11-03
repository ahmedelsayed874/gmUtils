package com.blogspot.gm4s1.gmutils.listeners;


public interface ActionCallback<Args, Return> {
    Return invoke(Args input);
}
