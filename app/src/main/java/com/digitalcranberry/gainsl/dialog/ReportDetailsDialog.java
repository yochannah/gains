package com.digitalcranberry.gainsl.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.digitalcranberry.gainsl.R;
import com.digitalcranberry.gainsl.model.Report;
import static com.digitalcranberry.gainsl.constants.Constants.DEBUGTAG;

public class ReportDetailsDialog extends DialogFragment {

    private Report report;
    private String snippet;

    public static ReportDetailsDialog newInstance(Report report) {
        ReportDetailsDialog f = new ReportDetailsDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("report", report);
        f.setArguments(args);

        return f;
    }

    public static ReportDetailsDialog newInstance(String snippet) {
        ReportDetailsDialog f = new ReportDetailsDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("snippet", snippet);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Panel);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        if(savedInstanceState != null) {

            if (savedInstanceState.getString("snippet") != null) {
                this.snippet = savedInstanceState.getString("snippet");
            } else {
                this.report = (Report) savedInstanceState.getParcelable("report");
                snippet = report.getContent();
            }
            Log.i(DEBUGTAG, snippet);
        }
        
        final View fragView = inflater.inflate(R.layout.dialog_report_details, null);
        final Context context = fragView.getContext();

        TextView reportDetails = (TextView) fragView.findViewById(R.id.report_details_content);

        reportDetails.setText(snippet);

        builder.setView(fragView)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
