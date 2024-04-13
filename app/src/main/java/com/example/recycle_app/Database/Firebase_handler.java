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
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import com.chaos.view.PinView;
import com.example.recycle_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Firebase_handler implements Parcelable{

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private Activity activity = null;
    private String uname,email,password,phoneNumber,address;
    private String verificationID;
    private ProgressBar circle;

    protected Firebase_handler(Parcel in) {
        resendingToken = in.readParcelable(PhoneAuthProvider.ForceResendingToken.class.getClassLoader());
        phoneNumber = in.readString();
        verificationID = in.readString();
    }

    public static final Creator<Firebase_handler> CREATOR = new Creator<Firebase_handler>() {
        @Override
        public Firebase_handler createFromParcel(Parcel in) {
            return new Firebase_handler(in);
        }

        @Override
        public Firebase_handler[] newArray(int size) {
            return new Firebase_handler[size];
        }
    };

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getVerificationID() {
        return verificationID;
    }


    public void setCircle(ProgressBar circle) {
        this.circle = circle;
    }

    public Firebase_handler(Activity act)
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
                    Log.e("Phone Verification","Success");
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.e("Phone Verification Error","Failed");
                }
            };


    public void sendOTP()
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

    public void signInWithOTP(PhoneAuthCredential credential) {
        show_Progress(true);
        auth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    show_Progress(false);
                    Log.e("TAG", "PhoneAuth:success");
                    addToFireStore();
                }
                else {
                    PinView pin_otp = activity.findViewById(R.id.pin_otp);
                    pin_otp.setText("");
                    Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    show_Progress(false);

                    Toast.makeText(activity,"There is already someone using this phone number, or OTP isn't valid.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createUserWithEmail(NavController navController, Bundle bundle, TextView prev, TextView txt)
    {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.e("TAG", "createUserWithEmail:success");

                        sendOTP();
                        navController.navigate(R.id.action_fragment_credentials_to_fragmentOtp, bundle);
                        prev.setVisibility(View.VISIBLE);
                        txt.setText("OTP Verification");

                        FirebaseUser user = auth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(uname)
                                .build();
                        user.updateProfile(profileUpdates);
                    }
                    else
                        Toast.makeText(activity,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    }


    void show_Progress(boolean flag){
        if(flag)
            circle.setVisibility(View.VISIBLE);
        else
            circle.setVisibility(View.INVISIBLE);

    }

    void addToFireStore()
    {

        Map<String,Object> user = new HashMap<>();
        user.put("username",uname);
        user.put("email",email);
        user.put("phone_num",phoneNumber);
        user.put("address",address);

//        Log.e("Tag",user.getUsername()+" "+user.getEmail());
        Log.e("Tag",db.toString());
        db.collection("MobileAppUsers").document(email).set(user).
                addOnCompleteListener(task->{
                    if(task.isSuccessful())
                        Log.e("Tag","Successfully added to fire store");
                    else
                        Log.e("Tag","Failed to add to fire store");
                    auth.signOut();
                    activity.finish();
                });
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
