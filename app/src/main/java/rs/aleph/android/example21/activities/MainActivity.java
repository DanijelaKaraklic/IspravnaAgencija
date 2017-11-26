package rs.aleph.android.example21.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.RelativeLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rs.aleph.android.example21.R;
import rs.aleph.android.example21.adapters.DrawerListAdapter;
import rs.aleph.android.example21.db.DatabaseHelper;
import rs.aleph.android.example21.db.model.RealEstate;
import rs.aleph.android.example21.model.NavigationItem;

public class MainActivity extends AppCompatActivity{
    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "PERMISSIONS";
    /* The click listner for ListView in the navigation drawer */
    /*
    *Ova klasa predstavlja reakciju na klik neke od stavki iz navigation drawer-a
    *Kljucni element je 'int position' argument koji nam kaze koji tacno element
    *je izabran. To nam je dovoljno da odredimo koju akciju zelimo da pozovemo.
    */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           selectItemFromDrawer(position);
        }
    }


    /**Drawer potrebni elementi*/
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;
    private CharSequence drawerTitle;
    private CharSequence title;

    private ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();




    //private AlertDialog dialog;
    //za rad sa bazom
    private DatabaseHelper databaseHelper;





    private RealEstate realEstate;
    private static int NOTIFICATION_ID = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);



        // Draws navigation items
        //U navigation drawer postavimo koje to elemente zelimo da imamo. Ikonicu, naziv i krratak opis
        navigationItems.add(new NavigationItem(getString(R.string.drawer_home), getString(R.string.navigation_re_long), R.drawable.ic_action_real_estates));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_settings),getString(R.string.drawer_Settings_long), R.drawable.ic_action_settings));
        //navigationItems.add(new NavigationItem(getString(R.string.drawer_about), getString(R.string.drawer_about_long), R.drawable.ic_action_about));


        title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.navList);

        // Populate the Navigtion Drawer with options
        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);

        //Prethodno definisanu listu koja sadrzi iokne,naslov i opis svake stavke postavimo u navigation drawer
        DrawerListAdapter adapter = new DrawerListAdapter(this, navigationItems);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer, GravityCompat.START);

        //Zelimo da ragujemo na izbog stavki unutar navigation drawer-a.
        //Prethodno smo definisali klasu koja ce na osnovu pozicije ragovati
        //Ovde povezujemo izbor stavke i kako ragovati
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Nakon toga potrebno je aplikaciji dopustiti da koristi toolbar
        setSupportActionBar(toolbar);

        //Zbog mera predostroznosti.Proveriti da li je rad sa toolbar-om moguc.
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //Ako jeste dopustimo klik na tu ikonu da bi mogli da otvaramo/zatvaramo navigation drawer
            actionBar.setDisplayHomeAsUpEnabled(true);
            //postavljamo ikonicu untar toolbar-a
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
            //i prikazujemo ikonu
            actionBar.show();
        }


         /*
        *Zelimo da postignemo da se navigation drawer otvara/zatvara uspesno.
        *Potrebno je da damo kontekst(aktivnost) u kome se prikazuje 'this'
        *toolbar na kojem ce se prikazivati ikona kao i menjati naslov 'toolbar'
        *i dva teksta sta prikazivati kada je navigation drawer otvoren/zatvoren.
        */
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            //kada se navigation drawer ne prikazuje zelimo da reagujemo na taj dogadjaj
            public void onDrawerClosed(View view) {
                //postavimo naslov u toolbar
                getSupportActionBar().setTitle(title);
                //i obrisemo sadrzaj toolbar-a.
                //Ako svaka nova aktivnost ili fragment ima drugaciji sadrzaj toolbar-a
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            //reagujemo kada se navigation drawer otvori
            public void onDrawerOpened(View drawerView) {
                //postavimo naslov u toolbar
                getSupportActionBar().setTitle(drawerTitle);
                //i obrisemo sadrzaj toolbar-a.
                //Ako svaka nova aktivnost ili fragment ima drugaciji sadrzaj toolbar-a
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


        final ListView listView = (ListView)findViewById(R.id.real_estates);

        try {
            List<RealEstate> listRs = getDatabaseHelper().getRealEstateDao().queryForAll();
            ListAdapter adapter1 = new ArrayAdapter<RealEstate>(MainActivity.this,R.layout.list_item,listRs);
            listView.setAdapter(adapter1);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                    RealEstate r = (RealEstate)listView.getItemAtPosition(position);
                    long selectedItemId = r.getmId();
                    intent.putExtra("selectedItemId",selectedItemId);
                    startActivity(intent);
                }
            });



        } catch (SQLException e) {
            e.printStackTrace();
        }





        //showing notification
       /* NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_stat_buy);
        builder.setSmallIcon(R.drawable.ic_stat_buy);
        builder.setContentTitle("Title");
        builder.setContentText("Content title");
        builder.setLargeIcon(bitmap);

        // Shows notification with the notification manager (notification ID is used to update the notification later on)
        //umesto this aktivnost
        NotificationManager manager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());



        //showing AboutDialog
        if (dialog == null){
            dialog = new AboutDialog(MainActivity.this).prepareDialog();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        dialog.show();

        //Pristupanje deljenim podesavanjima,primaju samo primitivne tipove
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //default vrednost iz liste "1"
        String s = sharedPreferences.getString("@string/pref_sync","1");
        boolean b = sharedPreferences.getBoolean("@string/pref_sync",false);*/




        //samples of views
      /*  EditText name = (EditText) findViewById(R.id.name);
        name.setText(product.getmName());

        EditText description = (EditText) findViewById(R.id.description);
        description.setText(product.getDescription());

        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
        ratingBar.setRating(product.getRating());

        ImageView imageView = (ImageView) findViewById(R.id.image);
        InputStream is = null;
        try {
            is = getAssets().open(product.getImage());
            Drawable drawable = Drawable.createFromStream(is, null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */


        //Showing a dialog

