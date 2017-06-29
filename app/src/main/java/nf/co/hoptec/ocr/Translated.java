package nf.co.hoptec.ocr;

/**
 * Created by shivesh on 19/1/17.
 */


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Translated {

    @SerializedName("code")
    @Expose
    public Integer code;
    @SerializedName("lang")
    @Expose
    public String lang;
    @SerializedName("text")
    @Expose
    public List<String> text = null;

}
