package com.jayesh.finalyearproject.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jayesh.finalyearproject.R
import com.jayesh.finalyearproject.fragment.HomeFragment
import com.jayesh.finalyearproject.fragment.SettingFragment


class MainActivity : AppCompatActivity() {
    //main variables
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var tbMain: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var navMain: NavigationView
    var previousMenuItemSelected: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        tbMain = findViewById(R.id.tbMain)
        frameLayout = findViewById(R.id.frameLayout)
        drawerLayout = findViewById(R.id.drawerLayout)
        navMain = findViewById(R.id.navMain)

        navMain.menu.getItem(0).isCheckable = true
        navMain.menu.getItem(0).isChecked = true

        setUpToolBar(tbMain)
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        //setting on click listener on items in navigation drawer
        navMain.setNavigationItemSelectedListener {
            if (previousMenuItemSelected != null) {
                previousMenuItemSelected?.isChecked = false
            }
            previousMenuItemSelected = it
            it.isCheckable = true
            it.isChecked = true

            when (it.itemId) {
                R.id.itmHome -> {
                    defaultFragment()
                    drawerLayout.closeDrawers()
                }

                R.id.itmUsers -> {
                    Toast.makeText(this, "Users", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawers()
                }

                R.id.itmSettings -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout, SettingFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                }

                R.id.itmLogout -> {
                    drawerLayout.closeDrawers()
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setMessage("Do you want to log out?")
                    alertDialog.setPositiveButton("Yes") { _, _ ->
                        Firebase.auth.signOut()
                        val intent = Intent(this, WelcomeAuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    alertDialog.setNegativeButton("No") { _, _ ->

                    }
                    alertDialog.create()
                    alertDialog.show()
                }
            }

            return@setNavigationItemSelectedListener true
        }

        defaultFragment()

    }

    private fun defaultFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.frameLayout, HomeFragment()
        ).commit()
        navMain.setCheckedItem(R.id.itmHome)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            !is HomeFragment -> {
                navMain.menu.getItem(0).isChecked = true
                defaultFragment()
            }
            else -> super.onBackPressed()
        }
    }

    private fun setUpToolBar(toolbar: androidx.appcompat.widget.Toolbar) {
        setSupportActionBar(toolbar)
//        supportActionBar?.title = "Home"
        supportActionBar?.title = " "
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        super.onResume()
    }

    private fun restartApp() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }



}