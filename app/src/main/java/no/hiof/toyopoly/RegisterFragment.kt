package no.hiof.toyopoly

import android.os.Bundle
import android.text.Editable
import android.text.NoCopySpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.text.TextUtils
import android.text.TextWatcher
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.userManagment.DateInputMask

class RegisterFragment : Fragment(), NoCopySpan{
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
        val db = Firebase.firestore

        registerEmail  = requireView().findViewById(R.id.emailAddressRegister)
        registerPassword  = requireView().findViewById(R.id.passwordRegister)
        registerButton = requireView().findViewById(R.id.UserRegisterButton)

        val registerFirstName = requireView().findViewById<EditText>(R.id.firstName)
        val registerLastName = requireView().findViewById<EditText>(R.id.lastName)
        val registerBirthday = requireView().findViewById<EditText>(R.id.birthday)
        val registerAddress = requireView().findViewById<EditText>(R.id.address)

        DateInputMask(registerBirthday).listen()

        registerButton.setOnClickListener{
            val email: String = registerEmail.text.toString()
            val password: String = registerPassword.text.toString()
            val birthday: String = registerBirthday.text.toString()
            val address: String = registerAddress.text.toString()
            val firstName: String = registerFirstName.text.toString()
            val lastName: String = registerLastName.text.toString()
            val navController = findNavController()
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()

            if(
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(birthday) ||
                TextUtils.isEmpty(address) ||
                TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName)) {
                Toast.makeText(activity, "Please fill all the fields", Toast.LENGTH_LONG).show()
            }
            else if (birthday.length != 10){
                Toast.makeText(activity, "Date of birth is not valid", Toast.LENGTH_LONG).show()

            }
            else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener((requireActivity()), OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userToSave = hashMapOf(
                                "firstName" to registerFirstName.text.toString(),
                                "lastName" to registerLastName.text.toString(),
                                "birthday" to registerBirthday.text.toString(),
                                "address" to registerAddress.text.toString(),
                                "email" to email
                            )
                            db.collection("Users").document(auth.currentUser!!.uid)
                                .set(userToSave)
                            Toast.makeText(activity, "Successfully Registered", Toast.LENGTH_LONG)
                                .show()
                            navController.navigate(action)
                        } else {
                            Toast.makeText(activity, task.exception?.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    })
                }
        }
    }
}