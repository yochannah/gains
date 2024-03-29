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
import android.widget.Toast;

import com.digitalcranberry.gainsl.R;
import com.digitalcranberry.gainsl.caching.TileCacheManager;
import com.digitalcranberry.gainsl.map.MapEventsOverlay;
import com.digitalcranberry.gainsl.model.events.map.AddOverlay;
import com.digitalcranberry.gainsl.model.events.map.RemoveOverlay;
import com.digitalcranberry.gainsl.model.events.map.TouchEvent;
import static com.digitalcranberry.gainsl.constants.Constants.DEBUGTAG;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class CacheTilesDialog extends DialogFragment {
//    private TileCacheManager tcm;
//    private List<GeoPoint> corners;
//    MapEventsOverlay mOverlay;
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

    //    corners = new ArrayList<>();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppTheme_DialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View fragView = inflater.inflate(R.layout.fragment_select_map_area, null);
        final Context context = fragView.getContext();
        builder.setView(fragView)
                .setPositiveButton(R.string.ok_got_it, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Todo save the whales. Or, alternatively, save the preference so this doesn't come back up
                    }
                });

        builder.setTitle(R.string.inital_setup_required);
        // Create the AlertDialog object and return it
        return builder.create();
    }

//    public void onEvent(TouchEvent event){
//        //allow the user to long press two points.
//        corners.add(event.point);
//        Log.i(DEBUGTAG, "you long touched!");
//        if(corners.size() >=2) {
//            //figure out the correct square, show it to the user, and say it's saved.
//            ////new dialog. That's great! Here's the area you've selected.
//            ////the zoom needs to be fixed quite small for this. how do we prevent huge files from being selected?
//            //future todo: if you want to add any more, please go to settings to adjust cached maptiles
//            Toast.makeText(getActivity(), "Cool, you've selected an area",
//                    Toast.LENGTH_LONG).show();
//            EventBus.getDefault().post(new RemoveOverlay(mOverlay));
//        }
//
//    }




}

