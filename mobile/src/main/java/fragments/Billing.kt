/*
 * Copyright (c) 2016 Lung Razvan
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package fragments


import activities.MainActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import billing.BillingFamily
import billing.BillingFamilyAdapter
import com.vince.childcare.R
import kotlinx.android.synthetic.main.fragment_billing.view.*


class Billing : Fragment() {

  private lateinit var billingFamilyAdapter: BillingFamilyAdapter
  private var billingFamilies: ArrayList<BillingFamily> = ArrayList()
  private lateinit var rv: RecyclerView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    val view: View = inflater.inflate(R.layout.fragment_billing, container, false)
    setupRecyclerView(view)
    setRefreshListener()
    return view
  }

  private fun setRefreshListener() {
    (activity as MainActivity).setBillingFragmentRefreshListener(object : MainActivity.BillingFragmentRefreshListener {
      override fun onRefresh(billingFamilies: ArrayList<BillingFamily>, position: Int) {
        refreshData(billingFamilies, position)
      }

      override fun setProgress(visibleState: Int) {
        setProgressVisibility(visibleState)
      }
    })
  }

  private fun setProgressVisibility(visibleState: Int) {
    // progress_layout_atten.visibility = visibleState
  }


  private fun refreshData(billingFamilies: ArrayList<BillingFamily>, position: Int) {
    this.billingFamilies = billingFamilies
    billingFamilyAdapter.refreshData(this.billingFamilies, position)
  }

  private fun setupRecyclerView(view: View) {

    rv = view.billing_family_rv as RecyclerView
    rv.layoutManager = LinearLayoutManager(this.context)
    billingFamilyAdapter = BillingFamilyAdapter(this.context!!, billingFamilies)
    rv.adapter = billingFamilyAdapter
  }
}
