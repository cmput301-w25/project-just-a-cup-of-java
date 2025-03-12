package com.example.justacupofjavapersonal.ui.home;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.justacupofjavapersonal.R;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class HomeFragmentDirections {
  private HomeFragmentDirections() {
  }

  @NonNull
  public static ActionNavigationHomeToAddMoodEventFragment actionNavigationHomeToAddMoodEventFragment(
      @NonNull String selectedDate) {
    return new ActionNavigationHomeToAddMoodEventFragment(selectedDate);
  }

  @NonNull
  public static NavDirections actionNavigationHomeToMoodHistory() {
    return new ActionOnlyNavDirections(R.id.action_navigation_home_to_moodHistory);
  }

  public static class ActionNavigationHomeToAddMoodEventFragment implements NavDirections {
    private final HashMap arguments = new HashMap();

    @SuppressWarnings("unchecked")
    private ActionNavigationHomeToAddMoodEventFragment(@NonNull String selectedDate) {
      if (selectedDate == null) {
        throw new IllegalArgumentException("Argument \"selectedDate\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("selectedDate", selectedDate);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public ActionNavigationHomeToAddMoodEventFragment setSelectedDate(
        @NonNull String selectedDate) {
      if (selectedDate == null) {
        throw new IllegalArgumentException("Argument \"selectedDate\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("selectedDate", selectedDate);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NonNull
    public Bundle getArguments() {
      Bundle __result = new Bundle();
      if (arguments.containsKey("selectedDate")) {
        String selectedDate = (String) arguments.get("selectedDate");
        __result.putString("selectedDate", selectedDate);
      }
      return __result;
    }

    @Override
    public int getActionId() {
      return R.id.action_navigation_home_to_addMoodEventFragment;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public String getSelectedDate() {
      return (String) arguments.get("selectedDate");
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
          return true;
      }
      if (object == null || getClass() != object.getClass()) {
          return false;
      }
      ActionNavigationHomeToAddMoodEventFragment that = (ActionNavigationHomeToAddMoodEventFragment) object;
      if (arguments.containsKey("selectedDate") != that.arguments.containsKey("selectedDate")) {
        return false;
      }
      if (getSelectedDate() != null ? !getSelectedDate().equals(that.getSelectedDate()) : that.getSelectedDate() != null) {
        return false;
      }
      if (getActionId() != that.getActionId()) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int result = 1;
      result = 31 * result + (getSelectedDate() != null ? getSelectedDate().hashCode() : 0);
      result = 31 * result + getActionId();
      return result;
    }

    @Override
    public String toString() {
      return "ActionNavigationHomeToAddMoodEventFragment(actionId=" + getActionId() + "){"
          + "selectedDate=" + getSelectedDate()
          + "}";
    }
  }
}
