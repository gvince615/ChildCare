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


  fun postAttendance() {

    var checkIn = ""
    var checkOut = ""

    var d = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childReference).collection(COLLECTION_ATTENDANCE_DATA)
    d.orderBy("timestamp", Query.Direction.DESCENDING).limit(1).get()
        .addOnCompleteListener { task ->

          if (task.isSuccessful) {

            for (doc in task.result.documents) {
              if (doc.contains("checkIn")) {
                checkIn = doc["checkIn"].toString()

              }
              if (doc.contains("checkOut")) {
                checkOut = doc["checkOut"].toString()
              }

              if (checkIn != "" && checkOut == "") {
                postUpdate(doc.reference)
              } else {
                postNew()
              }
              Log.d("Firebase:Attendance", doc.id + " => " + checkIn + "::" + checkOut)
            }
          } else {
            Log.d("Firebase:Attendance", "Error getting documents: ", task.exception)
          }
        }
  }

  private fun postNew() {
    val attenHash = HashMap<String, Any>()
    val sdf = SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US)
    val currentFormattedTime = sdf.format(Date())

    attenHash.put("checkIn", currentFormattedTime)
    attenHash.put("timestamp", FieldValue.serverTimestamp())

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childReference).collection(COLLECTION_ATTENDANCE_DATA).document()
        .set(attenHash)
        .addOnSuccessListener {
          MainActivity().retrieveChildDataCollection(FirebaseAuth.getInstance().currentUser)
          Log.d("Firestrore", "DocumentSnapshot successfully written!")
        }
        .addOnFailureListener { e -> Log.w("Firestore", "Error writing document", e) }
  }

  private fun postUpdate(reference: DocumentReference) {

    val sdf = SimpleDateFormat(FIRESTORE_DATE_TIME_FORMAT, Locale.US)
    val currentFormattedTime = sdf.format(Date())

    val attenHash = HashMap<String, Any>()
    attenHash.put("checkOut", currentFormattedTime)

    FirebaseFirestore.getInstance().document(reference.path)
        .update(attenHash)
        .addOnSuccessListener {

          MainActivity().retrieveChildDataCollection(FirebaseAuth.getInstance().currentUser)
          Log.d("Firestrore", "DocumentSnapshot successfully written!")
        }
        .addOnFailureListener { e -> Log.w("Firestore", "Error writing document", e) }
  }


  fun setUp(context: Context, childRef: String, activated: Boolean) {
    this.childReference = childRef
    this.context = context
    this.alreadyCheckedIn = activated
  }
}
