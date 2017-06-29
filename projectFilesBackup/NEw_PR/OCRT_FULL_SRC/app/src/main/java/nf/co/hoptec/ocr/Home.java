package nf.co.hoptec.ocr;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Home extends AppCompatActivity {
    private FirebaseAuth mAuth;;
    private FirebaseAuth.AuthStateListener mAuthListener;


    public static String TAG="HOME";

    public static int PICK;
    public static Context ctx;
    public static Activity act;
    public static TextView ocrtext,ocrtextt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctx=this;
        act=this;
        setTitle("");
        data=Constants.dataFile(ctx);
        initView();
        Firebase.setAndroidContext(ctx);
        AndroidNetworking.initialize(ctx);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    utl.log(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    utl.log(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        utl.log(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                          //  Toast.makeText(ctx, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PICK=PickerBuilder.SELECT_FROM_CAMERA;
                pick();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PICK=PickerBuilder.SELECT_FROM_GALLERY;
                pick();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClip(curOCRText);
             }
        });
        ocrtext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setClip(curOCRText);
                return false;
            }
        });




        savet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        copyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClip(ocrtextt.getText().toString());
            }
        });
        ocrtextt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setClip(ocrtextt.getText().toString());
                return false;
            }
        });
        save.setVisibility(View.GONE);
        savet.setVisibility(View.GONE);


        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setPAR();;
            }
        });



        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setTarget();;
            }
        });


        Firebase fb=new Firebase(Constants.fire(ctx));
         fb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                utl.log("FB",""+dataSnapshot.getValue().toString());
                String status=dataSnapshot.child("status").getValue().toString();
                if(!status.equalsIgnoreCase("online"))
                {
                    utl.toast(ctx,"Some Error Occured ! Please contact developer !!");
                    finish();;
                }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        if(!CheckNetwork.isConnected(ctx))
        {
            utl.toast(ctx,"You MUST be connected to internet to continue .");
        }



      //  process("https://firebasestorage.googleapis.com/v0/b/cmapp1919.appspot.com/o/ocr%2Fphoto_1486558562457.jpg?alt=media&token=7612133e-8231-4e01-bad7-844815aa8f36");


    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



    public void setClip(String txt)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("OCR", txt);
        clipboard.setPrimaryClip(clip);
        utl.toast(ctx,"Text Copied to Clipboard !");

    }
    Button save,copy;
    Button savet,copyt;
    Button source,target;
    FirebaseStorage storage;
    StorageReference storageReference;;
    ImageView ocrimg;
    public void initView()
    {

        try {
            FirebaseOptions opts = FirebaseApp.getInstance().getOptions();
            Log.i("ZZ", "Bucket = " + opts.getStorageBucket());
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReferenceFromUrl("gs://cmapp1919.appspot.com");
        } catch (Exception e) {
            e.printStackTrace();
        }


        ocrtext=(TextView) findViewById(R.id.ocrtext);
        ocrtextt=(TextView) findViewById(R.id.ocrtextt);
        ocrimg=(ImageView) findViewById(R.id.ocrimg);
        save=(Button) findViewById(R.id.save);
        copy=(Button) findViewById(R.id.copy);
        savet=(Button) findViewById(R.id.savet);
        copyt=(Button) findViewById(R.id.copyt);
        source=(Button) findViewById(R.id.source);
        target=(Button) findViewById(R.id.target);

    }


    public class Source{

        public String name;
        public String code;
        public Source(String n,String c)
        {
            name=n;
            code=c;
        }
    }



    Dialog dig;
    public void setTarget()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Enter Valid Target Language Code");
        builder.setView(R.layout.dialog_input);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                EditText button=(EditText)dig.findViewById(R.id.search_et);
                TextView view=(TextView) dig.findViewById(R.id.view);
                view.setVisibility(View.GONE);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent it=new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/cmapp1919.appspot.com/o/codes.html?alt=media&token=b46bf10b-14fe-47d0-ad07-d835fdb28e39"));
                        startActivity(it);
                    }
                });


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });



                final String phone=button.getText().toString();
                if(tmp.txt.contains(phone)||!((phone).length()<2)||phone.length()>3) {
                    TARGET=phone;
                    utl.log("TARGET "+TARGET);
                    target.setText(""+TARGET);

                    dialog.dismiss();
                    if(uploadedFile!=null)
                    {
                        process(uploadedFile);
                    }
                }
                else {
                    setTarget();
                }



            }
        });
        builder.setNegativeButton("View Codes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

                Intent it=new Intent(Intent.ACTION_VIEW);
                it.setData(Uri.parse("https://firebasestorage.googleapis.com/v0/b/cmapp1919.appspot.com/o/codes.html?alt=media&token=b46bf10b-14fe-47d0-ad07-d835fdb28e39"));
                startActivity(it);

                //   dialog.dismiss();;
            }
        });
        dig = builder.create();
        dig.show();



    }


    public void setPAR()
    {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ctx);
        builderSingle.setIcon(R.drawable.ayi);
        builderSingle.setTitle("Select Source Lang Code :");
       final  ArrayList<Source> src=new ArrayList<>();
            src.add(new Source("Danish","dan"));
            src.add(new Source("Dutch","dut"));
            src.add(new Source("English","eng"));
            src.add(new Source("Finnish","fin"));
            src.add(new Source("French","fre"));
            src.add(new Source("German","ger"));
            src.add(new Source("Greek","gre"));
            src.add(new Source("ungarian","hun"));
            src.add(new Source("Korean"," kor"));
            src.add(new Source("Italian","ita"));
            src.add(new Source("Japanese","jpn"));
            src.add(new Source("Norwegian","nor"));
            src.add(new Source("Polish","pol"));
            src.add(new Source("Portuguese","por"));
            src.add(new Source("Russian","rus"));
            src.add(new Source("Spanish","spa"));
            src.add(new Source("Swedish","swe"));
            src.add(new Source("Turkish","tur"));






        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ctx, android.R.layout.select_dialog_singlechoice);
        for(int i=0;i<src.size();i++)
        {
            arrayAdapter.add(src.get(i).name);
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                SOURCE=src.get(which).code;

                utl.log("SOURCE "+SOURCE);
                source.setText(""+SOURCE);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(ctx);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Language is : ");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(uploadedFile!=null)
                        {
                            process(uploadedFile);
                        }
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();

    }
    String curUploaded;
    Uri rUri=null;
    String uploadedFile=null;
    private void uploadFile(Uri filePath) {
        //if there is a file to upload
         if (filePath != null) {
            //displaying a progress dialog while upload is going on
            pd.setMessage("Uploading");
            pd.show();


            StorageReference riversRef = storageReference.child("ocr/"+code+".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                          //  pd.dismiss();
                            curUploaded= "ocr/"+code+".jpg";
                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                            getUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            pd.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            pd.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

    public void getUrl()
    {
        storageReference.child(curUploaded).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                utl.log("URL ",""+uri.toString());
                uploadedFile=uri.toString();
                process(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                // Handle any errors
            }
        });
    }

    String data;
    String code;
    public static int TAKE_PHOTO_CODE=14132;
    public void pick()
    {
        pd=new ProgressDialog(ctx);
        pd.setCancelable(false);
       code=""+System.currentTimeMillis();
        code="photo_"+code;
        curPhoto=Constants.getFolder(ctx)+"/photo_"+code;
        File newfile = new File(curPhoto);
        try {
            newfile.createNewFile();
        }
        catch (IOException e)
        {
        }

        Uri outputFileUri = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

       // startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

        new PickerBuilder(act,PICK)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(final Uri imageUri) {
                        Toast.makeText(ctx,"Got image - " + imageUri, Toast.LENGTH_LONG).show();
                        utl.log("PHOTO : "+imageUri.getPath());
                        curPhoto=imageUri.getPath();
                        ocrimg.setImageURI(imageUri);


                        photo=new File(curPhoto);
                        ExecuterU ex=new ExecuterU(ctx,"Compressing...")
                        {
                            @Override
                            public void doIt() {
                                 compressBitmap(photo,1.0,100);
                            }

                            @Override
                            public void doNe() {
                                rUri=imageUri;
                                uploadFile(imageUri);
                            }
                        };
                        ex.execute();
                    //    uploadFile(imageUri);

                    }
                })
                 .setImageName( code)
              .setImageFolderName(Constants.getFolderName(ctx))
                .withTimeStamp(false)
                .setCropScreenColor(getResources().getColor(R.color.colorPrimary))
                .start();







    }

        ProgressDialog pd;

    public static String SOURCE="eng",TARGET="eng";
    public void process(String url)
    {
        // process();
        //  imageView.setImageURI(imageUri);

        final  File photo=new File("/storage/emulated/0/.OCRDemo/photo_1484825523678.jpg");
        ocrtext.setText(curPhoto);
        utl.log(""+url);
        if(pd==null)
            pd=new ProgressDialog(ctx);
        pd.setMessage("Processing");
        if(!pd.isShowing())
        pd.show();
        utl.log("SOURCE : "+SOURCE);
        AndroidNetworking.post(Constants.ocr_url)
                .addBodyParameter("apikey",getResources().getString(R.string.ocr_api_key))
                .addBodyParameter("language",SOURCE)
                .addBodyParameter("url",url)
              //  .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pd.dismiss();

                        try {
                            utl.log("RES",""+response);
                            result=new Result();
                            Gson g=new Gson();
                            result=g.fromJson(response.toString(),Result.class);
                            curOCRText=result.parsedResults.get(0).parsedText;
                            ocrtext.setText(""+curOCRText);
                            transtate(curOCRText);
                        } catch (Exception e) {

                            ocrtext.setText("RESPONSE  : \n"+response.toString());



                            e.printStackTrace();
                        }


                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        pd.dismiss();
                        utl.log("ERR",""+error.getErrorDetail()+"\n"+error.getErrorBody());
                        utl.toast(ctx,"Error Occured While Uploading! Please try again   ERR : "+error.getErrorDetail());
                    }
                });








                /*
               AndroidNetworking .upload(Constants.ocr_url)
                .addMultipartFile("file",photo)
                .addMultipartParameter("apikey",Constants.api_key)
                .addMultipartParameter("language","eng")*/



    }
    public void transtate(String txt)
    {
        //https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170119T133133Z.6558629dc21cdebe.0c10657f968ccb2569d62f97e9c536d0917ed1df
        // &text= हाय वहाँ भाई&lang=eng&format=json


        ocrtextt.setText("Translating...");
        String url="https://translate.yandex.net/api/v1.5/tr.json/translate?key="+getResources().getString(R.string.yandex_api_key)
                +"&text="+txt+"&format=json&lang="+TARGET;
        utl.log("TRANS URL : "+url);
        AndroidNetworking.get(url)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Gson h=new Gson();
                            Translated td=h.fromJson(response.toString(),Translated.class);
                            ocrtextt.setText(td.text.get(0));
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();

                            ocrtextt.setText("RESPONSE  : \n"+response);
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {

                        utl.log(""+ANError.getErrorDetail());
                        utl.toast(ctx,"Error Occured While translating! Please try again with a clear pic. ERR : "+ANError.getErrorDetail());

                    }
                });
    }
    String curPhoto=null,curOCRText;
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }



    }
    public Result result;


    public static File photo;
    public static Double SIZE_LIMIT=1000.0;
    public void compressBitmap(File file, Double sampleSize, int quality) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize.intValue();
            FileInputStream inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            FileOutputStream outputStream = new FileOutputStream(curPhoto);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.close();
            Double lengthInKb = photo.length() / 1024.0; //in kb
            if (lengthInKb > SIZE_LIMIT) {
                compressBitmap(file, (sampleSize*1.5), (quality/4));
            }

            selectedBitmap.recycle();
            utl.log("SAMPLE","SIZE : "+sampleSize+"\nSIZe IN KB "+lengthInKb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
