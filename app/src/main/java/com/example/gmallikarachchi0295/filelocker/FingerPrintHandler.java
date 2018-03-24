package com.example.gmallikarachchi0295.filelocker;

import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by gmallikarachchi0295 on 3/18/2018.
 */

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    public FingerPrintHandler(Context context) {

        this.context = context;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {

        CancellationSignal cancellationSignal = new CancellationSignal();

        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        this.update("There is a Auth Error " + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Auth Failed",false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error "+helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
       this.update("You can now access the app", true);
    }

    private void update(String s, boolean b) {

        TextView changeLabel = (TextView) ((Activity)context).findViewById(R.id.textView3);
        ImageView image = (ImageView) ((Activity)context).findViewById(R.id.imageView);

        changeLabel.setText(s);

        if(b){
            image.setImageResource(R.mipmap.done);
        }


    }
}