package rs.aleph.android.example21.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import rs.aleph.android.example21.R;
import rs.aleph.android.example21.db.DatabaseHelper;
import rs.aleph.android.example21.db.model.RealEstate;

/**
 * Created by KaraklicDM on 26.11.2017.
 */

public class SecondActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "PERMISSIONS";

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar = null;

    private EditText editName;
    private EditText editDescription;
    private Button btnImage;
    private ImageView ivImage;
    private EditText editAdress;
    private EditText editTel;
    private EditText editQuad;
    private EditText editRoom;
    private EditText editPrice;

    private boolean toast;
    private boolean notification;
    private SharedPreferences sharedPreferences;


    private DatabaseHelper databaseHelper;
    private RealEstate realEstate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //prikaz u second activity svih podataka
        long id = getIntent().getExtras().getLong(Home.REAL_ESTATE);
        try {
            realEstate = getDatabaseHelper().getRealEstateDao().queryForId((int) id);
            editName = (EditText) findViewById(R.id.re_name);
            editDescription = (EditText) findViewById(R.id.re_description);
            ivImage = (ImageView) findViewById(R.id.re_image);
            editAdress = (EditText) findViewById(R.id.re_adress);
            editTel = (EditText) findViewById(R.id.re_telephone);
            editQuad = (EditText) findViewById(R.id.re_quad);
            editRoom = (EditText) findViewById(R.id.re_room);
            editPrice = (EditText) findViewById(R.id.re_price);

            editName.setText(realEstate.getmName());
            editDescription.setText(realEstate.getmDescription());
            editAdress.setText(realEstate.getmAdress());
            editTel.setText(String.valueOf(realEstate.getmTel()));
            editTel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent call = new Intent(Intent.ACTION_CALL);
                    call.setData(Uri.parse("tel:" + String.valueOf(realEstate.getmTel())));

                if (isStoragePermissionGranted()){
                    startActivity(call);
                }

                }
            });


            double q = realEstate.getmQuadrature();
            editQuad.setText(String.valueOf(q));
            editRoom.setText(String.valueOf(realEstate.getmRoom()));
            editPrice.setText(String.valueOf(realEstate.getmPrice()));





            Button button = (Button)findViewById(R.id.btn_schedule);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toast = sharedPreferences.getBoolean(Home.TOAST,false);
                    notification = sharedPreferences.getBoolean(Home.NOTIFICATION,false);

                    if (notification){
                        showNotification(getString(R.string.notif_schedule),getString(R.string.notif_schedule_title));
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    private void showNotification(String title,String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_action_real_estates);
        builder.setSmallIcon(R.drawable.ic_action_real_estates);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setLargeIcon(bitmap);

        // Shows notification with the notification manager (notification ID is used to update the notification later on)
        //umesto this aktivnost
        NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void showMessage(String message,String title){
        toast = sharedPreferences.getBoolean(Home.TOAST,false);
        notification = sharedPreferences.getBoolean(Home.NOTIFICATION,false);
        if (toast){
            Toast.makeText(this, message,Toast.LENGTH_SHORT).show();
        }
        if (notification){
            showNotification(message,title);
        }

    }

    private void refresh(){
        //realEstate = getDatabaseHelper().getRealEstateDao().queryForId((int)id);
        editName = (EditText) findViewById(R.id.re_name);
        editDescription = (EditText) findViewById(R.id.re_description);
        ivImage= (ImageView) findViewById(R.id.re_image);
        editAdress = (EditText) findViewById(R.id.re_adress);
        editTel = (EditText) findViewById(R.id.re_telephone);
        editQuad = (EditText) findViewById(R.id.re_quad);
        editRoom = (EditText) findViewById(R.id.re_room);
        editPrice = (EditText) findViewById(R.id.re_price);

        editName.setText(realEstate.getmName());
        editDescription.setText(realEstate.getmDescription());
        editAdress.setText(realEstate.getmAdress());
        editTel.setText(String.valueOf(realEstate.getmTel()));


        double q = realEstate.getmQuadrature();
        editQuad.setText(String.valueOf(q));
        editRoom.setText(String.valueOf(realEstate.getmRoom()));
        editPrice.setText(String.valueOf(realEstate.getmPrice()));

}



    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
        }
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
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_edit:
                final Dialog dialog = new Dialog(SecondActivity.this);

                dialog.setContentView(R.layout.dialog_layout);

                dialog.setTitle("Update an actor");

                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        editName = (EditText) dialog.findViewById(R.id.re_name);
                        editDescription = (EditText) dialog.findViewById(R.id.re_description);
                        editAdress = (EditText) dialog.findViewById(R.id.re_adress);
                        editTel = (EditText) dialog.findViewById(R.id.re_telephone);
                        editQuad = (EditText) dialog.findViewById(R.id.re_quad);
                        editRoom = (EditText) dialog.findViewById(R.id.re_room);
                        editPrice = (EditText) dialog.findViewById(R.id.re_price);


                        if (!editName.getText().toString().isEmpty()){
                            realEstate.setmName(editName.getText().toString());
                            //Toast.makeText(SecondActivity.this, "Name can't be empty.",Toast.LENGTH_SHORT).show();

                        }

                        if (!editDescription.getText().toString().equals("")){
                            realEstate.setmDescription(editDescription.getText().toString());
                            //Toast.makeText(SecondActivity.this, "Description can't be empty.",Toast.LENGTH_SHORT).show();

                        }


                        if (!editAdress.getText().toString().equals("")){
                            realEstate.setmAdress(editAdress.getText().toString());
                           // Toast.makeText(SecondActivity.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();

                        }

                        int tel =0;
                        try {
                            tel = Integer.parseInt(editTel.getText().toString());
                            realEstate.setmTel(Integer.parseInt(editTel.getText().toString()));

                        } catch (NumberFormatException e) {
                            Toast.makeText(SecondActivity.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();
                        }
                        int room =0;
                        try {
                            if (!editRoom.getText().toString().isEmpty()){
                                room = Integer.parseInt(editRoom.getText().toString());
                                realEstate.setmRoom(Integer.parseInt(editRoom.getText().toString()));

                            }


                        } catch (NumberFormatException e) {
                            Toast.makeText(SecondActivity.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();
                        }
                        double quad =0.0;
                        try {
                            if (!editQuad.getText().toString().isEmpty()){
                                quad = Double.parseDouble(editQuad.getText().toString());
                                realEstate.setmQuadrature(Double.parseDouble(editQuad.getText().toString()));

                            }

                        } catch (NumberFormatException e) {
                            Toast.makeText(SecondActivity.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();
                        }
                        double price =0.0;
                        try {
                            if (!editPrice.getText().toString().isEmpty())
                            price = Integer.parseInt(editTel.getText().toString());
                            realEstate.setmPrice(Double.parseDouble(editPrice.getText().toString()));
                        } catch (NumberFormatException e) {
                            Toast.makeText(SecondActivity.this, "Adress can't be empty.",Toast.LENGTH_SHORT).show();
                        }



                        try {
                            getDatabaseHelper().getRealEstateDao().update(realEstate);
                            showMessage(getString(R.string.sec_mess_update),getString(R.string.sec_mess_up_title));
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
                break;
            case R.id.action_delete:
                final Dialog deleteDia = new Dialog(SecondActivity.this);
                deleteDia.setContentView(R.layout.dialog_delete);

                deleteDia.setTitle("Delete an actor");

                Button ok1 =(Button)deleteDia.findViewById(R.id.ok);
                ok1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            getDatabaseHelper().getRealEstateDao().delete(realEstate);
                            showMessage(getString(R.string.sec_mess_delete),getString(R.string.sec_mess_del_title));
                            deleteDia.dismiss();
                            finish();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Button cancel1 = (Button)deleteDia.findViewById(R.id.cancel);
                cancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDia.dismiss();
                    }
                });


                deleteDia.show();

                break;

        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //here is the main place where we need to work on.
        int id=item.getItemId();
        switch (id){

            case R.id.nav_home:
                Intent h= new Intent(SecondActivity.this,Home.class);
                startActivity(h);
                break;
            case R.id.nav_settings:
                Intent i = new Intent(SecondActivity.this,SettingsActivity.class);
                break;


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