package com.vince.childcare.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.vince.childcare.R
import com.vince.childcare.core.adapters.RegistrationAdapter
import com.vince.childcare.core.registration.BillingData
import com.vince.childcare.core.registration.ChildData
import com.vince.childcare.core.registration.MedicalData
import com.vince.childcare.core.registration.ParentData
import kotlinx.android.synthetic.main.activity_registration.*






class RegistrationActivity : BaseActivity() {

  private val cards = ArrayList<Any>()
  private lateinit var adapter: RegistrationAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_registration)

    child_menu_item.setOnClickListener { childMenuButtonClicked() }
    parent_menu_item.setOnClickListener { parentMenuButtonClicked() }
    medical_menu_item.setOnClickListener { medicalMenuButtonClicked() }
    billing_menu_item.setOnClickListener { billingMenuButtonClicked() }

    setUpRecyclerView()
  }

  private fun billingMenuButtonClicked() {
    cards.add(BillingData())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
  }

  private fun medicalMenuButtonClicked() {
    cards.add(MedicalData())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
  }

  private fun parentMenuButtonClicked() {
    cards.add(ParentData())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
  }

  private fun childMenuButtonClicked() {
    cards.add(ChildData())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
  }

  private fun setUpRecyclerView() {

    val llm = LinearLayoutManager(applicationContext)
    registration_rv.layoutManager = llm
    registration_rv.itemAnimator = DefaultItemAnimator()
    adapter = RegistrationAdapter(cards)
    registration_rv.adapter = adapter
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onBackPressed() {
    super.onBackPressed()
    beginBackTransition()
  }

  private fun beginBackTransition() {
    val colorAnimation = ValueAnimator.ofObject(
        ArgbEvaluator(), resources.getColor(R.color.colorWhite, null), resources.getColor(R.color.colorWhiteTrans, null))
    colorAnimation.duration = 250 // milliseconds
    colorAnimation.addUpdateListener { animator -> content.setBackgroundColor(animator.animatedValue as Int) }
    colorAnimation.start()
  }
}
