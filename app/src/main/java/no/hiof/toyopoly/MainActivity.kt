package no.hiof.toyopoly

// Sendbird

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    val user = Firebase.auth.currentUser
    private lateinit var homeFragment: HomeFragment


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbar)

        val drawerLayout : DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,R.id.allToysFragment, R.id.messageFragment,R.id.messageActivity, R.id.signOut
            ), drawerLayout
        )

        navView.menu.findItem(R.id.signOut).setOnMenuItemClickListener {
            Firebase.auth.signOut()
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(this,"Logged out", Toast.LENGTH_LONG).show()
            true
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.navView.setupWithNavController(navHostFragment.navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ad_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentFragment = supportFragmentManager.fragments.last()
        return when(item.itemId) {
            R.id.createAdsFragment -> {
                NavHostFragment.findNavController(currentFragment).navigate(R.id.createAdsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        getUser()
    }

    fun getUser() {
        val getName = findViewById<TextView>(R.id.userName)
        val getEmail = findViewById<TextView>(R.id.emailAddress)
        val userUID = user!!.uid

        val docRef = db.collection("Users").document(userUID)
        docRef
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("isHere", "Snapshot: ${document.data}")
                    getEmail?.text = document.getString("email")
                    getName?.text = document.getString("firstName")+ " " + document.getString("lastName")
                    //time_ad?.text = document.getDate("timestamp").toString()
                } else {
                    Log.d("isNotHere", "The document snapshot doesn't exist")
                }
            }
            .addOnFailureListener { e -> Log.d("Error", "Fail at: ", e) }
    }

    public fun disableDrawer(){
        val drawerLayout : DrawerLayout = binding.drawerLayout
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
    public fun enableDrawer(){
        val drawerLayout : DrawerLayout = binding.drawerLayout
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}


