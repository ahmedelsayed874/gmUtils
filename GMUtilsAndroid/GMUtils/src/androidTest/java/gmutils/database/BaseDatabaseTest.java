package gmutils.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.*;

public class BaseDatabaseTest {


    @Test
    public void test() {
        class x extends BaseDatabase {

            public x(@NonNull Context context) {
                super(context);
            }

            @NonNull
            @Override
            protected String databaseName() {
                return null;
            }

            @Override
            protected int databaseVersion() {
                return 0;
            }

            @NonNull
            @Override
            protected Class<?>[] databaseEntities() {
                return new Class[0];
            }
        }

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        new x(context);
    }

}