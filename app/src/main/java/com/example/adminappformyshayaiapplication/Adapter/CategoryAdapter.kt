package com.example.adminappformyshayaiapplication.Adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminappformyshayaiapplication.AllShayariActivity
import com.example.adminappformyshayaiapplication.MainActivity
import com.example.adminappformyshayaiapplication.R
import com.example.adminappformyshayaiapplication.databinding.ItemCategoryBinding
import com.example.shyariapplicationbyanirudhlohiya.Model.CategoryModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CategoryAdapter(val mainActivity: MainActivity, val list: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.CatViewHolder>() {

    val db = FirebaseFirestore.getInstance()

//    val colorsList = arrayListOf<String>("#1abc9c", "#2ecc71", "#3498db", "#9b59b6", "#34495e")


    class CatViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        return CatViewHolder(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
//        if (position % 5 == 0) {
//            holder.binding.itemText.setBackgroundColor(android.graphics.Color.parseColor(colorsList[0]))
//        } else if (position % 5 == 1) {
//            holder.binding.itemText.setBackgroundColor(android.graphics.Color.parseColor(colorsList[1]))
//        } else if (position % 5 == 2) {
//            holder.binding.itemText.setBackgroundColor(android.graphics.Color.parseColor(colorsList[2]))
//        } else if (position % 5 == 3) {
//            holder.binding.itemText.setBackgroundColor(android.graphics.Color.parseColor(colorsList[3]))
//        } else if (position % 5 == 4) {
//            holder.binding.itemText.setBackgroundColor(android.graphics.Color.parseColor(colorsList[4]))
//        }


//        holder.binding.btnDelete.setOnClickListener {
//            db.collection("Shayari").document(list[position].id!!).delete().addOnSuccessListener {
//                Toast.makeText(mainActivity, "Category Delete Sucessfully", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }

        holder.binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(mainActivity).apply {
                setTitle("Delete Confirmation")
                setMessage("Are you sure you want to delete this category?")
                setPositiveButton("Yes") { _, _ ->
                    val categoryDocRef = db.collection("Shayari").document(list[position].id!!)

                    // Create a reference to the "all" subcollection.
                    val allCollectionRef = categoryDocRef.collection("all")

                    // Delete all documents in the "all" subcollection.
                    allCollectionRef.get().addOnSuccessListener { querySnapshot ->
                        val batch = db.batch()

                        for (document in querySnapshot) {
                            batch.delete(document.reference)
                        }

                        batch.commit().addOnSuccessListener {
                            // All documents in "all" subcollection deleted successfully.

                            // Now, delete the category document.
                            categoryDocRef.delete().addOnSuccessListener {
                                // Category document deleted successfully.
                                Toast.makeText(
                                    mainActivity,
                                    "Category Delete Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener { e ->
                            // Error occurred while deleting documents in "all" subcollection.
                            Toast.makeText(
                                mainActivity,
                                "Error Deleting Subcategories: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener { e ->
                        // Error occurred while fetching documents in "all" subcollection.
                        Toast.makeText(
                            mainActivity,
                            "Error Fetching Subcategories: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                create()
                show()
            }
        }

        holder.binding.btnUpdateCategory.setOnClickListener {
            // Create a dialog.
            val dialog = AlertDialog.Builder(mainActivity).create()
            val inflater = LayoutInflater.from(mainActivity)
            val dialogView = inflater.inflate(R.layout.dialog_add_cat, null)

            // Get the EditText and TextView from the dialog layout.
            val editText = dialogView.findViewById<EditText>(R.id.dialogCatName)
            val textView = dialogView.findViewById<TextView>(R.id.dialogAddCat)

            // Pre-fill the EditText with the current category name.
            editText.setText(list[position].name)

            // Change the TextView text to "Update Category".
            textView.text = "Update Category"

            // Set an OnClickListener for the TextView.
            textView.setOnClickListener {
                // Get the new category name from the EditText.
                val newCategoryName = editText.text.toString()

                // Check if the EditText is not empty.
                if (newCategoryName.isNotEmpty()) {
                    // Update the category name in the database.
                    db.collection("Shayari").document(list[position].id!!)
                        .update("name", newCategoryName)
                        .addOnSuccessListener {
                            // Update was successful.
                            Toast.makeText(mainActivity, "Category Updated Successfully", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            // Update failed.
                            Toast.makeText(mainActivity, "Error Updating Category: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // EditText is empty.
                    Toast.makeText(mainActivity, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }

            // Show the dialog.
            dialog.setView(dialogView)
            dialog.show()

            // Set the dialog's width to match the parent's width.
            val window = dialog.window
            if (window != null) {
                val layoutParams = WindowManager.LayoutParams().apply {
                    copyFrom(dialog.window!!.attributes)
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                }
                dialog.window!!.attributes = layoutParams
            }
        }


        holder.binding.itemText.text = list[position].name.toString()
        holder.binding.root.setOnClickListener {
            val intent = Intent(mainActivity, AllShayariActivity::class.java)
            intent.putExtra("id", list[position].id)
            intent.putExtra("name", list[position].name)
            mainActivity.startActivity(intent)
        }
    }

    override fun getItemCount() = list.size
}