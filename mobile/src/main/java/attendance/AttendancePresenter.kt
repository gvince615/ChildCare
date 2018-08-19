package attendance

import activities.MainActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import core.*
import java.text.SimpleDateFormat
import java.util.*

class AttendancePresenter {

  lateinit var childReference: String
  lateinit var activity: MainActivity
  private lateinit var children: ArrayList<AttenChild>

  fun setUp(context: MainActivity, children: ArrayList<AttenChild>) {
    this.activity = context
    this.children = children
  }

  fun postAttendance(childReference: String, position: Int, view: View) {

    activity.showProgress()

    this.childReference = childReference

    var ref = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childReference).collection(COLLECTION_ATTENDANCE_DATA)

    ref.orderBy(TIME_STAMP, Query.Direction.DESCENDING).limit(1).get()
        .addOnCompleteListener { task ->

          if (task.isSuccessful) {

            for (doc in task.result.documents) {
              if (doc.contains(CHECK_IN).and(doc[CHECK_IN].toString() != "")) {
                if (doc.contains(CHECK_OUT).and(doc[CHECK_OUT].toString() != "")) {
                  postNew(position)
                } else if (doc.contains(CHECK_OUT).and(doc[CHECK_OUT].toString() == "")) {
                  postUpdate(doc, position)
                }
              }

              Log.d(FIRESTORE_TAG, doc.id + " => " + doc[CHECK_IN].toString() + "::" + doc[CHECK_OUT].toString())
            }
          } else {
            // todo - unsuccessful
            Log.d(FIRESTORE_TAG, "Error getting documents: ", task.exception)
          }
          if (task.result.isEmpty) {
            Log.d(FIRESTORE_TAG, "empty result, need to postNew: ")
            postNew(position)
          }
        }

  }

  private fun postNew(position: Int) {
    val currentFormattedTime = SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US).format(Date())

    val attenMap = HashMap<String, Any>()
    attenMap[CHECK_IN] = currentFormattedTime
    attenMap[CHECK_OUT] = ""
    attenMap[TIME_STAMP] = FieldValue.serverTimestamp()

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childReference).collection(COLLECTION_ATTENDANCE_DATA).document()
        .set(attenMap)
        .addOnSuccessListener {
          activity.hideProgress()

          activity.updateChildAttendanceData(attenMap, position)

          Log.d(FIRESTORE_TAG, "DocumentSnapshot successfully written!")
        }
        .addOnFailureListener { e ->
          //todo
          activity.hideProgress()

          Log.w(FIRESTORE_TAG, "Error writing document", e)
        }
  }

  private fun postUpdate(doc: DocumentSnapshot, position: Int) {

    val currentFormattedTime = SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US).format(Date())

    val attenMap = HashMap<String, Any>()
    attenMap[CHECK_OUT] = currentFormattedTime

    FirebaseFirestore.getInstance().document(doc.reference.path)
        .update(attenMap)
        .addOnSuccessListener {
          activity.hideProgress()

          activity.updateChildAttendanceData(attenMap, position)

          Log.d(FIRESTORE_TAG, "DocumentSnapshot successfully written!")
        }
        .addOnFailureListener { e ->
          //todo
          activity.hideProgress()

          Log.w(FIRESTORE_TAG, "Error writing document", e)
        }
  }

  private fun getLatestAttendanceData(refDoc: DocumentSnapshot, child: AttenChild) {

    var reference = FirebaseFirestore.getInstance().document(refDoc.reference.path).collection(COLLECTION_ATTENDANCE_DATA)
    reference.orderBy(TIME_STAMP, Query.Direction.DESCENDING).limit(1).get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            for (doc in task.result.documents) {
              if (doc.contains(CHECK_IN).and(doc[CHECK_IN].toString() != "") && doc.contains(CHECK_OUT).and(doc[CHECK_OUT].toString() == "")) {
                child.checkInTime = doc[CHECK_IN].toString()
                Log.d(FIRESTORE_TAG, doc.id + " => " + doc[CHECK_IN].toString() + "::" + doc[CHECK_OUT].toString())
              }
            }
          } else {
            // todo - unsuccessful
            Log.d(FIRESTORE_TAG, "Error getting documents: ", task.exception)
          }

          if (activity.getFragmentRefreshListener() != null) {
            activity.getFragmentRefreshListener()?.onRefresh(children, -1)
          }
        }
  }

  fun getChildData(currentUser: FirebaseUser?) {
    var d = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA)
    d.get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            children.clear()
            for (document in task.result) {
              val child = AttenChild(document[CHILD_ID].toString(), document[CHILD_IMAGE_URI].toString(), document[FIRST_NAME].toString(),
                  document[LAST_NAME].toString(),
                  document[IS_ACTIVE].toString(), document[BIRTH_DATE].toString(), "", "")
              Log.d(FIRESTORE_TAG, document.id + " => " + document.data)
              children.add(child)
              getLatestAttendanceData(document, child)
            }
          } else {
            // todo - unsuccessful
            Log.d(FIRESTORE_TAG, "Error getting documents: ", task.exception)
          }
        }
  }

}
