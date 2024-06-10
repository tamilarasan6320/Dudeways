package com.app.dudeways.listeners

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.dudeways.Adapter.ChatAdapter
import com.app.dudeways.Model.ChatModel

class CustomScrollListener(
    private val adapter: ChatAdapter,
    private val onViewEntered : (ChatModel?) -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        // Calculate 40% of RecyclerView height
        val fortyPercentHeight = recyclerView.height * 0.4

        for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
            val view = layoutManager.findViewByPosition(i) ?: continue
            val viewTop = view.top
            val viewBottom = view.bottom

            if (viewTop < fortyPercentHeight && viewBottom > 0) {
                // Item is within the 40% of the viewport
                val chatItem = adapter.getItemInfo(i)
                onViewEntered(chatItem)
            }
        }
    }
}