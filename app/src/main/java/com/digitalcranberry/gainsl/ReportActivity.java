package com.digitalcranberry.gainsl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalcranberry.gainsl.comms.ReportCommsService;
import com.digitalcranberry.gainsl.dialog.CacheTilesDialog;
import com.digitalcranberry.gainsl.dialog.NewReportDialog;
import com.digitalcranberry.gainsl.map.MapFragment;
import com.digitalcranberry.gainsl.model.events.PendingReportCountUpdated;
import com.digitalcranberry.gainsl.settings.SettingsActivity;

import java.util.Date;
import java.util.GregorianCalendar;

import de.greenrobot.event.EventBus;

import static com.digitalcranberry.gainsl.constants.Constants.DEBUGTAG;


public class ReportActivity extends ActionBarActivity  {
    private MapFragment mapFrag; //this is used, honest, but android studio is a fool

    /*
Binds Eventbus listener
 */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /*
    Unbinds Eventbus listener
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /*
Eventbus event handler for newreport creation. Adds map marker.
 */
    public void onEvent(PendingReportCountUpdated event){
        Log.i(DEBUGTAG, "pending count updated");
        TextView statusBar = (TextView) findViewById(R.id.statusbar);
        GregorianCalendar calendar = new GregorianCalendar();
        Date now = new Date();
        calendar.setTime(now);
        if(event.reports <= 0 ) {
            statusBar.setText("Last synchronised " + now);
        } else {
            statusBar.setText("Currently working offline. Unsent reports: " + event.reports + ". ");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        //setup map
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.map_container, mapFrag = new MapFragment())
                    .commit();
        }

        //start service to monitor reports and send if network is present
        Intent intent = new Intent(this, ReportCommsService.class);
        startService(intent);

        //check for gps and ask user to enable it if it is not.
        if(!isGPSEnabled()) {
            buildAlertMessageNoGps();
        }

//        checkTileCache();

    }

    public void addNewReportClickListener(View view) {
        NewReportDialog d = new NewReportDialog();
        d.show(getSupportFragmentManager(), "new_report");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(DEBUGTAG, data + ", result: " + resultCode + ", request: " + requestCode);

//        super.onActivityResult(requestCode,resultCode, data);


        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Fragment reportFrag = getSupportFragmentManager().findFragmentById(R.id.new_report_dialog_fragment);
            Toast toast = Toast.makeText(getApplicationContext(), "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();

            Log.i(DEBUGTAG, data.toString() + " " + resultCode);

            reportFrag.onActivityResult(requestCode, resultCode, data);

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "What is the story, bob?" + data, Toast.LENGTH_LONG);
            Log.i(DEBUGTAG, Integer.toString(requestCode) + " " + resultCode);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();
            super.onActivityResult(requestCode,resultCode, data);
        }

    }

    private boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        boolean isEnabled = manager.isProviderEnabled( LocationManager.GPS_PROVIDER );
        Log.i(DEBUGTAG, "Gps enabled: " + String.valueOf(isEnabled));
        return isEnabled;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void checkTileCache() {
        //check if this is the first run
        //userprefs.
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if(!prefs.contains("setupComplete")) {
            CacheTilesDialog d = new CacheTilesDialog();
            d.show(getSupportFragmentManager(), "select_map_area");
            //prefs.edit().putBoolean("setupComplete", true);
        } else {
            //TODO if it's not, check if all reports are within mapped areas.
        }
    }
}
