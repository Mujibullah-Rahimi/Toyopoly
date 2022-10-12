package no.hiof.toyopoly.login

import android.app.Activity
import android.os.Bundle
import android.text.NoCopySpan
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import no.hiof.toyopoly.MainActivity
import no.hiof.toyopoly.R
import no.hiof.toyopoly.model.UserModel
import no.hiof.toyopoly.util.DateInputMask


class RegisterFragment : Fragment(), NoCopySpan{
    private lateinit var db : FirebaseFirestore
    private lateinit var auth:FirebaseAuth
    private lateinit var registerButton: Button
    private lateinit var cancelRegisterButton: Button
    private lateinit var googleLoginButton : Button
    private lateinit var navController : NavController
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }
    private fun handleResult(task: Task<GoogleSignInAccount>) {

        if (task.isSuccessful) {
            val account : GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }

        }else {
            Toast.makeText(activity,task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val googleUserToSave = UserModel(account.givenName.toString(), account.familyName.toString(), account.email.toString())
                Log.v("GOOGLE", googleUserToSave.toString())
                db.collection("Users").document(auth.currentUser!!.uid)
                    .set(googleUserToSave)
                val googleLoginSuccess = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                navController.navigate(googleLoginSuccess)
                Toast.makeText(activity, "Successfully Logged In", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(activity, it.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        registerButton = requireView().findViewById(R.id.UserRegisterButton)
        cancelRegisterButton = requireView().findViewById(R.id.cancelRegistrationButton)

        val registerEmail  = requireView().findViewById<EditText>(R.id.emailAddressRegister)
        val registerPassword  = requireView().findViewById<EditText>(R.id.passwordRegister)


        val registerFirstName = requireView().findViewById<EditText>(R.id.firstName)
        val registerLastName = requireView().findViewById<EditText>(R.id.lastName)
        val registerBirthday = requireView().findViewById<EditText>(R.id.birthday)
        val registerAddress = requireView().findViewById<EditText>(R.id.address)

        DateInputMask(registerBirthday).listen()
        cancelRegisterButton.setOnClickListener{
            val cancelAction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            navController.navigate(cancelAction)
        }
        googleLoginButton = view.findViewById(R.id.googleLoginBtn)
        googleLoginButton.setOnClickListener{
            signInGoogle()
        }

        registerButton.setOnClickListener{
            val email: String = registerEmail.text.toString()
            val password: String = registerPassword.text.toString()
            val birthday: String = registerBirthday.text.toString()
            val address: String = registerAddress.text.toString()
            val firstName: String = registerFirstName.text.toString()
            val lastName: String = registerLastName.text.toString()
            //val navController = findNavController()
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
                            val userToSave = UserModel(firstName, lastName, birthday, address, email)
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