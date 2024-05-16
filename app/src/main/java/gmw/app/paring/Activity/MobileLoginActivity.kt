package gmw.app.paring.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import gmw.app.paring.R
import gmw.app.paring.databinding.ActivityMobileLoginBinding

class MobileLoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityMobileLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMobileLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGenerateOtp.setOnClickListener {

            if (binding.etMobileNumber.text.toString().isEmpty()) {
                binding.etMobileNumber.error = "Please enter mobile number"
                return@setOnClickListener
            }
            else if (binding.etMobileNumber.text.toString().length != 10) {
                binding.etMobileNumber.error = "Please enter valid mobile number"
                return@setOnClickListener
            }
            else{
                val intent = Intent(this, OtpActivity::class.java)
                startActivity(intent)
            }



        }



    }
}