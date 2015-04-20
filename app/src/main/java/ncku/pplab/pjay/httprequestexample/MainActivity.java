package ncku.pplab.pjay.httprequestexample;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends ActionBarActivity {

    private EditText urlEditText;
    private EditText nameEditText;
    private EditText valueEditText;
    private TextView respTextView;
    private Button doPostButton;

    private void initialViews() {
        urlEditText = (EditText) findViewById(R.id.urleditText);
        nameEditText = (EditText) findViewById(R.id.parNameEditText);
        valueEditText = (EditText) findViewById(R.id.parValueEditText);
        respTextView = (TextView) findViewById(R.id.resptextView);
        doPostButton = (Button) findViewById(R.id.doPostbutton);

        //urlEditText.setText("http://malteseann.appspot.com/rxfix");
        urlEditText.setText("http://140.116.156.227:8888/rxfix");
        nameEditText.setText("fix");
        valueEditText.setText("300315, 065812.000, 2259.8259N, 12013.3452E");

        doPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Invoke asynchornized task to execute the http POST
                //Due to access the Internet resource can invoke unpredictable delays,
                //use main thread to do http post will assert exception
                new HttpRequestTask().execute();
            }
        });
    }


    private String doPOST() {
        HttpURLConnection httpConn = null;
        try {
            Log.v("POST", "do post!");
            //Get the data from Views for sending to server
            String urlMalteseAnn = urlEditText.getText().toString();
            String postName = nameEditText.getText().toString();
            String postData = valueEditText.getText().toString();

            URL url = new URL(urlMalteseAnn);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpConn.setUseCaches(false);
            httpConn.connect();

            DataOutputStream dos = new DataOutputStream(httpConn.getOutputStream());

            String postContent = URLEncoder.encode(postName, "UTF-8")
                    + "="
                    + URLEncoder.encode(postData, "UTF-8");

            dos.write(postContent.getBytes());
            dos.flush();
            dos.close();// finish the post request

            //Check the Http Code is 200(HTTP_OK) or NOT
            int respondCode = httpConn.getResponseCode();
            if(respondCode == HttpURLConnection.HTTP_OK) {
                Log.v("POST", "send <" + postName + ", " + postData + "> to " + urlMalteseAnn);
                Log.v("POST", "response OK!");

            }else {
                Log.v("POST", "response fail!");
            }

            //Read the response from the server
            Reader reader = new InputStreamReader(httpConn.getInputStream(), "UTF-8");
            char[] buffer = new char[200];
            int cnt;
            cnt = reader.read(buffer);

//            buffer[cnt - 1] = '\0';
            String bufferStr = new String(buffer, 0, cnt);

            Log.v("POST", "response msg" + "(" + (cnt) + " bytes)" + ": " + bufferStr);
            return bufferStr;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        return new String("Fail");
    }

    // AsyncTask class
    private class HttpRequestTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            //after the doPOST() executed,
            //the return value will be the parameter for onPostExecute
            return doPOST();
        }

        @Override
        protected void onPostExecute(String s) {
            respTextView.setText( respTextView.getText() + s + "\n");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialViews();
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


