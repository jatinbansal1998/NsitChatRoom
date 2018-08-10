package com.example.jatin_bansal_269co15.nsitchatroom;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String user_name;
    private Button add_room;
    private EditText room_name;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private String name;
    private FirebaseAuth mAuth;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference delete;
    private FloatingActionButton fab;
    private String caps_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_name = getIntent().getExtras().get("user_name").toString();
//        mAuth = FirebaseAuth.getInstance();
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        add_room = (Button) findViewById(R.id.btn_add_room);
        room_name = (EditText) findViewById(R.id.room_name_edittext);
        listView = (ListView) findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_rooms) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                text.setAllCaps(true);
                return view;
            }
        }
        ;

        listView.setAdapter(arrayAdapter);

        //request_user_name();

        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();

                Map<String, Object> map = new HashMap<String, Object>();
                caps_string = room_name.getText().toString();
                map.put(caps_string.toUpperCase(), "");
                root.updateChildren(map);
                room_name.setText("");
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(), Chat_Room.class);
                intent.putExtra("room_name", ((TextView) view).getText().toString());
                intent.putExtra("user_name", user_name);
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), About_me.class);
                startActivity(intent);
            }
        });

        final AlertDialog.Builder delete_room = new AlertDialog.Builder(this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {


                delete_room.setTitle("Delete Room");
                delete_room.setMessage("Are you sure you want to delete " + ((TextView) view).getText().toString() + "?");
                delete_room.setIcon(android.R.drawable.ic_dialog_alert);
                delete_room.setCancelable(false);
                delete_room.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        delete = FirebaseDatabase.getInstance().getReference().child(((TextView) view).getText().toString());
                        delete.removeValue();


                    }
                });


                delete_room.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });


                delete_room.show();


                return true;
            }
        });

    }

    private void request_user_name() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name:");
        builder.setCancelable(false);


        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (input_field.getText().toString().equals(null) || input_field.getText().toString().equals(""))
                    request_user_name();
                name = input_field.getText().toString();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();
    }


}

