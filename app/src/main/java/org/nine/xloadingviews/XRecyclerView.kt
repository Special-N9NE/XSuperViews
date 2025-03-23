package org.nine.xloadingviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    private var isSwipeRefresh = false
    private var hasDivider = false

    enum class State {
        LOADING, EMPTY, SUCCESS
    }

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.XRecyclerView,
            0, 0
        )

        try {
            mEmptyText =
                typedArray.getString(R.styleable.XRecyclerView_emptyText) ?: defaultEmptyText
            setEmptyText(mEmptyText)

            mLoading = typedArray.getBoolean(R.styleable.XRecyclerView_loading, true)

            hasDivider = typedArray.getBoolean(R.styleable.XRecyclerView_hasDivider, false)

            isSwipeRefresh =
                typedArray.getBoolean(R.styleable.XRecyclerView_swipeRefreshEnabled, false)

            setSwipeRefreshEnabled(isSwipeRefresh)

            if (isSwipeRefresh) {
                setSwipeRefreshColor(
                    typedArray.getResourceId(
                        R.styleable.XRecyclerView_swipeRefreshColor,
                        0
                    )
                )
            }
        } finally {
            typedArray.recycle()
        }
    }

    // Sets the swipe refresh color scheme using a resource ID
    fun setSwipeRefreshColor(@ColorRes color: Int) {
        b?.swipeRefresh?.setColorSchemeResources(color)
    }

    // Enables or disables the swipe refresh functionality
    fun isSwipeRefreshEnabled(enabled: Boolean) {
        setSwipeRefreshEnabled(enabled)
        refresh()
    }

    // Private method to manage swipe refresh enabled status
    private fun setSwipeRefreshEnabled(enabled: Boolean) {
        isSwipeRefresh = enabled
        if (isSwipeRefresh && mLoadingView != null) {
            b?.flContainer?.removeView(mLoadingView)
        }
        b?.swipeRefresh?.isEnabled = isSwipeRefresh
    }

    // Hides the loading view
    private fun hideLoading() {
        mLoading = false
        if (isSwipeRefresh)
            b?.swipeRefresh?.isRefreshing = false
        else
            mLoadingView?.hide()
    }

    // Shows the loading view
    private fun showLoading() {
        mLoading = true
        if (isSwipeRefresh)
            b?.swipeRefresh?.isRefreshing = true
        else
            mLoadingView?.show()
    }

    // Sets a listener to load more data when the user scrolls to the bottom
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

    // Sets a custom empty view for the recycler view
    fun setCustomEmptyView(view: View) {
        b?.apply {
            (view.parent as? ViewGroup)?.removeView(view)
            if (mEmptyView != null) flContainer.removeView(mEmptyView)
            flContainer.addView(view, view.layoutParams)
        }
        if (view is TextView) mEmptyText = view.text.toString()
        mEmptyView = view
        refresh()
    }

    // Sets the text to be displayed when the list is empty
    fun setEmptyText(value: String?) {
        mEmptyText = value ?: defaultEmptyText
        if (mEmptyView is TextView) {
            (mEmptyView as TextView).text = mEmptyText
        }
    }

    // Sets the adapter for the RecyclerView
    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mAdapter = adapter
        refresh()
    }

    // Sets the loading view for the recycler view
    fun setLoadingView(view: View) {
        b?.apply {
            (view.parent as? ViewGroup)?.removeView(view)
            if (mLoadingView != null) flContainer.removeView(mLoadingView)
            flContainer.addView(view, view.layoutParams)
        }

        mLoadingView = view
        refresh()
    }

    // Sets the layout manager for the RecyclerView
    fun setLayoutManager(manager: RecyclerView.LayoutManager) {
        mLayoutManager = manager
        setupRecyclerView()
    }

    // Adds or removes a divider in the recycler view depending on the parameter
    fun hasDivider(enabled: Boolean) {
        b?.rv?.apply {
            if (enabled)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            else {
                if (itemDecorationCount > 0)
                    removeItemDecorationAt(0)
            }
        }
    }

    // Refreshes the view based on the current loading state
    private fun refresh() {
        if (b == null) b = XrecyclerViewBinding.inflate(
            LayoutInflater.from(context), this, true
        )

        b?.apply {
            if (mEmptyView == null) {
                mEmptyView = EmptyViewBinding.inflate(
                    LayoutInflater.from(context), flContainer, false
                ).root

                flContainer.addView(mEmptyView)
            }

            setEmptyText(mEmptyText)

            if (mLoadingView == null && !isSwipeRefresh) {
                mLoadingView = LoadingViewBinding.inflate(
                    LayoutInflater.from(context), flContainer, false
                ).root

                flContainer.addView(mLoadingView)
            }
            if (mLoading) {
                _state.postValue(State.LOADING)
                showLoading()
                mEmptyView?.hide()
                rv.hide()
            } else {
                hideLoading()
                rv.show()
                setupRecyclerView()
            }

            b?.swipeRefresh?.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
                override fun onRefresh() {
                    _state.postValue(State.LOADING)
                }
            })
        }
    }

    // Sets up the RecyclerView with the provided layout manager and adapter
    private fun setupRecyclerView() {
        hideLoading()
        b?.apply {
            rv.adapter = mAdapter

            hasDivider(hasDivider)

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

    // Handles empty state based on the adapter's item count
    fun handleEmptyState() {
        hideLoading()
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

    // Sets the loading state and refreshes the view
    fun setLoading(value: Boolean) {
        mLoading = value
        refresh()
    }
}

// Extension function to show the view
fun View.show() {
    visibility = View.VISIBLE
}

// Extension function to hide the view
fun View.hide() {
    visibility = View.GONE
}
