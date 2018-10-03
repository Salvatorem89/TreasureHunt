package it.unisannio.www.treasurehunt;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.Spinner;

import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Enrico on 03/10/2018.
 */

public class TestQuestion extends ActivityInstrumentationTestCase2<Question> {

    private Button confirm;
    private Button back;
    private Question question;
    private Spinner spinner;

    public TestQuestion(){ super(Question.class); }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        question = getActivity();
        Thread.sleep(2000);
    }

    @Test
    public void testSpinner()throws InterruptedException{
        spinner = question.findViewById(R.id.spinner);
        String selectionText = "Chi ha vinto il mondiale di calcio 2006";

        Thread.sleep(2000);
        onView(withId(R.id.spinner)).perform(click());
        Thread.sleep(2000);
        onData(allOf(is(instanceOf(String.class)), is(selectionText))).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString(selectionText))));
    }

}
