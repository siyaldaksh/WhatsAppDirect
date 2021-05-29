package com.appbin.easymessage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdRequest
import com.appbin.easymessage.ui.main.AdMobAds
import com.appbin.easymessage.ui.main.Constants
import com.google.android.gms.ads.MobileAds
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import com.appbin.easymessage.databinding.ActivityMainBinding
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    var adView: AdView? = null
    var adRequest: AdRequest? = null
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
   // private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        adView = findViewById(R.id.banner_ad) as AdView

        uiScope.launch {

            withContext(Dispatchers.IO) {
                MobileAds.initialize(this@MainActivity) {}

                val adMobAd = AdMobAds(this@MainActivity, binding.nativeAdContainer)
                adMobAd.refreshAd(Constants.NATIVE_AD)


                adRequest = AdRequest.Builder().build()

            }
            adView?.loadAd(adRequest)
            adView?.visibility = View.VISIBLE
        }

       /* val loadingLayout : LinearLayout = findViewById(R.id.loading)

        mHandler = Handler()
        mHandler.postDelayed(Runnable {
            *//*if (loadingAd.isLoaded) {
                try {
                    loadingAd.show()
                } catch (e: Exception) {

                }
            }*//*
            loadingLayout.visibility = View.GONE
            supportActionBar?.show()
        },3000)*/

        /* val send = findViewById<Button>(R.id.sendButton)
        val number = findViewById<EditText>(R.id.enterNumber)
        val message = findViewById<EditText>(R.id.enterMessage)
        val countryCode = findViewById<CountryCodePicker>(R.id.countryCodeHolder)
        val clearNumber = findViewById<ImageView>(R.id.clearNumber)
        val clearMessage = findViewById<ImageView>(R.id.clearMessage)*/

        binding.sendButton.setOnClickListener {
            val numberWithCountryCode = binding.countryCodeHolder.selectedCountryCodeWithPlus+
                    binding.enterNumber.text.toString().trim()
            val messageString = binding.enterMessage.text.toString().trim()


            if(binding.enterNumber.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please Enter Number...",Toast.LENGTH_SHORT).show()
            }else {
                val uri =
                    Uri.parse("https://api.whatsapp.com/send?phone=$numberWithCountryCode&text=$messageString")

                val sendIntent = Intent(Intent.ACTION_VIEW, uri)

                startActivity(sendIntent)
            }
        }

        binding.enterNumber.doOnTextChanged { text, start, before, count ->
            binding.clearNumber.visibility = View.VISIBLE

        }
        binding.enterMessage.doOnTextChanged { text, start, before, count ->
            binding.clearMessage.visibility = View.VISIBLE

        }

        binding.clearNumber.setOnClickListener {
            binding.enterNumber.text.clear()
            binding.clearNumber.visibility = View.GONE
        }

        binding.enterMessage.setOnClickListener {
            binding.enterMessage.text?.clear()
            binding.clearNumber.visibility = View.GONE
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
        viewModelJob.cancel()
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
