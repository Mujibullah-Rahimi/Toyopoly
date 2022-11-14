package no.hiof.toyopoly.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import no.hiof.toyopoly.MainActivity
import no.hiof.toyopoly.R
import android.net.ConnectivityManager as ConnectivityManagerInternet

class LoginFragment : Fragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_login, container, false)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener(this)

        val registerButton = view.findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener(this)

        val logintext = view.findViewById<TextView>(R.id.loginTextView)

        if(this.activity?.let { isOnline(it) } == false){
            loginButton.alpha = .5f
            registerButton.alpha = .5f
            loginButton.isClickable = false
            registerButton.isClickable = false
            logintext.text = getString(R.string.noInternet)
        }
    }

    override fun onClick(v: View?) {
        auth = FirebaseAuth.getInstance()
        val navController = v?.findNavController()
        val emailField = requireView().findViewById<EditText>(R.id.emailAddressLogin)
        val passwordField = requireView().findViewById<EditText>(R.id.passwordLogin)
        val email: String= emailField?.text.toString()
        val password: String = passwordField?.text.toString()

        when (v?.id) {
            R.id.loginButton -> {
                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(activity, "Email and Password missing", Toast.LENGTH_LONG).show()
                }
                else if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(activity, "Please fill in all fields", Toast.LENGTH_LONG).show()
                }
                else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), OnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Successfully Logged In",
                                    Toast.LENGTH_LONG
                                ).show()
                                val action =
                                    LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                                navController?.navigate(action)
//                         Hide keyboard when logged in
                                val imm: InputMethodManager =
                                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                            }else{
                                Toast.makeText(activity, "One or more fields were incorrect", Toast.LENGTH_LONG).show()
                            }
                        })
                }
            }
            R.id.registerButton ->{
                val action =
                    LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                navController?.navigate(action)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        (activity as MainActivity?)!!.disableDrawer()
        
    }
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as MainActivity?)!!.enableDrawer()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context) : Boolean{
        val connectionManger = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManagerInternet
        if(connectionManger != null){
            val capabilities = connectionManger.getNetworkCapabilities(connectionManger.activeNetwork)
            if(capabilities != null){
                //checks for cellular network
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    Log.i("hasInternet", "Cellular is on")
                    return true
                }
                //checks for Wi-FI
                else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    Log.i("hasInternet", "Wi-Fi is on")
                    return true
                }
            }
        }
        return false
    }
}

