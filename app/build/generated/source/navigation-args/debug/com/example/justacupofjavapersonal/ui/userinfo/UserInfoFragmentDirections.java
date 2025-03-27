package com.example.justacupofjavapersonal.ui.userinfo;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.justacupofjavapersonal.R;

public class UserInfoFragmentDirections {
  private UserInfoFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNavigationUserInfoToNavigationChangePassword() {
    return new ActionOnlyNavDirections(R.id.action_navigation_user_info_to_navigation_change_password);
  }
}
