package com.example.listycity

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CityRepository {
    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")

    private val _cities = mutableStateListOf<City>()

    val cities: List<City>
        get() = _cities

    init {
        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Firestore", "Listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                _cities.clear()
                for (doc in snapshot.documents) {
                    val city = doc.toObject(City::class.java)
                    if (city != null) {
                        _cities.add(city)
                    }
                }
            }
        }
    }

    fun addCity(city: City) {
        citiesRef.add(city)
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        citiesRef
            .whereEqualTo("name", oldCity.name)
            .whereEqualTo("province", oldCity.province)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    doc.reference.set(updatedCity)
                }
            }
    }

    fun deleteCity(city: City) {
        citiesRef
            .whereEqualTo("name", city.name)
            .whereEqualTo("province", city.province)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    doc.reference.delete()
                }
            }
    }
}