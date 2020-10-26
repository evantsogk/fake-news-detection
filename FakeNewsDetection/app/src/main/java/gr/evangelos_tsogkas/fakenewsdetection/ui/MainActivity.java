package gr.evangelos_tsogkas.fakenewsdetection.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.util.Base64;
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.android.material.snackbar.Snackbar;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.evangelos_tsogkas.fakenewsdetection.R;
import gr.evangelos_tsogkas.fakenewsdetection.domain.News;
import gr.evangelos_tsogkas.fakenewsdetection.domain.NewsFromContent;
import gr.evangelos_tsogkas.fakenewsdetection.domain.NewsFromUrl;


public class MainActivity extends AppCompatActivity {
    private Socket requestSocket;
    private News news;
    private int method = 0;
    private Bitmap provided_image = null;
    private Uri provided_image_uri =  null;
    private AppCompatButton btn_image;
    private EditText edt_url, edt_text;
    private String url, text;
    private SharedPreferences sharedpreferences;

    private static final String SERVER_IP = "37.6.193.51";
    private static final int PORT = 50000;
    private static final int PICK_IMAGE = 100;
    private static final String PREFERENCES= "preferences";
    private static final String NIGHT_MODE= "night_mode";
    private static final String NIGHT_MODE_ON = "on";
    private static final String NIGHT_MODE_OFF = "off";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // apply night mode preference
        sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String mode = sharedpreferences.getString(NIGHT_MODE, "");
        if (mode.equals(NIGHT_MODE_ON)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        ConstraintLayout layout_url = findViewById(R.id.layout_url);
        ConstraintLayout layout_content= findViewById(R.id.layout_content);
        btn_image = findViewById(R.id.btn_image);
        AppCompatButton btn_eval = findViewById(R.id.btn_evaluation);
        edt_url = findViewById(R.id.input_url);
        edt_text = findViewById(R.id.input_text);

        // segmented group
        SegmentedButtonGroup sbg = findViewById(R.id.buttonGroup_input_method);
        sbg.setOnPositionChangedListener(position -> {
            // hide keyboard
            InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (view != null && imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            method = sbg.getPosition();
            if (method == 0) {
                layout_content.setVisibility(View.GONE);
                layout_url.setVisibility(View.VISIBLE);
            }
            else {
                layout_url.setVisibility(View.GONE);
                layout_content.setVisibility(View.VISIBLE);
            }
        });

        // image button
        btn_image.setOnClickListener(v -> handlePermission());

        // evaluation button
        ObjectAnimator objAnim; // pressing effect
        objAnim= ObjectAnimator.ofPropertyValuesHolder(btn_eval,
                PropertyValuesHolder.ofFloat("scaleX", 0.95f),
                PropertyValuesHolder.ofFloat("scaleY", 0.95f));
        objAnim.setDuration(100);
        objAnim.setRepeatCount(1);
        objAnim.setRepeatMode(ObjectAnimator.REVERSE);

        btn_eval.setOnClickListener(v -> {
            objAnim.start();

            if (method == 0 && edt_url.getText().toString().trim().length() == 0) {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Please enter a news URL.", Snackbar.LENGTH_SHORT);
                snack.show();
            }
            else if (method == 1 && edt_text.getText().toString().trim().length() == 0) {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Please enter the news content.", Snackbar.LENGTH_SHORT);
                snack.show();
            }
            else {
                if (method==0) url = edt_url.getText().toString().trim();
                else text = edt_text.getText().toString().trim();
                ServerAsyncTask serverAsyncTask = new ServerAsyncTask(MainActivity.this);
                serverAsyncTask.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        String mode = sharedpreferences.getString(NIGHT_MODE, "");
        if (mode.equals(NIGHT_MODE_ON)) menu.getItem(0).setChecked(true);
        else menu.getItem(0).setChecked(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_night_mode) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (item.isChecked()) {
                editor.putString(NIGHT_MODE, NIGHT_MODE_OFF);
                editor.apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            else {
                editor.putString(NIGHT_MODE, NIGHT_MODE_ON);
                editor.apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            getWindow().setWindowAnimations(R.style.WindowAnimationTransitionFade);
        }
        else if (id == R.id.action_research) {
            Intent research = new Intent(MainActivity.this, ResearchActivity.class);
            startActivity(research);
        }
        return super.onOptionsItemSelected(item);
    }

    /* Opens gallery to select an image */
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // shows selected image as the button's drawable
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri imageUri = data.getData();
            if (imageUri!= null){
                provided_image_uri = imageUri;
                ImageButtonLoadAsyncTask imageButtonLoadAsyncTask = new ImageButtonLoadAsyncTask(MainActivity.this, imageUri, btn_image.getWidth());
                imageButtonLoadAsyncTask.execute();
            }
        }
    }

    /* Asks for permission to access storage. If already permitted opens gallery. */
    private void handlePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
        }
        else openGallery();
    }

    /* If permission is accepted opens gallery. */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PICK_IMAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* Async task to create drawable thumbnail for image button and avoid lag. */
    private static class ImageButtonLoadAsyncTask extends AsyncTask<Void, Void, Drawable> {
        private WeakReference<MainActivity> activityReference;
        private Uri imageUri;
        private int size;

        private ImageButtonLoadAsyncTask(MainActivity context, Uri imageUri, int size) {
            activityReference = new WeakReference<>(context);
            this.imageUri = imageUri;
            this.size = size;
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return null;

            try {
            InputStream input = activity.getContentResolver().openInputStream(imageUri);
            Cursor cursor = activity.getContentResolver().query(imageUri, new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
                if (input != null && cursor != null && cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    int orientation = cursor.getInt(0);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);

                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                    activity.provided_image = bitmap;
                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, size, size);
                    Drawable drawable = new BitmapDrawable(activity.getResources(), thumbnail);
                    drawable.setBounds(0, 0, size, size);

                    input.close();
                    cursor.close();
                    return drawable;
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (result != null) {
                activity.btn_image.setClipToOutline(true);
                activity.btn_image.setCompoundDrawables(null, result, null, null);
            }
        }
    }


    /* Async task to send news to server and receive results. */
    private static class ServerAsyncTask extends AsyncTask<String, Void, String> {
        private WeakReference<MainActivity> activityReference;
        private ProgressDialog progressDialog;

        private ServerAsyncTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            progressDialog = new ProgressDialog(activity);
            progressDialog=ProgressDialog.show(activity,null,null, true, false);
            progressDialog.setContentView(new ProgressBar(activity));
            if (progressDialog.getWindow() != null)
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String connection="failed";
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return connection;

            if (activity.method == 0) {
                activity.news = new NewsFromUrl(activity.url);
            }
            else {
                String text = activity.text;
                String image = null;
                if (activity.provided_image != null) {
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    activity.provided_image.compress(Bitmap.CompressFormat.PNG, 100, bao);
                    byte[] byteArray= bao.toByteArray();
                    image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
                activity.news = new NewsFromContent(text, image);
            }

            try {
                activity.requestSocket = new Socket(SERVER_IP, PORT);

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(activity.news);

                OutputStreamWriter out = new OutputStreamWriter(activity.requestSocket.getOutputStream(), StandardCharsets.UTF_8);
                out.write(Integer.toString(json.length()), 0, Integer.toString(json.length()).length());
                out.flush();
                out.write(json, 0, json.length());
                out.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(activity.requestSocket.getInputStream(), StandardCharsets.UTF_8));
                json = in.lines().collect(Collectors.joining());
                ObjectMapper objectMapper = new ObjectMapper();

                if (activity.news instanceof NewsFromContent)
                    activity.news = objectMapper.readValue(json, NewsFromContent.class);
                else
                    activity.news = objectMapper.readValue(json, NewsFromUrl.class);

                connection="successful";
            }
            catch (Exception  e) {
                e.printStackTrace();
                return connection;
            }
            return connection;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (result.equals("successful") && activity.method==0 && activity.news.getText()==null){
                Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), "Failed to fetch news from url!", Snackbar.LENGTH_SHORT);
                snack.show();
            }
            else if (result.equals("successful")) {
                Intent results = new Intent(activity, ResultsActivity.class);
                results.putExtra("news", activity.news);
                if (activity.method==1 && activity.provided_image != null)
                    results.putExtra("image_uri", activity.provided_image_uri);

                //clear input
                if (activity.method==0) activity.edt_url.getText().clear();
                else {
                    activity.edt_text.getText().clear();
                    Drawable drawable = activity.getDrawable(R.drawable.ic_image);
                    if (drawable != null) drawable.setBounds(0, 0, activity.btn_image.getWidth(), activity.btn_image.getWidth());
                    activity.btn_image.setCompoundDrawables(null, drawable, null, null);
                    activity.provided_image = null;
                }
                activity.startActivity(results);
            }
            else {
                Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), "Connection failed!", Snackbar.LENGTH_SHORT);
                snack.show();
            }
        }
    }
}
