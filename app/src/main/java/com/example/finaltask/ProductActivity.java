package com.example.finaltask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.finaltask.databinding.ActivityProductBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collections;
public class ProductActivity extends AppCompatActivity {
    ActivityProductBinding binding;
    String title, phar_name;
    int width, height;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = getIntent().getStringExtra("name");
        phar_name = getIntent().getStringExtra("phar_name");
        id = getIntent().getIntExtra("id", 0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Лекарство " + title); // Установить заголовок
        actionBar.setSubtitle("Аптека " + phar_name); // Установить подзаголовок
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pharmacy-d6081.appspot.com/");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pharmacy-d6081-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference productsReference = database.getReference("Products/" + phar_name);
        productsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    if (productSnapshot.exists() && (productSnapshot.child("ind").getValue(Integer.class)) == id) {
                        String description = productSnapshot.child("desc").getValue(String.class);
                        String imageUrl = productSnapshot.child("imageUrl").getValue(String.class);
                        // Загрузка изображения из Firebase Storage
                        StorageReference imageRef = storage.getReference(imageUrl);
                        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                            // Успешно загруженные байты изображения
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            binding.prodImage.setImageBitmap(scaleBitmap(bitmap));
                            binding.descr.setText(description);
                        }).addOnFailureListener(exception -> {
                            // Обработка ошибок загрузки изображения
                            binding.descr.setText(description);
                            Log.e("myLogs", "Failed to load image from Firebase Storage", exception);
                        });
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            startActivityAfterCleanup(ProdustListActivity.class);
            return true;
        } return false;
    }
    private void startActivityAfterCleanup(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra("name", phar_name);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public Bitmap scaleBitmap(Bitmap bitmap) { // метод почучения bitmap по ширине девайса
        // Масштабируем изображение до ширины экрана
        return Bitmap.createScaledBitmap(bitmap, 800, 800, true);
    }
}