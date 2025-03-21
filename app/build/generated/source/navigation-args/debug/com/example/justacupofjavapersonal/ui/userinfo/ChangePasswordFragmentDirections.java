package com.example.justacupofjavapersonal.ui.userinfo;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.justacupofjavapersonal.R;

public class ChangePasswordFragmentDirections {
  private ChangePasswordFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNavigationChangePasswordToNavigationUserInfo() {
    return new ActionOnlyNavDirections(R.id.action_navigation_change_password_to_navigation_user_info);
  }
}
