// Generated by view binder compiler. Do not edit!
package com.example.justacupofjavapersonal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.justacupofjavapersonal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class MoodHistoryFilterBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button applyFilterButton;

  @NonNull
  public final Spinner emotionSpinner;

  @NonNull
  public final EditText reasonKeywordEditText;

  @NonNull
  public final CheckBox recentWeekCheckBox;

  private MoodHistoryFilterBinding(@NonNull LinearLayout rootView,
      @NonNull Button applyFilterButton, @NonNull Spinner emotionSpinner,
      @NonNull EditText reasonKeywordEditText, @NonNull CheckBox recentWeekCheckBox) {
    this.rootView = rootView;
    this.applyFilterButton = applyFilterButton;
    this.emotionSpinner = emotionSpinner;
    this.reasonKeywordEditText = reasonKeywordEditText;
    this.recentWeekCheckBox = recentWeekCheckBox;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static MoodHistoryFilterBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static MoodHistoryFilterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.mood_history_filter, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static MoodHistoryFilterBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.applyFilterButton;
      Button applyFilterButton = ViewBindings.findChildViewById(rootView, id);
      if (applyFilterButton == null) {
        break missingId;
      }

      id = R.id.emotionSpinner;
      Spinner emotionSpinner = ViewBindings.findChildViewById(rootView, id);
      if (emotionSpinner == null) {
        break missingId;
      }

      id = R.id.reasonKeywordEditText;
      EditText reasonKeywordEditText = ViewBindings.findChildViewById(rootView, id);
      if (reasonKeywordEditText == null) {
        break missingId;
      }

      id = R.id.recentWeekCheckBox;
      CheckBox recentWeekCheckBox = ViewBindings.findChildViewById(rootView, id);
      if (recentWeekCheckBox == null) {
        break missingId;
      }

      return new MoodHistoryFilterBinding((LinearLayout) rootView, applyFilterButton,
          emotionSpinner, reasonKeywordEditText, recentWeekCheckBox);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
