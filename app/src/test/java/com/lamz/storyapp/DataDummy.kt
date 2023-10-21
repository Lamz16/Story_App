package com.lamz.storyapp

import com.lamz.storyapp.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl + $i",
                "createdAt + $i",
                "name $i",
                "description $i",
                "id $i",
            )
            items.add(story)
        }
        return items
    }
}