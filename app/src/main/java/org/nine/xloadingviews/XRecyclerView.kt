package org.nine.xloadingviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.nine.xloadingviews.databinding.EmptyViewBinding
import org.nine.xloadingviews.databinding.LoadingViewBinding
import org.nine.xloadingviews.databinding.XrecyclerViewBinding

class XRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        refresh()
    }

    private var b: XrecyclerViewBinding? = null

    private var mLoadingView: View? = null
    private var mEmptyView: View? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mLoading = false
    private val defaultEmptyText = "No data provided"
    private var mEmptyText = defaultEmptyText

    fun setCustomEmptyView(view: View) {
        b?.apply {
            (view.parent as? ViewGroup)?.removeView(view)
            if (mEmptyView != null)
                root.removeView(mEmptyView)
            root.addView(view, view.layoutParams)
        }
        if (view is TextView)
            mEmptyText = view.text.toString()
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
        setupRecyclerView()
    }

    fun setLoadingView(view: View) {
        b?.apply {
            (view.parent as? ViewGroup)?.removeView(view)
            if (mLoadingView != null)
                root.removeView(mLoadingView)
            root.addView(view, view.layoutParams)
        }

        mLoadingView = view
        refresh()
    }

    fun setLayoutManager(manager: RecyclerView.LayoutManager) {
        mLayoutManager = manager
        setupRecyclerView()
    }

    private fun refresh() {
        if (b == null)
            b = XrecyclerViewBinding.inflate(
                LayoutInflater.from(context),
                this,
                true
            )

        b?.apply {
            if (mEmptyView == null) {
                mEmptyView = EmptyViewBinding.inflate(
                    LayoutInflater.from(context),
                    root,
                    false
                ).root

                root.addView(mEmptyView)
            }

            setEmptyText(mEmptyText)

            if (mLoadingView == null) {
                mLoadingView = LoadingViewBinding.inflate(
                    LayoutInflater.from(context),
                    root,
                    false
                ).root

                root.addView(mLoadingView)
            }
            if (mLoading) {
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
            } else
                mLayoutManager


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
                rv.hide()
                mEmptyView?.show()
            } else {
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