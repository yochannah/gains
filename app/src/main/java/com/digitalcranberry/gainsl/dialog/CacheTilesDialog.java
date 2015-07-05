package com.digitalcranberry.gainsl.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.digitalcranberry.gainsl.R;
import com.digitalcranberry.gainsl.caching.TileCacheManager;

public class CacheTilesDialog extends DialogFragment {
    private TileCacheManager tcm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppTheme_DialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View fragView = inflater.inflate(R.layout.fragment_select_map_area, null);
        final Context context = fragView.getContext();
        builder.setView(fragView)
                .setPositiveButton(R.string.select_map_area, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: SELECT AREA ON MAP
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.setTitle(R.string.no_maptiles_cached);
        // Create the AlertDialog object and return it
        return builder.create();
    }}
