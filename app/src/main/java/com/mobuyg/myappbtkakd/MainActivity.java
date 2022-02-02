package com.mobuyg.myappbtkakd;

import com.mobuyg.myappbtkakd.web.WebFragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment currentFragment;
    private Listener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

         if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
        } else {
            currentFragment = WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frameLayoutMain, currentFragment);
            fragmentTransaction.commit();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (listener != null) {
            if (listener.onBackPressed()) return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

		
        if (item.getItemId() == R.id.section1) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }
        if (item.getItemId() == R.id.section2) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/catalog", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }
        if (item.getItemId() == R.id.section3) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/profile", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }
        if (item.getItemId() == R.id.section4) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/statistics", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }
        if (item.getItemId() == R.id.section5) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/courses", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }
        if (item.getItemId() == R.id.section6) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/certificates", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }
        if (item.getItemId() == R.id.section7) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/courses/notes", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }
        if (item.getItemId() == R.id.section8) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/courses/favourites", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }
        if (item.getItemId() == R.id.section9) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/news", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }
        if (item.getItemId() == R.id.section10) {replaceFragment(WebFragment.newInstance("https://www.btkakademi.gov.tr/portal/faq", "mailto:,tel:,market:,play.google,vid:,whatsapp:", true, false, false)); }

        

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutMain, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (currentFragment instanceof WebFragment) {
			currentFragment.onActivityResult(requestCode, resultCode, intent);
		}
	}


    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof Listener)
            listener = (Listener) fragment;
        else
            listener = null;
    }

    public interface Listener {
        boolean onBackPressed();
    }

}
