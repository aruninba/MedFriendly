package arun.com.medfriendly;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utilities.Constants;
import utilities.Globalpreferences;
import utilities.Utils;

/**
 * Created by arun_i on 25-Jul-17.
 */

public class Profile extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {
    private ImageView profileView;
    private Toolbar toolbar;
    private EditText nameEt, phoneEt, emailEt;
    private FloatingActionButton fab;
    Globalpreferences globalpreferences;
    Uri fileUri;
    Bitmap bitmap;
    private CoordinatorLayout coordinatorLayout;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.profile);
        initialize();
        initCollapsingToolbar();

    }

    private void initialize() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        toolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        nameEt = (EditText) findViewById(R.id.nameEt);
        phoneEt = (EditText) findViewById(R.id.numberEt);
        emailEt = (EditText) findViewById(R.id.emailEt);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        profileView = (ImageView) findViewById(R.id.imageView);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateProfile);
        globalpreferences = Globalpreferences.getInstances(Profile.this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) && !(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && !(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.PHOTOPERMISSION);
                    } else {
                        showOption();
                    }
                } else {
                    showOption();
                }
            }
        });

        //set values
        nameEt.setText(globalpreferences.getString("username"));
        emailEt.setText(globalpreferences.getString("email"));
        if (globalpreferences.getInt("isPhotochanged") == 1) {
            Bitmap bitmap1 = Utils.StringToBitMap(globalpreferences.getString("photo"));
            profileView.setImageBitmap(bitmap1);
        } else {
            Glide.with(Profile.this).load(globalpreferences.getString("photo"))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileView);
        }
    }

    private void showOption() {
        CharSequence colors[] = new CharSequence[]{"From Gallery", "From Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case Constants.FILE_CHOOSER:
                        fileChooser();
                        break;

                    case Constants.CAMERA:
                        takePictureFromCamera();
                        break;
                }

            }
        });
        builder.show();
    }

    private void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        fileUri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, Constants.TAKE_IMAGE_REQUEST_CODE);
    }

    private void fileChooser() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i, "Select an Image"), Constants.CHOOSE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Constants.CHOOSE_IMAGE_REQUEST_CODE && data != null) {
            fileUri = data.getData();
            System.out.println("picked" + fileUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                profileView.setImageBitmap(bitmap);
                globalpreferences.putInt("isPhotochanged", 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == Constants.TAKE_IMAGE_REQUEST_CODE) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                profileView.setImageBitmap(bitmap);
                globalpreferences.putInt("isPhotochanged", 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No image has been selected.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save_reminder:
                saveProfile();
                return true;

            case R.id.logout:
                doLogout();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doLogout() {
            if(globalpreferences.getString("loginby").equalsIgnoreCase("google")){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                logoutSuccess();
                            }
                        });
            }else{
                if (AccessToken.getCurrentAccessToken() == null) {
                    return; // already logged out
                }
                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                        .Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                        logoutSuccess();
                    }
                }).executeAsync();
            }
    }

    private void logoutSuccess() {
        globalpreferences.putString("username","");
        Intent in = new Intent(Profile.this, Login.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }

    private void saveProfile() {
        globalpreferences.putString("username", nameEt.getText().toString());
        if (globalpreferences.getInt("isPhotochanged") == 1) {
            globalpreferences.putString("photo", Utils.BitMapToString(bitmap));
        }
        globalpreferences.putString("email", emailEt.getText().toString());
        globalpreferences.putString("profileNumber", phoneEt.getText().toString());
        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Profile saved Successfully",Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);
        // hiding & showing the txtPostTitle when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Profile");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.PHOTOPERMISSION: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                    Toast.makeText(Profile.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
    }


}
