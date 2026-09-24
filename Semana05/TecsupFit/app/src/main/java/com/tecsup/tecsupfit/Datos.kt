package com.tecsup.tecsupfit

data class ClaseGym(
    val id: Int,
    val nombre: String,
    val horario: String,
    val periodo: String,
    val horarioDisponibles: List<String>,
    val sala: String,
    val duracion: String,
    val descripcion: String,
    val cuposDisponibles: Int,
    val cuposTotales: Int
)

data class Reserva(
    val clase: ClaseGym,
    val hora: String,
    val estado: String
)

val listaClases = listOf(
    ClaseGym(
        id = 1,
        nombre = "Yoga",
        horario = "Lunes a Viernes",
        periodo = "Hoy",
        horarioDisponibles = listOf("8:00 am", "4:00 pm", "9:00 pm"),
        sala = "Sala 1",
        duracion = "45 min",
        descripcion = "Clase de yoga para relajar cuerpo y mente, mejorar la flexibilidad y reducir el estrés.",
        cuposDisponibles = 8,
        cuposTotales = 12
    ),
    ClaseGym(
        id = 2,
        nombre = "Calistenia y entrenamiento funcional",
        horario = "Lunes y Sabado",
        periodo = "Esta semana",
        horarioDisponibles = listOf("9:30 am", "5:30 pm", "8:00 pm"),
        sala = "Sala 2",
        duracion = "60 min",
        descripcion = "Entrenamiento con peso corporal para desarrollar fuerza, agilidad y resistencia muscular.",
        cuposDisponibles = 5,
        cuposTotales = 15
    ),
    ClaseGym(
        id = 3,
        nombre = "Entrenamiento de Fuerza y Resistencia",
        horario = "Lunes a Miercoles",
        periodo = "Esta semana",
        horarioDisponibles = listOf("9:00 am", "6:00 pm", "10:00 pm"),
        sala = "Sala 3",
        duracion = "50 min",
        descripcion = "Rutina enfocada en el aumento de masa muscular y tonificación general con pesas.",
        cuposDisponibles = 10,
        cuposTotales = 20
    )
)
