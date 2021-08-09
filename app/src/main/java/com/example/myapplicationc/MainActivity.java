package com.example.myapplicationc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipOutputStream;


public class MainActivity extends AppCompatActivity {
    private static final int RQS_OPEN = 101;

    Button envtexto,envimage,envselect,selectfile,antext,animg;
    ImageView imagen;
    TextView file;



    int Select_Foto = 200;
    long size,size_image;
    Bitmap bitmap;


    String myText,image_name;
    File doc,image_file;
    FileOutputStream fos;


    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());


        envtexto = (Button) findViewById(R.id.btntxt);
        envimage = (Button) findViewById(R.id.btnimage);
        envselect = (Button) findViewById(R.id.btnfile);
        selectfile = (Button) findViewById(R.id.btnselectfile);
        file = (TextView) findViewById(R.id.filetext);
        antext = (Button) findViewById(R.id.btnantex);
        animg = (Button) findViewById(R.id.btnanim);

        doc = new File(getExternalFilesDir("DES"),"texto.csv");
        image_file = new File(getExternalFilesDir("DES"),"image.csv");
        if (!doc.exists()){
            fos = null;
            try {
                fos = new FileOutputStream(doc);
                String cabecera ="Nombre,Archivo,Tamanio B,Key,Tiempo de Encriptación,Tiempo de Desencriptación,Tiempo Total,Uso de Memoria KB\n";
                fos.write(cabecera.trim().getBytes());
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!image_file.exists()){
            fos = null;
            try {
                fos = new FileOutputStream(image_file);
                String cabecera ="Nombre,Imagen,Tamanio KB,Key,Tiempo de Encriptación,Tiempo de Desencriptación,Tiempo Total,Uso de Memoria KB\n";
                fos.write(cabecera.trim().getBytes());
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        imagen = (ImageView) findViewById(R.id.iv);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        selectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myText="";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                String[] extraMimeTypes = {"text/plain"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, RQS_OPEN);
            }
        });

        envselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFoto();
            }
        });

        envimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null==imagen.getDrawable())
                {
                   print("Primero Seleccione una imagen");
                   return;
                }

                try {
                    Bitmap image_bit= ((BitmapDrawable)imagen.getDrawable()).getBitmap();;
                    ByteArrayOutputStream stream=new ByteArrayOutputStream();
                    image_bit= image_bit.createScaledBitmap(image_bit,200,200,false);
                    image_bit.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imagen = stream.toByteArray();
                    String img_str = Base64.encodeToString(imagen, 0);

                    String nameuser = getDeviceName();

                    String key = "CLAVE";
                    GetParams(img_str,key);

                    String TiempoE=GetTimeEnc();
                    String TiempoD= GetTimeDes();
                    String TiempoT=GetTime();
                    String Memoria=GetMEM();

                    String tosend = nameuser+","+image_name+","+size_image+","+key+","+TiempoE+","+TiempoD+","+TiempoT+","+Memoria;
                    try (BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(image_file, true), "UTF-8"))) {
                        wr.newLine();
                        wr.write(tosend);
                        wr.close();
                        print("Archivo ingresado a csv");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        print("Error al ingresar el archivo");
                    } catch (IOException e) {
                        e.printStackTrace();
                        print("Error al registrar el archivo");
                    }

                }catch (Exception e){
                    print("imagen muy grande para procesar");
                }
                imagen.setImageBitmap(null);
            }
        });

        envtexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.getText().toString()=="" || myText==""){
                    print("Seleccione un archivo que incluya texto");
                    return;
                }
                String nameuser = getDeviceName();
                String fileName = file.getText().toString();
                file.setText("");

                String key = "CLAVE";
                GetParams(myText,key);

                String TiempoE=GetTimeEnc();
                String TiempoD= GetTimeDes();
                String TiempoT=GetTime();
                String Memoria=GetMEM();

                String tosend = nameuser+","+fileName+","+size+","+key+","+TiempoE+","+TiempoD+","+TiempoT+","+Memoria;
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(doc, true), "UTF-8"))) {
                    writer.newLine();
                    writer.write(tosend);
                    writer.close();
                    print("Archivo ingresado a csv");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    print("Error al ingresar el archivo");
                    //print("Archivo no creado");
                } catch (IOException e) {
                    e.printStackTrace();
                    //print("Archivo no creado");
                    print("Error al registrar el archivo");
                }

            }
        });

        animg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!image_file.exists()){
                    print("No existe archivo con datos");
                    return;
                }
                //Intent imagein = new Intent(MainActivity.this,DataImage.class);
                //startActivity(imagein);

            }
        });

        antext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!doc.exists()){
                    print("No existe archivo con datos");
                    return;
                }
                //Intent textac = new Intent(MainActivity.this,DataText.class);
                //startActivity(textac);


            }
        });
    }


    public void seleccionarFoto(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria,Select_Foto);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == Select_Foto){
            Uri imageuri  = data.getData();
            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = getApplicationContext().getContentResolver().openAssetFileDescriptor(imageuri, "r");
                imagen.setImageURI(imageuri);
                size_image=fileDescriptor.getLength()/1024;
                image_name=getFileName(imageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            if (data.getData() != null){
                try{
                    Uri uri = data.getData();
                    AssetFileDescriptor fileDescriptor = getApplicationContext().getContentResolver().openAssetFileDescriptor(uri , "r");
                    size = fileDescriptor.getLength();
                    myText = readTextFromUri(MainActivity.this , uri);
                    file.setText(getFileName(uri));
                }catch(Exception e){

                }
            }
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String readTextFromUri(Context context, Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append("\n"+line);
            }
        }
        return stringBuilder.toString();
    }

    public  void print(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public native String stringFromJNI();

    public native String GetParams(String s,String k);
    public native String RESP1();
    public native String GetTimeEnc();
    public native String GetTimeDes();
    public native String GetTime();
    public native String GetMEM();
    public native String SetImage(String s, String k);


}