package no.hiof.toyopoly

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(), View.OnClickListener{
    val user = Firebase.auth.currentUser
    val CurrentUser_TAG = "CURRENTUSER"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (user != null){
            Log.d(CurrentUser_TAG, FirebaseAuth.getInstance().currentUser.toString())
        }else{
            Log.d(CurrentUser_TAG,FirebaseAuth.getInstance().currentUser.toString())
        }

        val signOutBtn = view.findViewById<Button>(R.id.signOutBtn)
        signOutBtn.setOnClickListener(this)

        //val dollsButton = view.findViewById<Button>(R.id.dollsButton)
        //dollsButton.setOnClickListener(this)
        //val carsButton = view.findViewById<Button>(R.id.carsButton)
        //carsButton.setOnClickListener(this)

        val createAdsButton = view.findViewById<Button>(R.id.createAdButton)
        createAdsButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val navController = v?.findNavController()
        val signOutBtn = v?.findViewById<Button>(R.id.signOutBtn)
        //val dollsButton = v?.findViewById<Button>(R.id.dollsButton)
        //val carsButton = v?.findViewById<Button>(R.id.carsButton)
        val createAdsButton = v?.findViewById<Button>(R.id.createAdButton)

        val CategoryAction = HomeFragmentDirections.actionHomeFragmentToCategoryFragment()
        val CreateAdAction = HomeFragmentDirections.actionHomeFragmentToCreateAdsFragment()

        when (v?.id){
            R.id.signOutBtn -> {
                Firebase.auth.signOut()
                navController?.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                Toast.makeText(activity,"Logged out", Toast.LENGTH_LONG).show()
            }
            //R.id.dollsButton -> {
            //    CategoryAction.category = dollsButton?.text.toString()
            //    navController?.navigate(CategoryAction)
            //}
            //R.id.carsButton -> {
            //    CategoryAction.category = carsButton?.text.toString()
            //    navController?.navigate(CategoryAction)
            //}
            R.id.createAdButton -> {
                CreateAdAction.createAds = createAdsButton?.text.toString()
                navController?.navigate(CreateAdAction)
            }
        }
    }
}