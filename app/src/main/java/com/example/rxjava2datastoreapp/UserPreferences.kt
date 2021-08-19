package com.example.rxjava2datastoreapp

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.rxjava2.RxDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Flowable
import io.reactivex.Single
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KParameter

class UserPreferencesRepository(val dataStore: RxDataStore<Preferences>) {

    companion object {
        val string = stringPreferencesKey("string")
        val int = intPreferencesKey("int")
        val bool = booleanPreferencesKey("bool")
        val set = stringSetPreferencesKey("set")
        val cat = stringPreferencesKey("cat")
        val catsInJSONString = stringPreferencesKey("catsJson")
    }

    fun <T> set(key: Preferences.Key<T>, value: T): Single<Preferences> =
        dataStore.updateDataAsync {
            val m = it.toMutablePreferences()
            m[key] = value
            return@updateDataAsync Single.just(m)
        }

    fun delete(key: Preferences.Key<*>): Single<Preferences> = dataStore.updateDataAsync {
        val m = it.toMutablePreferences()
        m.remove(key)
        return@updateDataAsync Single.just(m)
    }

    fun <T> setObject(key: Preferences.Key<String>, value: T): Single<Preferences> =
        dataStore.updateDataAsync {
            val m = it.toMutablePreferences()
            m[key] = value.toJSONString()
            return@updateDataAsync Single.just(m)
        }


    inline fun <reified T> get(key: Preferences.Key<T>, defaultValue: T? = null): Flowable<T> {
        return when (T::class) {
            String::class -> dataStore.data().map { it[key] ?: defaultValue ?: "" } as Flowable<T>
            Int::class -> dataStore.data().map { it[key] ?: defaultValue ?: 0 } as Flowable<T>
            Boolean::class -> dataStore.data()
                .map { it[key] ?: defaultValue ?: false } as Flowable<T>
            Set::class -> dataStore.data()
                .map { it[key] ?: defaultValue ?: emptySet<String>() } as Flowable<T>
            else -> throw IllegalStateException("Unsupported type")
        }
    }

    inline fun <reified T> getObject(key: Preferences.Key<String>, defaultValue: T): Flowable<T> {
        return dataStore.data().map {
            it[key]?.let { it.objectFromJSON<T>() } ?: kotlin.run { defaultValue }
        }
    }

    inline fun <reified T> getObjectList(key: Preferences.Key<String>): Flowable<List<T>> {
        return dataStore.data().map {
            it[key]?.let {
                it.listFromJSON()
            } ?: kotlin.run { listOf() }
        }
    }

    private fun <T> T.toJSONString(): String {
        return Gson().toJson(this)
    }

    inline fun <reified T> String.objectFromJSON(): T? {
        return try {
            Gson().fromJson(this, T::class.java)
        } catch (ex: NullPointerException) {
            null
        }
    }

    inline fun <reified T> String.listFromJSON(): List<T> {
        return try {
            Gson().fromJson(this, TypeToken.getParameterized(List::class.java, T::class.java).type)
        } catch (ex: NullPointerException) {
            listOf()
        }
    }

}


