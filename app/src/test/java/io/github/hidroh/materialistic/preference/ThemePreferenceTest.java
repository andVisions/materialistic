package io.github.hidroh.materialistic.preference;

import android.content.Intent;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.Arrays;
import java.util.List;

import io.github.hidroh.materialistic.Preferences;
import io.github.hidroh.materialistic.PreferencesActivity;
import io.github.hidroh.materialistic.R;
import io.github.hidroh.materialistic.test.ParameterizedRobolectricGradleTestRunner;
import io.github.hidroh.materialistic.test.ShadowSupportPreference;
import io.github.hidroh.materialistic.test.ShadowSupportPreferenceManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@Config(shadows = {ShadowSupportPreferenceManager.class, ShadowSupportPreference.class})
@RunWith(ParameterizedRobolectricGradleTestRunner.class)
public class ThemePreferenceTest {
    private final int preferenceId;
    private final int styleResId;
    private ActivityController<PreferencesActivity> controller;
    private PreferencesActivity activity;
    private View preferenceView;

    @ParameterizedRobolectricGradleTestRunner.Parameters
    public static List<Object[]> provideParameters() {
        return Arrays.asList(
                new Object[]{R.id.theme_dark, R.style.AppTheme_Dark},
                new Object[]{R.id.theme_light, R.style.AppTheme}
        );
    }

    public ThemePreferenceTest(int preferenceId, int styleResId) {
        this.preferenceId = preferenceId;
        this.styleResId = styleResId;
    }

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(PreferencesActivity.class);
        activity = controller.withIntent(new Intent()
                .putExtra(PreferencesActivity.EXTRA_TITLE, R.string.display)
                .putExtra(PreferencesActivity.EXTRA_PREFERENCES, R.xml.preferences_display))
                .create().postCreate(null).start().resume().visible().get();
        RecyclerView list = (RecyclerView) activity.findViewById(R.id.list);
        RecyclerView.Adapter adapter = list.getAdapter();
        int position = ShadowSupportPreferenceManager
                .getPreferencePosition((PreferenceGroupAdapter) adapter, ThemePreference.class);
        RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(list,
                adapter.getItemViewType(position));
        adapter.onBindViewHolder(holder, position);
        preferenceView = holder.itemView;
    }

    @Test
    public void test() {
        preferenceView.findViewById(preferenceId).performClick();
        Preferences.Theme.apply(activity, false, false);
        shadowOf(activity).recreate();
        assertThat(shadowOf(activity).callGetThemeResId()).isEqualTo(styleResId);
    }

    public void tearDown() {
        controller.pause().stop().destroy();
    }
}
