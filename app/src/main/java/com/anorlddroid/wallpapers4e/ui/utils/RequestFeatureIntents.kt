package com.anorlddroid.wallpapers4e.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

fun openTwitter(context: Context) {
    var intent: Intent?
    try {
        //Get the twitter app if installed
        context.packageManager.getPackageInfo("com.twitter.android", 0)
        intent = Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=screen_name")) //TODO(Enter your screen name)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    } catch (e: Exception) {
        intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/screen name")) //TODO(Enter your screen name)
    }
    context.startActivity(intent)
}

fun openWhatsApp(context: Context) {
    var intent: Intent? = null
    try {
        //open whatsapp if installed else web browser
        val url = "https://api.whatsapp.com/send?phone=number&text=" + URLEncoder.encode(
            "message",
            "UTF-8"
        )//TODO(Enter your phone number registered with whatsapp starting with country code and message)
        intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Sorry, your phone does not support this activity",
            Toast.LENGTH_SHORT
        ).show()
    }
    context.startActivity(intent)
}


fun makePhoneCall(context: Context) {
    try {
        val callIntent: Intent = Uri.parse("tel:phone_number").let { number ->
            Intent(Intent.ACTION_DIAL, number)
        }//TODO(Enter your phone number starting with the country code)
        context.startActivity(callIntent)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "An error occurred while trying to make the call, try again later",
            Toast.LENGTH_SHORT
        ).show()
    }
}