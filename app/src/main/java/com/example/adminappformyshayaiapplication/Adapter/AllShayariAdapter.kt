package com.example.adminappformyshayaiapplication.Adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminappformyshayaiapplication.AllShayariActivity
import com.example.adminappformyshayaiapplication.R
import com.example.adminappformyshayaiapplication.databinding.ItemShayariBinding
import com.example.shyariapplicationbyanirudhlohiya.Model.ShayariModel
import com.google.firebase.firestore.FirebaseFirestore


class AllShayariAdapter(
    val allShayariAdapter: AllShayariActivity,
    val shayariList: ArrayList<ShayariModel>,
    val catid: String
) : RecyclerView.Adapter<AllShayariAdapter.ShayariViewHolder>() {

    val db = FirebaseFirestore.getInstance()

    class ShayariViewHolder(val binding: ItemShayariBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShayariViewHolder {
        return ShayariViewHolder(
            ItemShayariBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = shayariList.size

    override fun onBindViewHolder(holder: ShayariViewHolder, position: Int) {
        holder.binding.itemShayari.text = shayariList[position].data.toString()

        holder.binding.btnUpdateShayari.setOnClickListener {
            // Create a dialog.
            val dialog = AlertDialog.Builder(allShayariAdapter).create()
            val inflater = LayoutInflater.from(allShayariAdapter)
            val dialogView = inflater.inflate(R.layout.dialog_add_shayari, null)

            // Get the EditText and TextView from the dialog layout.
            val editText = dialogView.findViewById<EditText>(R.id.dialogShayari)
            val textView = dialogView.findViewById<TextView>(R.id.dialogAddShayari)

            // Pre-fill the EditText with the current Shayari.
            editText.setText(shayariList[position].data)

            // Change the TextView text to "Update Shayari".
            textView.text = "Update Shayari"

            // Set an OnClickListener for the TextView.
            textView.setOnClickListener {
                // Get the new Shayari from the EditText.
                val newShayari = editText.text.toString()

                // Check if the EditText is not empty.
                if (newShayari.isNotEmpty()) {
                    // Update the Shayari in the database.
                    db.collection("Shayari").document(catid).collection("all")
                        .document(shayariList[position].id!!)
                        .update("data", newShayari)
                        .addOnSuccessListener {
                            // Update was successful.
                            Toast.makeText(allShayariAdapter, "Shayari Updated Successfully", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .addOnFailureListener { e ->
                            // Update failed.
                            Toast.makeText(allShayariAdapter, "Error Updating Shayari: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // EditText is empty.
                    Toast.makeText(allShayariAdapter, "Shayari cannot be empty", Toast.LENGTH_SHORT).show()
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

        holder.binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(allShayariAdapter).apply {
                setTitle("Delete")
                setMessage("Are you sure you want to delete this Shayari?")
                setPositiveButton("Yes") { dialog, which ->
                    db.collection("Shayari").document(catid).collection("all")
                        .document(shayariList[position].id!!).delete().addOnCompleteListener {
                            Toast.makeText(
                                allShayariAdapter,
                                "Delete Sucessfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                }
                setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                create()
                show()
            }
        }

    }
}