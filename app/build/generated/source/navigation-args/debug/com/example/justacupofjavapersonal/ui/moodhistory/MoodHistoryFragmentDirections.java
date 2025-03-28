package com.example.justacupofjavapersonal.ui.moodhistory;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.justacupofjavapersonal.R;

public class MoodHistoryFragmentDirections {
  private MoodHistoryFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNavigationMoodHistoryToHome() {
    return new ActionOnlyNavDirections(R.id.action_navigation_moodHistory_to_home);
  }
}
