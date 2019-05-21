package cytex.co.zw.concertfinder.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import cytex.co.zw.concertfinder.NavMain;
import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.utils.MessageToast;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    EditText email,password;
    Button btnLogin;

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_login, container, false);

        email=view.findViewById(R.id.email);
        password=view.findViewById(R.id.password);
        btnLogin=view.findViewById(R.id.btnLogin);


        progressDialog=new ProgressDialog(getActivity());

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            MessageToast.show(getActivity(),"Welcome back");
            startActivity(new Intent(getActivity(), NavMain.class));

        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(checkTexts()){
                    //do it
                }
                else{
                    progressDialog.setTitle("Authenticating user");
                    progressDialog.show();
                    String temail=email.getText().toString().trim();
                    String tpassword=password.getText().toString().trim();
                    //authenticate user
                    auth.signInWithEmailAndPassword(temail, tpassword)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    progressDialog.dismiss();
                                    if (!task.isSuccessful()) {

                                            MessageToast.show(getActivity(),"Authentiction failed, please try differently");
                                            //startActivity(new Intent(LoginActivity.this,NavMain.class));

                                    } else {
                                        MessageToast.show(getActivity(),"Welcome to Concert Finder App");
                                        Intent intent = new Intent(getActivity(), NavMain.class);
                                        startActivity(intent);

                                    }
                                }
                            });
                }
            }
        });




        return view;
    }



    private boolean checkTexts(){
        if(TextUtils.isEmpty(email.getText().toString())){
            email.setFocusable(true);
            email.setError("Email is required");
            return true;
        }
        else if(TextUtils.isEmpty(password.getText().toString())){
            password.setFocusable(true);
            password.setError("Password is required");
            return true;
        }
        else{
            return false;
        }
    }

}
