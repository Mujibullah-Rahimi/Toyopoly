package no.hiof.toyopoly.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import no.hiof.toyopoly.MainActivity
import no.hiof.toyopoly.R

class LoginFragment : Fragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_login, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener(this)

        val registerButton = view.findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener(this)



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
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), OnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(activity, "Successfully Logged In", Toast.LENGTH_LONG).show()
                        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                        navController?.navigate(action)
//                         Hide keyboard when logged in
                        val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
                        }else {
                        Toast.makeText(activity, "Login Failed ", Toast.LENGTH_LONG).show()
                    }
                })
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

}

