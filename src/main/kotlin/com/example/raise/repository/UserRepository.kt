package com.example.raise.repository

import com.example.raise.domain.User

interface UserRepository {

    fun find(id: Int): User?

    fun save(user: User): Boolean
}