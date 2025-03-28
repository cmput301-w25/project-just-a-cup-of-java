package com.example.justacupofjavapersonal.ui.login;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.justacupofjavapersonal.R;

public class LoginFragmentDirections {
  private LoginFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNavigationLoginToHome() {
    return new ActionOnlyNavDirections(R.id.action_navigation_login_to_home);
  }

  @NonNull
  public static NavDirections actionNavigationLoginToNavigationUserInfo2() {
    return new ActionOnlyNavDirections(R.id.action_navigation_login_to_navigation_user_info2);
  }
}
