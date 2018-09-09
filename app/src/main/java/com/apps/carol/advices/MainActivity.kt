package com.apps.carol.advices

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.act
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var database : AppDatabase? = null
    private var currentlyDisplayedAdvice : Advice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            currentlyDisplayedAdvice?.let { advice -> sendAdvice(advice) }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /*
            My things
         */
        database = AppDatabase.getInstance(this)
        fillAdvice()

        next_advice_button.setOnClickListener { fillAdvice() }
        favourite_button.setOnClickListener {
            currentlyDisplayedAdvice?.let { advice -> saveAdvice(advice) }
        }
        show_favourites.setOnClickListener { getFavourites() }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun fillAdvice() {
        doAsync {
            val text = URL("http://api.adviceslip.com/advice").readText()
            println("Text from API is $text")
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val moshiAdapter = moshi.adapter(Slip::class.java)
            val adviceSlip = moshiAdapter.fromJson(text)
            uiThread {
                adviceSlip?.slip?.let { advice ->
                    advice_text.text = advice.advice
                    advice_text.visibility = View.VISIBLE
                    currentlyDisplayedAdvice = advice
                }
            }
        }
    }

    private fun saveAdvice(advice: Advice) {
        doAsync {
            database?.adviceDao()?.insert(advice)
            uiThread {
                toast("We saved your advice!")
            }
        }
    }

    private fun getFavourites() {
        doAsync {
            val favouriteAdvices = database?.adviceDao()?.getAll() ?: emptyList()
            uiThread {
                favourite_advices.text = favouriteAdvices.toString()
                favourite_advices.visibility = View.VISIBLE
            }
        }
    }

    private fun sendAdvice(advice: Advice) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Hey, I have an advice for you! " + advice.advice)
            type = "text/plain"
        }
        startActivity(sendIntent)
    }
}
