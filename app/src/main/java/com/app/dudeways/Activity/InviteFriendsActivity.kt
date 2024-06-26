package com.app.dudeways.Activity

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.dudeways.databinding.ActivityInviteFriendsBinding
import com.app.dudeways.helper.Constant
import com.app.dudeways.helper.Session

class InviteFriendsActivity : AppCompatActivity() {

    lateinit var binding: ActivityInviteFriendsBinding
    lateinit var activity: Activity
    lateinit var session: Session

    var baseUrl: String = "https://play.google.com/store/apps/details?id=com.app.dudeways"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInviteFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activity = this
        session = Session(activity)

        val referralCode = "aShgEc"
        binding.btnReferText.text = referralCode

        binding.btnRefer.setOnClickListener {
            val urlWithReferral = generateReferralUrl(baseUrl, referralCode)
            shareTextAndUrl("Click this link to join Dude Ways App ☺", urlWithReferral)
        }

        binding.btnReferText.setOnClickListener {
            copyTextToClipboard(referralCode)
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Refer Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "sent to device", Toast.LENGTH_SHORT).show()
    }

    private fun generateReferralUrl(baseUrl: String, referralCode: String): String {
        return "$baseUrl&referralCode=$referralCode"
    }

    private fun shareTextAndUrl(text: String, url: String) {
        val shareContent = "$text\n$url"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareContent)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

}
