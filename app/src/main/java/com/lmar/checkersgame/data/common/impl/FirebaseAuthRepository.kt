package com.lmar.checkersgame.data.common.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lmar.checkersgame.data.common.IAuthRepository
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor() : IAuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun login(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)

    override fun signup(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password)

    override fun signout() = auth.signOut()

    override fun isAuthenticated(): Boolean = auth.currentUser != null

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}