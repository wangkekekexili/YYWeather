package com.yiyangzhu.yyweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private LinearLayout mainLinearLayout;
    private RadioButton defaultLocationRadioButton;
    private TextView defaultLocationTextView;
    private RadioButton customLocationRadioButton;
    private AutoCompleteTextView customLocationAutoCompleteTextView;
    private RadioButton celciusRadioButton;
    private RadioButton fahrenheitRadioButton;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mainLinearLayout = (LinearLayout) findViewById(R.id.settings_main_linearlayout);
        defaultLocationRadioButton = (RadioButton) findViewById(R.id.settings_default_radiobutton);
        defaultLocationTextView = (TextView) findViewById(R.id.settings_default_location);
        customLocationRadioButton = (RadioButton) findViewById(R.id.settings_custom_radiobutton);
        customLocationAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.settings_custom_location);
        celciusRadioButton = (RadioButton) findViewById(R.id.settings_celcius);
        fahrenheitRadioButton = (RadioButton) findViewById(R.id.settings_fehrenheit);

        mainLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });

        defaultLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultLocationRadioButton.setChecked(true);
            }
        });

        customLocationAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customLocationAutoCompleteTextView.setCursorVisible(true);
                customLocationRadioButton.setChecked(true);
            }
        });

        customLocationAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    customLocationAutoCompleteTextView.setCursorVisible(false);

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        String[] autoCompleteCities = getResources().getStringArray(R.array.autocomplete_cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, autoCompleteCities);
        customLocationAutoCompleteTextView.setAdapter(adapter);
        customLocationAutoCompleteTextView.setThreshold(1);

        settings = new Settings(this);
        if (settings.getCity().equals(Settings.DEFAULT_LOCATION)) {
            defaultLocationRadioButton.setChecked(true);
        } else {
            customLocationRadioButton.setChecked(true);
            customLocationAutoCompleteTextView.setText(settings.getCity());
        }
        if (settings.getMeasurement().equals(Settings.MEASUREMENT_CELSIUS)) {
            celciusRadioButton.setChecked(true);
        } else if (settings.getMeasurement().equals(Settings.MEASUREMENT_FAHRENHEIT)) {
            fahrenheitRadioButton.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (defaultLocationRadioButton.isChecked()) {
            settings.setCity(Settings.DEFAULT_LOCATION);
        } else {
            settings.setCity(customLocationAutoCompleteTextView.getText().toString());
        }
        if (celciusRadioButton.isChecked()) {
            settings.setMeasurement(Settings.MEASUREMENT_CELSIUS);
        } else {
            settings.setMeasurement(Settings.MEASUREMENT_FAHRENHEIT);
        }
        finish();
        return true;
    }
}
