package com.example.nicolas.speechrecognizertest;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private EditText met_texthint;
    private Button mbt_speak;
    private Spinner ms_textmatches;
    private ListView mlv_textmatches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        met_texthint = (EditText) findViewById(R.id.etTextHint);
        mbt_speak = (Button) findViewById(R.id.btSpeak);
        ms_textmatches = (Spinner) findViewById(R.id.sNoOfMatches);
        mlv_textmatches = (ListView) findViewById(R.id.lvTextMatches);

    }

    public void CheckVoiceRecognition(){
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        if (activities.size() == 0){
            mbt_speak.setEnabled(false);
            Toast.makeText(this, getResources().getString(R.string.VR_notpresent),Toast.LENGTH_SHORT).show();
        }
    }

    public void speak(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, met_texthint.getText().toString());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        if (ms_textmatches.getSelectedItemPosition() == AdapterView.INVALID_POSITION){
            Toast.makeText(this, getResources().getString(R.string.NR_forSpinner),Toast.LENGTH_SHORT).show();
            return;
        }
        int noOfMatches = Integer.parseInt(ms_textmatches.getSelectedItem().toString());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            ArrayList<String> textMatchlist = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!textMatchlist.isEmpty()){
                if (textMatchlist.get(0).contains("search")){
                    String searchQuery = textMatchlist.get(0).replace("search", " ");
                    Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    search.putExtra(SearchManager.QUERY,searchQuery);
                    startActivity(search);
                }
                else {
                    mlv_textmatches.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,textMatchlist));
                }
            }
        }
        else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
            Toast.makeText(this,"Audio Error", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
            Toast.makeText(this,"Client Error", Toast.LENGTH_SHORT).show();

        }
        else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
            Toast.makeText(this,"Network Error", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){
            Toast.makeText(this,"No Match", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
            Toast.makeText(this,"Server Error", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
