package com.github.caay2000.ktcache.example.application

import java.util.UUID

data class Item(
    val id: UUID,
    val name: String,
    val value: Int
) {

    fun update(newValue: Int) = copy(value = newValue)
}
