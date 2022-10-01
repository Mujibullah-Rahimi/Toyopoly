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

        val signOutBtn = view.findViewById<Button>(R.id.signOutBtn)
        signOutBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val navController = v?.findNavController()
        when (v?.id){
            R.id.signOutBtn -> {
                Firebase.auth.signOut()
                navController?.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                Toast.makeText(activity,"Logged out", Toast.LENGTH_LONG).show()
            }
        }
    }
}