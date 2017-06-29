package nf.co.hoptec.ocr;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.UUID;

/**
 * Created by shivesh on 24/6/16.
 */
public class Constants {







    public static String uid(int l)
    {
        final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println("uuid Full= " + uuid);
        String ret= uuid.substring(0, Math.min(uuid.length(), l));;
        System.out.println("uuid "+l+" = " + ret);
        return ret;
    }


    /*
    public static String [] sources={
            Chinese(Simplified)=chs
            Chinese(Traditional)=cht
            Danish = dan
            Dutch = dut
            English = eng
            Finnish = fin
            French = fre
            German = ger
            Greek = gre
            Hungarian = hun
            Korean = kor
            Italian = ita
            Japanese = jpn
            Norwegian = nor
            Polish = pol
            Portuguese = por
            Russian = rus
            Spanish = spa
            Swedish = swe
            Turkish = tur
    }*/

    public static Conf conf;
    public static final String TAG_EMAIL = "email";
    public static final String v = "v1";
    public static final String TAG_LOGIN = "login";
    public static  String folder = "login";
    public static  String datafile = "login";
    public static String FIRE_BASE="https://shiveshnavin.firebaseio.com/";
    public static String FIRE_BASE_STORAGE="gs://ccmapp1919.appspot.com/";

    public static String fire(Context ctx)
    {
        return Constants.FIRE_BASE+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
    }
    public static String getFireStorage(Context ctx)
    {
        return Constants.FIRE_BASE_STORAGE;//+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
    }

    public static String getFolderName(Context ctx)
    {
      return  "."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
    }
    public static String getFolder(Context ctx)
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
        return folder;
    }

    public static String dataFile(Context ctx)
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");

        File file=new File(folder);
        if(!file.exists())
        {
            file.mkdir();
        }
        datafile=folder+"/conf.json";
        return datafile;
    }

    public static String ocr_url="https://api.ocr.space/Parse/Image";
   // public static String api_key="4ff7b5396288957";
    //?apikey=4ff7b5396288957&url=https://pbs.twimg.com/media/B8HwtCxIgAAPK3v.jpg&language=eng"




    public static String getApp(Context ctx)
    {
        return utl.refineString(ctx.getResources().getString(R.string.app_name),"");
    }
    public static String localData(Context ctx)
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");

        File file=new File(folder);
        if(!file.exists())
        {
            file.mkdir();
        }
        datafile=folder+"/data.json";
        return datafile;
    }


    public static class Conf{

        public boolean isProductDigital=false;
        public boolean requireAddress=true;

        public String appname;


    }





}
