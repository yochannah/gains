package com.digitalcranberry.gainsl;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;

import com.digitalcranberry.gainsl.comms.ReportCommsService;
import com.digitalcranberry.gainsl.map.MapFragment;


public class ReportActivity extends ActionBarActivity {
    private String TAG = "gainslDebug";
    private MapFragment mapFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.map_container, mapFrag = new MapFragment())
                    .commit();
        }
        Intent intent = new Intent(this, ReportCommsService.class);
        startService(intent);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, data + ", result: " + resultCode + ", request: " + requestCode);

//        super.onActivityResult(requestCode,resultCode, data);


        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Fragment reportFrag = getSupportFragmentManager().findFragmentById(R.id.new_report_dialog_fragment);
            Toast toast = Toast.makeText(getApplicationContext(), "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();

            Log.i(TAG, data.toString() + " " + resultCode);

            reportFrag.onActivityResult(requestCode, resultCode, data);

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "What is the story, bob?" + data, Toast.LENGTH_LONG);
            Log.i(TAG, Integer.toString(requestCode) + " " + resultCode);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();
            super.onActivityResult(requestCode,resultCode, data);
        }

    }

}
