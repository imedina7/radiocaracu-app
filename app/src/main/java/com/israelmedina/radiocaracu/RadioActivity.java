package com.israelmedina.radiocaracu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import co.mobiwise.library.radio.RadioListener;
import co.mobiwise.library.radio.RadioManager;

/**
 * Created by mertsimsek on 04/11/15.
 */
public class RadioActivity extends Activity implements RadioListener{

    private String[] RADIO_URL = {"http://50.22.219.37:3335/caritadecu"};

    Button mButtonControlStart;
    TextView mTextViewControl;
    EditText url_input;
    RadioManager mRadioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetStreamingURL g = new GetStreamingURL();

        g.execute();
        setContentView(R.layout.activity_radio);

        mRadioManager = RadioManager.with(getApplicationContext());
        mRadioManager.registerListener(this);
        mRadioManager.setLogging(true);
    }
    private class GetStreamingURL extends AsyncTask<Void,Void,Void> {
        private ProgressDialog progressDialog = new ProgressDialog(RadioActivity.this);
        InputStream inputStream = null;
        String result = "";


        protected void onPreExecute() {
            progressDialog.setMessage("Getting server data...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    GetStreamingURL.this.cancel(true);
                }
            });
        }
        @Override
        protected Void doInBackground(Void... s) {
            String inputLine;
            try {
                URL coso = new URL("http://www.caritade.cu.cc/server.json");
                URLConnection conn = coso.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                StringBuilder sBuilder = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    sBuilder.append(inputLine);
                    Log.i("GetStreamingURL", "[*] Got message: " + inputLine);
                }
                in.close();
                result = sBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void v) {
            //parse JSON data
            try {

                JSONObject json;
                json = new JSONObject(result);
                RADIO_URL[0] = json.getString("streaming_uri");

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } finally {
                this.progressDialog.dismiss();
                initializeUI();
                // catch (JSONException e)
            }
        } // protected void onPostExecute(Void v)

    }
    public void initializeUI() {
        mButtonControlStart = (Button) findViewById(R.id.buttonControlStart);
        mTextViewControl = (TextView) findViewById(R.id.textviewControl);
        url_input = (EditText) findViewById(R.id.radio_url);

        url_input.setText(RADIO_URL[0]);

        mButtonControlStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRadioManager.isPlaying())
                    mRadioManager.startRadio(url_input.getText().toString());
                else
                    mRadioManager.stopRadio();
            }
        });
    }
    public void updateMetadata(String text) {
        TextView metadataText = (TextView) findViewById(R.id.metadataText);
        metadataText.setText("Now playing: " + text);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mRadioManager.connect();
    }

    @Override
    public void onRadioLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO Do UI works here.
                mTextViewControl.setText("RADIO STATE : LOADING...");
            }
        });
    }

    @Override
    public void onRadioConnected() {

    }

    @Override
    public void onRadioStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO Do UI works here.
                mTextViewControl.setText("RADIO STATE : PLAYING...");
                mButtonControlStart.setText(R.string.pause_btn);
            }
        });
    }

    @Override
    public void onRadioStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO Do UI works here
                mTextViewControl.setText("RADIO STATE : STOPPED.");
                mButtonControlStart.setText(R.string.play_btn);
            }
        });
    }

    @Override
    public void onMetaDataReceived(String s, String s1) {
        //TODO Check metadata values. Singer name, song name or whatever you have.
        final String text = s1;
        if (s != null )
            if (s.equals("StreamTitle") && !s1.equals(""))
            runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      updateMetadata(text);
                  }
              }

            );

    }

    @Override
    public void onError() {

    }
}
