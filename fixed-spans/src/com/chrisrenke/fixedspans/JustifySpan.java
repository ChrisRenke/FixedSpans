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
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import static com.chrisrenke.fixedspans.JustifySpan.Mode.ALL_CHARACTERS;
import static com.chrisrenke.fixedspans.JustifySpan.Mode.WHITESPACE_ONLY;
import static java.lang.Character.isWhitespace;

/**
 * Justifies a line of text relative to the given width (assumed to be the width of the view).
 * If using outside of a singleline context, use {@link JustifySpan#justify(TextView,
 * boolean, float)} or {@link JustifySpan#justify(android.widget.TextView, boolean)} to
 * handle all the hard work for you.
 */
public class JustifySpan extends ReplacementSpan {

  /** Justifies the given {@code textView} with weighted all character justification. */
  public static void justify(final TextView textView, final boolean justifyLastLine,
      final float whitespaceWeight) {
    justify(textView, justifyLastLine, whitespaceWeight, ALL_CHARACTERS);
  }

  /** Justifies the given {@code textView} with whitespace justification. */
  public static void justify(final TextView textView, final boolean justifyLastLine) {
    justify(textView, justifyLastLine, 1f, ALL_CHARACTERS);
  }

  public static enum Mode {WHITESPACE_ONLY, ALL_CHARACTERS}

  private final float lineWidth;
  private final float whitespaceWeight;

  public JustifySpan(float lineWidth) {
    this(lineWidth, -1);
  }

  public JustifySpan(float lineWidth, float whitespaceWeight) {
    this.lineWidth = lineWidth;
    this.whitespaceWeight = whitespaceWeight;
  }

  /** Since this span justifies text, it will always take the full line width. */
  @Override
  public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
    return (int) lineWidth;
  }

  @Override
  public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
      int bottom, Paint paint) {
    CharSequence actualText = text.subSequence(start, end);

    // Prune trailing whitespace characters from line
    if (Character.isWhitespace(actualText.charAt(actualText.length() - 1))) {
      actualText = actualText.subSequence(0, actualText.length() - 1);
    }

    float textWidth = paint.measureText(actualText, 0, actualText.length());
    float differenceWidth = lineWidth - textWidth;

    // If there's no available space, draw the text as usual.
    if (differenceWidth <= 0) {
      canvas.drawText(text, start, end, x, y, paint);
      return;
    }

    int whitespaceCharacters = 0;
    for (int i = 0; i < actualText.length(); i++) {
      if (isWhitespace(actualText.charAt(i))) whitespaceCharacters++;
    }

    if (whitespaceWeight > 0) {
      drawTextOmniSpacing(canvas, text, start, end, x, y, paint, actualText, differenceWidth,
          whitespaceCharacters);
    } else {
      drawTextWhitespace(canvas, text, start, end, x, y, paint, actualText, differenceWidth,
          whitespaceCharacters);
    }
  }

  /**
   * Draws the given sequence of text onto the canvas with additional width given to whitespace
   * characters; non-whitespace characters will be drawn normally.
   */
  private void drawTextWhitespace(Canvas canvas, CharSequence text, int start, int end, float x,
      int y, Paint paint, CharSequence actualText, float differenceWidth,
      int whitespaceCharacters) {

    ((TextPaint) paint).bgColor = Color.RED;
    float addPerWhitespace = differenceWidth / (float) whitespaceCharacters;

    for (int i = 0; i < actualText.length(); i++) {
      float characterWidth = paint.measureText(actualText, i, i + 1);
      canvas.drawText(actualText, i, i + 1, x, y, paint);
      x += characterWidth;
      if (isWhitespace(actualText.charAt(i))) x += addPerWhitespace;
    }
  }

  /**
   * Draws the given sequence of text onto the canvas with additional spacing placed around every
   * character, with more or less space given to whitespace characters based on the strength of
   * this span's {@code whitespaceWeight}.
   */
  private void drawTextOmniSpacing(Canvas canvas, CharSequence text, int start, int end, float x,
      int y, Paint paint, CharSequence actualText, float differenceWidth,
      int whitespaceCharacters) {
    int nonWhitespaceCharacters = actualText.length() - whitespaceCharacters;

    float addPerCharacter =
        differenceWidth / ((whitespaceCharacters * whitespaceWeight) + nonWhitespaceCharacters);
    float halfAddPerCharacter = addPerCharacter / 2f;

    for (int i = 0; i < actualText.length(); i++) {
      float characterWidth = paint.measureText(actualText, i, i + 1);
      float characterPadding = isWhitespace(actualText.charAt(i)) //
          ? halfAddPerCharacter * whitespaceWeight //
          : halfAddPerCharacter;

      x += characterPadding;
      canvas.drawText(actualText, i, i + 1, x, y, paint);
      x += characterPadding + characterWidth;
    }
  }

  /** Internal method to be started from the two sister justify() methods or package members. */
  static void justify(final TextView textView, final boolean justifyLastLine,
      final float whitespaceWeight, final Mode mode) {
    ViewTreeObserver vto = textView.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      public void onGlobalLayout() {
        ViewTreeObserver obs = textView.getViewTreeObserver();
        obs.removeGlobalOnLayoutListener(this);
        doJustify(textView, justifyLastLine, whitespaceWeight, mode);
      }
    });
  }

  private static void doJustify(TextView textView, boolean justifyLastLine, float whitespaceWeight,
      Mode mode) {
    Layout layout = textView.getLayout();

    // If layout is null, we can't do anything, abort abort abort.
    if (layout == null) return;

    int lines = layout.getLineCount();
    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
    for (int line = 0; line < lines; line++) {
      CharSequence lineText =
          layout.getText().subSequence(layout.getLineStart(line), layout.getLineEnd(line));

      // If justifyLastLine is false, don't format the last line at all
      if (lines > 1 && line == lines - 1 && !justifyLastLine) {
        spannableStringBuilder.append(lineText);
      } else {
        SpannableString spannableString = new SpannableString(lineText);
        int width = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
        JustifySpan span = mode == WHITESPACE_ONLY //
            ? new JustifySpan(width) //
            : new JustifySpan(width, whitespaceWeight);
        spannableString.setSpan(span, 0, spannableString.length(), 0);
        spannableStringBuilder.append(spannableString);
      }

      // Add a manual newline or bad stuff happens as a result of replacement span.
      spannableStringBuilder.append(line != lines - 1 ? "\n" : "");
    }

    // Update textView with new charsequence
    textView.setText(spannableStringBuilder.subSequence(0, spannableStringBuilder.length()));
  }
}
