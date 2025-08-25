package com.example.raise.junit

import com.example.raise.domain.DomainError
import com.example.raise.domain.User
import com.example.raise.repository.UserRepository
import com.example.raise.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class UserServiceJUnitTest {

    private val repo: UserRepository = mock()
    private val service = UserService(repo)

    @Test
    @DisplayName("happy-path: creates and saves a user")
    fun happyPath() {
        whenever(repo.find(1)).thenReturn(null)
        whenever(repo.save(User(1, "a@b.com"))).thenReturn(true)
        val result = service.createUser(1, "a@b.com")
        assertThat(result.getOrNull()).isEqualTo(User(1, "a@b.com"))
        assertThat(result.leftOrNull()).isNull()
        Mockito.verify(repo).find(1)
        Mockito.verify(repo).save(User(1, "a@b.com"))
    }

    @Test
    fun invalidId() {
        val result = service.createUser(0, "a@b.com")
        val error = result.leftOrNull()
        assertThat(error).isInstanceOf(DomainError.InvalidId::class.java)
        assertThat(result.getOrNull()).isNull()
        Mockito.verifyNoInteractions(repo)
    }

    @Test
    fun invalidEmail() {
        val result = service.createUser(1, "not-an-email")
        val error = result.leftOrNull()
        assertThat(error).isInstanceOf(DomainError.InvalidEmail::class.java)
        assertThat(result.getOrNull()).isNull()
        Mockito.verifyNoInteractions(repo)
    }

    @Test
    fun existingUserAlreadyExistsError() {
        whenever(repo.find(42)).thenReturn(User(42, "x@y.com"))
        val result = service.createUser(42, "x@y.com")
        assertThat(result.leftOrNull()).isInstanceOf(DomainError.AlreadyExists::class.java)
        Mockito.verify(repo).find(42)
        Mockito.verify(repo, Mockito.never()).save(any())
    }

    @Test
    fun repoSaveFailureFailureError() {
        whenever(repo.find(5)).thenReturn(null)
        whenever(repo.save(User(5, "ok@ok.com"))).thenReturn(false)
        val result = service.createUser(5, "ok@ok.com")
        assertThat(result.leftOrNull()).isInstanceOf(DomainError.RepositoryFailure::class.java)
        Mockito.verify(repo).find(5)
        Mockito.verify(repo).save(User(5, "ok@ok.com"))
    }
}
