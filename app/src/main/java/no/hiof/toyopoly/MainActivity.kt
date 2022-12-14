package no.hiof.toyopoly

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import no.hiof.toyopoly.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(){
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navView : NavigationView
    private val db = FirebaseFirestore.getInstance()
    private var currentUser : FirebaseUser? = null
    private var storageRef = FirebaseStorage.getInstance().reference
    private lateinit var authStateListener : FirebaseAuth.AuthStateListener
    private var CHANNELID = "notificationChannel"


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        setSupportActionBar(binding.toolbar)



        val drawerLayout : DrawerLayout = binding.drawerLayout
        navView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,R.id.allToysFragment, R.id.messageFragment, R.id.signOut
            ), drawerLayout
        )

        navView.menu.findItem(R.id.signOut).setOnMenuItemClickListener {
            Firebase.auth.signOut()
            currentUser = null
            deleteCache(this)
            val currentFragment = supportFragmentManager.fragments.last()
            NavHostFragment.findNavController(currentFragment).navigate(R.id.loginFragment)
            //navController.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(this,"Logged out", Toast.LENGTH_LONG).show()
            true
        }

        authStateListener = FirebaseAuth.AuthStateListener {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null){
                val header = navView.getHeaderView(0)
                val headerUserImage = header.findViewById<ImageView>(R.id.drawerHeaderImageView)
                val headerUserName = header.findViewById<TextView>(R.id.drawerHeaderUserName)
                val headerEmail = header.findViewById<TextView>(R.id.drawerHeaderUserEmail)
                var imageUri = ""
                val docRef = db.collection("Users").document(firebaseUser.uid)
                docRef
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("isHere", "Snapshot: ${document.data}")
                            headerEmail?.text = document.getString("email")
                            headerUserName?.text = document.getString("firstName")+ " " + document.getString("lastName")
                            imageUri = document.getString("imageUri").toString()

                            val pictureReference = storageRef.storage.getReference(imageUri)
                            Glide.with(this)
                                .load(pictureReference)
                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                .apply(RequestOptions.skipMemoryCacheOf(true))
                                .into(headerUserImage)
                                .clearOnDetach()
                        } else {
                            Log.d("isNotHere", "The document snapshot doesn't exist")
                        }
                    }
                    .addOnFailureListener { e -> Log.d("Error", "Fail at: ", e) }
            }
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.navView.setupWithNavController(navHostFragment.navController)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNELID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ad_action, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentFragment = supportFragmentManager.fragments.last()
        item.isVisible = false
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

    }

    override fun onResume() {
        super.onResume()

        auth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()

        auth.removeAuthStateListener(authStateListener)
    }
    fun disableDrawer(){
        val drawerLayout : DrawerLayout = binding.drawerLayout
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }
    fun enableDrawer(){
        val drawerLayout : DrawerLayout = binding.drawerLayout
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun deleteCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            deleteDir(dir)
        } catch (_: Exception) {
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list() as Array<String>
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }
}


