package com.vince.childcare.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R
import com.vince.childcare.core.FirestoreUtil
import com.vince.childcare.core.adapters.RegistrationAdapter
import com.vince.childcare.core.registration.BillingData
import com.vince.childcare.core.registration.ChildData
import com.vince.childcare.core.registration.MedicalData
import com.vince.childcare.core.registration.ParentData
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.registration_child_data_card.view.*

class RegistrationActivity : BaseActivity() {

  val cards = ArrayList<Any>()
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
    menu.close(true)
  }

  private fun medicalMenuButtonClicked() {
    cards.add(MedicalData())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun parentMenuButtonClicked() {
    cards.add(ParentData())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun childMenuButtonClicked() {
    cards.add(ChildData())
    registration_rv.adapter.notifyItemInserted(registration_rv.childCount + 1)
    menu.close(true)
  }

  private fun setUpRecyclerView() {

    val llm = LinearLayoutManager(applicationContext)
    registration_rv.layoutManager = llm
    registration_rv.itemAnimator = DefaultItemAnimator()
    adapter = RegistrationAdapter(cards, this)
    registration_rv.adapter = adapter
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_save -> {
        Toast.makeText(this, "save tapped ", Toast.LENGTH_SHORT).show()
        //todo - save card
        saveRegistration()
      }

      android.R.id.home -> {
        onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun saveRegistration() {

    for ((number, num) in cards.withIndex()) {
      if (num is ChildData) {

        FirestoreUtil(FirebaseFirestore.getInstance(), this).saveChildData(FirebaseAuth.getInstance().currentUser, getChildData(number))


      }
      if (num is ParentData) {
        Log.d(this.packageName, "Parent Data")
      }
    }

  }

  private fun getChildData(number: Int): ChildData {
    Log.d(this.packageName, "Child Data")
    val childData = ChildData()
    childData.first_name = registration_rv.getChildAt(number).input_layout_first_name.editText?.text.toString()
    childData.last_name = registration_rv.getChildAt(number).input_layout_first_name.editText?.text.toString()
    childData.birth_date = registration_rv.getChildAt(number).input_layout_dob.editText?.text.toString()
    childData.address_ln_1 = registration_rv.getChildAt(number).input_layout_address_ln_1.editText?.text.toString()
    childData.address_ln_2 = registration_rv.getChildAt(number).input_layout_address_ln_2.editText?.text.toString()
    childData.address_city = registration_rv.getChildAt(number).input_layout_city.editText?.text.toString()
    childData.address_state = registration_rv.getChildAt(number).input_layout_state.editText?.text.toString()
    childData.address_zip = registration_rv.getChildAt(number).input_layout_zip.editText?.text.toString()
    return childData
  }

  override fun onBackPressed() {
    super.onBackPressed()
    beginBackTransition()
  }

  private fun beginBackTransition() {
    registration_rv.visibility = View.GONE
    val colorAnimation = ValueAnimator.ofObject(
        ArgbEvaluator(), resources.getColor(R.color.colorWhite, null), resources.getColor(R.color.colorWhiteTrans, null))
    colorAnimation.duration = 250 // milliseconds
    colorAnimation.addUpdateListener { animator -> content.setBackgroundColor(animator.animatedValue as Int) }
    colorAnimation.start()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.registration_menu, menu)
    return true
  }
}
