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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.digitalcranberry.gainsl.R;
import com.digitalcranberry.gainsl.caching.CacheDbConstants;
import com.digitalcranberry.gainsl.caching.PendingReportCounter;
import com.digitalcranberry.gainsl.caching.ReportCacheManager;
import com.digitalcranberry.gainsl.constants.ReportStatuses;
import com.digitalcranberry.gainsl.model.Report;
import com.digitalcranberry.gainsl.model.events.report.UpdatedByUser;

import de.greenrobot.event.EventBus;

public class UpdateReportDialog extends DialogFragment {

    private Report report;
    private ReportCacheManager saveReport;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Holo_Light_Panel);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View fragView = inflater.inflate(R.layout.fragment_update_report_dialog, null);
        final Context context = fragView.getContext();
        builder.setView(fragView)
                .setPositiveButton(R.string.send_update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: Save new details to report db.
                        //phones only have the current known status, for simplicity
                        //but the entire audit history is present on the server.
                        updateReport(fragView, context);
                        EventBus.getDefault().post(new UpdatedByUser(report));
                        PendingReportCounter.updatePendingReportCount(context, saveReport);
                        Log.i("GAINSL", report.toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // User cancelled the dialog
                    }
                });

        Spinner spinner = (Spinner) fragView.findViewById(R.id.status_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.user_report_statuses, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void updateReport(View view, Context context) {
        Spinner status = (Spinner) view.findViewById(R.id.status_spinner);
        report.setSendStatus(status.getSelectedItem().toString());
        EditText content = (EditText) view.findViewById(R.id.input_report_description_update);
        report.setContent(content.getText().toString());

        saveReport = new ReportCacheManager();
        saveReport.save(context, report, CacheDbConstants.UnsentReportEntry.TABLE_NAME);
    }

    private void generateReportDetails(View view) {
        report.setSendStatus(ReportStatuses.REPORT_UPDATED);

    }

    public void setReport(Report report) {
        this.report = report;
    }

}
