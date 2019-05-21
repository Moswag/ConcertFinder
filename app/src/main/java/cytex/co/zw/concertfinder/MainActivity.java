package cytex.co.zw.concertfinder;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cytex.co.zw.concertfinder.fragments.LoginFragment;
import cytex.co.zw.concertfinder.fragments.RegisterFragment;

public class MainActivity extends AppCompatActivity {
    TextView btnLogin,btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginFragment loginFragment=new LoginFragment();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout,loginFragment,"Login fragment");
        transaction.commit();

        btnLogin=findViewById(R.id.btnLogin);
        btnSignup=findViewById(R.id.btnSignup);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignup.setBackgroundResource(R.drawable.button_background);
                btnLogin.setBackgroundResource(R.drawable.button_background_selected);
                LoginFragment loginFragment=new LoginFragment();
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout,loginFragment,"Login fragment");
                transaction.commit();
            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setBackgroundResource(R.drawable.button_background);
                btnSignup.setBackgroundResource(R.drawable.button_background_selected);
                RegisterFragment registerFragment=new RegisterFragment();
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout,registerFragment,"Register fragment");
                transaction.commit();
            }
        });


    }
}
