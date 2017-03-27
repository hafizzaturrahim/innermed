package hafizzaturrahim.com.poliklinikubantrianonline;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import hafizzaturrahim.com.poliklinikubantrianonline.fragment.AntriankuFragment;
import hafizzaturrahim.com.poliklinikubantrianonline.fragment.HistoryFragment;
import hafizzaturrahim.com.poliklinikubantrianonline.fragment.BerandaFragment;
import hafizzaturrahim.com.poliklinikubantrianonline.fragment.ProfilFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    long lastPress;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());
        //        session.checkLogin();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);

        TextView nav_user = (TextView)hView.findViewById(R.id.txtusername);
        TextView nav_nama = (TextView)hView.findViewById(R.id.textnama);
        nav_user.setText(session.getUsernameSession());
        nav_nama.setText(session.getNamaUser());
        navigationView.getMenu().getItem(0).setChecked(true);


        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_beranda));
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastPress > 5000){
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        }else{
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        displayView(id);
        return true;
    }

    private void displayView(int id){
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_beranda) {
            fragment = new BerandaFragment();
            title = "Beranda";
        } else if (id == R.id.nav_antrian) {
            fragment = new AntriankuFragment();
            title = "Antrianku";
        } else if (id == R.id.nav_history) {
            fragment = new HistoryFragment();
            title = "Riwayat";
        } else if (id == R.id.nav_pengaturan) {
            fragment = new ProfilFragment();
            title = "Profil";
        } else if (id == R.id.nav_logout) {
            logout();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_main, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    private void logout(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setTitle("Logout");
        dialog.show();

        Button yes = (Button)dialog.findViewById(R.id.yes);
        Button no = (Button)dialog.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                session.logoutUser();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                // Closing all the Activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialog.dismiss();

            }
        });
    }


}
