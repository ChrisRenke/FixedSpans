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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import static java.lang.Math.ceil;

public class MonospaceSpan extends ReplacementSpan {

  private static final String REFERENCE_CHARACTERS = "MW";

  private final String relativeCharacters;

  /**
   * Set the {@code relativeMonospace} flag to true to monospace based on the widest character
   * in the content string; false will base the monospace on the widest width of 'M' or 'W'.
   */
  public MonospaceSpan(boolean relativeMonospace) {
    this.relativeCharacters = relativeMonospace ? null : REFERENCE_CHARACTERS;
  }

  /** Use the widest character from {@code relativeCharacters} to determine monospace width. */
  public MonospaceSpan(String relativeCharacters) {
    this.relativeCharacters = relativeCharacters;
  }

  public MonospaceSpan() {
    this.relativeCharacters = REFERENCE_CHARACTERS;
  }

  @Override
  public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
    if (fm != null) paint.getFontMetricsInt(fm);
    return (int) ceil((end - start) * getMonoWidth(paint, text.subSequence(start, end)));
  }

  @Override
  public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
      int bottom, Paint paint) {
    CharSequence actualText = text.subSequence(start, end);
    float monowidth = getMonoWidth(paint, actualText);
    for (int i = 0; i < actualText.length(); i++) {
      float textWidth = paint.measureText(actualText, i, i + 1);
      float halfFreeSpace = (textWidth - monowidth) / 2f;
      canvas.drawText(actualText, i, i + 1, x + (monowidth * i) - halfFreeSpace, y, paint);
    }
  }

  private float getMonoWidth(Paint paint, CharSequence text) {
    text = relativeCharacters == null ? text : relativeCharacters;
    float maxWidth = 0;
    for (int i = 0; i < text.length(); i++) {
      maxWidth = Math.max(paint.measureText(text, i, i + 1), maxWidth);
    }
    return maxWidth;
  }
}
