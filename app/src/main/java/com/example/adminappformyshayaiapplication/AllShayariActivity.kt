package com.example.adminappformyshayaiapplication

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminappformyshayaiapplication.Adapter.AllShayariAdapter
import com.example.adminappformyshayaiapplication.databinding.ActivityAllShayariBinding
import com.example.adminappformyshayaiapplication.databinding.DialogAddShayariBinding
import com.example.shyariapplicationbyanirudhlohiya.Model.ShayariModel
import com.google.firebase.firestore.FirebaseFirestore

class AllShayariActivity : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    lateinit var binding: ActivityAllShayariBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllShayariBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val id = intent.getStringExtra("id")

        binding.toolbarTitle.text = name.toString()

        // adding this test line

        db = FirebaseFirestore.getInstance()

        db.collection("Shayari").document(id!!).collection("all")
            .addSnapshotListener { value, error ->
                val shayariList = arrayListOf<ShayariModel>()
                val data = value?.toObjects(ShayariModel::class.java)
                shayariList.addAll(data!!)

                binding.rcvAllShayari.layoutManager = LinearLayoutManager(this)
                binding.rcvAllShayari.adapter = AllShayariAdapter(this, shayariList, id)
            }

        binding.btnAddShayari.setOnClickListener {

            val addCatDialog = Dialog(this@AllShayariActivity)
            val binding = DialogAddShayariBinding.inflate(layoutInflater)
            addCatDialog.setContentView(binding.root)

            if (addCatDialog.window != null) {
                addCatDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            binding.dialogAddShayari.setOnClickListener {
                val uid = db.collection("Shayari").document().id
                val editShayariGet = binding.dialogShayari.text.toString()

                // No need to manually set the timestamp; Firebase will handle it
                val finalValue = ShayariModel(uid, editShayariGet)


                db.collection("Shayari").document(id).collection("all").document(uid)
                    .set(finalValue).addOnCompleteListener {
                        if (it.isSuccessful) {
                            addCatDialog.dismiss()
                            Toast.makeText(
                                this@AllShayariActivity,
                                "Shayari Added Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Toast.makeText(
                            this@AllShayariActivity,
                            "" + it.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }


            addCatDialog.show()
        }

    }
}