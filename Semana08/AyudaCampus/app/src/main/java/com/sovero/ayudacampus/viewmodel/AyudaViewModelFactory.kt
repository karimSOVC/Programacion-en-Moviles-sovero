package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sovero.ayudacampus.data.repository.AyudaRepository

// Crea los ViewModels pasandoles el repository
class AyudaViewModelFactory(private val repo: AyudaRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriasViewModel::class.java)) {
            return CategoriasViewModel(repo) as T
        }

        // TODO: agrega aqui cada ViewModel nuevo que crees
        // (Sesion, MisTickets, NuevoTicket, TicketDetalle, Panel, Bandeja, Atender)

        throw IllegalArgumentException("ViewModel desconocido: " + modelClass.name)
    }
}
