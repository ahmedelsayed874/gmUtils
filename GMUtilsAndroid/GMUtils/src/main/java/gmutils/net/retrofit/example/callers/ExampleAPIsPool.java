package gmutils.net.retrofit.example.callers;

import gmutils.DateOp;
import gmutils.net.retrofit.example.callers._interfaces.FileAPIs;
import gmutils.net.retrofit.example.callers._interfaces.ImageAPIs;
import gmutils.net.retrofit.example.callers._interfaces.TimeAPIs;
import gmutils.net.retrofit.example.callers.dummy.FakeImageAPI;
import gmutils.net.retrofit.example.callers.dummy.FakeTimeAPIs;

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
public class ExampleAPIsPool {

    public static DateOp TEST_DEADLINE = DateOp.getInstance("10-10-2020 00:00:00", DateOp.PATTERN_dd_MM_yyyy_HH_mm_ss);

    public static APIs instance() {
        if (System.currentTimeMillis() <= TEST_DEADLINE.getTimeInMillis()) {
            return new FakeAPI();
        } else {
            return new ProductionAPI();
        }
    }

    public interface APIs {
        FileAPIs fileAPIs();
        TimeAPIs timeAPIs();
        ImageAPIs imageAPI();
    }

    public static class ProductionAPI implements APIs {

        @Override
        public FileAPIs fileAPIs() {
            return new gmutils.net.retrofit.example.callers.production.FileAPIs();
        }

        @Override
        public TimeAPIs timeAPIs() {
            return new gmutils.net.retrofit.example.callers.production.TimeAPIs();
        }

        @Override
        public ImageAPIs imageAPI() {
            return new gmutils.net.retrofit.example.callers.production.ImageAPI();
        }
    }

    public static class FakeAPI implements APIs {

        @Override
        public FileAPIs fileAPIs() {
            return null;
        }

        @Override
        public TimeAPIs timeAPIs() {
            return new FakeTimeAPIs();
        }

        @Override
        public ImageAPIs imageAPI() {
            return new FakeImageAPI();
        }
    }

}