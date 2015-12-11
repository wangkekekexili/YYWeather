package com.yiyangzhu.yyweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private RadioButton defaultLocationRadioButton;
    private RadioButton customLocationRadioButton;
    private AutoCompleteTextView customLocationAutoCompleteTextView;
    private RadioButton celciusRadioButton;
    private RadioButton fahrenheitRadioButton;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        defaultLocationRadioButton = (RadioButton) findViewById(R.id.settings_default_radiobutton);
        customLocationRadioButton = (RadioButton) findViewById(R.id.settings_custom_radiobutton);
        customLocationAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.settings_custom_location);
        celciusRadioButton = (RadioButton) findViewById(R.id.settings_celcius);
        fahrenheitRadioButton = (RadioButton) findViewById(R.id.settings_fehrenheit);

        customLocationAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customLocationAutoCompleteTextView.setCursorVisible(true);
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

    public void done(View view) {
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
    }

}
