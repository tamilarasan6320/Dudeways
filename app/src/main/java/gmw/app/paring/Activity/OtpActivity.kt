package gmw.app.paring.Activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import gmw.app.paring.R
import gmw.app.paring.databinding.ActivityOtpBinding

class OtpActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpBinding




    private var countDownTimer: CountDownTimer? = null
    private val COUNTDOWN_TIME = 10000L // 45 seconds in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_otp)


        binding = ActivityOtpBinding.inflate(layoutInflater)

        startCountdown()



        binding.btnVerifyOTP.setOnClickListener {

            val otp = binding.otpview.getOTP().length == 6
            if (binding.otpview.getOTP().length == 6) {
                val intent = Intent(this, ProfileDetailsActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
            }

        }



            // Set click listener for the "Resend" button
            binding.btnResendOTP.setOnClickListener {
                resetCountdown()
                startCountdown()
              Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show()

                // Disable the button
                binding.btnResendOTP.isEnabled = false
            }


        setContentView(binding.root)
    }
    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(COUNTDOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                // Update UI to show remaining seconds
                binding.btnResendOTP.text = "Resend in $secondsLeft seconds"
            }

            override fun onFinish() {
                // Enable the button when countdown finishes
                binding.btnResendOTP.isEnabled = true
                binding.btnResendOTP.text = "Resend OTP"
            }
        }.start()
    }
    private fun resetCountdown() {
        countDownTimer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        resetCountdown()
    }
}