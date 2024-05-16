package gmw.app.paring.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import gmw.app.paring.R
import gmw.app.paring.databinding.ActivityProfileDetailsBinding

class ProfileDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileDetailsBinding
    lateinit var activity: ProfileDetailsActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)





        binding.btnSave.setOnClickListener {

            if (binding.etName.text.toString().isEmpty()) {
                binding.etName.error = "Please enter name"
                return@setOnClickListener
            }
            else if (binding.etName.text.toString().length < 4) {
                binding.etName.error = "Name should be atleast 4 characters"
                return@setOnClickListener
            } else if (binding.etEmail.text.toString().isEmpty()) {
                binding.etEmail.error = "Please enter email"
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()) {
                binding.etEmail.error = "Enter a valid Email address"
                return@setOnClickListener
            }else if (binding.etGender.text.toString().isEmpty()) {
                binding.etGender.error = "Please Select Gender"
                return@setOnClickListener
            } else if (binding.etProfession.text.toString().isEmpty()) {
                binding.etProfession.error = "Please enter profession"
                return@setOnClickListener
            } else {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }


        }
        binding.etGender.setOnClickListener {
            showGenderDialog(binding.etGender)
        }

        binding.etProfession.setOnClickListener {
            binding.cardProfession.visibility = View.VISIBLE
            showProfessionDialog(binding.etProfession)

        }


    }

    private fun showProfessionDialog(etProfession: EditText) {
        val professions = listOf("Doctor", "Engineer", "Teacher", "Artist", "Lawyer")
        val adapter = ProfessionAdapter(professions) { selectedProfession ->
            binding.etProfession.setText(selectedProfession)
            binding.cardProfession.visibility = View.GONE
            binding.etProfession.error = null
        }
        binding.rvProfession.adapter = adapter
        binding.rvProfession.layoutManager = LinearLayoutManager(this)

    }


    private fun showGenderDialog(editText: EditText) {
        val genderOptions = arrayOf("Male", "Female")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Gender")
            .setItems(genderOptions) { _, which ->
                val selectedGender = genderOptions[which]
                editText.setText(selectedGender)
                binding.etGender.error = null
            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
        val dialog = builder.create()
        dialog.show()
    }


}