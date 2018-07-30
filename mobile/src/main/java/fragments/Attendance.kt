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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import attendance.AttenChild
import attendance.AttendanceAdapter
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.vince.childcare.R
import kotlinx.android.synthetic.main.fragment_attendance.view.*
import registration.Parent


class Attendance : Fragment() {
  private var swipeController: SwipeController? = null

  var children: ArrayList<AttenChild> = ArrayList()
  lateinit var rv: RecyclerView
  lateinit var adapter: AttendanceAdapter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    val view: View = inflater.inflate(R.layout.fragment_attendance, container, false)

    setupRecyclerView(view)

    (activity as MainActivity).setFragmentRefreshListener(object : MainActivity.FragmentRefreshListener {
      override fun onRefresh(children: java.util.ArrayList<AttenChild>) {

        refreshData(children)
      }
    })

    setDocumentListener()

    return view
  }

  private fun setDocumentListener() {

  }

  private fun refreshData(children: ArrayList<AttenChild>) {
    this.children = children
    adapter.refreshData(this.children)
  }

  private fun setupRecyclerView(view: View) {


    rv = view.attendance_rv as RecyclerView
    rv.layoutManager = LinearLayoutManager(this.context)
    adapter = AttendanceAdapter(children, this.context!!)
    rv.adapter = adapter


    swipeController = SwipeController(this.context!!, object : SwipeControllerActions() {
      override fun onDeleteClicked(position: Int) {
        //todo - delete child/data associated from firestore
        //todo - get fresh data from firestore on completion of previous
        //todo - update recyclerview data on completion of previous
      }

      override fun onEditClicked(position: Int) {

        var childToLoad = adapter.items[position].lastName + "_" + adapter.items[position].firstName

        val intent = Intent(activity, RegistrationActivity::class.java).putExtra("childToLoad", childToLoad)
        val options = activity?.let {
          ActivityOptionsCompat.makeSceneTransitionAnimation(it, rv.getChildAt(position).findViewById(R.id.child_image),
              ViewCompat.getTransitionName(rv.getChildAt(position).findViewById(R.id.child_image)))

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

  private fun getParentsList(childRef: String, document: QueryDocumentSnapshot): ArrayList<Parent> {

    var parentList: ArrayList<Parent> = ArrayList()


    return parentList
  }

}
