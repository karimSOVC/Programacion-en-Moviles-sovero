package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sovero.ayudacampus.data.prefs.PreferenciasStore
import com.sovero.ayudacampus.data.repository.AyudaRepository

// Crea los ViewModels pasandoles el repository (y las preferencias si las necesitan)
class AyudaViewModelFactory(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriasViewModel::class.java)) {
            return CategoriasViewModel(repo) as T
        }
        if (modelClass.isAssignableFrom(SesionViewModel::class.java)) {
            return SesionViewModel(repo, prefs) as T
        }
        if (modelClass.isAssignableFrom(MisTicketsViewModel::class.java)) {
            return MisTicketsViewModel(repo, prefs) as T
        }
        if (modelClass.isAssignableFrom(NuevoTicketViewModel::class.java)) {
            return NuevoTicketViewModel(repo, prefs) as T
        }
        if (modelClass.isAssignableFrom(TicketDetalleViewModel::class.java)) {
            return TicketDetalleViewModel(repo) as T
        }
        if (modelClass.isAssignableFrom(PanelViewModel::class.java)) {
            return PanelViewModel(repo) as T
        }
        if (modelClass.isAssignableFrom(BandejaViewModel::class.java)) {
            return BandejaViewModel(repo) as T
        }
        if (modelClass.isAssignableFrom(AtenderViewModel::class.java)) {
            return AtenderViewModel(repo, prefs) as T
        }

        throw IllegalArgumentException("ViewModel desconocido: " + modelClass.name)
    }
}
