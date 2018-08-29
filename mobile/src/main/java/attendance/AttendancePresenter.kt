package attendance

import activities.MainActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.gson.Gson
import core.*
import registration.Child
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

  private lateinit var familyId: String

  fun postAttendance(childReference: String, position: Int) {

    activity.showProgress()
    var id = childReference.split("-")
    this.familyId = id[0]
    this.childReference = childReference

    var ref = FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId).collection(COLLECTION_CHILDREN).document(childReference).collection(
            COLLECTION_ATTENDANCE_DATA)

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
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId).collection(COLLECTION_CHILDREN).document(childReference).collection(
            COLLECTION_ATTENDANCE_DATA).document()
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

  private fun getChild(document: DocumentSnapshot): Child? {
    return if (document.contains(CHILD)) {
      val gson = Gson()
      gson.fromJson<Child>(gson.toJsonTree(document[CHILD]), Child::class.java)
    } else {
      null
    }
  }

  fun activateChild(childRef: String) {
    activity.showProgress()

    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(
        FirebaseAuth.getInstance().currentUser?.displayName.toString().replace(" ", "") +
            PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(familyId).collection(COLLECTION_CHILDREN).document(childRef)
        .get()
        .addOnSuccessListener {


          var childData = getChild(it)
          childData?.isActive = ACTIVE
          var childMap = HashMapUtil().createChildMap(childData!!)

          it.reference.update(CHILD, childMap)
              .addOnSuccessListener {
                activity.hideProgress()
                activity.updateChildData()
                Log.d(FIRESTORE_TAG, "DocumentSnapshot successfully written!")
              }
              .addOnFailureListener { e ->
                //todo
                activity.hideProgress()
                Log.w(FIRESTORE_TAG, "Error writing document", e)
              }
        }
        .addOnFailureListener { e ->
          //todo
          activity.hideProgress()
          Log.w(FIRESTORE_TAG, "Error writing document", e)
        }
  }

  fun getChildData(currentUser: FirebaseUser?) {
    FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA)
        .document(currentUser?.displayName.toString().replace(" ", "") + PREFIX_UID + currentUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).get()

        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            children.clear()
            for (document in task.result) {

              document.reference.collection(COLLECTION_CHILDREN).get()
                  .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                      children.clear()
                      for (document in task.result) {
                        val child = getAttenChildData(document)
                        if (child != null) {
                          children.add(child)
                          getLatestAttendanceData(document, child)
                          Log.d(FIRESTORE_TAG, document.id + " => " + document.data)
                        }
                      }
                    } else {
                      // todo - unsuccessful
                      Log.d(FIRESTORE_TAG, "Error getting documents: ", task.exception)
                    }
                  }
              }
          } else {
            // todo - unsuccessful
            Log.d(FIRESTORE_TAG, "Error getting documents: ", task.exception)
          }
        }
  }

  private fun getAttenChildData(document: QueryDocumentSnapshot): AttenChild? {
    return if (document.contains(CHILD)) {
      val gson = Gson()
      gson.fromJson<AttenChild>(gson.toJsonTree(document[CHILD]), AttenChild::class.java)
    } else {
      null
    }
  }

}
