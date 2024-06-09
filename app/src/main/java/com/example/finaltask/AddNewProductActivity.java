package com.example.finaltask;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.finaltask.databinding.ActivityAddNewProductBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class AddNewProductActivity extends AppCompatActivity {
    String title;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1001;
    private static final int REQUEST_CAMERA = 1;
    private static final String PREF_UID = "Uid";
    private static final String PREFS_FILE = "Account";
    SharedPreferences settings;
    Uri selectedImageUri = null;
    public String imageName = "", recordKey;
    Bitmap photo;
    ActivityAddNewProductBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        title = getIntent().getStringExtra("name");
        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        String key_ = settings.getString(PREF_UID, "Не определено");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Добавить товар"); // Установить заголовок
        actionBar.setSubtitle("Aптека " + title); // Установить заголовок
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pharmacy-d6081-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference tableReference_ = database.getReference("Products");
        DatabaseReference tableReference = tableReference_.child(title);
        DatabaseReference recordReference = tableReference.push();
        recordKey = recordReference.getKey(); // название ключа для одной записи
        // Создание объекта данных для одной записи
        ProductForDB productForDB = new ProductForDB();
        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.edTitle.getText().toString();
                String cost = binding.edCost.getText().toString();
                String ind = binding.edInd.getText().toString();
                String desc = binding.edDesc.getText().toString();
                if (name != "" && cost != "" && ind != ""
                        && photo != null && imageName != "") {
                    productForDB.setCost(Integer.valueOf(cost));
                    productForDB.setKey(key_);
                    productForDB.setImageUrl(imageName);
                    productForDB.setInd(Integer.valueOf(ind));
                    productForDB.setName(name);
                    productForDB.setDesc(desc);
                    recordReference.setValue(productForDB)
                            .addOnSuccessListener(aVoid -> {
                                Snackbar.make(binding.getRoot(), "Запись данных прошла успешно",
                                        Snackbar.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e -> {
                            });
                }
            }
        });
    }
    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите источник изображения")
                .setItems(new String[]{"Камера", "Галерея"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
                    }
                });
        builder.create().show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }
    private void uploadImageToStorage(Bitmap bitmap) {
        // Конвертируем Bitmap в массив байтов
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Создаем ссылку на хранилище Firebase
        FirebaseStorage storageRef = FirebaseStorage.getInstance("gs://pharmacy-d6081.appspot.com/");
        // Создаем ссылку для изображения в хранилище с уникальным именем
        imageName = "image_" + recordKey + ".png";
        StorageReference imageRef = storageRef.getReference().child(imageName);
        // Загружаем изображение по указанной ссылке
        imageRef.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> {
                    // Загрузка успешно завершена, получаем URL загруженного изображения
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Получаем URL успешно
                        String imageURL = uri.toString();
                        // Теперь вы можете использовать imageURL для сохранения в Realtime Database
                        // Например, создаем объект с данными о картинке
                        Map<String, Object> imageInfo = new HashMap<>();
                        imageInfo.put("imageUrl", imageURL);
                        imageInfo.put("imageName", imageName);

                        // Сохраняем данные о картинке в Realtime Database
                        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference("images");
                        imagesRef.push().setValue(imageInfo)
                                .addOnSuccessListener(aVoid -> {
                                    // Обработка успешного сохранения данных в базу данных
                                    Snackbar.make(binding.getRoot(), "Картинка успешно загружена и сохранена в базе данных",
                                            Snackbar.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Обработка ошибок сохранения данных в базу данных
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(binding.getRoot(), "Не получилось",
                            Snackbar.LENGTH_SHORT).show();
                });
    }

    @Override // метод для получения результатов (получения изображения)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            binding.imgAdd.setImageURI(selectedImageUri);
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            uploadImageToStorage(photo);
        }
        else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
            photo = (Bitmap) data.getExtras().get("data");
            binding.imgAdd.setImageBitmap(photo);
            uploadImageToStorage(photo);
            // Save the avatar image or do further processing
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            startActivityAfterCleanup(ProdustListActivity.class);
            return true;
        }
        return false;
    }
    private void startActivityAfterCleanup(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra("name", title);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}