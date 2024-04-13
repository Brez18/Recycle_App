package com.example.recycle_app;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.chaos.view.PinView;
import com.example.recycle_app.Database.Firebase_handler;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


public class ActivityAccountCreate extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create);

        View hidden_view = findViewById(R.id.hidden_view1);
        ViewCompat.setOnApplyWindowInsetsListener(hidden_view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.LayoutParams hidden_viewLayoutParams = (ViewGroup.LayoutParams) v.getLayoutParams();

            hidden_viewLayoutParams.height = insets.bottom;
            hidden_view.setLayoutParams(hidden_viewLayoutParams);

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });

        // hide keyboard on click
        RelativeLayout rl = findViewById(R.id.layout_create_account);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCurrentFocus() != null) {
                    InputMethodManager inm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        ImageView btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        TextView prev = findViewById(R.id.txt_prev);
        TextView txt = findViewById(R.id.txt_title);
        NavController nav_controller = Navigation.findNavController(this,R.id.fragment_container);
        ImageButton submit = findViewById(R.id.btn_create_account);
        Firebase_handler firebase_handler = new Firebase_handler(ActivityAccountCreate.this);
        Bundle bundle = new Bundle();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if (nav_controller.getCurrentDestination().getId() == R.id.fragment_credentials)
                {
                    if (Is_Valid_Uname_Password_Address(findViewById(R.id.editText_uname), "uname") && Is_Valid_Email(findViewById(R.id.editText_mail))
                            && Is_Valid_Uname_Password_Address(findViewById(R.id.editText_password), "password") &&
                            Is_Valid_Phone(findViewById(R.id.editText_phone)) && Is_Valid_Uname_Password_Address(findViewById(R.id.editText_address),"address"))
                    {
                        EditText edt_uname = findViewById(R.id.editText_uname);
                        EditText edt_phone = findViewById(R.id.editText_phone);
                        EditText edt_mail = findViewById(R.id.editText_mail);
                        EditText edt_pass = findViewById(R.id.editText_password);
                        EditText edt_address = findViewById(R.id.editText_address);

                        firebase_handler.setUname(edt_uname.getText().toString());
                        firebase_handler.setEmail(edt_mail.getText().toString());
                        firebase_handler.setPassword(edt_pass.getText().toString());
                        firebase_handler.setPhoneNumber(edt_phone.getText().toString());
                        firebase_handler.setAddress(edt_address.getText().toString());


                        bundle.putString("phone_no", edt_phone.getText().toString());
                        bundle.putParcelable("handler", firebase_handler);
                        firebase_handler.createUserWithEmail(nav_controller,bundle,prev,txt);

                    }
                }

                else if(nav_controller.getCurrentDestination().getId() == R.id.fragment_otp){
                    ProgressBar circle = findViewById(R.id.progress_circle);
                    PinView pin_otp = findViewById(R.id.pin_otp);
                    String sms_code = pin_otp.getText().toString();
                    String verificationID = firebase_handler.getVerificationID();

                    firebase_handler.setCircle(circle);

                    if(!sms_code.equals("") && verificationID!=null) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, sms_code);
                        firebase_handler.signInWithOTP(credential);
                    }
                    else
                    {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt.setText("Create Account");
                nav_controller.popBackStack();
                prev.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        TextView prev = findViewById(R.id.txt_prev);
        TextView txt = findViewById(R.id.txt_title);

        if(txt.getText()=="OTP Verification")
            txt.setText("Create Account");

        if(prev.getVisibility() == View.VISIBLE)
            prev.setVisibility(View.GONE);

        super.onBackPressed();
    }

    boolean Is_Valid_Uname_Password_Address(EditText edt, String type)
    {
        ImageView error;
        View background_view;
        if(type.equals("uname")) {
            error = findViewById(R.id.ic_error1);
            background_view = findViewById(R.id.background_error1);
        }
        else if(type.equals("password")){
            error = findViewById(R.id.ic_error3);
            background_view = findViewById(R.id.background_error3);
        }
        else{
            error = findViewById(R.id.ic_error5);
            background_view = findViewById(R.id.background_error5);
        }
        TextWatcher(edt,error,background_view);
        if(edt.getText().toString().equals("")) {
            error.setVisibility(View.VISIBLE);
            background_view.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    boolean Is_Valid_Email(EditText edt)
    {
        String mail = edt.getText().toString();
        ImageView error = findViewById(R.id.ic_error2);
        View background_view = findViewById(R.id.background_error2);
        TextWatcher(edt,error,background_view);

        boolean result = Patterns.EMAIL_ADDRESS.matcher(mail).matches();
        if(!result) {
            error.setVisibility(View.VISIBLE);
            background_view.setVisibility(View.VISIBLE);
            return false;
        }
        error.setVisibility(View.GONE);
        background_view.setVisibility(View.GONE);
        return true;
    }

    boolean Is_Valid_Phone(EditText edt)
    {
        String phone = edt.getText().toString();
        ImageView error = findViewById(R.id.ic_error4);
        View background_view = findViewById(R.id.background_error4);
        TextWatcher(edt,error,background_view);
        boolean result = Patterns.PHONE.matcher(phone).matches();

        if(!result || phone.length()!=10) {
            error.setVisibility(View.VISIBLE);
            background_view.setVisibility(View.VISIBLE);
            return false;
        }
        error.setVisibility(View.GONE);
        background_view.setVisibility(View.GONE);
        return true;

    }

    void TextWatcher(EditText edt, ImageView error,View background_view)
    {
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(error.getVisibility()==View.VISIBLE) {
                    error.setVisibility(View.GONE);
                    background_view.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
    
}