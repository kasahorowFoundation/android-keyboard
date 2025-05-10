package com.anysoftkeyboard.ui.settings.setup;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.ComponentName;
import android.database.ContentObserver;
import android.os.Build;
import android.provider.Settings;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.anysoftkeyboard.AnySoftKeyboardRobolectricTestRunner;
import com.anysoftkeyboard.rx.TestRxSchedulers;
import com.menny.android.anysoftkeyboard.BuildConfig;
import com.menny.android.anysoftkeyboard.InputMethodManagerShadow;
import com.menny.android.anysoftkeyboard.R;
import com.menny.android.anysoftkeyboard.SoftKeyboard;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

@RunWith(AnySoftKeyboardRobolectricTestRunner.class)
public class SetUpKeyboardWizardTest {

  @Rule
  public ActivityScenarioRule<SetupWizardActivity> mActivityScenarioRule =
      new ActivityScenarioRule<>(SetupWizardActivity.class);

  @Before
  public void setup() {
    InputMethodManagerShadow.setKeyboardEnabled(RuntimeEnvironment.getApplication(), false);
  }

  @TargetApi(Build.VERSION_CODES.M)
  @Test
  @Config(sdk = Build.VERSION_CODES.M)
  public void testHasPermissionsPage() {
    mActivityScenarioRule.getScenario().moveToState(Lifecycle.State.STARTED);
    mActivityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              // ensuring we registered for Secure settings observing
              final Collection<ContentObserver> contentObservers =
                  Shadows.shadowOf(activity.getContentResolver())
                      .getContentObservers(Settings.Secure.CONTENT_URI);
              Assert.assertEquals(1, contentObservers.size());

              final ViewPager2 pager = activity.findViewById(R.id.wizard_pages_pager);
              Assert.assertNotNull(pager);
              Assert.assertEquals(5, pager.getAdapter().getItemCount());
              Assert.assertTrue(
                  ((FragmentStateAdapter) pager.getAdapter()).createFragment(3)
                      instanceof WizardPermissionsFragment);
            });
  }

  @Test
  /*I don't want to also verify the permissions page*/
  @Config(sdk = Build.VERSION_CODES.LOLLIPOP)
  public void testHappyPath() {
    mActivityScenarioRule.getScenario().moveToState(Lifecycle.State.STARTED);
    mActivityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              // ensuring we registered for Secure settings observing
              final ShadowContentResolver shadowContentResolver =
                  Shadows.shadowOf(activity.getContentResolver());
              final Collection<ContentObserver> contentObservers =
                  shadowContentResolver.getContentObservers(Settings.Secure.CONTENT_URI);
              Assert.assertEquals(1, contentObservers.size());

              final ViewPager2 pager = activity.findViewById(R.id.wizard_pages_pager);
              Assert.assertNotNull(pager);
              Assert.assertEquals(4, pager.getAdapter().getItemCount());
              // starts at page one - welcome keyboard
              Assert.assertEquals(0, pager.getCurrentItem());
              Assert.assertTrue(
                  ((FragmentStateAdapter) pager.getAdapter()).createFragment(0)
                      instanceof WizardPageWelcomeFragment);

              pager.findViewById(R.id.go_to_start_setup).performClick();
              TestRxSchedulers.foregroundAdvanceBy(1000 /*after the animation*/);

              // page two - enable ASK
              Assert.assertEquals(1, pager.getCurrentItem());
            });

    // now, lets say that ASK was enabled.
    // moving back to state CREATED (basically, pause then stop).
    mActivityScenarioRule.getScenario().moveToState(Lifecycle.State.CREATED);
    TestRxSchedulers.drainAllTasks();

    Application application = RuntimeEnvironment.getApplication();
    InputMethodManagerShadow.setKeyboardEnabled(application, true);
    final Collection<ContentObserver> contentObservers =
        Shadows.shadowOf(application.getContentResolver())
            .getContentObservers(Settings.Secure.CONTENT_URI);
    contentObservers.iterator().next().dispatchChange(false, Settings.Secure.CONTENT_URI);
    TestRxSchedulers.drainAllTasks();
    // notifying about the change.
    mActivityScenarioRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    TestRxSchedulers.foregroundAdvanceBy(1000 /*after the animation*/);

    // now at page three - activate keyboard
    mActivityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              final ViewPager2 pager = activity.findViewById(R.id.wizard_pages_pager);
              Assert.assertNotNull(pager);
              Assert.assertEquals(2, pager.getCurrentItem());
            });

    mActivityScenarioRule.getScenario().moveToState(Lifecycle.State.STARTED);
    mActivityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              final String flatASKComponent =
                  new ComponentName(BuildConfig.APPLICATION_ID, SoftKeyboard.class.getName())
                      .flattenToString();
              Settings.Secure.putString(
                  activity.getContentResolver(),
                  Settings.Secure.DEFAULT_INPUT_METHOD,
                  flatASKComponent);
              // notifying about the change.
              contentObservers.iterator().next().dispatchChange(false, Settings.Secure.CONTENT_URI);
              TestRxSchedulers.drainAllTasks();
            });
    mActivityScenarioRule.getScenario().moveToState(Lifecycle.State.RESUMED);
    mActivityScenarioRule
        .getScenario()
        .onActivity(
            activity -> {
              TestRxSchedulers.foregroundAdvanceBy(1000 /*after the animation*/);
              // now at page four - more settings.
              final ViewPager2 pager = activity.findViewById(R.id.wizard_pages_pager);
              Assert.assertNotNull(pager);
              Assert.assertEquals(3, pager.getCurrentItem());
            });

    // destroying the fragment should unregister from Secure content provider
    mActivityScenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
    Assert.assertEquals(
        0,
        Shadows.shadowOf(application.getContentResolver())
            .getContentObservers(Settings.Secure.CONTENT_URI)
            .size());
  }
}
