package com.appbin.easymessage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import com.google.android.material.textfield.TextInputLayout
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdRequest
import com.appbin.easymessage.ui.main.AdMobAds
import com.appbin.easymessage.ui.main.Constants
import com.google.android.gms.ads.MobileAds
import com.hbb20.CountryCodePicker
import android.view.MenuInflater
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.widget.doOnTextChanged


class MainActivity : AppCompatActivity() {

    var adView: AdView? = null
    var adRequest: AdRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}

        adView = findViewById(R.id.banner_ad) as AdView
        adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)


        val adLayout : LinearLayout = findViewById(R.id.nativeAdContainer)
        val adMobAd = AdMobAds(this, adLayout)
        adMobAd.refreshAd(Constants.NATIVE_AD)



        val send = findViewById<Button>(R.id.sendButton)
        val number = findViewById<EditText>(R.id.enterNumber)
        val message = findViewById<EditText>(R.id.enterMessage)
        val countryCode = findViewById<CountryCodePicker>(R.id.countryCodeHolder)
        val clearNumber = findViewById<ImageView>(R.id.clearNumber)
        val clearMessage = findViewById<ImageView>(R.id.clearMessage)

        send.setOnClickListener {
            val numberWithCountryCode = countryCode.selectedCountryCodeWithPlus+number?.text.toString().trim()
            val messageString = message?.text.toString().trim()


            if(number?.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please Enter Number...",Toast.LENGTH_SHORT).show()
            }else {
                val uri =
                    Uri.parse("https://api.whatsapp.com/send?phone=$numberWithCountryCode&text=$messageString")

                val sendIntent = Intent(Intent.ACTION_VIEW, uri)

                startActivity(sendIntent)
            }
        }

        number?.doOnTextChanged { text, start, before, count ->
            clearNumber.visibility = View.VISIBLE

        }
        message?.doOnTextChanged { text, start, before, count ->
            clearMessage.visibility = View.VISIBLE

        }

        clearNumber.setOnClickListener {
            number?.text?.clear()
            clearNumber.visibility = View.GONE
        }

        clearMessage.setOnClickListener {
            message?.text?.clear()
            clearMessage.visibility = View.GONE
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.nav_share -> shareSuccess()

            R.id.nav_rate -> rateReview()

            R.id.nav_feedback -> userFeedback()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun userFeedback() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_id)) )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.menu_feedback))
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
        emailIntent.type = getString(R.string.email_type)

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.email_title)))

        } catch (e : android.content.ActivityNotFoundException){
            Toast.makeText(this, getString(R.string.email_error_toast), Toast.LENGTH_SHORT).show()
        }
    }


    private fun shareSuccess(){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun rateReview(){
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + this.packageName)
                )
            )
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
                )
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(adView!=null){
            adView?.destroy()
        }
    }

    override fun onPause() {
        super.onPause()
        if(adView!=null){
            adView?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if(adView!=null){
            adView?.resume()
        }
    }


}