/*        final Dialog dialog = new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.dialog_layout);

        dialog.setTitle("Dialog");

        Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final EditText editName = (EditText) dialog.findViewById(R.id.product_name);
                final EditText editDescription = (EditText) dialog.findViewById(R.id.product_description);
                final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.rb_product);
                Button btnImage = (Button) dialog.findViewById(R.id.btn_browse_image);
                btnImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                        *//*final Spinner image = (Spinner) dialog.findViewById(R.id.sp_dialog_images);

                        List<String> images  = new ArrayList<String>();
                        images.add("apples.jpg");
                        images.add("bananas.jpg");
                        images.add("oranges.jpg");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, images);
                        image.setAdapter(adapter);
                        image.setSelection(1);*//*

                Spinner category = (Spinner)dialog.findViewById(R.id.sp_dialog_categories);


                //ArrayAdapter<Category> adapter1 = new ArrayAdapter<Category>(MainActivity.this,android.R.layout.simple_spinner_item,categories);
                //category.setAdapter(adapter1);
                //category.setSelection(0);


                dialog.dismiss();


            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this, R.string.dialog_message_no,Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();*/











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

    /**
     * Da bi dobili pristup Galeriji slika na uredjaju
     * moramo preko URI-ja pristupiti delu baze gde su smestene
     * slike uredjaja. Njima mozemo pristupiti koristeci sistemski
     * ContentProvider i koristeci URI images/* putanju
     *
     * Posto biramo sliku potrebno je da pozovemo aktivnost koja icekuje rezultat
     * Kada dobijemo rezultat nazad prikazemo sliku i dobijemo njenu tacnu putanju
     * */
    private void selectPicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    /**
     * Sismtemska metoda koja se automatksi poziva ako se
     * aktivnost startuje u startActivityForResult rezimu
     *
     * Ako je ti slucaj i ako je sve proslo ok, mozemo da izvucemo
     * sadrzaj i to da prikazemo. Rezultat NIJE sliak nego URI do te slike.
     * Na osnovu toga mozemo dobiti tacnu putnaju do slike ali i samu sliku
     * */
    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                //String selectedImagePath = selectedImageUri.getPath();

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.image_dialog);
                dialog.setTitle("Image dialog");

                ImageView image = (ImageView) dialog.findViewById(R.id.image);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    image.setImageBitmap(bitmap);
                    Toast.makeText(this, selectedImageUri.getPath(),Toast.LENGTH_SHORT).show();

                    dialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/




    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /*
    *Ova metoda reaguje na izbor neke od stavki iz navigation drawer-a
    *Na osnovu pozicije iz liste navigation drawer-a odredimo sta tacno
    *zelimo da odradimo.
    */
    private void selectItemFromDrawer(int position) {
        if (position == 0){

        } else if (position == 1){
            Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settings);
        }

        //ovom liniom oznacavamo elemtn iz liste da je selektovano.
        //Pozadinska boja elementa ce biti promenjena.
        drawerList.setItemChecked(position, true);

        //Menjamo naslov
        setTitle(navigationItems.get(position).getTitle());

        //I bitna stvar.Kada odradimo neku akciju zatvorimo navigation drawer
        drawerLayout.closeDrawer(drawerPane);
    }









   /* private void addItem(String name, String description, float rating, Category category, String image){

        Product product = new Product();
        product.setmName(name);
        product.setDescription(description);
        product.setRating(rating);
        product.setImage(image);
        product.setCategory(category);

        //pozovemo metodu create da bi upisali u bazu
        try {
            getDatabaseHelper().getProductDao().create(product);
            getDatabaseHelper().getProductDao().update(product);
            if (product != null) {
                getDatabaseHelper().getProductDao().delete(product);
            }
            getDatabaseHelper().getProductDao().queryBuilder().
                    where().
                    eq(Product.FIELD_NAME_NAME,"nesto").
                    query();

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            try {
                Date date = sdf.parse("12.02.2014.");
                String dateString = sdf.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }



            refresh();

            Toast.makeText(this, "Product inserted", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    */



    /**Toolbar**/
    /**
     *Ova metoda 'ubacuje' sadrzaj 'R.menu.activity_item_detail' fajla unutar toolbar-a
     *Svaki pojedinacan element 'R.menu.activity_item_detail' fajla ce biti jedno dugme
     *na toolbar-u
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_master, menu);
        return super.onCreateOptionsMenu(menu);
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


    /**
     *Metoda koja je zaduzena za rekaciju tj dogadjaj kada se klikne na neki
     *od dugmica u toolbar-u. Pozivom 'item.getItemId()' dobijamo jedinstveni identifikator
     *kliknutog dugmeta na osnovu cega mozemo da odredimo kako da reagujemo.
     *Svaki element unutar 'R.menu.activity_item_detail' fajla ima polje id na osnovu koga
     *lako mozemo odrediti sta smo tacno kliknuli.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add:
                final Dialog dialog = new Dialog(MainActivity.this);

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

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        //Namena ove metode jeste da promeni naslov koji se prikazuje unutar
        //toolbar-a
        getSupportActionBar().setTitle(title);
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


