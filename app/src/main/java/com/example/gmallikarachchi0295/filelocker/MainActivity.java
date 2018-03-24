package com.example.gmallikarachchi0295.filelocker;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {


    private ImageView fingerPrintImg;
    private TextView changeText;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager; // TODO Check 4

    private EditText dataToencrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fingerPrintImg = (ImageView) findViewById(R.id.imageView);
        changeText = (TextView) findViewById(R.id.textView3);

        dataToencrypt = (EditText) findViewById(R.id.data);

        /*
        TODO Check 1: Android version should be greater than or equl to marshmellow
        TODO CHECK 2: Device has Fingerprint Scaner
        TODO CHECK 3: Permission has given to the fingerprint scaner
        TODO CHECK 4: Lock screen is secured at least 1 type of lock
        TODO CHECK 5: Atleast one finger print is registerd
        */

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager =  (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if(!fingerprintManager.isHardwareDetected()){

                changeText.setText("Fingerprint scanner not detected!");
            }else if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT )!= PackageManager.PERMISSION_GRANTED){

                changeText.setText("Permission not granted to use fingerprint scanner");
            }else if(!keyguardManager.isKeyguardSecure()){

                changeText.setText("Add Lock to your phone");
            }else if(!fingerprintManager.hasEnrolledFingerprints()){

                changeText.setText("Register your fingerprint");
            }else{

                changeText.setText("Place your finger here!");

                FingerPrintHandler fingerPrintHandler = new FingerPrintHandler(this);
                fingerPrintHandler.startAuth(fingerprintManager,null);
            }
        }

        InitUIListners();


    }

    private void InitUIListners(){

        final Button encrypt_button = findViewById(R.id.encrypt_button);
        encrypt_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                decryptData();
            }
        });

    }




    private void decryptData(){

        //get data to encrypt
        String data = dataToencrypt.getText().toString();
        //generate secret key
        SecretKey secretKey = generateKey();

        String encrypted_text;

        try {
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+ "/" + KeyProperties.BLOCK_MODE_CBC + "/" +
                    KeyProperties.ENCRYPTION_PADDING_PKCS7);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptionIV = cipher.getIV();
            byte[] dataBytes = data.getBytes("UTF-8");
            byte[] encryptedData = cipher.doFinal(dataBytes);

            encrypted_text = Base64.encodeToString(encryptedData, Base64.DEFAULT);

            Utils.saveStringsInSP(this, "encryptData", encrypted_text);
            Utils.saveStringsInSP(this, "encryptionIV", Base64.encodeToString(encryptionIV, Base64.DEFAULT));


//                    dataToencrypt.setText(null);
//                    dataToencrypt.setText(encrypted_text);

            Context context = getApplicationContext();
            CharSequence text = encrypted_text;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();



        } catch(UserNotAuthenticatedException e){
            displayAuthScreen(1);
        }catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |UnsupportedEncodingException |IllegalBlockSizeException  e) {
            e.printStackTrace();

        } catch (BadPaddingException e) {
            e.printStackTrace();
        }



    }


    
    private void displayAuthScreen(int requestCode){

        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null,null);
        if(intent != null){
            startActivityForResult(intent,requestCode);
        }
    }

    private SecretKey generateKey() {

        KeyGenerator keyGenerator = null;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder("Key",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(5)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());



        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        return keyGenerator.generateKey();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                decryptData();
            }
        }

    }


}
