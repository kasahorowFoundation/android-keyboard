package com.kasahorow.keyboard.support.ui;

/*
 * Copyright (c) 2023 Menny Even-Danan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* The following code was written by Matthew Wiggins
 * and is released under the APACHE 2.0 license
 *
 * additional code was written by Menny Even Danan, and is also released under APACHE 2.0 license
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.anysoftkeyboard.base.utils.Logger;
import com.kasahorow.keyboard.R;

public class SupportKeyboardController {
  private static final String TAG = "SupportKeyboardController";

  private final Context context;
  private final String mSelectedKeyboardName;
  private final String mSourceScreen;

  public SupportKeyboardController(
      @NonNull Context context,
      @NonNull String selectedKeyboardName,
      @NonNull String sourceScreen) {
    this.context = context;
    this.mSelectedKeyboardName = selectedKeyboardName;
    this.mSourceScreen = sourceScreen;
  }

  public void launchSupportKeyboard() {
    startSupportActivity(context, mSelectedKeyboardName, mSourceScreen);
  }

  public static void startSupportActivity(
      @NonNull Context context,
      @NonNull String selectedKeyboardName,
      @NonNull String sourceScreen) {
    try {
      String supportKeyboardUrl =
          context.getString(R.string.support_keyboard_url, sourceScreen, selectedKeyboardName);
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(supportKeyboardUrl));
      browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(browserIntent);
    } catch (Exception ex) {
      Logger.e(TAG, "Could not launch Store search!", ex);
      Toast.makeText(context, context.getText(R.string.could_not_open_browser), Toast.LENGTH_LONG)
          .show();
    }
  }
}
