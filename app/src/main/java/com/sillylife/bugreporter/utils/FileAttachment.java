package com.sillylife.bugreporter.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileAttachment implements Parcelable {
    @NonNull
    private final File mFile;
    @NonNull private final String mMimeType;

    public FileAttachment(@NonNull File file, @NonNull String mimeType) {
        this.mFile = file;
        this.mMimeType = mimeType;
    }

    public JSONObject toJSON() throws JSONException, IOException {
        JSONObject json = new JSONObject();
        json.put("filename", mFile.getName());

        byte[] data = toByteArray();
        if (data != null) {
            json.put("base64_attachment_data", Base64.encodeToString(data, Base64.NO_WRAP));
        }

        json.put("mime_type", mMimeType);
        return json;
    }

    @NonNull public File getFile() {
        return mFile;
    }

    public boolean isImage() {
        return mMimeType.equals(MimeTypes.JPG) || mMimeType.equals(MimeTypes.PNG);
    }

    public boolean isVideo() {
        return mMimeType.equals(MimeTypes.MP4);
    }

    private byte[] toByteArray() throws IOException {
        FileInputStream inputStream = new FileInputStream(mFile);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int) mFile.length());
        try {
            IOUtils.write(inputStream, outputStream);
            return outputStream.toByteArray();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /* Parcelable */

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mFile);
        dest.writeString(this.mMimeType);
    }

    protected FileAttachment(Parcel in) {
        this.mFile = (File) in.readSerializable();
        this.mMimeType = in.readString();
    }

    public static final Creator<FileAttachment> CREATOR = new Creator<FileAttachment>() {
        @Override public FileAttachment createFromParcel(Parcel source) {
            return new FileAttachment(source);
        }

        @Override public FileAttachment[] newArray(int size) {
            return new FileAttachment[size];
        }
    };
}
