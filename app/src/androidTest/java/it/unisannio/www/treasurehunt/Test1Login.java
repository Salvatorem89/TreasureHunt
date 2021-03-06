package it.unisannio.www.treasurehunt;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Button;

import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

//Test sulla activity Login


public class Test1Login extends ActivityInstrumentationTestCase2<Login> {

    private Button loggati;
    private Button registrati;
    private Login log;

    public Test1Login() { super(Login.class); }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        log = getActivity();
        Thread.sleep(3000);
    }

    //controllo se l'utente inserisce un username o password non presente nel database
    @Test
    public void testNotExistingUser() throws InterruptedException{

        loggati = log.findViewById(R.id.sign);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_S);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_A);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_L);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_V);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
        Thread.sleep(1000);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_S);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_A);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_L);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_V);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_O);
        Thread.sleep(1000);

        log.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loggati.performClick();
            }
        });

        Thread.sleep(2000);
        onView(withText("Username o Password errate")).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    //controlla se l'utente lascia il campo vuoto
    @Test
    public void testLogNull() throws InterruptedException{

        loggati = log.findViewById(R.id.sign);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_S);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_A);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_L);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_V);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
        Thread.sleep(1000);


        log.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loggati.performClick();
            }
        });

        Thread.sleep(2000);
        onView(withText("Dati inseriti non validi")).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    //verifica se l'utente registrato riesce ad accedere correttamente
    @Test
    public void testCorrectLogin() throws InterruptedException{

        loggati = log.findViewById(R.id.sign);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_S);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_A);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_L);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_V);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_O);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_TAB);
        Thread.sleep(2000);

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_S);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_A);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_L);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_V);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_O);
        Thread.sleep(2000);

        log.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loggati.performClick();
            }
        });

        Thread.sleep(2000);
        onView(withText("Login avvenuto con successo")).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

}
