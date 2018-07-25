package com.vince.childcare.core

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R
import com.vince.childcare.core.registration.Child
import com.vince.childcare.core.registration.Parent
import com.vince.childcare.core.registration.RegistrationCardItem
import java.util.*


class FirestoreUtil(private val db: FirebaseFirestore, private val context: Context) {

  fun updateOrAddUser(firebaseUser: FirebaseUser?) {
    val user = HashMap<String, Any>()
    firebaseUser?.displayName?.let { user.put("name", it) }
    firebaseUser?.email?.let { user.put("email", it) }
    firebaseUser?.uid?.let { user.put("uid", it) }

    firebaseUser?.uid?.let {
      // define path to document here
      db.collection("user_data").document("uid_" + firebaseUser.uid).set(user).addOnSuccessListener {
        Log.d(context.getString(R.string.login_activity_tag), context.getString(R.string.data_update_succeeded))
      }.addOnFailureListener {
        Log.e(context.getString(R.string.login_activity_tag), context.getString(R.string.data_update_failed))
      }
    }
  }

  fun saveChildDataDocument(firebaseUser: FirebaseUser?, childData: RegistrationCardItem<Child>) {

    db.collection("user_data").document("uid_" + firebaseUser?.uid)
        .collection("registration_data").document(childData.`object`.firstName + "_" + childData.`object`.lastName)
        .set(childData).addOnSuccessListener {
          Log.d(context.getString(R.string.registration_activity_tag), context.getString(R.string.child_data_succeeded))
        }.addOnFailureListener {
          Log.e(context.getString(R.string.registration_activity_tag), context.getString(R.string.child_data_update_failed))
        }

  }

  fun retrieveChildDataCollection(firebaseUser: FirebaseUser?) {
    db.collection("user_data").document("uid_" + firebaseUser?.uid)
        .collection("registration_data")
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            for (document in task.result) {
              Log.d(context.getString(R.string.registration_activity_tag), document.id + " => " + document.data)
            }
          } else {
            Log.d(context.getString(R.string.registration_activity_tag), "Error getting documents: ", task.exception)
          }
        }

  }


  fun saveParentDataDocument(firebaseUser: FirebaseUser?, parentData: RegistrationCardItem<Parent>, childData: RegistrationCardItem<Child>) {
    db
        .collection("user_data").document("uid_" + firebaseUser?.uid)
        .collection("registration_data").document(childData.`object`.firstName + "_" + childData.`object`.lastName)
        .collection("parents").document(parentData.`object`.firstName + "_" + parentData.`object`.lastName)
        .set(parentData).addOnSuccessListener {
          Log.d(context.getString(R.string.registration_activity_tag), context.getString(R.string.parent_data_succeeded))
        }.addOnFailureListener {
          Log.e(context.getString(R.string.registration_activity_tag), context.getString(R.string.parent_data_update_failed))
        }
  }
}
