package com.tyy.eatingtogether;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class PersonActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addBtn = findViewById(R.id.addPerson);
        final ListView person_list_view = findViewById(R.id.person_list_view);

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"database").allowMainThreadQueries().build();
        final List<Person> person_list = db.personDao().getAllPersons();
        personAdapter arrayAdapter = new personAdapter(this , person_list);
        person_list_view.setAdapter(arrayAdapter);

        // SetLongClick Listener on list View
        person_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Person thisPerson = (Person) person_list_view.getItemAtPosition(position);
                if (thisPerson.balance != 0.0) {
                    Toast.makeText(PersonActivity.this, "Chỉ xóa được người dùng có số dư bằng 0 !", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    AlertDialog.Builder deleteAlert = new AlertDialog.Builder(PersonActivity.this);
                    deleteAlert.setMessage("Bạn muốn xóa người dúng này ?");
                    deleteAlert.setCancelable(true);
                    deleteAlert.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int what) {
                            // Delete meal activities here
                            db.personDao().deleteByName(thisPerson.name);

                            finish();
                            startActivity(getIntent());
                        }
                    });
                    deleteAlert.setNegativeButton("Nhầm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int what) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog deleteDialog = deleteAlert.create();
                    deleteDialog.show();
                    return true;
                }
            }
        });
        // End of SetLongClick Listener on list View


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(PersonActivity.this);
                View aView = getLayoutInflater().inflate(R.layout.layout_add_person_dialog, null);

                final EditText person_name = aView.findViewById(R.id.person_name);
                final EditText person_balance = aView.findViewById(R.id.person_balance);

                Button validate = aView.findViewById(R.id.addPerson);

                aBuilder.setView(aView);
                final AlertDialog dialog = aBuilder.create();

                validate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!person_name.getText().toString().isEmpty()) {
                            Toast.makeText(PersonActivity.this, "Người ăn thêm thành công !", Toast.LENGTH_SHORT).show();
                            Person addedPerson = new Person(person_name.getText().toString());
                            if (!person_balance.getText().toString().isEmpty()) {
                                addedPerson.addBalance(Double.parseDouble(person_balance.getText().toString()));
                            }
                            db.personDao().insertAll(addedPerson);
                            finish();
                            startActivity(getIntent());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(PersonActivity.this, "Xin điền tên người ăn", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            // getLayoutInflater().inflate(R.layout.app_bar_meal, null);
            startActivity(new Intent(PersonActivity.this, MainActivity.class));
            return true;

        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(PersonActivity.this, MealActivity.class));
            return true;

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"database").allowMainThreadQueries().build();
            db.clearAllTables();
            finish();
            startActivity(getIntent());
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
