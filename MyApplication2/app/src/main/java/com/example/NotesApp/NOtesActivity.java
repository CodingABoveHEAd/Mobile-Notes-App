package com.example.NotesApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NOtesActivity extends AppCompatActivity {

    FloatingActionButton mcreatenotesfab;
    private FirebaseAuth firebaseAuth;

    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder>noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes);
        mcreatenotesfab = findViewById(R.id.createnotefab);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Notes");

        mcreatenotesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NOtesActivity.this, createnote.class));
            }
        });


        Query query = firebaseFirestore.collection("Notes").
                document(firebaseUser.getUid()).collection("Mynotes").
                orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel> allusernotes =
                new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();
        noteAdapter = new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull firebasemodel model) {  //Recyclerview code
                ImageView popupbutton=holder.itemView.findViewById(R.id.menupopupbutton);
                int colorcode=getRandomcolor();  //Random Color
                holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(colorcode,null));
                holder.notetitle.setText(model.getTitle());
                holder.notecontent.setText(model.getContent());
                String docId=noteAdapter.getSnapshots().getSnapshot(position).getId();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(view.getContext(), notedetails.class);
                        intent.putExtra("title",model.getTitle());
                        intent.putExtra("content",model.getContent());
                        intent.putExtra("noteid",docId);
                        view.getContext().startActivity(intent);
                        Toast.makeText(getApplicationContext(),"This is Clicked",Toast.LENGTH_SHORT).show();
                    }
                });

                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu=new PopupMenu(view.getContext(),view);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {   //editnote code
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {

                                Intent intent=new Intent(view.getContext(),editnotesactivity.class);
                                intent.putExtra("title",model.getTitle());
                                intent.putExtra("content",model.getContent());
                                intent.putExtra("noteId",docId);
                                view.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {    //delete note code
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                                DocumentReference documentReference= firebaseFirestore.
                                        collection("Notes").
                                        document(firebaseUser.getUid()).collection("Mynotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(),"This note is deleted",Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(),"Failed to delete",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                noteAdapter.notifyDataSetChanged();
            }
        };
        mrecyclerview = findViewById(R.id.recylerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(noteAdapter);

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView notetitle, notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecontent);
            mnote = itemView.findViewById(R.id.note);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //signout code
        if (item.getItemId()==R.id.logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(NOtesActivity.this, MainActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null)
        {
            noteAdapter.stopListening();
        }
    }


    private int getRandomcolor()
    {
        List<Integer>colourcode=new ArrayList<>();
        colourcode.add(R.color.gray);
        // colourcode.add(R.color.black);
        colourcode.add(R.color.white);
        colourcode.add(R.color.lightgray);
        colourcode.add(R.color.darkgray);
        colourcode.add(R.color.silver);
        //  colourcode.add(R.color.dimgray);
        colourcode.add(R.color.red);
        colourcode.add(R.color.green);
        colourcode.add(R.color.blue);
        colourcode.add(R.color.yellow);
        colourcode.add(R.color.cyan);
        colourcode.add(R.color.magenta);
        colourcode.add(R.color.orange);
        colourcode.add(R.color.purple);
        colourcode.add(R.color.pink);
        colourcode.add(R.color.brown);
        colourcode.add(R.color.lightblue);
        colourcode.add(R.color.skyblue);
        colourcode.add(R.color.royalblue);
        colourcode.add(R.color.navy);
        colourcode.add(R.color.teal);
        colourcode.add(R.color.lime);
        colourcode.add(R.color.olive);
        colourcode.add(R.color.coral);
        colourcode.add(R.color.salmon);
        colourcode.add(R.color.khaki);
        colourcode.add(R.color.plum);
        colourcode.add(R.color.lavender);
        colourcode.add(R.color.beige);
        colourcode.add(R.color.chocolate);
        colourcode.add(R.color.peru);
        colourcode.add(R.color.tomato);
        colourcode.add(R.color.orchid);
        colourcode.add(R.color.lightpink);
        colourcode.add(R.color.darkviolet);
        colourcode.add(R.color.lightslategray);
        colourcode.add(R.color.midnightblue);
        colourcode.add(R.color.steelblue);
        colourcode.add(R.color.lightseagreen);
        colourcode.add(R.color.darkgreen);
        colourcode.add(R.color.forestgreen);
        colourcode.add(R.color.seagreen);
        colourcode.add(R.color.aqua);
        colourcode.add(R.color.darkcyan);
        colourcode.add(R.color.indigo);
        colourcode.add(R.color.darkorange);
        colourcode.add(R.color.lightgoldenrodyellow);
        colourcode.add(R.color.mediumpurple);
        colourcode.add(R.color.fuchsia);
        colourcode.add(R.color.violet);
        colourcode.add(R.color.lightcoral);
        colourcode.add(R.color.sandybrown);
        colourcode.add(R.color.goldenrod);
        colourcode.add(R.color.darkkhaki);
        colourcode.add(R.color.powderblue);
        colourcode.add(R.color.thistle);
        colourcode.add(R.color.darkslategray);
        colourcode.add(R.color.lightsalmon);
        colourcode.add(R.color.bisque);
        colourcode.add(R.color.mistyrose);
        colourcode.add(R.color.mediumseagreen);
        colourcode.add(R.color.lightsteelblue);
        colourcode.add(R.color.slateblue);
        colourcode.add(R.color.palevioletred);
        colourcode.add(R.color.mediumpink);
        colourcode.add(R.color.yellowgreen);
        colourcode.add(R.color.mediumaquamarine);
        colourcode.add(R.color.darkturquoise);
        colourcode.add(R.color.lightcyan);
        colourcode.add(R.color.darkorchid);
        colourcode.add(R.color.slategray);
        colourcode.add(R.color.mediumspringgreen);
        colourcode.add(R.color.chartreuse);
        colourcode.add(R.color.darkslateblue);
        colourcode.add(R.color.cadetblue);
        colourcode.add(R.color.darkseagreen);
        colourcode.add(R.color.rosybrown);
        colourcode.add(R.color.gold);
        colourcode.add(R.color.tan);
        colourcode.add(R.color.lightyellow);
        colourcode.add(R.color.blanchedalmond);
        colourcode.add(R.color.seashell);
        colourcode.add(R.color.aliceblue);
        colourcode.add(R.color.amethyst);
        colourcode.add(R.color.apricot);
        colourcode.add(R.color.aureolin);
        colourcode.add(R.color.banana);
        colourcode.add(R.color.battleshipgray);
        colourcode.add(R.color.beaublue);
        // colourcode.add(R.color.bistre);
        colourcode.add(R.color.blizzardblue);
        colourcode.add(R.color.bluebell);
        colourcode.add(R.color.bonbon);
        colourcode.add(R.color.brightgreen);
        colourcode.add(R.color.brightmaroon);
        colourcode.add(R.color.bubblegum);
        colourcode.add(R.color.capri);
        colourcode.add(R.color.caribbeanblue);
        colourcode.add(R.color.cerulean);
        colourcode.add(R.color.ceruleanblue);
        colourcode.add(R.color.chambray);
        colourcode.add(R.color.chartreusegreen);
        colourcode.add(R.color.cherry);
        colourcode.add(R.color.chocolatebrown);
        colourcode.add(R.color.cinnamon);
        colourcode.add(R.color.cobaltblue);
        colourcode.add(R.color.coolgrey);
        colourcode.add(R.color.coralred);
        colourcode.add(R.color.coyote);
        colourcode.add(R.color.darkbluegray);
        // colourcode.add(R.color.darkchocolate);
        colourcode.add(R.color.darkfuchsia);
        colourcode.add(R.color.darklavender);
        colourcode.add(R.color.darkplum);
        colourcode.add(R.color.darkslategrey);
        colourcode.add(R.color.darkspringgreen);
        // colourcode.add(R.color.deepforestgreen);
        colourcode.add(R.color.deeppink);
        colourcode.add(R.color.dijon);
        colourcode.add(R.color.dodgerblue);
        colourcode.add(R.color.dreamer);
        colourcode.add(R.color.dusk);
        colourcode.add(R.color.eggshell);
        colourcode.add(R.color.emerald);
        colourcode.add(R.color.emeraldgreen);
        colourcode.add(R.color.fern);
        colourcode.add(R.color.gainsboro);
        colourcode.add(R.color.georgia);
        colourcode.add(R.color.glaucous);
        colourcode.add(R.color.grullo);
        colourcode.add(R.color.halyconblue);
        colourcode.add(R.color.harlequin);
        colourcode.add(R.color.hazel);
        colourcode.add(R.color.heliotrope);
        colourcode.add(R.color.honey);
        colourcode.add(R.color.iceblue);
        colourcode.add(R.color.independence);
        colourcode.add(R.color.jasmine);
        colourcode.add(R.color.junglegreen);
        colourcode.add(R.color.kellygreen);
        colourcode.add(R.color.lilac);
        colourcode.add(R.color.limegreen);
        colourcode.add(R.color.malachite);
        colourcode.add(R.color.maroon);
        colourcode.add(R.color.mint);
        colourcode.add(R.color.mossgreen);
        colourcode.add(R.color.mountainmeadow);
        colourcode.add(R.color.mysticrose);
        colourcode.add(R.color.neonblue);
        colourcode.add(R.color.olivegreen);
        colourcode.add(R.color.orangepeel);
        colourcode.add(R.color.pansy);
        colourcode.add(R.color.peacockblue);
        colourcode.add(R.color.pearl);
        colourcode.add(R.color.peach);
        colourcode.add(R.color.pear);
        colourcode.add(R.color.pearlwhite);
        colourcode.add(R.color.peacock);
        colourcode.add(R.color.periwinkle);
        colourcode.add(R.color.pistachio);
        colourcode.add(R.color.powderpink);
        colourcode.add(R.color.saffron);
        colourcode.add(R.color.salmonpink);
        colourcode.add(R.color.sapphire);
        colourcode.add(R.color.scarlet);
        colourcode.add(R.color.seasalt);
        colourcode.add(R.color.shamrock);
        colourcode.add(R.color.sienna);
        colourcode.add(R.color.smokyrose);
        colourcode.add(R.color.snow);
        colourcode.add(R.color.stormcloud);
        colourcode.add(R.color.sunflower);
        colourcode.add(R.color.tangerine);
        colourcode.add(R.color.tea);
        colourcode.add(R.color.topaz);
        colourcode.add(R.color.tornado);
        colourcode.add(R.color.turquoise);
        colourcode.add(R.color.valleygreen);
        colourcode.add(R.color.vanilla);
        colourcode.add(R.color.wasabi);
        colourcode.add(R.color.wildflower);
      //  colourcode.add(R.color.woodbrown);

        Random random=new Random();
        int number=random.nextInt(colourcode.size());
        return colourcode.get(number);

    }
}



