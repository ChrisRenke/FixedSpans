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

package com.example.fixedspans;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.TypefaceSpan;
import android.widget.TextView;
import com.chrisrenke.fixedspans.MonospaceSpan;
import com.chrisrenke.fixedspans.TabularSpan;

public class DemoActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(com.example.fixedspans.R.layout.main);

    ActionBar actionBar = getActionBar();
    SpannableString title = new SpannableString(getResources().getText(R.string.app_name));
    title.setSpan(new MonospaceSpan(), 0, title.length(), 0);
    actionBar.setTitle(title);

    span(R.id.mono_0, new MonospaceSpan());
    span(R.id.mono_1, new MonospaceSpan());
    span(R.id.mono_2, new MonospaceSpan(true));
    span(R.id.mono_3, new MonospaceSpan(true));

    span(R.id.tabular_0, new MoonFlowerSpan(this));
    span(R.id.tabular_0, new TabularSpan());
    span(R.id.tabular_1, new MoonFlowerSpan(this));
    span(R.id.tabular_1, new TabularSpan());
    span(R.id.tabular_2, new MoonFlowerSpan(this));
    span(R.id.tabular_2, new TabularSpan(" ()-", "0123456789"));
    span(R.id.tabular_3, new MoonFlowerSpan(this));
    span(R.id.tabular_3, new TabularSpan(" ()-", "0123456789"));
    span(R.id.tabular_4, new MoonFlowerSpan(this));
    span(R.id.tabular_4, new TabularSpan());

    span(R.id.normal_0, new MoonFlowerSpan(this));
    span(R.id.normal_1, new MoonFlowerSpan(this));
    span(R.id.normal_2, new MoonFlowerSpan(this));
    span(R.id.normal_3, new MoonFlowerSpan(this));
    span(R.id.normal_4, new MoonFlowerSpan(this));
  }

  private void span(int id, CharacterStyle span) {
    TextView view = (TextView) findViewById(id);
    SpannableString spannableString = new SpannableString(view.getText());
    spannableString.setSpan(span, 0, spannableString.length(), 0);
    view.setText(spannableString);
  }

  public class MoonFlowerSpan extends TypefaceSpan {

    private static final String FONT_FAMILY = "sans-serif";

    private final Context context;

    MoonFlowerSpan(Context context) {
      super(FONT_FAMILY);
      this.context = context;
    }

    @Override public void updateDrawState(TextPaint p) {
      applyCustomTypeface(p);
    }

    @Override public void updateMeasureState(TextPaint p) {
      applyCustomTypeface(p);
    }

    private void applyCustomTypeface(Paint paint) {
      paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/moonflowerfont.ttf"));
    }
  }
}
