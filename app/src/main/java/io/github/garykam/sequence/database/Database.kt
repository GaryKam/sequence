package io.github.garykam.sequence.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object Database {
    private val firebase = Firebase.database
    private val movesRef = firebase.getReference("moves")
    private var _moves = MutableStateFlow(emptyMap<String, String>())
    val moves = _moves

    init {
        movesRef.setValue(null)
        movesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _moves.update { snapshot.getValue<Map<String, String>>().orEmpty() }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun addMove(
        boardIndex: Int,
        markerChipColor: String
    ) {
        movesRef.setValue(_moves.value + mapOf(boardIndex.toString() to markerChipColor))
    }
}
