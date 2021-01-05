package com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blogspot.gm4s1.gmutils.net.retrofit.zcore.responseHolders.BaseResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
/*
    it's not suitable to declare this class in this package which is dedicated to retrofit requests only
    but I added it here for demonstration purpose
 */
public class TimeZones extends BaseResponse implements Collection<String> {
    private List<String> list = new ArrayList<>();

    @Override
    public Statuses getExternalStatus() {
        return list.size() > 0 ? Statuses.Succeeded : Statuses.Error;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.size() == 0;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return list.contains(o);
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(String s) {
        return list.add(s);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends String> c) {
        return list.addAll(c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }
}
