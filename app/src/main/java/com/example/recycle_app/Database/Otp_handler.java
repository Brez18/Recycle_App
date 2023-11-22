package com.example.recycle_app.Database;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import com.chaos.view.PinView;
import com.example.recycle_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Otp_handler implements Parcelable{

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private Activity activity = null;

    private String phoneNumber;

    protected Otp_handler(Parcel in) {
        resendingToken = in.readParcelable(PhoneAuthProvider.ForceResendingToken.class.getClassLoader());
        phoneNumber = in.readString();
        verificationID = in.readString();
    }

    public static final Creator<Otp_handler> CREATOR = new Creator<Otp_handler>() {
        @Override
        public Otp_handler createFromParcel(Parcel in) {
            return new Otp_handler(in);
        }

        @Override
        public Otp_handler[] newArray(int size) {
            return new Otp_handler[size];
        }
    };

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String verificationID;

    public String getVerificationID() {
        return verificationID;
    }

    private ProgressBar circle;

    public void setCircle(ProgressBar circle) {
        this.circle = circle;
    }

    public Otp_handler(Activity act)
    {
        activity = act;
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationID = s;
                    resendingToken = forceResendingToken;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.e("Verification Error","Failed");
                }
            };


    public void send()
    {
         //this is used for getting OTP on user phone number
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder().
                setPhoneNumber("+91" + phoneNumber).
                setTimeout(30L, TimeUnit.SECONDS).
                setActivity(activity).
                setCallbacks(callbacks).
                build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void signIn(PhoneAuthCredential credential) {
        show_Progress(true);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    show_Progress(false);
                    activity.finish();
                }
                else {
                    PinView pin_otp = activity.findViewById(R.id.pin_otp);
                    pin_otp.setText("");
                    Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    show_Progress(false);
                }
            }
        });
    }

    void show_Progress(boolean flag){
        if(flag)
            circle.setVisibility(View.VISIBLE);
        else
            circle.setVisibility(View.INVISIBLE);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(resendingToken, i);
        parcel.writeString(phoneNumber);
        parcel.writeString(verificationID);
    }
}
