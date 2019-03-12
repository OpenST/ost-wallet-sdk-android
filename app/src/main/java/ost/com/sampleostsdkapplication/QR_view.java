package ost.com.sampleostsdkapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ost.mobilesdk.OstSdk;
import com.ost.mobilesdk.models.entities.OstDevice;

public class QR_view extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_view);
        Bitmap bmp;
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView = findViewById(R.id.imageDisplay);
        Button button = findViewById(R.id.btnDone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Need to be on click of button "start polling"
                LogInUser logInUser = ((App) OstSdk.getContext()).getLoggedUser();
                String userId = logInUser.getOstUserId();

                OstSdk.startPolling(userId, OstSdk.getUser(userId).getCurrentDevice().getAddress(),
                        OstSdk.DEVICE, OstDevice.CONST_STATUS.AUTHORIZED,
                        OstDevice.CONST_STATUS.REGISTERED, new WorkFlowHelper());
                finish();
            }
        });
        imageView.setImageBitmap(bmp);
    }
}