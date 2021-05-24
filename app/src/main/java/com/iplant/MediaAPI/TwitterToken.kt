package com.iplant.MediaAPI

import android.R.id.message
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.iplant.data.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import twitter4j.StatusUpdate
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.io.File


class TwitterToken() {
    lateinit var twitter: Twitter
    lateinit var twitterDialog: Dialog
    fun getRequestToken(context : Context) {
        GlobalScope.launch(Dispatchers.Default) {
            val builder = ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(Keys.token)
                .setOAuthConsumerSecret(Keys.secret)
            val config = builder.build()
            val factory = TwitterFactory(config)
            twitter = factory.instance
            try {
                val requestToken = twitter.oAuthRequestToken
                withContext(Dispatchers.Main) {
                    setupTwitterWebviewDialog(requestToken.authorizationURL,context)
                }
            } catch (e: IllegalStateException) {
                Log.e("ERROR: ", e.toString())
            }
        }
    }

    fun setupTwitterWebviewDialog(url: String,context: Context) {
        twitterDialog = Dialog(context)
        val webView = WebView(context)
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = TwitterWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        twitterDialog.setContentView(webView)
        twitterDialog.show()
    }

    inner class TwitterWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request?.url.toString().startsWith(Keys.callback)) {
                Log.d("Authorization URL: ", request?.url.toString())
                handleUrl(request?.url.toString())

                if (request?.url.toString().contains(Keys.callback)) {
                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(Keys.callback)) {
                Log.d("Authorization URL: ", url)
                handleUrl(url)

                if (url.contains(Keys.callback)) {

                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }

        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)
            val oauthVerifier = uri.getQueryParameter("oauth_verifier") ?: ""
            GlobalScope.launch(Dispatchers.Main) {
                try {
                        withContext(Dispatchers.IO) { twitter.getOAuthAccessToken(oauthVerifier) }
                }
                catch(e:Exception )
                {
                    twitterToken = null
                }
            }
        }
    }
    companion object{
         var twitterToken: TwitterToken? = null
        fun checkToken(context:Context): Boolean
        {
            if (twitterToken == null)
            {
                twitterToken = TwitterToken()
                twitterToken?.getRequestToken(context)
                return false
            }
            return true
        }
        fun toMessage(plant: Plant):String
        {
            return plant.toString()
        }
        fun tryTweet(plant: Plant,img: File,context:Context)
        {
            if(checkToken(context))
            {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val status = StatusUpdate(toMessage(plant))
                        status.setMedia(img)
                        twitterToken?.twitter?.updateStatus(status)
                    }
                    catch(e:Exception )
                    {

                    }
                }

            }
        }
        fun  tryTweet(plant: Plant,context:Context) {

            if(checkToken(context))
             {
                 GlobalScope.launch(Dispatchers.IO) {
                     try {
                         val status = StatusUpdate(toMessage(plant))
                         twitterToken?.twitter?.updateStatus(status)
                     }
                     catch(e:Exception )
                     {

                     }
                 }

             }

    }
}
}