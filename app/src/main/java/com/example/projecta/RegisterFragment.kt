package com.example.projecta

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.projecta.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {
    lateinit var binding: FragmentRegisterBinding
    private lateinit var rdb: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

//        binding.textLogin.setOnClickListener() {
//            val intent = Intent(requireActivity(), Login::class.java)
//            startActivity(intent)
//        }


        //get email, name, nik, pass

        binding.btnSignup.setOnClickListener() {
            validateCreds()
        }
        binding.textLogin.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                transaction.replace(com.example.projecta.R.id.frame_layout, LoginFragment())
                transaction.disallowAddToBackStack()
                transaction.commit()
            }

        }
    }

    private fun validateCreds() {

        val fullName = binding.uName.text.toString()
        val birth = binding.uBirth.text.toString()
        val nik = binding.uNik.text.toString()
        val emerg1 = binding.uEmerg1.toString()
        val emerg2 = binding.uEmerg2.toString()
        val email = binding.uEmail.text.toString()
        val password = binding.uPassword.text.toString()



        if (email.isNotEmpty() && fullName.isNotEmpty() && nik.isNotEmpty() && password.isNotEmpty() && birth.isNotEmpty() && emerg1.isNotEmpty() && emerg2.isNotEmpty()) {

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    requireContext(), "Incorrect email!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.length < 6) {
                Toast.makeText(
                    requireContext(), "Password must be more than 6 characters!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (fullName.length < 3) {
                Toast.makeText(
                    requireContext(), "Name must consist of 3 or more characters!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                performSignUp(fullName, birth, nik, emerg1, emerg2, email, password)
//                val uid = auth.uid
//                val database = FirebaseDatabase.getInstance().getReference("users")
//                val userInfo:HashMap<String,Any?> = HashMap()
//                userInfo["uid"] = uid
//                userInfo["email"] = email
//                userInfo["fullName"] = fullName
//                userInfo["nik"] = nik
//
//                database.child(uid!!)
//                    .setValue(userInfo)
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                    .addOnFailureListener(){
//                        Toast.makeText(this, "Failed saving user info", Toast.LENGTH_SHORT).show()
//                    }
//                toggleLoad(loading)
            }
        }

    }

    private fun saveUserInfo(uid: String, fullname: String, birth: String, nik: String,emerg1: String,emerg2: String,email: String) {
        rdb = FirebaseDatabase.getInstance().getReference("Users")
        val newUser = newUser(uid, fullname, birth, nik, emerg1, emerg2, email)
        rdb.child(uid).setValue(newUser).addOnSuccessListener {
            Toast.makeText(requireContext(), " Succesfully saved! ", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
            }
    }

    private fun performSignUp(fullname: String, birth: String, nik: String, emerg1: String, emerg2:String, email: String, password:String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign up success

                    //save user info
                    val currUser = auth.currentUser
                    val currUid = currUser?.uid
                    saveUserInfo(currUid.toString(), fullname, birth, nik, emerg1, emerg2, email)
//                    Toast.makeText(this, " ${currUid} ",Toast.LENGTH_SHORT).show()

                    //Redirect to main act
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign up fail, display error
                    Toast.makeText(
                        requireContext(), "Registration failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            .addOnFailureListener() {
                Toast.makeText(requireContext(), "Error occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}