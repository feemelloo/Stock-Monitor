package com.stockmonitor.stockmonitor;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.InputFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText symbolText;
    TextView quoteView;
    TextView priceView;
    ProgressBar progressBar;
    Button searchButton;
    static final String API_KEY = "LKPZD190UQB5Z0G9";
    static final String API_URL = "https://www.alphavantage.co/query?function=BATCH_QUOTES_US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quoteView = (TextView) findViewById(R.id.quoteView);
        priceView = (TextView) findViewById(R.id.priceView);
        symbolText = (EditText) findViewById(R.id.symbolText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        searchButton = (Button) findViewById(R.id.searchButton);
        symbolText.setFilters(new InputFilter[] {new InputFilter.AllCaps(),
                                new InputFilter.LengthFilter(5)});
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });
        symbolText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                    new RetrieveFeedTask().execute();
                return false;
            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            progressBar.setVisibility(View.VISIBLE);
            quoteView.setText("");
            priceView.setText("");
        }

        protected String doInBackground(Void... urls) {
            String symbol = symbolText.getText().toString();
            if (symbol.length() == 0) {
                return null;
            }
            try {
                URL url = new URL(API_URL + "&symbols=" + symbol + "&apikey=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            String quote = "";
            if (response == null) {
                response = "\n\n\nSymbol not found";
            } else try {
                JSONObject obj = new JSONObject(response);
                JSONArray quotes = obj.getJSONArray("Stock Batch Quotes");
                double open = 0, high = 0, low = 0, price = 0;
                int volume = 0;
                String currency = null;
//                String timestamp = null;
                for (int i = 0; i < quotes.length(); i++) {
                    JSONObject qObj = quotes.getJSONObject(i);
                    open = qObj.getDouble("2. open");
                    high = qObj.getDouble("3. high");
                    low = qObj.getDouble("4. low");
                    price = qObj.getDouble("5. price");
                    volume = qObj.getInt("6. volume");
                    currency = qObj.getString("8. currency");
//                        timestamp = qObj.getString("7. timestamp");
                }
                if (price != 0) {
                    quote = currency + " " + price;
                    if (price >= open) {
                        priceView.setTextColor(Color.parseColor("#87A96B"));
                    } else {
                        priceView.setTextColor(Color.parseColor("#CC0000"));
                    }
                    response = "\n" + "Open: " + open + "\n\n" + "High: " + high + "\n\n"
                                + "Low: " + low + "\n\n" + "Volume: " + volume + "\n\n";
                } else {
                    response = "\n\n\nSymbol not found";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                response = "\n\nSearch volume exceeded, please wait a few minutes and try again.";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            quoteView.setText(response);
            priceView.setText(quote);
        }
    }
}