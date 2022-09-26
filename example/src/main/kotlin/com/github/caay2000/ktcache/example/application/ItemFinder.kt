package com.github.caay2000.ktcache.example.application

import com.github.caay2000.ktcache.KtCache
import java.util.UUID

class ItemFinder(private val repository: ItemRepository) {

    fun invoke(id: UUID): Item = KtCache.cached("findById($id)") { repository.findById(id) }
        ?: throw RuntimeException("item $id does not exists")
}
