package com.example.projecta

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projecta.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth
        if(FirebaseAuth.getInstance().currentUser != null){
            var intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener{
            validateCreds()
        }

        binding.goRegister.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                transaction.replace(R.id.frame_layout, RegisterFragment())
                transaction.disallowAddToBackStack()
                transaction.commit()
            }

        }

    }
    private fun validateCreds(){

        val email = binding.uEmail.text.toString()
        val password = binding.uPassword.text.toString()

        if (email.isNotEmpty() || password.isNotEmpty()){
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(
                    requireContext(), "Incorrect email!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if(password.length < 6){
                Toast.makeText(
                    requireContext(), "Password must be more than 6 characters!",
                    Toast.LENGTH_SHORT
                ).show()
            } else{
                performSignIn(email, password)
            }
        } else {

            Toast.makeText(
                requireContext(), "Please fill in the information",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun performSignIn(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}