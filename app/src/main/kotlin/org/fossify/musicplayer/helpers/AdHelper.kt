package org.fossify.musicplayer.helpers

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import org.fossify.musicplayer.R
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object AdHelper {
    private const val TAG = "AdMobBanner"

    // Flag to enable/disable ads in development
    var isAdsEnabled = true

    // Official AdMob Test Banner Unit ID
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    fun setupBannerAd(activity: Activity, container: FrameLayout) {
        // Check if active Premium user
        if (PremiumManager.isPremiumActive(activity)) {
            Log.d(TAG, "Premium entitlement is active. Hiding banner ad container.")
            container.visibility = View.GONE
            container.removeAllViews()
            return
        }

        if (!isAdsEnabled) {
            Log.d(TAG, "Ads disabled via isAdsEnabled flag. Hiding banner ad container.")
            container.visibility = View.GONE
            container.removeAllViews()
            return
        }

        Log.d(TAG, "Setting up AdMob banner ad with Test Unit ID: $TEST_BANNER_AD_UNIT_ID")

        try {
            // Check if MobileAds / AdView class is present on classpath
            val mobileAdsClass = Class.forName("com.google.android.gms.ads.MobileAds")
            val adViewClass = Class.forName("com.google.android.gms.ads.AdView")
            val adRequestClass = Class.forName("com.google.android.gms.ads.AdRequest")
            val adSizeClass = Class.forName("com.google.android.gms.ads.AdSize")
            val adListenerClass = Class.forName("com.google.android.gms.ads.AdListener")

            // Initialize MobileAds safely
            val initMethod = mobileAdsClass.getMethod("initialize", android.content.Context::class.java)
            initMethod.invoke(null, activity.applicationContext)
            Log.d(TAG, "MobileAds initialized successfully.")

            // Instantiate AdView
            val adView = adViewClass.getConstructor(android.content.Context::class.java).newInstance(activity) as View

            // Set Ad Unit ID
            val setAdUnitIdMethod = adViewClass.getMethod("setAdUnitId", String::class.java)
            setAdUnitIdMethod.invoke(adView, TEST_BANNER_AD_UNIT_ID)

            // Get adaptive ad size or banner ad size
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val density = displayMetrics.density
            var adWidthPixels = container.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = displayMetrics.widthPixels.toFloat()
            }
            val adWidth = (adWidthPixels / density).toInt()

            val getCurrentWindowAdSizeMethod = adSizeClass.getMethod(
                "getCurrentWindowAnchoredAdaptiveBannerAdSize",
                android.content.Context::class.java,
                Int::class.javaPrimitiveType
            )
            val adSize = getCurrentWindowAdSizeMethod.invoke(null, activity, adWidth)

            val setAdSizeMethod = adViewClass.getMethod("setAdSize", adSizeClass)
            setAdSizeMethod.invoke(adView, adSize)

            // Attach AdListener to log success and failure
            try {
                val invocationHandler = object : InvocationHandler {
                    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
                        when (method?.name) {
                            "onAdLoaded" -> {
                                Log.d(TAG, "AdMob banner ad loaded successfully.")
                                container.visibility = View.VISIBLE
                            }
                            "onAdFailedToLoad" -> {
                                val error = args?.firstOrNull()
                                val errorCode = try {
                                    error?.javaClass?.getMethod("getCode")?.invoke(error)
                                } catch (e: Exception) { -1 }
                                val errorMessage = try {
                                    error?.javaClass?.getMethod("getMessage")?.invoke(error)
                                } catch (e: Exception) { "Unknown error" }
                                Log.e(TAG, "AdMob banner ad failed to load. Error Code: $errorCode, Message: $errorMessage")
                                // Attach fallback banner on ad load error
                                showFallbackTestBanner(activity, container)
                            }
                            "onAdOpened" -> Log.d(TAG, "AdMob banner ad opened.")
                            "onAdClosed" -> Log.d(TAG, "AdMob banner ad closed.")
                            "onAdClicked" -> Log.d(TAG, "AdMob banner ad clicked.")
                        }
                        return null
                    }
                }
                val listenerProxy = Proxy.newProxyInstance(
                    adListenerClass.classLoader,
                    arrayOf(adListenerClass),
                    invocationHandler
                )
                val setAdListenerMethod = adViewClass.getMethod("setAdListener", adListenerClass)
                setAdListenerMethod.invoke(adView, listenerProxy)
            } catch (e: Throwable) {
                Log.w(TAG, "Could not attach AdListener via reflection: ${e.message}")
            }

            // Add adView to container
            container.removeAllViews()
            container.addView(adView)

            // Create AdRequest and load ad
            val builderClass = Class.forName("com.google.android.gms.ads.AdRequest\$Builder")
            val builderInstance = builderClass.getConstructor().newInstance()
            val buildMethod = builderClass.getMethod("build")
            val adRequest = buildMethod.invoke(builderInstance)

            val loadAdMethod = adViewClass.getMethod("loadAd", adRequestClass)
            loadAdMethod.invoke(adView, adRequest)
            Log.d(TAG, "Calling AdView.loadAd() for banner...")

            container.visibility = View.VISIBLE
        } catch (e: Throwable) {
            Log.w(TAG, "AdMob SDK not present or failed to load. Reason: ${e.message}")
            showFallbackTestBanner(activity, container)
        }
    }

    private fun showFallbackTestBanner(activity: Activity, container: FrameLayout) {
        try {
            activity.runOnUiThread {
                container.removeAllViews()
                val bannerView = LayoutInflater.from(activity).inflate(R.layout.view_admob_test_banner, container, false)
                container.addView(bannerView)
                container.visibility = View.VISIBLE
                Log.d(TAG, "Attached test banner view (view_admob_test_banner) to ad_banner_holder container.")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to display fallback banner: ${e.message}")
        }
    }
}
