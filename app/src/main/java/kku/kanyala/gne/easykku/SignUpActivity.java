package kku.kanyala.gne.easykku;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.jibble.simpleftp.SimpleFTP;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    //Explicit
    private EditText nameEditText, phoneEditText,
            userEditText, passwordEditText;
    private ImageView imageView;
    private Button button;
    private String nameString, phoneString, userString, passwordString,
            imagePathSring, imageNameString;
    private Uri uri;
    private boolean aBoolean = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //Bind Widget
        nameEditText = (EditText) findViewById(R.id.editText);
        phoneEditText = (EditText) findViewById(R.id.editText2);
        userEditText = (EditText) findViewById(R.id.editText3);
        passwordEditText = (EditText) findViewById(R.id.editText4);
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button3);


        //sign Controller
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get Value From Edit Text
                nameString = nameEditText.getText().toString().trim();
                phoneString = phoneEditText.getText().toString().trim();
                userString = userEditText.getText().toString().trim();
                passwordString = passwordEditText.getText().toString().trim();


                //Check Space
                if (nameString.equals("") || phoneString.equals("") ||
                        userString.equals("") || passwordString.equals("")) {
                    //Have Space
                    Log.d("12novV1", "Have Space");
                    MyAlert myAlert = new MyAlert(SignUpActivity.this, R.drawable.doremon48,
                            "มีช่องว่าง", "กรุณากรอกให้ครบทุกช่อง");
                    myAlert.myDialog();
                } else if (aBoolean) {
                    MyAlert myAlert = new MyAlert(SignUpActivity.this,R.drawable.nobita48,
                            "ยังไม่เลือกรูป", "กรุณาเลือกรูป");
                    myAlert.myDialog();
                } else {
                    upLoadImageToServer();

                }


            }   //onClick
        });

        // Image Controller
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("Image/*");
                startActivityForResult(Intent.createChooser(intent, "โปรดเลือกแอปดูภาพ"), 0);


            } // onClick
        });

    } // Main Method

    private void upLoadImageToServer(){
        //Change Policy
        StrictMode.ThreadPolicy threadPolicy = new  StrictMode.ThreadPolicy
                .Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        try{

            SimpleFTP simpleFTP = new SimpleFTP();
            simpleFTP.connect("ftp.swiftcoding.com",21,"kku@swiftcodingthai.com","Abc12345");
            simpleFTP.bin();
            simpleFTP.cwd("Image");
            simpleFTP.stor(new File(imagePathSring));
            simpleFTP.disconnect();

        }catch (Exception e){
            Log.d("12novV1","e simpleFTP ==>"+e.toString());
        }


    }//upload


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode==0) && (resultCode== RESULT_OK)) {

            Log.d("12novV1","Result OK");
            aBoolean = false;

            //Show Image
            uri = data.getData();
            try{

                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                imageView.setImageBitmap(bitmap);
            } catch ( Exception e){
                e.printStackTrace();
            }
            //Find Path of Image
            imagePathSring = myFindPath(uri);
            Log.d("12novV1","imagePath ==>"+imagePathSring);

            //Find Name of Image
            imageNameString = imagePathSring.substring(imagePathSring.lastIndexOf("/"));
            Log.d("12novV1","imageName ==>"+imageNameString);

        } //if


    }// onActivity

    private String myFindPath(Uri uri) {
        String result = null;
        String[] strings = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,strings,null,null,null);

        if (cursor != null) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            result = cursor.getString(index);
        } else {
            result = uri.getPath();
        }
        return result;
    }
}   //Main Class
