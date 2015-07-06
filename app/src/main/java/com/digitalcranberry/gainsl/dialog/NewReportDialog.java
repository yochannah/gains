package com.digitalcranberry.gainsl.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.digitalcranberry.gainsl.GeoLocator;
import com.digitalcranberry.gainsl.R;
import com.digitalcranberry.gainsl.constants.ReportStatuses;
import com.digitalcranberry.gainsl.caching.CacheDbConstants;
import com.digitalcranberry.gainsl.caching.ReportCacheManager;
import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.model.events.ReportCreated;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import de.greenrobot.event.EventBus;

public class NewReportDialog extends DialogFragment {

    private ImageButton cameraButton;
    private Report report;
    private GeoLocator geo;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri imageUri;
    private ReportCacheManager saveReport;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Panel);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View fragView = inflater.inflate(R.layout.fragment_new_report_dialog, null);
        final Context context = fragView.getContext();
        builder.setView(fragView)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        generateReportDetails(fragView);
                        geo.stopListening();
                        saveReport = new ReportCacheManager();
                        saveReport.save(context,report, CacheDbConstants.UnsentReportEntry.TABLE_NAME);
                        EventBus.getDefault().post(new ReportCreated(report));

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        //start listening for location.
        geo = new GeoLocator();
        geo.startListening(context);



        cameraButton = (ImageButton) fragView.findViewById(R.id.input_report_take_picture);
        addPhotoClickListener();

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void addPhotoClickListener() {
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dispatchTakePictureIntent();
            }

        });

    }

    private void generateReportDetails(View view) {
        EditText content = (EditText) view.findViewById(R.id.input_report_description);
        report = new Report(content.getText().toString());
        report.setLocation(geo.getCurrentLocation());
        report.setOrgName("OU");
        report.setId(UUID.randomUUID().toString());
        report.setStatus(ReportStatuses.REPORT_UNSENT);
        if(imageUri != null) {
            report.setImage(imageUri); // save uri to the report
        }
    }

    private void dispatchTakePictureIntent() {

        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "gainsl");
        imagesFolder.mkdirs();
        String fileName = "gainsl_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
        File image = new File(imagesFolder, fileName);
        Uri uriSavedImage = Uri.fromFile(image);
        imageUri = uriSavedImage;
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this.getActivity(), "Image saved successfully", Toast.LENGTH_LONG).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                Toast.makeText(this.getActivity(), "Image problem, please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }


}
