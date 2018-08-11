package core

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R


class FirestoreUtil(private val db: FirebaseFirestore, private val context: Context) {

  fun updateOrAddUser(firebaseUser: FirebaseUser?) {
    val user = HashMap<String, Any>()
    firebaseUser?.displayName?.let { user.put("name", it) }
    firebaseUser?.email?.let { user.put("email", it) }
    firebaseUser?.uid?.let { user.put("uid", it) }

    firebaseUser?.uid?.let {
      // define path to document here
      db.collection(COLLECTION_USER_DATA).document(PREFIX_UID + firebaseUser.uid).set(user).addOnSuccessListener {
        Log.d(context.getString(R.string.login_activity_tag), context.getString(R.string.data_update_succeeded))
      }.addOnFailureListener {
        Log.e(context.getString(R.string.login_activity_tag), context.getString(R.string.data_update_failed))
      }
    }
  }

  fun saveChildDataDocument(firebaseUser: FirebaseUser?, childData: HashMap<String, Any>) {

    db.collection(COLLECTION_USER_DATA).document(PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData[LAST_NAME].toString() + "_" + childData[FIRST_NAME].toString())
        .set(childData).addOnSuccessListener {
          Log.d(context.getString(R.string.registration_activity_tag), context.getString(R.string.child_data_succeeded))
        }.addOnFailureListener {
          Log.e(context.getString(R.string.registration_activity_tag), context.getString(R.string.child_data_update_failed))
        }
  }

  fun retrieveChildDataCollection(firebaseUser: FirebaseUser?): ArrayList<Any> {
    val children = ArrayList<Any>()

    db.collection(COLLECTION_USER_DATA).document(PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA)
        .get()
        .addOnCompleteListener { task ->

          if (task.isSuccessful) {
            for (document in task.result) {
              Log.d(context.getString(R.string.registration_activity_tag), document.id + " => " + document.data)
              children.add(document.data)
            }
          } else {
            Log.d(context.getString(R.string.registration_activity_tag), "Error getting documents: ", task.exception)
          }
        }
    return children
  }


  fun saveParentDataDocument(firebaseUser: FirebaseUser?, parentData: HashMap<String, Any>, childData: HashMap<String, Any>?) {
    db.collection(COLLECTION_USER_DATA).document(PREFIX_UID + firebaseUser?.uid)
        .collection(COLLECTION_REGISTRATION_DATA).document(childData?.get(LAST_NAME).toString() + "_" + childData?.get(FIRST_NAME).toString())
        .collection(COLLECTION_PARENTS).document(parentData[LAST_NAME].toString() + "_" + parentData[FIRST_NAME].toString())
        .set(parentData).addOnSuccessListener {
          Toast.makeText(context, "Registration saved ", Toast.LENGTH_SHORT).show()
          Log.d(context.getString(R.string.registration_activity_tag), context.getString(R.string.parent_data_succeeded))
        }.addOnFailureListener {
          Log.e(context.getString(R.string.registration_activity_tag), context.getString(R.string.parent_data_update_failed))
        }
  }
}
