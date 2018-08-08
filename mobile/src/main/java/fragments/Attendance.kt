package fragments

import activities.MainActivity
import activities.RegistrationActivity
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import attendance.AttenChild
import attendance.AttendanceAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vince.childcare.R
import core.*
import kotlinx.android.synthetic.main.fragment_attendance.view.*

class Attendance : Fragment() {

  private var swipeController: SwipeController? = null
  var children: ArrayList<AttenChild> = ArrayList()
  lateinit var rv: RecyclerView
  lateinit var adapter: AttendanceAdapter




  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    val view: View = inflater.inflate(R.layout.fragment_attendance, container, false)
    setupRecyclerView(view)

    setRefreshListener()
    return view
  }

  private fun setRefreshListener() {
    (activity as MainActivity).setFragmentRefreshListener(object : MainActivity.FragmentRefreshListener {
      override fun onRefresh(children: java.util.ArrayList<AttenChild>) {
        refreshData(children)
      }
    })
  }

  private fun refreshData(children: ArrayList<AttenChild>) {
    this.children = children
    adapter.refreshData(this.children)
  }

  private fun setupRecyclerView(view: View) {
    rv = view.attendance_rv as RecyclerView
    rv.layoutManager = LinearLayoutManager(this.context)
    adapter = AttendanceAdapter(this.context!!, children, (activity as MainActivity))
    rv.adapter = adapter

    swipeController = SwipeController(this.context!!, object : SwipeControllerActions() {
      override fun onDeleteClicked(position: Int) {
        var childToDelete = adapter.items[position].lastName + "_" + adapter.items[position].firstName
        FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(PREFIX_UID + FirebaseAuth.getInstance().currentUser?.uid)
            .collection(COLLECTION_REGISTRATION_DATA).document(childToDelete)
            .delete()
            .addOnSuccessListener {
              (activity as MainActivity).updateChildData()
              Log.d(FIRESTORE_TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
              //todo failed
              Log.w(FIRESTORE_TAG, "Error deleting document", e)
            }
      }

      override fun onEditClicked(position: Int) {
        var childToLoad = adapter.items[position].lastName + "_" + adapter.items[position].firstName
        val intent = Intent(activity, RegistrationActivity::class.java).putExtra(CHILD_TO_LOAD, childToLoad)
        val options = activity?.let {
          ActivityOptionsCompat.makeSceneTransitionAnimation(it,
              rv.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.child_image),
              ViewCompat.getTransitionName(rv.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.child_image)))
        }
        startActivity(intent, options?.toBundle())
      }
    })

    val itemTouchhelper = ItemTouchHelper(swipeController)
    itemTouchhelper.attachToRecyclerView(rv)

    rv.addItemDecoration(object : RecyclerView.ItemDecoration() {
      override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        swipeController!!.onDraw(c)
      }
    })
  }
}
