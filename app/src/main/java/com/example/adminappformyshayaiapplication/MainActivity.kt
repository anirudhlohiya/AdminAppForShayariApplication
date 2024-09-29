package com.example.adminappformyshayaiapplication

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminappformyshayaiapplication.databinding.ActivityMainBinding
import com.example.adminappformyshayaiapplication.Adapter.CategoryAdapter
import com.example.adminappformyshayaiapplication.databinding.DialogAddCatBinding
import com.example.shyariapplicationbyanirudhlohiya.Model.CategoryModel
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.btnAddCat.setOnClickListener {
            val addCatDialog = Dialog(this@MainActivity)
            val binding = DialogAddCatBinding.inflate(layoutInflater)
            addCatDialog.setContentView(binding.root)

            if (addCatDialog.window != null) {
                addCatDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            binding.dialogAddCat.setOnClickListener {
                val name = binding.dialogCatName.text.toString()
                val id = db.collection("Shayari").document().id
                val data = CategoryModel(id, name)
                db.collection("Shayari").document(id).set(data).addOnSuccessListener {
                    Toast.makeText(this@MainActivity, "Shayari add Sucessfully", Toast.LENGTH_SHORT)
                        .show()
                    addCatDialog.dismiss()
                }.addOnCanceledListener {
                    Toast.makeText(this@MainActivity, "" + it.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            addCatDialog.show()
        }

        db.collection("Shayari").addSnapshotListener { value, error ->

            val list = arrayListOf<CategoryModel>()
            val data = value?.toObjects(CategoryModel::class.java)
            list.addAll(data!!)

            binding.rcvCategory.layoutManager = LinearLayoutManager(this)
            binding.rcvCategory.adapter = CategoryAdapter(this, list)
        }
    }
}