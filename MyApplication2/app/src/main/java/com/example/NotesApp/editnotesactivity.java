package com.example.NotesApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class editnotesactivity extends AppCompatActivity {

    private Intent data;
    private EditText medittitleofnote, meditcontentofnote;
    private FloatingActionButton msaveeditnote;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnotesactivity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        medittitleofnote = findViewById(R.id.edittitleofnote);
        meditcontentofnote = findViewById(R.id.editcontentofnote);
        msaveeditnote = findViewById(R.id.saveeditnote);
        data = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbarofeditnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if (data != null) {
            String notetitle = data.getStringExtra("title");
            String notecontent = data.getStringExtra("content");

             medittitleofnote.setText(notetitle);
            meditcontentofnote.setText(notecontent);
        }

        msaveeditnote.setOnClickListener(view -> {
            String newTitle = medittitleofnote.getText().toString();
            String newContent = meditcontentofnote.getText().toString();

            if (newTitle.isEmpty() || newContent.isEmpty()) {
                Toast.makeText(editnotesactivity.this, "Both fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            String noteId = data.getStringExtra("noteId");
            if (noteId == null) {
                Toast.makeText(editnotesactivity.this, "Error: Note ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentReference documentReference = firebaseFirestore
                    .collection("Notes")
                    .document(firebaseUser.getUid())
                    .collection("Mynotes")
                    .document(data.getStringExtra("noteId"));

            Map<String,Object> note = new HashMap<>();
            note.put("title", newTitle);
            note.put("content", newContent);

            documentReference.set(note)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(editnotesactivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(editnotesactivity.this, "Failed to update note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
