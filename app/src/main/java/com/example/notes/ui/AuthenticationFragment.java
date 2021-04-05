package com.example.notes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class AuthenticationFragment extends Fragment {
    private static final int RC_SIGN_IN = 40404 ;
    private static final String TAG = "GoogleAuthenticator";

    private GoogleSignInClient googleSignInClient;

    private com.google.android.gms.common.SignInButton signInButton;
    private MaterialButton signOutButton;
    private TextView emailView;
    private MaterialButton continueButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.authenticator_layout, container, false);
        initGoogleSignIn();
        initView(view);
        enableSignIn();
        return view;
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);
    }

    private void initView(View view) {
        signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> signIn());
        signOutButton = view.findViewById(R.id.sing_out_button);
        signOutButton.setOnClickListener(v -> signOut());
        emailView = view.findViewById(R.id.user_email);
        continueButton = view.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v -> initNotesList());
    }

    private void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut(){
        googleSignInClient.signOut().addOnCompleteListener(task -> {
           updateEmail("");
           enableSignIn();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> googleSignInAccountTask =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(googleSignInAccountTask);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> googleSignInAccountTask){
        try {
            GoogleSignInAccount googleSignInAccount =
                    googleSignInAccountTask.getResult(ApiException.class);

            disableSignIn();
            updateEmail(googleSignInAccount.getEmail());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void initNotesList() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        NotesListFragment notesListFragment = new NotesListFragment();
        fragmentTransaction.replace(R.id.notes_list_container, notesListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if (googleSignInAccount != null) {
            disableSignIn();
            updateEmail(googleSignInAccount.getEmail());
        }
    }

    private void disableSignIn(){
        signInButton.setEnabled(false);
        continueButton.setEnabled(true);
        signOutButton.setEnabled(true);
    }

    private void enableSignIn(){
        signInButton.setEnabled(true);
        continueButton.setEnabled(false);
        signOutButton.setEnabled(false);
    }

    private void updateEmail(String userEmail){
        emailView.setText(userEmail);
    }
}
