package com.example.prayartracker;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prayartracker.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    EditText email, username, password, repassword;
    Button signup, signin;
    SharedPreferences sp;
    DatabaseHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        email = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        signup = (Button) findViewById(R.id.btnsignup);
        signin = (Button) findViewById(R.id.signin);
        DB = new DatabaseHelper(this);


        sp = getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",true)){
            Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
            startActivity(intent);
        }


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString();
                String Username = username.getText().toString();
                String Pass = password.getText().toString();
                String Repass = repassword.getText().toString();
                if (Email.equals("") || Username.equals("") || Pass.equals("") || Repass.equals(""))
                    Toast.makeText(MainActivity.this, "لطفا قم بتعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                else {

                    if (Pass.length() >= 8) {
                        if (Pass.equals(Repass)) {

                        Boolean checkuserEmail = DB.checkemail(Email);
                        String emailRegex = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
                        Pattern pattern = Pattern.compile(emailRegex);
                        Matcher matcher = pattern.matcher(Email);
                        if (matcher.find()) {
                            if (checkuserEmail == false) {
                                Boolean insert = DB.insertData(Email, Username, Pass);
                                if (insert == true) {
                                    Toast.makeText(MainActivity.this, "تم تسجيل الحساب بنجاح", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "فشل تسجيل الحساب", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "البريد الإلكتروني مسجل بالفعل. لطفًا قم بتسجيل الدخول", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "لطفًا قم بإدخال البريد إلكتروني بشكل صحيح", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "كلمتي السر غير متطابقة", Toast.LENGTH_SHORT).show();
                    }
                }
                     else {
                        Toast.makeText(MainActivity.this, "لطفًا تأكد أن كلمة السر طولها ٨ أحرف على الأقل", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //create High Important Channel with id = high_important_channel
            NotificationChannel highImportantChannel = new NotificationChannel("high_important_channel",
                    "High Important Reminder Notification", android.app.NotificationManager.IMPORTANCE_HIGH);
            //create notification manager
            android.app.NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(highImportantChannel);
        }
    }

    public void goToHome(View view) {
        finish();
    }
}
