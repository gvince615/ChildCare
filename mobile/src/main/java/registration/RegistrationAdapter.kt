package registration

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

class RegistrationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private var listener: CardItemListener? = null
  private val context: Context
  private var list: MutableList<RegistrationCardItem<*>>

  constructor(context: Context, data: MutableList<RegistrationCardItem<*>>, listener: CardItemListener) {
    this.context = context
    this.listener = listener
    this.list = data
  }

  fun addParent(parent: Parent) {
    list.add(RegistrationCardItem(parent, RegistrationCardItem.PARENT))
    notifyItemInserted(list.size)
  }

  fun addChild(child: Child) {
    list.add(RegistrationCardItem(child, RegistrationCardItem.CHILD))
    notifyItemInserted(list.size)
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    when (viewType) {
      RegistrationCardItem.PARENT -> return ParentViewHolder.create(context, viewGroup)
      RegistrationCardItem.CHILD -> return ChildViewHolder.create(context, viewGroup)
    }
    throw RuntimeException()
  }

  override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

    when (getItemViewType(position)) {
      RegistrationCardItem.PARENT -> ParentViewHolder.bind(viewHolder as ParentViewHolder,
          list[position], listener)
      RegistrationCardItem.CHILD -> ChildViewHolder.bind(viewHolder as ChildViewHolder,
          list[position], listener)
    }
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun getItemViewType(position: Int): Int {
    return list[position].viewType
  }

  fun setListener(listener: CardItemListener) {
    this.listener = listener
  }

  fun getList(): MutableList<RegistrationCardItem<*>> {
    return list
  }

  interface CardItemListener {
    fun onParentCardClicked(message: String)
    fun onChildCardClicked(message: String)
  }
}
