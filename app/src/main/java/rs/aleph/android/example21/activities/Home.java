package rs.aleph.android.example21.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

import rs.aleph.android.example21.R;
import rs.aleph.android.example21.db.DatabaseHelper;
import rs.aleph.android.example21.db.model.RealEstate;

/**
 * Created by KaraklicDM on 26.11.2017.
 */

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;
    private CharSequence title;
    private CharSequence drawerTitle;
    private ActionBarDrawerToggle toggle;
    DrawerLayout.DrawerListener listener;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ListView listView = (ListView)findViewById(R.id.real_estates);

        try {
             /*   RealEstate rs = new RealEstate();
            rs.setmId(0);
            rs.setmName("nesto");
            rs.setmDescription("nesto");
            rs.setmAdress("nesto");
            rs.setmImage("nesto");
            rs.setmTel(11111);
            rs.setmPrice(2.2);
            rs.setmRoom(2);
            rs.setmQuadrature(12.5);
            List<RealEstate> listRs = new ArrayList<>();
            listRs.add(rs);*/

            List<RealEstate> listRs = getDatabaseHelper().getRealEstateDao().queryForAll();
            ListAdapter adapter1 = new ArrayAdapter<RealEstate>(Home.this,R.layout.list_item,listRs);
            listView.setAdapter(adapter1);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Home.this,SecondActivity.class);
                    RealEstate r = (RealEstate)listView.getItemAtPosition(position);
                    long selectedItemId = r.getmId();
                    intent.putExtra("selectedItemId",selectedItemId);
                    startActivity(intent);
                }
            });



        } catch (SQLException e) {
            e.printStackTrace();
        }


      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        title = drawerTitle = getTitle();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);





    }


    private void refresh(){
        ListView listview = (ListView) findViewById(R.id.real_estates);

        if (listview != null){
            ArrayAdapter<RealEstate> adapter = (ArrayAdapter<RealEstate>) listview.getAdapter();

            if(adapter!= null)
            {
                try {
                    adapter.clear();
                    List<RealEstate> list = getDatabaseHelper().getRealEstateDao().queryForAll();

                    adapter.addAll(list);

                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
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
        getMenuInflater().inflate(R.menu.home, menu);
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
        }else if (id == R.id.action_add){
            final Dialog dialog = new Dialog(Home.this);

            dialog.setContentView(R.layout.dialog_layout);

            dialog.setTitle("Insert an actor");

            Button ok = (Button) dialog.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    final EditText editName = (EditText) dialog.findViewById(R.id.re_name);
                    final EditText editDescription = (EditText) dialog.findViewById(R.id.re_description);
                    final EditText editImage = (EditText) dialog.findViewById(R.id.re_image);
                    final EditText editAdress = (EditText) dialog.findViewById(R.id.re_adress);
                    final EditText editTel = (EditText) dialog.findViewById(R.id.re_telephone);
                    final EditText editQuad = (EditText) dialog.findViewById(R.id.re_quad);
                    final EditText editRoom = (EditText) dialog.findViewById(R.id.re_room);
                    final EditText editPrice = (EditText) dialog.findViewById(R.id.re_price);
                    RealEstate realEstate = new RealEstate();

                    if (editName.getText().toString().isEmpty()){
                        Toast.makeText(Home.this, "Name can't be empty.",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editDescription.getText().toString().equals("")){
                        Toast.makeText(Home.this, "Description can't be empty.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (editImage.getText().toString().equals("")){
                        Toast.makeText(Home.this, "Image name can't be empty.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (editAdress.getText().toString().equals("")){
                        Toast.makeText(Home.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int tel =0;
                    try {
                        tel = Integer.parseInt(editTel.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(Home.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();
                    }
                    int room =0;
                    try {
                        room = Integer.parseInt(editRoom.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(Home.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();
                    }
                    double quad =0.0;
                    try {
                        quad = Double.parseDouble(editQuad.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(Home.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();
                    }
                    double price =0.0;
                    try {
                        price = Integer.parseInt(editTel.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(Home.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();
                    }


                    realEstate.setmName(editName.getText().toString());
                    realEstate.setmDescription(editDescription.getText().toString());
                    realEstate.setmImage(editImage.getText().toString());
                    realEstate.setmAdress(editAdress.getText().toString());
                    realEstate.setmTel(Integer.parseInt(editTel.getText().toString()));
                    realEstate.setmQuadrature(Double.parseDouble(editQuad.getText().toString()));
                    realEstate.setmRoom(Integer.parseInt(editRoom.getText().toString()));
                    realEstate.setmPrice(Double.parseDouble(editPrice.getText().toString()));

                    try {
                        getDatabaseHelper().getRealEstateDao().create(realEstate);
                        refresh();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    dialog.dismiss();


                }
            });

            Button cancel = (Button) dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });

            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //here is the main place where we need to work on.
        int id=item.getItemId();
        switch (id){

            case R.id.nav_home:
                Intent h= new Intent(Home.this,Home.class);
                startActivity(h);
                break;
            case R.id.nav_settings:
                Intent i = new Intent(Home.this,SecondActivity.class);
                startActivity(i);
            //pozivaju se aktivnosti koje su u navigation draweru
           /* case R.id.nav_import:
                Intent i= new Intent(Home.this,Import.class);
                startActivity(i);
                break;
            case R.id.nav_gallery:
                Intent g= new Intent(Home.this,Gallery.class);
                startActivity(g);
                break;
            case R.id.nav_slideshow:
                Intent s= new Intent(Home.this,Slideshow.class);
                startActivity(s);
            case R.id.nav_tools:
                Intent t= new Intent(Home.this,Tools.class);
                startActivity(t);
                break;*/
            // this is done, now let us go and intialise the home page.
            // after this lets start copying the above.
            // FOLLOW MEEEEE>>>
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}