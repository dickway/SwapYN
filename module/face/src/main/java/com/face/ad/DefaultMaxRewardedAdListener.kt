package com.face.ad

import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import java.lang.Deprecated

interface DefaultMaxRewardedAdListener : MaxRewardedAdListener {


    override fun onAdLoaded(ad: MaxAd) {
    }

    fun onAdLoaded(ad: MaxAd, rewardedAd: MaxRewardedAd?) {
    }

    override fun onAdDisplayed(ad: MaxAd) {
    }

    override fun onAdHidden(ad: MaxAd) {
    }

    override fun onAdClicked(ad: MaxAd) {
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
    }

    override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {
    }

    fun onAdReward(ad: MaxAd,reward: MaxReward?) {}
}