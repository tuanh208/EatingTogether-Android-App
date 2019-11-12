package com.tyy.eatingtogether;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addBtn = findViewById(R.id.addBtn);
        final ListView food_list_view = findViewById(R.id.food_list_view);

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"database").allowMainThreadQueries().build();
        List<FoodItem> unchosen_food_list = db.foodItemDao().getAllUnchosenFoods();
        foodAdapter arrayAdapter = new foodAdapter(this , unchosen_food_list);
        food_list_view.setAdapter(arrayAdapter);

        // SetLongClick Listener on list View
        food_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final FoodItem thisFI = (FoodItem) food_list_view.getItemAtPosition(position);
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(MainActivity.this);
                deleteAlert.setMessage("Bạn muốn xóa nguyên liệu này ?");
                deleteAlert.setCancelable(true);
                deleteAlert.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int what) {
                        // Delete meal activities here
                        Person buyer = db.personDao().getPersons(thisFI.buyer).get(0);
                        buyer.addBalance(-thisFI.price);
                        db.foodItemDao().delete(thisFI);

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
        });
        // End of SetLongClick Listener on list View

        //db.personDao().insertAll(new Person("Tú Anh"), new Person("Phước"), new Person("Quang"), new Person("Tú Lớn"));
        final List<Person> person_list = db.personDao().getAllPersons();
        final List<String> person_name_list = new ArrayList<>();
        for (Person per : person_list) {
            person_name_list.add(per.name);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(MainActivity.this);
                View aView = getLayoutInflater().inflate(R.layout.layout_add_item_dialog, null);

                final EditText food_name = aView.findViewById(R.id.food_name);
                final EditText food_price = aView.findViewById(R.id.food_price);

                final AutoCompleteTextView buyer_text_view = aView.findViewById(R.id.list_buyer);
                ArrayAdapter<String> person_adapter = new ArrayAdapter<>(aView.getContext(), android.R.layout.simple_dropdown_item_1line, person_name_list);
                buyer_text_view.setAdapter(person_adapter);
                //buyer_text_view.setDropDownHeight(LinearLayout.LayoutParams.MATCH_PARENT);

                ImageView expandBtn = aView.findViewById(R.id.expandBtn);
                expandBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buyer_text_view.showDropDown();
                        }
                    }
                );
                Button validate = aView.findViewById(R.id.addValidate);

                aBuilder.setView(aView);
                final AlertDialog dialog = aBuilder.create();

                validate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!food_name.getText().toString().isEmpty() && !food_price.getText().toString().isEmpty() && !buyer_text_view.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Món đồ thêm thành công !", Toast.LENGTH_SHORT).show();
                            db.foodItemDao().insertAll(new FoodItem(food_name.getText().toString(), Double.parseDouble(food_price.getText().toString()), buyer_text_view.getText().toString()));
                            String addedName = buyer_text_view.getText().toString();
                            if (!person_name_list.contains(addedName)) {
                                Person addedPerson = new Person(addedName);
                                addedPerson.addBalance(Double.parseDouble(food_price.getText().toString()));
                                db.personDao().insertAll(addedPerson);
                            }
                            else {
                                Person addedPerson = db.personDao().getPersons(addedName).get(0);
                                addedPerson.addBalance(Double.parseDouble(food_price.getText().toString()));
                                db.personDao().updateAll(addedPerson);
                            }
                            finish();
                            startActivity(getIntent());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Xin điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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

        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MainActivity.this, MealActivity.class));
            return true;

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(MainActivity.this, PersonActivity.class));
            return true;

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
