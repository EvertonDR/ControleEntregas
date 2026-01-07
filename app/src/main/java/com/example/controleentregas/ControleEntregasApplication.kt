package com.example.controleentregas

import android.app.Application
import android.content.Context
import com.example.controleentregas.data.AppDatabase
import com.example.controleentregas.data.EntregasRepository

/**
 * App container for holding and creating dependencies
 */
interface AppContainer {
    val entregasRepository: EntregasRepository
}

/**
 * Default implementation of AppContainer
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    override val entregasRepository: EntregasRepository by lazy {
        val database = AppDatabase.getDatabase(context)
        EntregasRepository(
            database.clienteDao(),
            database.bairroDao(),
            database.entregaDao(),
            database.custoDao()
        )
    }
}

/**
 * Custom Application class for the app.
 */
class ControleEntregasApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
