package billing

import activities.MainActivity
import android.util.Log
import attendance.AttendanceRecord
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import core.*

class BillingPresenter {
  private lateinit var activity: MainActivity
  private lateinit var billingFamilyData: ArrayList<BillingFamily>

  fun setUp(context: MainActivity, billingFamilyData: ArrayList<BillingFamily>) {
    this.activity = context
    this.billingFamilyData = billingFamilyData
  }

  fun getBillingFragmentData(currentUser: FirebaseUser?) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA)
        .document(currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            billingFamilyData.clear()
            for (familyDocument in task.result) {
              // get families
              var billingFamily = getBillingFamilyData(familyDocument)

              familyDocument.reference.collection(COLLECTION_CHILDREN).get()
                  .addOnCompleteListener {
                    if (it.isSuccessful) {
                      for ((childIndex, childDocument) in it.result.withIndex()) {
                        //get children
                        getBillingChildData(childDocument)?.let { it1 -> billingFamily?.children?.add(it1) }

                        childDocument.reference.collection(COLLECTION_ATTENDANCE_DATA).get()
                            .addOnCompleteListener {
                              if (it.isSuccessful) {
                                for (attenDocument in it.result) {
                                  //get attendance

                                  val attenRecord = AttendanceRecord(childDocument.id, childDocument[FIRST_NAME].toString(),
                                      childDocument[LAST_NAME].toString(), attenDocument[CHECK_IN].toString(), attenDocument[CHECK_OUT].toString())
                                  billingFamily?.children?.get(childIndex)?.attendanceRecord?.add(attenRecord)

                                }
                                if (activity.getFragmentRefreshListener() != null) {
                                  activity.getBillingFragmentRefreshListener()?.onRefresh(billingFamilyData, -1)
                                }
                              } else {

                              }
                            }
                      }
//                      if (activity.getFragmentRefreshListener() != null) {
//                        activity.getBillingFragmentRefreshListener()?.onRefresh(billingFamilyData, -1)
//                      }
                    }
                  }
              billingFamily?.let { billingFamilyData.add(it) }
            }
            Log.d(FIRESTORE_TAG + "BILLING", "Success getting billing data: ", task.exception)

          } else {
            // todo - unsuccessful
            Log.d(FIRESTORE_TAG + "BILLING", "Error getting billing data: ", task.exception)
          }
        }
  }

  private fun getBillingAttendanceForChild(attenDocument: QueryDocumentSnapshot?): AttendanceRecord? {
    return if (attenDocument?.contains(CHECK_IN)!!) {
      val gson = Gson()
      gson.fromJson<AttendanceRecord>(gson.toJsonTree(attenDocument), AttendanceRecord::class.java)
    } else {
      null
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

//  fun getBillingAttendanceRecords(childId: String): AttendanceRecord {
//    activity.showProgress()
//
//    val id = childId.split("-")
//    val familyId = id[0]
//    var records = ArrayList<AttendanceRecord>()
//
//    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
//        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
//            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
//        .collection(COLLECTION_REGISTRATION_DATA).document(familyId).collection(COLLECTION_CHILDREN).document(childId).collection(
//            COLLECTION_ATTENDANCE_DATA)
//        .orderBy(TIME_STAMP, Query.Direction.DESCENDING).get()
//        .addOnCompleteListener { task ->
//          if (task.isSuccessful) {
//            for (doc in task.result.documents) {
//              if (doc.contains(CHECK_IN).and(doc[CHECK_IN].toString() != "") && doc.contains(CHECK_OUT).and(doc[CHECK_OUT].toString() != "")) {
//                val childRecord = AttendanceRecord(childId, "", "", doc[CHECK_IN].toString(), doc[CHECK_OUT].toString())
//                records.add(childRecord)
//              }
//            }
//            activity.onGetBillingAttendanceRecordsSuccess(childId, records)
//            activity.hideProgress()
//          } else {
//            // todo - unsuccessful
//            Log.d(FIRESTORE_TAG + ATTENDANCE_TAG, "Error getting documents: ", task.exception)
//          }
//
////          if (activity.getFragmentRefreshListener() != null) {
////            activity.getFragmentRefreshListener()?.onRefresh(children, -1)
////          }
//        }
//
//  }

}
