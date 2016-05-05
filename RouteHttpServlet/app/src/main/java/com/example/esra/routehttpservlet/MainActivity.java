package com.example.esra.routehttpservlet;

import android.support.v7.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements OnClickListener{
        Button button;
        TextView outputText;
        EditText src;
        EditText dst;
        String sourceLon ;
        String sourceLat;
        String destinationLon;
        String destinationLat;
    ArrayList<String> latlons ;

        public static final String URL =
                "http://192.168.56.1:8080/RoutePlanner/Route";

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            findViewsById();
            button.setOnClickListener(this);
        }

        private void findViewsById() {
            button = (Button) findViewById(R.id.button);
            outputText = (TextView) findViewById(R.id.outputTxt);
            src = (EditText) findViewById(R.id.src);
            dst = (EditText) findViewById(R.id.dst);
        }
    private void split()
    {
        String[] source=src.getText().toString().split(",");
        String[] destination = dst.getText().toString().split(",");
        sourceLon = source[0];
        sourceLat =source[1];
        //Log.d("control","hhh");
        destinationLon = destination[0];
        destinationLat = destination[1];

    }

        public void onClick(View view) {
            GetXMLTask task = new GetXMLTask();
            split();
            task.execute(new String[]{URL+ "?src=" + sourceLon + "%2C" + sourceLat + "&dst=" + destinationLon + "%2C" + destinationLat});
            //Log.d("control", "yyyy");
        }

        private class GetXMLTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                String output = null;
                for (String url : urls) {
                    output = getOutputFromUrl(url);
                }
                return output;

            }

            private String getOutputFromUrl(String url) {

                try {
                    InputStream stream = getHttpConnection(url);
                    BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(stream));
                    latlons= new ArrayList<>();
                    String s = "";
                    while ((s = buffer.readLine()) != null)
                        if(s.contains("Geometry.Point")){
                            int index1 = s.indexOf("(");
                            int index2 = s.indexOf(")");
                            String special = s.substring(index1+2,index2-1);

                            latlons.add(special);
                        }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return latlons.toString();
            }




            // Makes HttpURLConnection and returns InputStream
            private InputStream getHttpConnection(String urlString)
                    throws IOException {
                InputStream stream = null;
                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();

                try {
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    httpConnection.setRequestMethod("GET");
                    httpConnection.connect();

                    if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        stream = httpConnection.getInputStream();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return stream;
            }


            @Override
            protected void onPostExecute(String output) {

                    outputText.setText(output);

            }
        }//class GetXMLTask
    }