package com.digitalcranberry.gainsl.comms;

import android.content.ContentResolver;
import android.net.Uri;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;

/**
 * Created by yo on 04/04/15.
 */
public class UploadImage {
    private String url;
    private String APP_LOCATION = "http://gainsl-offline.appspot.com/bloburl";

    public UploadImage() {
    }

    public String getUploadURL() {
        try {
            url = new SendGet()
                    .execute(new URL(APP_LOCATION))
                    .get(); //TODO: This get is blocking and defeats the whole purpose of using an asynctask...
            //http://stackoverflow.com/questions/9273989/how-do-i-retrieve-the-data-from-asynctasks-doinbackground
        } catch (Exception e) {
            url = "error getting URL";
            e.printStackTrace();
        }
        return url;
    }

    public void upload(String uploadUrl, Uri image) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            StringWriter writer = new StringWriter();
/*            String file = image;
            ContentResolver resolver = getContentResolver();
            context.getContentResolver.openInputStream();
            String str = writer.toString();
            HttpPost httppost = new HttpPost(str);
            MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (filePath !=null) {
                File file = new File(filePath);
                Log.d("EDIT USER PROFILE", "UPLOAD: file length = " + file.length());
                Log.d("EDIT USER PROFILE", "UPLOAD: file exist = " + file.exists());
                mpEntity.addPart("avatar", new FileBody(file, "application/octet"));
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
/*        String resultJson = new JSONObject(str);
        String blobKey = resultJson.getString("blobKey");
        String servingUrl = resultJson.getString("servingUrl");
        */
    }
}
