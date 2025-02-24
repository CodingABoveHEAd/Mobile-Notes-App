package com.example.NotesApp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class About_the_app extends AppCompatActivity {

    Button mtutorial, minformation;
    TextView minfoTextView;
    RequestQueue requestQueuei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_the_app);

        mtutorial = findViewById(R.id.tutorialButton);
        minformation = findViewById(R.id.informationButton);
        minfoTextView = findViewById(R.id.jsonContentTextView);
        minfoTextView.setMovementMethod(new android.text.method.ScrollingMovementMethod());
        requestQueuei = Volley.newRequestQueue(this);

        mtutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchTutorialData();
                Toast.makeText(getApplicationContext(),"Showing Tutorial",Toast.LENGTH_SHORT).show();
            }
        });

        minformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchInformationData();
                Toast.makeText(getApplicationContext(),"Showing Information",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void fetchInformationData() {
        String url = "https://api.myjson.online/v1/records/35cc7719-4ca6-4438-9b6e-64246babd66a";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.d("JSON_RESPONSE", response.toString());
                            if (response.has("data")) {
                                JSONObject dataObject = response.getJSONObject("data");
                                if (dataObject.has("information")) {
                                    String information = dataObject.getString("information");
                                    minfoTextView.setText(information);
                                } else {
                                    Toast.makeText(About_the_app.this, "Key 'information' not found in JSON", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(About_the_app.this, "Key 'data' not found in JSON", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(About_the_app.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(About_the_app.this, "Error fetching information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueuei.add(request);
    }

    public void fetchTutorialData() {
        String url = "https://api.myjson.online/v1/records/5db3dabb-6b5c-4a25-8af5-7c0f3b9e0426";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("JSON_RESPONSE", response.toString()); // Debug log

                            if (response.has("data")) {
                                JSONObject dataObject = response.getJSONObject("data");
                                if (dataObject.has("tutorial")) {
                                    JSONObject tutorialObject = dataObject.getJSONObject("tutorial");

                                    StringBuilder tutorialContent = new StringBuilder();

                                    if (tutorialObject.has("login")) {
                                        tutorialContent.append("Login:\n")
                                                .append(tutorialObject.getString("login"))
                                                .append("\n\n");
                                    }

                                    if (tutorialObject.has("create_note")) {
                                        JSONObject createNote = tutorialObject.getJSONObject("create_note");
                                        tutorialContent.append("Create Note:\n")
                                                .append(createNote.getString("description")).append("\n")
                                                .append("Requirements: ").append(createNote.getString("requirements")).append("\n")
                                                .append("Details: ").append(createNote.getString("details")).append("\n\n");
                                    }

                                    if (tutorialObject.has("edit_note")) {
                                        JSONObject editNote = tutorialObject.getJSONObject("edit_note");
                                        tutorialContent.append("Edit Note:\n")
                                                .append(editNote.getString("description")).append("\n")
                                                .append("Features:\n");
                                        for (int i = 0; i < editNote.getJSONArray("features").length(); i++) {
                                            tutorialContent.append("- ")
                                                    .append(editNote.getJSONArray("features").getString(i))
                                                    .append("\n");
                                        }
                                        tutorialContent.append("\n");
                                    }

                                    if (tutorialObject.has("delete_note")) {
                                        JSONObject deleteNote = tutorialObject.getJSONObject("delete_note");
                                        tutorialContent.append("Delete Note:\n")
                                                .append(deleteNote.getString("description")).append("\n")
                                                .append("Action Bar: ").append(deleteNote.getString("action_bar")).append("\n");
                                    }
                                    minfoTextView.setText(tutorialContent.toString());
                                } else {
                                    Toast.makeText(About_the_app.this, "Key 'tutorial' not found in JSON", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(About_the_app.this, "Key 'data' not found in JSON", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(About_the_app.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(About_the_app.this, "Error fetching tutorial: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueuei.add(request);
    }
}



