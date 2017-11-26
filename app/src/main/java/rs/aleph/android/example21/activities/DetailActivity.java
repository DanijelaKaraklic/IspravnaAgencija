package rs.aleph.android.example21.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import rs.aleph.android.example21.R;
import rs.aleph.android.example21.adapters.DrawerListAdapter;
import rs.aleph.android.example21.model.NavigationItem;

/**
 * Created by KaraklicDM on 21.11.2017.
 */

public class DetailActivity extends AppCompatActivity {

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



    private AlertDialog dialog;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_activity);

        // Draws navigation items
        //U navigation drawer postavimo koje to elemente zelimo da imamo. Ikonicu, naziv i krratak opis
        navigationItems.add(new NavigationItem(getString(R.string.drawer_home), getString(R.string.drawer_home_long), R.drawable.ic_action_product));
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




        // Enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
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




    }

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
            finish();
        } else if (position == 1){
            Intent settings = new Intent(DetailActivity.this,SettingsActivity.class);
            startActivity(settings);
        } else if (position == 2){
           /* if (dialog == null){
                dialog = new AboutDialog(MainActivity.this).prepareDialog();
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            dialog.show();*/
        }

        //ovom liniom oznacavamo elemtn iz liste da je selektovano.
        //Pozadinska boja elementa ce biti promenjena.
        drawerList.setItemChecked(position, true);

        //Menjamo naslov
        setTitle(navigationItems.get(position).getTitle());

        //I bitna stvar.Kada odradimo neku akciju zatvorimo navigation drawer
        drawerLayout.closeDrawer(drawerPane);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     * Metoda koja je izmenjena da reflektuje rad sa Asinhronim zadacima
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Toast.makeText(DetailActivity.this, "",Toast.LENGTH_SHORT).show();

                break;
            case R.id.action_add:

                    Toast.makeText(DetailActivity.this, "",Toast.LENGTH_SHORT).show();

                break;
            case R.id.action_delete:

                Toast.makeText(DetailActivity.this, "",Toast.LENGTH_SHORT).show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }








}
