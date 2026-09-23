package com.tecsup.tecsupfit

data class ClaseGym(
    val id: Int,
    val nombre: String,
    val horario: String,
    val periodo: String,
    val horarioDisponibles: List<String>
)

val listaClases = listOf(
    ClaseGym(
        id = 1,
        nombre = "Yoga",
        horario = "Lunes a Viernes",
        periodo = "Hoy",
        horarioDisponibles = listOf("8:00 am", "4:00 pm", "9:00 pm")
    ),
    ClaseGym(
        id = 2,
        nombre = "Calistenia y entrenamiento funcional",
        horario = "Lunes y Sabado",
        periodo = "Esta semana",
        horarioDisponibles = listOf("9:30 am", "5:30 pm", "8:00 pm")
    ),
    ClaseGym(
        id = 3,
        nombre = "Entrenamiento de Fuerza y Resistencia",
        horario = "Lunes a Miercoles",
        periodo = "Esta semana",
        horarioDisponibles = listOf("9:00 am", "6:00 pm", "10:00 pm")
    )
)