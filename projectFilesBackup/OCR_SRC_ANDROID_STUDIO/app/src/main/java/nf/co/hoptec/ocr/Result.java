package nf.co.hoptec.ocr;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shivesh on 19/1/17.
 */

public class Result {


    @SerializedName("ParsedResults")
    @Expose
    public List<ParsedResult> parsedResults = null;
    @SerializedName("OCRExitCode")
    @Expose
    public Integer oCRExitCode;
    @SerializedName("IsErroredOnProcessing")
    @Expose
    public Boolean isErroredOnProcessing;
    @SerializedName("ErrorMessage")
    @Expose
    public Object errorMessage;
    @SerializedName("ErrorDetails")
    @Expose
    public Object errorDetails;
    @SerializedName("ProcessingTimeInMilliseconds")
    @Expose
    public String processingTimeInMilliseconds;



    public static class ParsedResult {

        @SerializedName("TextOverlay")
        @Expose
        public TextOverlay textOverlay;
        @SerializedName("FileParseExitCode")
        @Expose
        public Integer fileParseExitCode;
        @SerializedName("ParsedText")
        @Expose
        public String parsedText;
        @SerializedName("ErrorMessage")
        @Expose
        public String errorMessage;
        @SerializedName("ErrorDetails")
        @Expose
        public String errorDetails;

    }

    public static class TextOverlay {

        @SerializedName("Lines")
        @Expose
        public List<Object> lines = null;
        @SerializedName("HasOverlay")
        @Expose
        public Boolean hasOverlay;
        @SerializedName("Message")
        @Expose
        public String message;

    }
}
