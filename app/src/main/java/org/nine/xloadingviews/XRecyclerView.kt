package org.nine.xloadingviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.nine.xloadingviews.databinding.LoadingViewBinding
import org.nine.xloadingviews.databinding.XrecyclerViewBinding

class XRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        refresh()
    }

    private lateinit var b: XrecyclerViewBinding

    private var mLoadingView: View? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mLoading = false
    private var mEmptyText = "No data provided"

    fun setEmptyText(value: String) {
        mEmptyText = value
        refresh()
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mAdapter = adapter
        refresh()
    }

    fun setLoadingView(view: View) {
        mLoadingView = view
        if (this::b.isInitialized) {
            (view.parent as? ViewGroup)?.removeView(view)
            if (b.root.childCount >= 3) {
                b.root.removeViewAt(b.root.childCount - 1)
            }

            b.root.addView(view, view.layoutParams)
        }
        refresh()
    }

    fun setLayoutManager(manager: RecyclerView.LayoutManager) {
        mLayoutManager = manager
        refresh()
    }

    private fun refresh() {

        if (!this::b.isInitialized)
            b = XrecyclerViewBinding.inflate(
                LayoutInflater.from(context),
                this,
                true
            )

        b.apply {
            tvEmpty.text = mEmptyText

            if (mLoadingView == null) {
                mLoadingView = LoadingViewBinding.inflate(
                    LayoutInflater.from(context),
                    b.root,
                    false
                ).root

                b.root.addView(mLoadingView)
            }
            if (mLoading) {
                mLoadingView?.show()
                rv.hide()
            } else {
                mLoadingView?.hide()
                rv.show()
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        b.apply {
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
        b.apply {
            if (mAdapter?.itemCount == 0) {
                rv.hide()
                tvEmpty.show()
            } else {
                tvEmpty.hide()
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