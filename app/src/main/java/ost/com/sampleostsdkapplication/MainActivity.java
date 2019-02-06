package ost.com.sampleostsdkapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ost.mobilesdk.biometric.OstBiometricAuthentication;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String BIO_AUTH = "Bio-auth";
    private static final String PIN_AUTH = "Pin-auth";
    private static final String REGISTERED_DEVICE = "RegisterDevice";
    private Spinner mDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDropDownSpinner();
        initActionButton();
    }

    private void initActionButton() {
        Button button = findViewById(R.id.action_button);
        button.setOnClickListener(this);
    }

    private void initDropDownSpinner() {
        mDropdown = findViewById(R.id.action_bar_spinner);
        String[] items = new String[]{BIO_AUTH, PIN_AUTH, REGISTERED_DEVICE};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mDropdown.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        String item = (String) mDropdown.getSelectedItem();
        switch (item) {
            case BIO_AUTH:
                Toast.makeText(this, BIO_AUTH, Toast.LENGTH_SHORT).show();
                new OstBiometricAuthentication(getApplicationContext(), new OstBiometricAuthentication.Callback() {
                    @Override
                    public void onAuthenticated() {
                        Toast.makeText(MainActivity.this, "Authenticated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case PIN_AUTH:
                Toast.makeText(this, PIN_AUTH, Toast.LENGTH_SHORT).show();
                break;
            case REGISTERED_DEVICE:
                Toast.makeText(this, REGISTERED_DEVICE, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Unknown item", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}