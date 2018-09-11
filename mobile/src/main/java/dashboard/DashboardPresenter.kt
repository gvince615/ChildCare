package dashboard

import billing.BillingFamily
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import core.*
import fragments.Dashboard

class DashboardPresenter {
  private lateinit var activity: Dashboard
  private var dashboardData: ArrayList<Family> = ArrayList()

  fun setUp(context: Dashboard) {
    this.activity = context
  }

  fun getDashboardFragmentData(currentUser: FirebaseUser?) {
    activity.showProgress()
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA)
        .document(currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).get()
        .addOnCompleteListener { task ->
          dashboardData.clear()
          if (task.isSuccessful) {
            for (familyDocument in task.result) {
              val family = Family()

              val billingFamily = getBillingFamilyData(familyDocument)
              family.familyName = billingFamily?.familyName.toString()
              getChildrenDocuments(familyDocument, family)
            }
          }
        }
  }

  private fun getChildrenDocuments(familyDocument: QueryDocumentSnapshot, family: Family) {
    familyDocument.reference.collection(COLLECTION_CHILDREN).get()
        .addOnCompleteListener {
          if (it.isSuccessful) {
            family.children = it.result.size()
            dashboardData.add(family)
            activity.setDashboardData(dashboardData)
          }
          activity.hideProgress()
        }
  }

  private fun getBillingFamilyData(document: QueryDocumentSnapshot): BillingFamily? {
    return if (document.contains(FAMILY_DATA)) {
      val gson = Gson()
      gson.fromJson<BillingFamily>(gson.toJsonTree(document[FAMILY_DATA]), BillingFamily::class.java)
    } else {
      null
    }
  }
}
