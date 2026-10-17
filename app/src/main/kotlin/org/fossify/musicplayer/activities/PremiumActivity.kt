package org.fossify.musicplayer.activities

import android.os.Bundle
import android.view.View
import org.fossify.commons.extensions.toast
import org.fossify.musicplayer.R
import org.fossify.musicplayer.databinding.ActivityPremiumBinding
import org.fossify.musicplayer.helpers.BillingHelper
import org.fossify.musicplayer.helpers.PremiumManager

class PremiumActivity : SimpleActivity() {
    private lateinit var binding: ActivityPremiumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        updateStatus()
    }

    private fun setupUI() {
        binding.premiumCloseBtn.setOnClickListener {
            finish()
        }

        binding.planMonthlyCard.setOnClickListener {
            binding.radioMonthly.isChecked = true
            binding.radioYearly.isChecked = false
        }

        binding.planYearlyCard.setOnClickListener {
            binding.radioYearly.isChecked = true
            binding.radioMonthly.isChecked = false
        }

        binding.radioMonthly.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.radioYearly.isChecked = false
        }

        binding.radioYearly.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.radioMonthly.isChecked = false
        }

        binding.btnSubscribeNow.setOnClickListener {
            val selectedProductId = if (binding.radioYearly.isChecked) {
                PremiumManager.PRODUCT_YEARLY
            } else {
                PremiumManager.PRODUCT_MONTHLY
            }

            BillingHelper.launchBillingFlow(this, selectedProductId)
            updateStatus()
            toast(R.string.premium_active)
        }

        binding.btnRestore.setOnClickListener {
            BillingHelper.restorePurchases(this) { isPremium ->
                updateStatus()
                if (isPremium) {
                    toast(R.string.premium_active)
                } else {
                    toast("No active subscription found.")
                }
            }
        }

        binding.btnManage.setOnClickListener {
            BillingHelper.openManageSubscriptions(this)
        }
    }

    private fun updateStatus() {
        val isPremium = PremiumManager.isPremiumActive(this)
        if (isPremium) {
            binding.tvPremiumStatus.visibility = View.VISIBLE
            binding.tvPremiumStatus.text = getString(R.string.premium_active)
        } else {
            binding.tvPremiumStatus.visibility = View.GONE
        }
    }
}
