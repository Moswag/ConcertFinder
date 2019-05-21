package cytex.co.zw.concertfinder.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.ConstraintAnchor;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cytex.co.zw.concertfinder.MainActivity;
import cytex.co.zw.concertfinder.NavMain;
import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.models.User;
import cytex.co.zw.concertfinder.utils.Constants;
import cytex.co.zw.concertfinder.utils.MessageToast;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    EditText name,email,password,confirm_password;
    Button btnRegister;

    DatabaseReference databaseReference;
    private FirebaseAuth auth;
    ProgressDialog progressDialog ;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_register, container, false);

        name=view.findViewById(R.id.name);
        email=view.findViewById(R.id.email);
        password=view.findViewById(R.id.password);
        confirm_password=view.findViewById(R.id.confirm_password);

        btnRegister=view.findViewById(R.id.btnRegister);

        progressDialog=new ProgressDialog(getActivity());

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TABLE_USER);



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String myName=name.getText().toString();
                String myEmail=email.getText().toString();
                String myPassword=password.getText().toString();
                String confirmPassword=confirm_password.getText().toString();

                User user=new User();
                user.setName(myName);
                user.setEmail(myEmail);
                user.setPassword(myPassword);

                if(checkTexts(user,confirmPassword)){
                    //ita zvawaudzwa
                }
                else{
                    // Setting progressDialog Title.
                    progressDialog.setTitle("Registering...");
                    // Showing progressDialog.
                    progressDialog.show();

                    //it will create a unique id and we will use it as the Primary Key for our User
                    String id = databaseReference.push().getKey();
                    user.setUserId(id);
                    databaseReference.child(id).setValue(user);

                    auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    MessageToast.show(getActivity(), "createUserWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        MessageToast.show(getActivity(), "Authentication failed." + task.getException());
                                        // Hiding the progressDialog after done uploading.
                                        progressDialog.dismiss();
                                    } else {
                                        MessageToast.show(getActivity(), "User added");
                                        // Hiding the progressDialog after done uploading.
                                        progressDialog.dismiss();
                                        clearTexts();
                                        startActivity(new Intent(getActivity(),NavMain.class));


                                    }
                                }
                            });


                }


            }
        });






        return view;
    }

    private void clearTexts(){
        name.setText("");
        email.setText("");
        password.setText("");
        confirm_password.setText("");
    }

    private boolean checkTexts(User user, String confirmPassowrd){
        if(TextUtils.isEmpty(user.getName())){
            name.setFocusable(true);
            name.setError("Name is required");
            return true;
        }
        else if(TextUtils.isEmpty(user.getEmail())){
            email.setFocusable(true);
            email.setError("Email is required");
            return true;
        }
        else if(TextUtils.isEmpty(user.getPassword())){
            password.setFocusable(true);
            password.setError("Password is required");
            return true;
        }
        else if(TextUtils.isEmpty(confirmPassowrd )){
            confirm_password.setFocusable(true);
            confirm_password.setError("Confirm password is required");
            return true;
        }
        else if(!user.getPassword().equals(confirmPassowrd)){
            password.setFocusable(true);
            password.setError("Password should be the same");
            MessageToast.show(getActivity(),"Password should be the same");
            return true;
        }
        else{
            return false;
        }
    }

}
