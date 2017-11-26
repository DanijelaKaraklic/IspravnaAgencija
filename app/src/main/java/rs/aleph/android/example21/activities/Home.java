package rs.aleph.android.example21.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rs.aleph.android.example21.R;
import rs.aleph.android.example21.db.DatabaseHelper;
import rs.aleph.android.example21.db.model.RealEstate;

/**
 * Created by KaraklicDM on 26.11.2017.
 */

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "PERMISSIONS";

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;
    private CharSequence title;
    private CharSequence drawerTitle;
    private ActionBarDrawerToggle toggle;
    DrawerLayout.DrawerListener listener;
    public static String REAL_ESTATE = "selectedItemId";

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<RealEstate> listRs1 = new ArrayList<>();
        try {
            listRs1 = getDatabaseHelper().getRealEstateDao().queryForAll();
            if (listRs1 == null){

                insertToExternalStorage("zamak_duga.jpg");
                insertToExternalStorage("zamak.jpg");
                insertToExternalStorage("zamak_prolece.jpg");
                insertToExternalStorage("zimska_kuca.jpg");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        final ListView listView = (ListView)findViewById(R.id.real_estates);

        try {

            List<RealEstate> listRs = getDatabaseHelper().getRealEstateDao().queryForAll();
            ListAdapter adapter1 = new ArrayAdapter<RealEstate>(Home.this,R.layout.list_item,listRs);
            listView.setAdapter(adapter1);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Home.this,SecondActivity.class);
                    RealEstate r = (RealEstate)listView.getItemAtPosition(position);
                    long selectedItemId = r.getmId();
                    intent.putExtra(REAL_ESTATE,selectedItemId);
                    startActivity(intent);
                }
            });



        } catch (SQLException e) {
            e.printStackTrace();
        }




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

    private void insertToExternalStorage(String nameImage) throws IOException {
        InputStream is = null;
        is = getAssets().open(nameImage);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        MediaStore.Images.Media.insertImage(this.getContentResolver(),bitmap,nameImage,"jpg");
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


    /**
     * Od verzije Marshmallow Android uvodi pojam dinamickih permisija
     * Sto korisnicima olaksva rad, a programerima uvodi dodadan posao.
     * Cela ideja ja u tome, da se permisije ili prava da aplikcija
     * nesto uradi, ne zahtevaju prilikom instalacije, nego prilikom
     * prve upotrebe te funkcionalnosti. To za posledicu ima da mi
     * svaki put moramo da proverimo da li je odredjeno pravo dopustneo
     * ili ne. Iako nije da ponovo trazimo da korisnik dopusti, u protivnom
     * tu funkcionalnost necemo obaviti uopste.
     * */
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    /**
     *
     * Ako odredjena funkcija nije dopustena, saljemo zahtev android
     * sistemu da zahteva odredjene permisije. Korisniku seprikazuje
     * diloag u kom on zeli ili ne da dopusti odedjene permisije.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
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