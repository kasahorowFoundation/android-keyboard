package com.anysoftkeyboard.ime;

import com.kasahorow.android.keyboard.app.R;
import com.anysoftkeyboard.keyboards.views.OnKeyboardActionListener;

public interface InputViewActionsProvider {

  /** Sets the listener of actions taken on this {@link InputViewActionsProvider}. */
  void setOnKeyboardActionListener(OnKeyboardActionListener keyboardActionListener);
}
