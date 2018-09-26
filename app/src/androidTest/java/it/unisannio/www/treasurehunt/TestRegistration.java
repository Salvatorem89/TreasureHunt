package it.unisannio.www.treasurehunt;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

/**
 * Created by Enrico on 26/09/2018.
 */

public class TestRegistration extends ActivityInstrumentationTestCase2<Registration> {

    private Registration reg;
    private Button registrati;

    public TestRegistration() {
        super(Registration.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        reg = getActivity();
        Thread.sleep(2000);
    }

    @Test
    public void  testRegWithoutAt() throws InterruptedException {
        Thread.sleep(2000);
        registrati = (Button) reg.findViewById(R.id.submit);
        //assertNotNull(registrati);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_J);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_I);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_M);
        Thread.sleep(2000);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
        Thread.sleep(1000);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
        Thread.sleep(1000);

        reg.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                registrati.performClick();
            }
        });
        onView(withText("Dati inseriti non validi")).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void testRegWithAT() throws InterruptedException {

        Thread.sleep(2000);
        registrati = (Button) reg.findViewById(R.id.submit);
        assertNotNull(registrati);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_S);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_A);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_L);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_V);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_O);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_AT);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_S);
        Thread.sleep(2000);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
        Thread.sleep(3000);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_S);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_A);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_L);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_V);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_O);
        Thread.sleep(2000);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
        Thread.sleep(2000);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_S);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_A);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_L);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_V);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_O);
        Thread.sleep(2000);

        reg.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                registrati.performClick();
            }
        });
        onView(withText("Email o username esistente")).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }


}

