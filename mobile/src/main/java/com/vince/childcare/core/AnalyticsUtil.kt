package com.vince.childcare.core

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseUser

class AnalyticsUtil(private val firebaseAnalytics: FirebaseAnalytics) {

  fun logLoginEvent(firebaseUser: FirebaseUser) {
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, firebaseUser.uid)
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
  }
}
