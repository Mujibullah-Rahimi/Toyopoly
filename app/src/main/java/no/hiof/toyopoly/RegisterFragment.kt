package no.hiof.toyopoly

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.text.TextUtils
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener

class RegisterFragment : Fragment() {
    private lateinit var auth:FirebaseAuth
    private lateinit var registerEmail: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        registerEmail  = requireView().findViewById(R.id.emailAdressRegister)
        registerPassword  = requireView().findViewById(R.id.passwordRegister)
        registerButton = requireView().findViewById(R.id.UserRegisterButton)

        registerButton.setOnClickListener{
            val email: String = registerEmail.text.toString()
            val password: String = registerPassword.text.toString()
            val navController = findNavController()
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(activity, "Please fill all the fields", Toast.LENGTH_LONG).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener((requireActivity()), OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "Successfully Registered", Toast.LENGTH_LONG)
                                .show()
                            navController.navigate(action)
                        } else {
                            Toast.makeText(activity, "Registration Failed", Toast.LENGTH_LONG)
                                .show()
                        }
                    })
                }
        }
    }
}