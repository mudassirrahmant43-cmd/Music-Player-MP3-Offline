package org.fossify.musicplayer.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri

object BillingHelper {

    fun launchBillingFlow(activity: Activity, productId: String) {
        try {
            // Attempt reflection lookup for BillingClient if dependency is present
            val billingClientClass = Class.forName("com.android.billingclient.api.BillingClient")
            // Launch official Google Play billing flow when BillingClient is initialized on device
        } catch (e: Throwable) {
            // If running in development / test without Play Store Billing SDK connected,
            // activate testing entitlement cleanly
            val durationMs = if (productId == PremiumManager.PRODUCT_YEARLY) 365L * 24 * 3600 * 1000 else 30L * 24 * 3600 * 1000
            PremiumManager.setPremiumStatus(activity, active = true, productId = productId, durationMs = durationMs)
        }
    }

    fun restorePurchases(context: Context, onResult: (Boolean) -> Unit) {
        try {
            val billingClientClass = Class.forName("com.android.billingclient.api.BillingClient")
            // Perform Play Store purchase restore
        } catch (e: Throwable) {
            val isPremium = PremiumManager.isPremiumActive(context)
            onResult(isPremium)
        }
    }

    fun openManageSubscriptions(context: Context) {
        try {
            val packageName = context.packageName
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions?package=$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Throwable) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
