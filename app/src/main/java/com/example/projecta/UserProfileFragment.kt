package com.example.projecta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.projecta.databinding.FragmentHomeBinding
import com.example.projecta.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.R
import com.google.firebase.ktx.Firebase

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val currUid = auth?.uid
        readData(currUid.toString())


    }
    private fun readData(uid: String) {

        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(uid).get().addOnSuccessListener {

            if (it.exists()){

                val fullname = it.child("fullname").value
                val nik = it.child("nik").value
                val uEmerg1 = it.child("uEmerg1").value
                val uEmerg2 = it.child("uEmerg2").value
                val email = it.child("email").value
//                Toast.makeText(this,"Successfuly Read",Toast.LENGTH_SHORT).show()
                binding.uName.text = fullname.toString()
                binding.uNik.text = nik.toString()
                binding.uEmerg1.text = uEmerg1.toString()
                binding.uEmerg2.text = uEmerg2.toString()
                binding.email.text = email.toString()

            }else{

                Toast.makeText(requireActivity(),"User Doesn't Exist", Toast.LENGTH_SHORT).show()


            }

        }.addOnFailureListener{

            Toast.makeText(requireActivity(),"Failed", Toast.LENGTH_SHORT).show()


        }

    }
}