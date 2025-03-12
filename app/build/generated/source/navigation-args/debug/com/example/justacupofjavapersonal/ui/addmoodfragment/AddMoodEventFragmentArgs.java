package com.example.justacupofjavapersonal.ui.addmoodfragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavArgs;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class AddMoodEventFragmentArgs implements NavArgs {
  private final HashMap arguments = new HashMap();

  private AddMoodEventFragmentArgs() {
  }

  @SuppressWarnings("unchecked")
  private AddMoodEventFragmentArgs(HashMap argumentsMap) {
    this.arguments.putAll(argumentsMap);
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public static AddMoodEventFragmentArgs fromBundle(@NonNull Bundle bundle) {
    AddMoodEventFragmentArgs __result = new AddMoodEventFragmentArgs();
    bundle.setClassLoader(AddMoodEventFragmentArgs.class.getClassLoader());
    if (bundle.containsKey("selectedDate")) {
      String selectedDate;
      selectedDate = bundle.getString("selectedDate");
      if (selectedDate == null) {
        throw new IllegalArgumentException("Argument \"selectedDate\" is marked as non-null but was passed a null value.");
      }
      __result.arguments.put("selectedDate", selectedDate);
    } else {
      throw new IllegalArgumentException("Required argument \"selectedDate\" is missing and does not have an android:defaultValue");
    }
    return __result;
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public static AddMoodEventFragmentArgs fromSavedStateHandle(
      @NonNull SavedStateHandle savedStateHandle) {
    AddMoodEventFragmentArgs __result = new AddMoodEventFragmentArgs();
    if (savedStateHandle.contains("selectedDate")) {
      String selectedDate;
      selectedDate = savedStateHandle.get("selectedDate");
      if (selectedDate == null) {
        throw new IllegalArgumentException("Argument \"selectedDate\" is marked as non-null but was passed a null value.");
      }
      __result.arguments.put("selectedDate", selectedDate);
    } else {
      throw new IllegalArgumentException("Required argument \"selectedDate\" is missing and does not have an android:defaultValue");
    }
    return __result;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public String getSelectedDate() {
    return (String) arguments.get("selectedDate");
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public Bundle toBundle() {
    Bundle __result = new Bundle();
    if (arguments.containsKey("selectedDate")) {
      String selectedDate = (String) arguments.get("selectedDate");
      __result.putString("selectedDate", selectedDate);
    }
    return __result;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public SavedStateHandle toSavedStateHandle() {
    SavedStateHandle __result = new SavedStateHandle();
    if (arguments.containsKey("selectedDate")) {
      String selectedDate = (String) arguments.get("selectedDate");
      __result.set("selectedDate", selectedDate);
    }
    return __result;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
        return true;
    }
    if (object == null || getClass() != object.getClass()) {
        return false;
    }
    AddMoodEventFragmentArgs that = (AddMoodEventFragmentArgs) object;
    if (arguments.containsKey("selectedDate") != that.arguments.containsKey("selectedDate")) {
      return false;
    }
    if (getSelectedDate() != null ? !getSelectedDate().equals(that.getSelectedDate()) : that.getSelectedDate() != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (getSelectedDate() != null ? getSelectedDate().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AddMoodEventFragmentArgs{"
        + "selectedDate=" + getSelectedDate()
        + "}";
  }

  public static final class Builder {
    private final HashMap arguments = new HashMap();

    @SuppressWarnings("unchecked")
    public Builder(@NonNull AddMoodEventFragmentArgs original) {
      this.arguments.putAll(original.arguments);
    }

    @SuppressWarnings("unchecked")
    public Builder(@NonNull String selectedDate) {
      if (selectedDate == null) {
        throw new IllegalArgumentException("Argument \"selectedDate\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("selectedDate", selectedDate);
    }

    @NonNull
    public AddMoodEventFragmentArgs build() {
      AddMoodEventFragmentArgs result = new AddMoodEventFragmentArgs(arguments);
      return result;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Builder setSelectedDate(@NonNull String selectedDate) {
      if (selectedDate == null) {
        throw new IllegalArgumentException("Argument \"selectedDate\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("selectedDate", selectedDate);
      return this;
    }

    @SuppressWarnings({"unchecked","GetterOnBuilder"})
    @NonNull
    public String getSelectedDate() {
      return (String) arguments.get("selectedDate");
    }
  }
}
