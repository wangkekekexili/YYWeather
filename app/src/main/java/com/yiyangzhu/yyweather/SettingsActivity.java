package com.yiyangzhu.yyweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private RadioButton defaultLocationRadioButton;
    private RadioButton customLocationRadioButton;
    private EditText customLocationEditText;
    private RadioButton celciusRadioButton;
    private RadioButton fahrenheitRadioButton;
    private Button doneButton;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        defaultLocationRadioButton = (RadioButton) findViewById(R.id.settings_default_radiobutton);
        customLocationRadioButton = (RadioButton) findViewById(R.id.settings_custom_radiobutton);
        customLocationEditText = (EditText) findViewById(R.id.settings_custom_location);
        celciusRadioButton = (RadioButton) findViewById(R.id.settings_celcius);
        fahrenheitRadioButton = (RadioButton) findViewById(R.id.settings_fehrenheit);
        doneButton = (Button) findViewById(R.id.settings_done);

        customLocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customLocationEditText.setCursorVisible(true);
            }
        });

        settings = new Settings(this);
        if (settings.getCity().equals(Settings.DEFAULT_LOCATION)) {
            defaultLocationRadioButton.setChecked(true);
        } else {
            customLocationRadioButton.setChecked(true);
            customLocationEditText.setText(settings.getCity());
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
            settings.setCity(customLocationEditText.getText().toString());
        }
        if (celciusRadioButton.isChecked()) {
            settings.setMeasurement(Settings.MEASUREMENT_CELSIUS);
        } else {
            settings.setMeasurement(Settings.MEASUREMENT_FAHRENHEIT);
        }
        finish();
    }

}
