package org.nine.xloadingviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.nine.xloadingviews.databinding.EmptyViewBinding
import org.nine.xloadingviews.databinding.LoadingViewBinding
import org.nine.xloadingviews.databinding.XrecyclerViewBinding

class XRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var b: XrecyclerViewBinding? = null

    private var mLoadingView: View? = null
    private var mEmptyView: View? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mLoading = true
    private val defaultEmptyText = "No data provided"
    private var mEmptyText = defaultEmptyText
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    enum class State {
        LOADING, EMPTY, SUCCESS
    }

    fun setNextLoadListener(onNextLoadListener: () -> Unit) {
        b?.rv?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager? ?: return
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalCount = layoutManager.itemCount

                if (lastVisibleItem >= totalCount - 2) {
                    onNextLoadListener()
                }
            }
        })
    }

    fun setCustomEmptyView(view: View) {
        b?.apply {
            (view.parent as? ViewGroup)?.removeView(view)
            if (mEmptyView != null) root.removeView(mEmptyView)
            root.addView(view, view.layoutParams)
        }
        if (view is TextView) mEmptyText = view.text.toString()
        mEmptyView = view
        refresh()
    }

    fun setEmptyText(value: String?) {
        mEmptyText = value ?: defaultEmptyText
        if (mEmptyView is TextView) {
            (mEmptyView as TextView).text = mEmptyText
        }
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mAdapter = adapter

        refresh()

        setupRecyclerView()
    }

    fun setLoadingView(view: View) {
        b?.apply {
            (view.parent as? ViewGroup)?.removeView(view)
            if (mLoadingView != null) root.removeView(mLoadingView)
            root.addView(view, view.layoutParams)
        }

        mLoadingView = view
        refresh()
    }

    fun setLayoutManager(manager: RecyclerView.LayoutManager) {
        mLayoutManager = manager
        setupRecyclerView()
    }

    fun addDivider() {
        b?.rv?.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun refresh() {
        if (b == null) b = XrecyclerViewBinding.inflate(
            LayoutInflater.from(context), this, true
        )

        b?.apply {
            if (mEmptyView == null) {
                mEmptyView = EmptyViewBinding.inflate(
                    LayoutInflater.from(context), root, false
                ).root

                root.addView(mEmptyView)
            }

            setEmptyText(mEmptyText)

            if (mLoadingView == null) {
                mLoadingView = LoadingViewBinding.inflate(
                    LayoutInflater.from(context), root, false
                ).root

                root.addView(mLoadingView)
            }
            if (mLoading) {
                _state.postValue(State.LOADING)
                mLoadingView?.show()
                mEmptyView?.hide()
                rv.hide()
            } else {
                mLoadingView?.hide()
                rv.show()
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        b?.apply {
            rv.adapter = mAdapter

            rv.layoutManager = if (mLayoutManager == null) {
                LinearLayoutManager(context).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
            } else mLayoutManager


            mAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    handleEmptyState()
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    handleEmptyState()
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    super.onItemRangeRemoved(positionStart, itemCount)
                    handleEmptyState()
                }
            })

            handleEmptyState()
        }
    }

    fun handleEmptyState() {
        b?.apply {
            if (mAdapter?.itemCount == 0) {
                _state.postValue(State.EMPTY)
                rv.hide()
                mEmptyView?.show()
            } else {
                _state.postValue(State.SUCCESS)
                mEmptyView?.hide()
                rv.show()
            }
        }
    }

    fun setLoading(value: Boolean) {
        mLoading = value
        refresh()
    }
}


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}