package com.blogspot.gm4s.gmutileexample;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmutils.collections.dataGroup.DataGroup1;
import gmutils.collections.ListWrapper;
import gmutils.collections.MapWrapper;
import gmutils.collections.dataGroup.DataGroup2;

public class ListAndMapWrappersExample {

    public void listWrapperExample() {

        //Without using List Wrapper
        List<Integer> list1 = new ArrayList<>();
        list1.add(1);
        list1.add(2);

        list1.addAll(Arrays.asList(3, 4, 5));

        for (Integer integer : Arrays.asList(6, 7, 8)) {
            list1.add(integer);
        }

        System.out.println(list1);


        //WITH List Wrapper
        List<Integer> list2 = ListWrapper.create(Integer.class) //OR-> ListWrapper.create([INSTANCE OF ANY TYPE OF LISTS])
                .add(1)
                .add(2)

                .add(Arrays.asList(3, 4, 5))

                .add(3, index -> {
                    return new int[]{6, 7, 8}[index];
                })

                //extra methods
                .filter(val -> {
                    return val % 2 == 0;
                }, r ->{
                    Log.d("***", "new list: " + r);
                })
                .map(val -> {
                    return "Value: " + val;
                }, result -> {
                    System.out.println(result);
                })
                .getList();

        System.out.println(list2);
    }

    public void mapWrapperExample() {

        //Without using List Wrapper
        Map<Integer, String> map1 = new HashMap<>();
        map1.put(1, "a");
        map1.put(2, "b");

        Map<Integer, String> otherMapForM1 = new HashMap<>();
        otherMapForM1.put(3, "c");
        otherMapForM1.put(4, "d");
        otherMapForM1.put(5, "e");
        map1.putAll(otherMapForM1);

        Map<Integer, String> otherMap2ForM1 = new HashMap<>();
        otherMap2ForM1.put(6, "f");
        otherMap2ForM1.put(7, "g");
        otherMap2ForM1.put(8, "h");
        for (Map.Entry<Integer, String> entry : otherMap2ForM1.entrySet()) {
            map1.put(entry.getKey(), entry.getValue());
        }

        System.out.println(map1.size());


        //WITH List Wrapper
        Map<Integer, String> map2 = MapWrapper.create(Integer.class, String.class) //OR-> MapWrapper.create([INSTANCE OF ANY TYPE OF MAPS])
                .add(1, "a")
                .add(2, "b")

                .add(Arrays.asList(3, 4, 5), Arrays.asList("c", "d", "e")) //or
                .add(ListWrapper.create(new ArrayList<DataGroup2<Integer, String>>())
                        .add(new DataGroup2<>(3, "c"))
                        .add(new DataGroup2<>(4, "d"))
                        .add(new DataGroup2<>(5, "e"))
                        .getList()
                )

                //extra methods
                .filter((k, v) -> {
                    return k % 2 == 0;
                }, result -> {
                    Log.d("****", "new map: " + result);
                })
                .map((k, v) -> {
                    return "Key: " + k + " is mapping Value: " + v;
                }, result -> {
                    System.out.println(result);
                })

                .getMap();

        System.out.println(map2);
    }

}
