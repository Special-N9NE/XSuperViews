package org.nine.xloadingviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.nine.xloadingviews.databinding.XrecyclerViewBinding

class XRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        refresh()
    }

    private lateinit var b: XrecyclerViewBinding

    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private var mLoading = false


    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mAdapter = adapter
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
            if (mLoading) {
                pb.show()
                rv.hide()
            } else {
                pb.hide()
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