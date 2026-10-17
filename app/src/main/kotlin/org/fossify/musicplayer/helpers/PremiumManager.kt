package org.fossify.musicplayer.helpers

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PremiumManager {
    private const val PREFS_NAME = "premium_prefs"
    private const val KEY_IS_PREMIUM = "is_premium_active"
    private const val KEY_PRODUCT_ID = "premium_product_id"
    private const val KEY_EXPIRY_TIMESTAMP = "premium_expiry_timestamp"

    const val PRODUCT_MONTHLY = "premium_monthly"
    const val PRODUCT_YEARLY = "premium_yearly"

    private const val MOCK_MONTHLY_PRICE = "PKR 150/month"
    private const val MOCK_YEARLY_PRICE = "PKR 1,260/year"

    private val _isPremiumActiveFlow = MutableStateFlow(false)
    val isPremiumActiveFlow: StateFlow<Boolean> = _isPremiumActiveFlow.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun init(context: Context) {
        val prefs = getPrefs(context)
        val isPremium = prefs.getBoolean(KEY_IS_PREMIUM, false)
        val expiryTs = prefs.getLong(KEY_EXPIRY_TIMESTAMP, 0L)

        val isActive = if (isPremium && expiryTs > 0L) {
            System.currentTimeMillis() < expiryTs
        } else {
            isPremium
        }

        _isPremiumActiveFlow.value = isActive
    }

    fun isPremiumActive(context: Context): Boolean {
        init(context)
        return _isPremiumActiveFlow.value
    }

    fun setPremiumStatus(context: Context, active: Boolean, productId: String = "", durationMs: Long = 30L * 24 * 3600 * 1000) {
        val prefs = getPrefs(context)
        val expiryTs = if (active) System.currentTimeMillis() + durationMs else 0L

        prefs.edit()
            .putBoolean(KEY_IS_PREMIUM, active)
            .putString(KEY_PRODUCT_ID, productId)
            .putLong(KEY_EXPIRY_TIMESTAMP, expiryTs)
            .apply()

        _isPremiumActiveFlow.value = active
    }

    fun getActiveProductId(context: Context): String {
        return getPrefs(context).getString(KEY_PRODUCT_ID, "") ?: ""
    }

    fun getMonthlyPricePlaceholder(): String = MOCK_MONTHLY_PRICE

    fun getYearlyPricePlaceholder(): String = MOCK_YEARLY_PRICE
}
