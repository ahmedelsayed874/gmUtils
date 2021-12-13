package com.blogspot.gm4s.gmutileexample

import androidx.test.core.app.ActivityScenario
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.blogspot.gm4s.gmutileexample.activities.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    @Test
    fun test_isActivityInview() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        //onView(withId(R.id.openBtn)).check(matches(isDisplayed()))
    }
}


















