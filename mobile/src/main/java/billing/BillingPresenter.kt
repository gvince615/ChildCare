package billing

import attendance.AttendanceRecord
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import core.*
import ui.fragments.Billing
import java.text.SimpleDateFormat
import java.util.*

class BillingPresenter {
  private lateinit var activity: Billing
  private lateinit var billingFamilyData: ArrayList<BillingFamily>

  fun setUp(context: Billing, billingFamilyData: ArrayList<BillingFamily>) {
    this.activity = context
    this.billingFamilyData = billingFamilyData
  }

  fun getBillingFragmentData(currentUser: FirebaseUser?) {
    activity.showProgress()
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA)
        .document(currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            billingFamilyData.clear()
            for (familyDocument in task.result!!) {
              // get families
              val billingFamily = getBillingFamilyData(familyDocument)
              billingFamily?.let { billingFamilyData.add(it) }
              getChildrenDocuments(familyDocument, billingFamily)
            }
          }
        }
  }

  private fun getChildrenDocuments(familyDocument: QueryDocumentSnapshot, billingFamily: BillingFamily?) {
    familyDocument.reference.collection(COLLECTION_CHILDREN).get()
        .addOnCompleteListener {
          if (it.isSuccessful) {
            for ((childIndex, childDocument) in it.result?.withIndex()!!) {
              //get children
              getBillingChildData(childDocument)?.let { it1 -> billingFamily?.children?.add(it1) }

              getAttendanceDocuments(childDocument, billingFamily, childIndex)
            }
          }
        }
  }

  private fun getAttendanceDocuments(childDocument: QueryDocumentSnapshot, billingFamily: BillingFamily?,
      childIndex: Int) {
    childDocument.reference.collection(COLLECTION_ATTENDANCE_DATA).orderBy(TIME_STAMP, Query.Direction.DESCENDING).get()
        .addOnCompleteListener {
          if (it.isSuccessful) {
            for (attenDocument in it.result!!) {
              //get attendance

              if (attenDocument[CHECK_OUT].toString() != "") {
                val attenRecord = AttendanceRecord(SimpleDateFormat(BILLING_ATTEN_CARD_TIME_FORMAT, Locale.US).format(
                    SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US).parse(attenDocument[CHECK_IN].toString())).toString(),
                    SimpleDateFormat(BILLING_ATTEN_CARD_TIME_FORMAT, Locale.US).format(
                        SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US).parse(attenDocument[CHECK_OUT].toString())).toString())
                billingFamily?.children?.get(childIndex)?.attendanceRecord?.add(attenRecord)
              }
            }
            activity.refresh(billingFamilyData)
          } else {

          }
          activity.hideProgress()
        }
  }

  private fun getBillingChildData(document: QueryDocumentSnapshot): BillingChildDataModel? {
    return if (document.contains(CHILD)) {
      val gson = Gson()
      gson.fromJson<BillingChildDataModel>(gson.toJsonTree(document[CHILD]), BillingChildDataModel::class.java)
    } else {
      null
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
