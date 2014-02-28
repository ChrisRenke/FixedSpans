/*
 * Copyright 2014 Chris Renke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrisrenke.fixedspans;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import static com.chrisrenke.fixedspans.JustifySpan.Mode;
import static com.chrisrenke.fixedspans.JustifySpan.Mode.WHITESPACE_ONLY;
import static com.chrisrenke.fixedspans.JustifySpan.justify;
import static com.example.fixedspans.R.styleable;

/** Thin extension of {@link TextView} that automatically justifies its text. */
public class JustifyingTextView extends TextView {

  private static final Mode DEF_MODE = WHITESPACE_ONLY;
  private static final boolean DEF_LAST_LINE = false;
  private static final float DEF_WEIGHT = 2f;

  private Mode mode;
  private boolean justifyLastLine;
  private float whitespaceWeight;

  public JustifyingTextView(Context context) {
    this(context, null);
  }

  public JustifyingTextView(Context context, AttributeSet attrs) {
    this(context, attrs, android.R.attr.textViewStyle);
  }

  public JustifyingTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    TypedArray a = context.obtainStyledAttributes(attrs, styleable.JustifyingTextView, defStyle, 0);
    whitespaceWeight = getFloat(a, styleable.JustifyingTextView_whitespaceWeight, DEF_WEIGHT);
    mode = getEnum(a, styleable.JustifyingTextView_justifyMode, Mode.values(), DEF_MODE);
    justifyLastLine = getBoolean(a, styleable.JustifyingTextView_justifyLastLine, DEF_LAST_LINE);
    a.recycle();
    justify(this, justifyLastLine, whitespaceWeight, mode);
  }

  @Override
  protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    super.onTextChanged(text, start, lengthBefore, lengthAfter);
     justify(this, justifyLastLine, whitespaceWeight, mode);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    justify(this, justifyLastLine, whitespaceWeight, mode);
  }

  @Override public void setTextSize(float size) {
    super.setTextSize(size);
    justify(this, justifyLastLine, whitespaceWeight, mode);
  }

  @Override public void setTextSize(int unit, float size) {
    super.setTextSize(unit, size);
    justify(this, justifyLastLine, whitespaceWeight, mode);
  }

  @Override public void setTextAppearance(Context context, int resid) {
    super.setTextAppearance(context, resid);
    justify(this, justifyLastLine, whitespaceWeight, mode);
  }

  private static <E extends Enum<E>> E getEnum(TypedArray attributes, int styleableIndex,
      E[] values, E defValue) {
    int index = attributes.getInteger(styleableIndex, -1);
    if (index < 0) return defValue;
    return values[index];
  }

  private static boolean getBoolean(TypedArray attributes, int styleableIndex, boolean defValue) {
    return attributes.getBoolean(styleableIndex, defValue);
  }

  private static float getFloat(TypedArray attributes, int styleableIndex, float defValue) {
    return attributes.getFloat(styleableIndex, defValue);
  }
}
