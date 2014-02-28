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

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import static android.graphics.Color.argb;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

public class ShadowedCrossFadeSpan extends CharacterStyle {

  private final int initialTextColor;
  private final int endTextColor;
  private final int initialShadowColor;
  private final int endShadowColor;
  private final int initialShadowOpacity;
  private final int endShadowOpacity;
  private final int shadowOffsetX;
  private final int shadowOffsetY;
  private final int shadowRadius;

  private int currentColor;
  private int currentShadowColor;
  private int currentShadowOpacity;

  public ShadowedCrossFadeSpan(int initialTextColor, int endTextColor, int shadowColor,
      int initialShadowOpacity, int endShadowOpacity, int shadowOffsetX, int shadowOffsetY,
      int shadowRadius) {
    this(initialTextColor, endTextColor, shadowColor, shadowColor, initialShadowOpacity,
        endShadowOpacity, shadowOffsetX, shadowOffsetY, shadowRadius);
  }

  public ShadowedCrossFadeSpan(int initialTextColor, int endTextColor, int initialShadowColor,
      int endShadowColor, int initialShadowOpacity, int endShadowOpacity, int shadowOffsetX,
      int shadowOffsetY, int shadowRadius) {
    this.initialTextColor = initialTextColor;
    this.endTextColor = endTextColor;
    this.initialShadowColor = initialShadowColor;
    this.endShadowColor = endShadowColor;
    this.initialShadowOpacity = initialShadowOpacity;
    this.endShadowOpacity = endShadowOpacity;
    this.shadowOffsetX = shadowOffsetX;
    this.shadowOffsetY = shadowOffsetY;
    this.shadowRadius = shadowRadius;

    // Set the span to the initial state.
    setParameter(0);
  }

  @Override public void updateDrawState(TextPaint tp) {
    tp.setColor(currentColor);
    tp.setShadowLayer(shadowRadius, shadowOffsetX, shadowOffsetY, argb(currentShadowOpacity, //
        red(currentShadowColor), green(currentShadowColor), blue(currentShadowColor)));
  }

  public void setParameter(float t) {
    currentShadowOpacity = param(initialShadowOpacity, endShadowOpacity, t);
    currentShadowColor = Color.rgb(param(red(initialShadowColor), red(endShadowColor), t), //
        param(green(initialShadowColor), green(endShadowColor), t), //
        param(blue(initialShadowColor), blue(endShadowColor), t));
    currentColor = Color.rgb(param(red(initialTextColor), red(endTextColor), t), //
        param(green(initialTextColor), green(endTextColor), t), //
        param(blue(initialTextColor), blue(endTextColor), t));
  }

  private static int param(int min, int max, float t) {
    return (int) ((max - min) * t) + min;
  }
}
