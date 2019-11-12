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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MealActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addBtn = findViewById(R.id.addMeal);
        final ListView meal_list_view = findViewById(R.id.meal_list_view);

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"database").allowMainThreadQueries().build();
        final List<Meal> meal_list = db.mealDao().getAllMeals();
        mealAdapter arrayAdapter = new mealAdapter(this , meal_list);
        meal_list_view.setAdapter(arrayAdapter);

        // Set OnClick on listView
        meal_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Meal thisMeal = (Meal) meal_list_view.getItemAtPosition(position);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MealActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.meal_view_dialog, null);
                mBuilder.setView(mView);

                ListView lv_ingredient = mView.findViewById(R.id.lv_ingredient);
                ListView lv_participant = mView.findViewById(R.id.lv_participant);

                // Set list view for ingredient
                String[] ingredient_name_list = new String[thisMeal.food_id.size()];
                for (int i = 0; i < thisMeal.food_id.size(); i ++) {
                    FoodItem ingre = db.foodItemDao().getFoodFromId(thisMeal.food_id.get(i)).get(0);
                    ingredient_name_list[i] = ingre.name + ", " + ingre.price.toString()+"€ bởi " + ingre.buyer;
                }
                ArrayAdapter<String> ingredient_adapter = new ArrayAdapter<String>(MealActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, ingredient_name_list);
                lv_ingredient.setAdapter(ingredient_adapter);
                // End of set list view for ingredient

                // Set list view for particiant
                String[] participant_name_list = new String[thisMeal.participant.size()];
                for (int i = 0; i < thisMeal.participant.size(); i ++) {
                    participant_name_list[i] = thisMeal.participant.get(i);
                }
                ArrayAdapter<String> participant_adapter = new ArrayAdapter<String>(MealActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, participant_name_list);
                lv_participant.setAdapter(participant_adapter);
                // End of set list view for participant

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        // End of SetOnClick on listView

        // SetLongClick Listener on list View
        meal_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Meal thisMeal = (Meal) meal_list_view.getItemAtPosition(position);
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(MealActivity.this);
                deleteAlert.setMessage("Bạn muốn hủy bữa ăn này ?");
                deleteAlert.setCancelable(true);
                deleteAlert.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int what) {
                        // Delete meal activities here
                        for (int i = 0; i < thisMeal.food_id.size(); i ++) {
                            FoodItem thisFI = db.foodItemDao().getFoodFromId(thisMeal.food_id.get(i)).get(0);
                            thisFI.unchoose();
                            db.foodItemDao().updateAll(thisFI);
                        }
                        for (int j = 0; j < thisMeal.participant.size(); j ++) {
                            Person thisPerson = db.personDao().getPersons(thisMeal.participant.get(j)).get(0);
                            thisPerson.addBalance(thisMeal.price/thisMeal.participant.size());
                            db.personDao().updateAll(thisPerson);
                        }
                        db.mealDao().deleteById(thisMeal.id);

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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(MealActivity.this);
                final View aView = getLayoutInflater().inflate(R.layout.layout_add_meal_dialog, null);
                aBuilder.setView(aView);

                final EditText meal_name = aView.findViewById(R.id.meal_name);
                final TextView tv_food_list = aView.findViewById(R.id.ingredient);
                final TextView tv_person_list = aView.findViewById(R.id.person_eat_list);

                ImageView addFoodBtn = aView.findViewById(R.id.addFoodBtn);
                ImageView addPersonBtn = aView.findViewById(R.id.addPersonBtn);

                // Add ingredients
                final ArrayList<FoodItem> chosen_food = new ArrayList<>();
                final List<FoodItem> avail_food_list = db.foodItemDao().getAllUnchosenFoods();
                final String[] avail_food_name_list = new String[avail_food_list.size()];
                for (int i = 0; i < avail_food_list.size(); i ++) {
                    avail_food_name_list[i] = avail_food_list.get(i).name + ", " + avail_food_list.get(i).price.toString() + "€ bởi " + avail_food_list.get(i).buyer;
                }
                final boolean[] checkBoxFood = new boolean[avail_food_list.size()];
                final ArrayList<Integer> chosen_food_pos = new ArrayList<>();

                addFoodBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder afBuilder = new AlertDialog.Builder(aView.getContext());
                        afBuilder.setTitle("Nguyên liệu có sẵn trong kho");
                        afBuilder.setMultiChoiceItems(avail_food_name_list, checkBoxFood, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                                if (isChecked) {
                                    if (!chosen_food_pos.contains(position)) {
                                        chosen_food_pos.add(position);
                                    }
                                }
                                else if (chosen_food_pos.contains(Integer.valueOf(position))) {
                                    chosen_food_pos.remove(Integer.valueOf(position));
                                }
                            }
                        });

                        afBuilder.setCancelable(false);
                        afBuilder.setPositiveButton("Nhập", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                String item = "";
                                chosen_food.clear();
                                for (int i = 0; i < chosen_food_pos.size(); i++) {
                                    item = item + avail_food_name_list[chosen_food_pos.get(i)].split(",")[0];
                                    if (i != chosen_food_pos.size() - 1) {
                                        item = item + ", ";
                                    }
                                    if (!chosen_food.contains(avail_food_list.get(chosen_food_pos.get(i)))) {
                                        chosen_food.add(avail_food_list.get(chosen_food_pos.get(i)));
                                    }
                                }
                                tv_food_list.setText(item);
                            }
                        });
                        afBuilder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        afBuilder.setNeutralButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                for (int i = 0; i < checkBoxFood.length; i ++) {
                                    checkBoxFood[i] = false;
                                    chosen_food_pos.clear();
                                    chosen_food.clear();
                                    tv_food_list.setText("");
                                }
                            }
                        });
                        AlertDialog afDialog = afBuilder.create();
                        afDialog.show();
                    }
                });
                // End of adding ingredients

                // Add Persons
                final ArrayList<Person> chosen_person_list = new ArrayList<>();
                final List<Person> person_list = db.personDao().getAllPersons();
                final String[] person_name_list = new String[person_list.size()];
                for (int i = 0; i < person_list.size(); i ++) {
                    person_name_list[i] = person_list.get(i).name;
                }
                final boolean[] checkBoxPerson = new boolean[person_list.size()];
                final ArrayList<Integer> chosen_person_pos = new ArrayList<>();

                addPersonBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder apBuilder = new AlertDialog.Builder(aView.getContext());
                        apBuilder.setTitle("Nhập danh sách người ăn");
                        apBuilder.setMultiChoiceItems(person_name_list, checkBoxPerson, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                                if (isChecked) {
                                    if (!chosen_person_pos.contains(position)) {
                                        chosen_person_pos.add(position);
                                    }
                                }
                                else if (chosen_person_pos.contains(Integer.valueOf(position))) {
                                    chosen_person_pos.remove(Integer.valueOf(position));
                                }
                            }
                        });

                        apBuilder.setCancelable(false);
                        apBuilder.setPositiveButton("Nhập", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                String item = "";
                                chosen_person_list.clear();
                                for (int i = 0; i < chosen_person_pos.size(); i++) {
                                    item = item + person_name_list[chosen_person_pos.get(i)];
                                    if (i != chosen_person_pos.size() - 1) {
                                        item = item + ", ";
                                    }
                                    if (!chosen_person_list.contains(person_list.get(chosen_person_pos.get(i)))) {
                                        chosen_person_list.add(person_list.get(chosen_person_pos.get(i)));
                                    }
                                }
                                tv_person_list.setText(item);
                            }
                        });
                        apBuilder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        apBuilder.setNeutralButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                for (int i = 0; i < checkBoxPerson.length; i ++) {
                                    checkBoxPerson[i] = false;
                                    chosen_person_pos.clear();
                                    chosen_person_list.clear();
                                    tv_person_list.setText("");
                                }
                            }
                        });
                        AlertDialog apDialog = apBuilder.create();
                        apDialog.show();
                    }
                });
                // End of adding Persons


                Button validate = aView.findViewById(R.id.addMealValidate);

                final AlertDialog dialog = aBuilder.create();
                validate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!meal_name.getText().toString().isEmpty() && !tv_food_list.getText().toString().isEmpty() && !tv_person_list.getText().toString().isEmpty()) {
                            Toast.makeText(MealActivity.this, "Món đồ thêm thành công !", Toast.LENGTH_SHORT).show();
                            db.mealDao().insertAll(new  Meal(meal_name.getText().toString(), chosen_food, chosen_person_list));
                            Double price = 0.;
                            for (FoodItem food : chosen_food) {
                                food.choose();
                                db.foodItemDao().updateAll(food);
                                price += food.price;
                            }

                            for (Person person : chosen_person_list) {
                                person.addBalance(-price/chosen_person_list.size());
                                db.personDao().updateAll(person);
                            }

                            finish();
                            startActivity(getIntent());
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MealActivity.this, "Xin điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(MealActivity.this, MainActivity.class));
            return true;

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(MealActivity.this, PersonActivity.class));
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
