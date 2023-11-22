package com.example.recycle_app.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class DatabaseHelper {

    private FirebaseAuth mAuth;

    public int login(String email, String password){

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                        }
                    }
                });
        return -1;
    }
    public static int verifyData(String email,String password) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
//
//        try {
////            AuthResult result = auth.signInWithEmailAndPassword(email, password).get();
//
//            if (result.getUser() != null) {
//                System.out.println("User signed in successfully");
//            } else {
//                System.out.println("Failed to sign in");
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            if (e.getCause() instanceof FirebaseAuthException) {
//                FirebaseAuthException authException = (FirebaseAuthException) e.getCause();
//                System.out.println("Failed to sign in: " + authException.getMessage());
//            } else {
//                e.printStackTrace();
//            }
//        }
        return 0;
    }
}