package activities

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import core.*
import java.text.SimpleDateFormat
import java.util.*


class AttendancePresenter {
  var alreadyCheckedIn: Boolean = false
  lateinit var childReference: String
  lateinit var context: Context

  fun setUp(context: Context, childRef: String, activated: Boolean) {
    this.childReference = childRef
    this.context = context
    this.alreadyCheckedIn = activated
  }

  fun postAttendance() {

    var checkIn = ""
    var checkOut = ""

    var ref = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childReference).collection(COLLECTION_ATTENDANCE_DATA)

    ref.orderBy(TIME_STAMP, Query.Direction.DESCENDING).limit(1).get()
        .addOnCompleteListener { task ->

          if (task.isSuccessful) {

            for (doc in task.result.documents) {
              if (doc.contains(CHECK_IN)) {
                checkIn = doc[CHECK_IN].toString()

              }
              if (doc.contains(CHECK_OUT)) {
                checkOut = doc[CHECK_OUT].toString()
              }

              if (checkIn != "" && checkOut == "") {
                postUpdate(doc.reference)
              } else {
                postNew()
              }

              Log.d(FIRESTORE_TAG, doc.id + " => " + checkIn + "::" + checkOut)
            }
          } else {
            // todo - unsuccessful
            Log.d(FIRESTORE_TAG, "Error getting documents: ", task.exception)
          }
          if (task.result.isEmpty) {
            Log.d(FIRESTORE_TAG, "empty result, need to postNew: ")
            postNew()
          }
        }
  }

  private fun postNew() {
    val attenHash = HashMap<String, Any>()
    val sdf = SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US)
    val currentFormattedTime = sdf.format(Date())

    attenHash[CHECK_IN] = currentFormattedTime
    attenHash[TIME_STAMP] = FieldValue.serverTimestamp()

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childReference).collection(COLLECTION_ATTENDANCE_DATA).document()
        .set(attenHash)
        .addOnSuccessListener {

          //todo update fragment

          Log.d(FIRESTORE_TAG, "DocumentSnapshot successfully written!")
        }
        .addOnFailureListener { e ->
          //todo
          Log.w(FIRESTORE_TAG, "Error writing document", e)
        }
  }

  private fun postUpdate(reference: DocumentReference) {

    val currentFormattedTime = SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US).format(Date())

    val attenMap = HashMap<String, Any>()
    attenMap[CHECK_OUT] = currentFormattedTime
    attenMap[TIME_STAMP] = FieldValue.serverTimestamp()

    FirebaseFirestore.getInstance().document(reference.path)
        .update(attenMap)
        .addOnSuccessListener {

          //todo update fragment

          Log.d(FIRESTORE_TAG, "DocumentSnapshot successfully written!")
        }
        .addOnFailureListener { e ->
          //todo
          Log.w(FIRESTORE_TAG, "Error writing document", e)
        }
  }

//  fun retrieveChildDataCollection(firebaseUser: FirebaseUser?) {
//
//    var d = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + firebaseUser?.uid)
//        .collection(COLLECTION_REGISTRATION_DATA)
//    d.get()
//        .addOnCompleteListener { task ->
//          if (task.isSuccessful) {
//            children.clear()
//            for (document in task.result) {
//              val child = AttenChild("", "", "", "", "", "")
//              child.firstName = document[FIRST_NAME].toString()
//              child.lastName = document[LAST_NAME].toString()
//              child.birthDate = document[BIRTH_DATE].toString()
//              child.isActive = document[IS_ACTIVE].toString()
//              Log.d(FIRESTORE_TAG, document.id + " => " + document.data)
//              children.add(child)
//              getLatestAttendanceData(child)
//            }
//
//          } else {
//            // todo - unsuccessful
//            Log.d(FIRESTORE_TAG, "Error getting documents: ", task.exception)
//          }
//        }
//  }
//
//  private fun getLatestAttendanceData(child: AttenChild) {
//    var checkIn = ""
//    var checkOut = ""
//
//    var d = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
//        .collection(COLLECTION_REGISTRATION_DATA).document(child.lastName + "_" + child.firstName).collection(COLLECTION_ATTENDANCE_DATA)
//
//    d.orderBy(TIME_STAMP, Query.Direction.DESCENDING).limit(1).get()
//        .addOnCompleteListener { task ->
//
//          if (task.isSuccessful) {
//            for (doc in task.result.documents) {
//              if (doc.contains(CHECK_IN)) {
//                checkIn = doc[CHECK_IN].toString()
//              }
//              if (doc.contains(CHECK_OUT)) {
//                checkOut = doc[CHECK_OUT].toString()
//              }
//              if (checkIn != "" && checkOut == "") {
//                child.checkInTime = checkIn
//              }
//              Log.d(FIRESTORE_TAG, doc.id + " => " + checkIn + "::" + checkOut)
//            }
//          } else {
//            // todo - unsuccessful
//            Log.d(FIRESTORE_TAG, "Error getting documents: ", task.exception)
//          }
//        }
//  }


}
