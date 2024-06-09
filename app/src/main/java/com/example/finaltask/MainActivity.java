package com.example.finaltask;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finaltask.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    User user = new User();
    FirebaseAuth auth;
    private static final String PREF_NAME = "Name", PREF_ROLE = "Role", PREF_UID = "Uid";
    private static final String PREFS_FILE = "Account";
    SharedPreferences settings;
    String name = "";
    FirebaseDatabase db;
    DatabaseReference users;
    public void saveName(View view, String nameN, String role, String key) {
        name = nameN;
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString(PREF_NAME, name);
        prefEditor.putString(PREF_ROLE, role);
        prefEditor.putString(PREF_UID, key);
        prefEditor.apply();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        String name = settings.getString(PREF_NAME, "");
        if (name != "") { // для автоматического захода если вошел в аккаунт
            Intent i = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(i);
        }
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance("https://pharmacy-d6081-default-rtdb.europe-west1.firebasedatabase.app/");
        users = db.getReference("Users");
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { showRegisterWindow(view); }
        });
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { showSignInWindow(view); }
        });
    }
    private void showRegisterWindow(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.register_window, null);
        dialog.setView(register_window);
        final EditText email = register_window.findViewById(R.id.emailField);
        final EditText pass = register_window.findViewById(R.id.passField);
        final EditText name = register_window.findViewById(R.id.nameField);
        final EditText phone = register_window.findViewById(R.id.phoneField);
        dialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("Зарегистрироваться", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(binding.root, "Введите свою почту",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name.getText().toString())) {
                    Snackbar.make(binding.root, "Введите свое имя",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Snackbar.make(binding.root, "Введите номер телефона",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (pass.getText().toString().length() < 5) {
                    Snackbar.make(binding.root, "Введите пароль с 5 и более символами",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email.getText().toString(),
                                pass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                user.setEmail(email.getText().toString());
                                user.setName(name.getText().toString());
                                user.setPass(pass.getText().toString());
                                user.setPhone(phone.getText().toString());
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Snackbar.make(binding.root, "Успешно!",
                                                        Snackbar.LENGTH_SHORT).show();
                                                String str = auth.getCurrentUser().getUid();
                                                saveName(view, user.getName(), user.getRole(), str);
                                                Intent intent = new Intent(MainActivity.this,
                                                        SecondActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(binding.root, "Ошибка регистрации: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
            }
        });
        dialog.show();
    }
    private void showSignInWindow(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View sign_in_window = inflater.inflate(R.layout.sign_in_window, null);
        dialog.setView(sign_in_window);
        final EditText email = sign_in_window.findViewById(R.id.emailField);
        final EditText pass = sign_in_window.findViewById(R.id.passField);
        dialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(binding.root, "Введите свою почту", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) { //success
                                String str = auth.getCurrentUser().getUid();
                                DatabaseReference userRef = users.child(str);
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Получение имени пользователя из dataSnapshot
                                        String name = dataSnapshot.child("name").getValue(String.class);
                                        String role = dataSnapshot.child("role").getValue(String.class);
                                        saveName(view, name, role, str);
                                        // Переход на следующую активность
                                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Snackbar.make(binding.root, "Ошибка авторизации: " + databaseError.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(binding.root, "Ошибка авторизации: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        dialog.show();
    }
}