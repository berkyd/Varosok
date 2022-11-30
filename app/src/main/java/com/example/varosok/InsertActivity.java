package com.example.varosok;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

public class InsertActivity extends AppCompatActivity {

    private Button addCityButton, visszaButton;
    private EditText varosText, orszagText, lakossagText;
    private static final String baseUrl = "https://retoolapi.dev/EXCwwX/varosok";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        init();
        visszaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InsertActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (varosText.getText().toString().equals("")) {
                    Toast.makeText(InsertActivity.this, "Nem adott meg város nevet!", Toast.LENGTH_SHORT).show();
                } else if (orszagText.getText().toString().equals("")) {
                    Toast.makeText(InsertActivity.this, "Nem adott meg országot", Toast.LENGTH_SHORT).show();
                } else if (lakossagText.getText().toString().equals("")) {
                    Toast.makeText(InsertActivity.this, "Nem adott meglakosságot", Toast.LENGTH_SHORT).show();
                }else if(Integer.parseInt(lakossagText.getText().toString()) < 0){
                    Toast.makeText(InsertActivity.this, "A lakosság minimum 0 lehet!", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        String json = createJsonFromFormdata();
                        RequestTask task = new RequestTask(baseUrl, "POST", json);
                        task.execute();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(InsertActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private String createJsonFromFormdata() {
        String nev = varosText.getText().toString().trim();
        String orszag = orszagText.getText().toString().trim();
        int lakossag = Integer.parseInt(lakossagText.getText().toString().trim());
        Varos varos = new Varos(0, nev, orszag, lakossag);
       Gson converter = new Gson();
        return converter.toJson(varos);
    }

    private void init(){
        addCityButton = findViewById(R.id.addCityButton);
        visszaButton = findViewById(R.id.visszaButton);
        varosText = findViewById(R.id.varosSzoveg);
        orszagText = findViewById(R.id.orszagSzoveg);
        lakossagText = findViewById(R.id.lakossagSzoveg);
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        private String requestUrl;
        private String requestMethod;
        private String requestBody;

        public RequestTask(String requestUrl) {
            this.requestUrl = requestUrl;
            this.requestMethod = "GET";
        }

        public RequestTask(String requestUrl, String requestMethod) {
            this.requestUrl = requestUrl;
            this.requestMethod = requestMethod;
        }

        public RequestTask(String requestUrl, String requestMethod, String requestBody) {
            this.requestUrl = requestUrl;
            this.requestMethod = requestMethod;
            this.requestBody = requestBody;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestMethod) {
                    case "GET":
                        response = RequestHandler.get(baseUrl);
                        break;
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestBody);
                        break;
                    case "PUT":
                        response = RequestHandler.put(requestUrl, requestBody);
                        break;
                    case "DELETE":
                        response = RequestHandler.delete(requestUrl);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }
}
