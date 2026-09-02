package com.sovero.biblioteca

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class Prestamo(
    val titulo: String,
    val tipoUsuario: String,
    val fechaPrestamo: LocalDate,
    val fechaEntrega: LocalDate,
    val fechaDevolucion: LocalDate
)

val formatoFecha: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun main() {
    println("========================================")
    println("   SISTEMA DE PRESTAMOS - BIBLIOTECA    ")
    println("========================================")
    println()

    val titulo = leerTexto("Titulo del libro: ")
    val tipoUsuario = leerTipoUsuario()

    println()
    println("Fecha de prestamo:")
    val fechaPrestamo = leerFecha()

    println()
    println("Fecha de entrega pactada:")
    val fechaEntrega = leerFecha()

    println()
    println("Fecha de devolucion (real):")
    val fechaDevolucion = leerFecha()

    val prestamo = Prestamo(titulo, tipoUsuario, fechaPrestamo, fechaEntrega, fechaDevolucion)

    println()
    mostrarResumen(prestamo)
    mostrarDetalleMultasPorDia(prestamo)
}

fun leerTexto(mensaje: String): String {
    print(mensaje)
    return readLine()?.trim() ?: ""
}

fun leerEntero(mensaje: String): Int {
    while (true) {
        print(mensaje)
        val numero = readLine()?.trim()?.toIntOrNull()
        if (numero != null) {
            return numero
        }
        println("Valor invalido, ingresa un numero.")
    }
}

fun leerTipoUsuario(): String {
    while (true) {
        println("Tipo de usuario:")
        println("1. Alumno")
        println("2. Maestro")
        val opcion = leerEntero("Elige una opcion (1 o 2): ")
        when (opcion) {
            1 -> return "Alumno"
            2 -> return "Maestro"
            else -> println("Opcion invalida, intenta de nuevo.")
        }
    }
}

fun leerFecha(): LocalDate {
    while (true) {
        val dia = leerEntero("Dia: ")
        val mes = leerEntero("Mes: ")
        val anio = leerEntero("Anio: ")
        try {
            return LocalDate.of(anio, mes, dia)
        } catch (e: Exception) {
            println("Fecha invalida, intenta de nuevo.")
        }
    }
}

fun diasDeAtraso(fechaEntrega: LocalDate, fechaDevolucion: LocalDate): Long {
    val dias = ChronoUnit.DAYS.between(fechaEntrega, fechaDevolucion)
    return if (dias > 0) dias else 0
}

fun tarifaMoraPorDia(tipoUsuario: String): Double {
    return if (tipoUsuario == "Alumno") 1.5 else 3.0
}

fun mostrarResumen(prestamo: Prestamo) {
    val dias = diasDeAtraso(prestamo.fechaEntrega, prestamo.fechaDevolucion)
    val estado = if (dias > 0) {
        "Devuelto con $dias dias de retraso"
    } else {
        "Devuelto a tiempo"
    }

    println("========================================")
    println("           RESUMEN DEL PRESTAMO")
    println("========================================")
    println("Titulo del libro : ${prestamo.titulo}")
    println("Tipo de usuario  : ${prestamo.tipoUsuario.uppercase()}")
    println("Fecha prestamo   : ${prestamo.fechaPrestamo.format(formatoFecha)}")
    println("Fecha entrega    : ${prestamo.fechaEntrega.format(formatoFecha)}")
    println("Fecha devolucion : ${prestamo.fechaDevolucion.format(formatoFecha)}")
    println("Estado           : $estado")
    println("========================================")
}

fun mostrarDetalleMultasPorDia(prestamo: Prestamo) {
    val dias = diasDeAtraso(prestamo.fechaEntrega, prestamo.fechaDevolucion)
    val tarifa = tarifaMoraPorDia(prestamo.tipoUsuario)

    println()
    println("--- DETALLE DE MULTAS POR DIA ---")

    if (dias == 0L) {
        println("No hay multas, el libro se devolvio a tiempo.")
        return
    }

    println(String.format("%-4s | %-12s | %-9s | %-10s", "Dia", "Fecha", "Multa", "Acumulado"))
    println("---------------------------------------------")

    var acumulado = 0.0
    for (dia in 1L..dias) {
        val fechaDelDia = prestamo.fechaEntrega.plusDays(dia)
        acumulado += tarifa
        println(
            String.format(
                "%-4d | %-12s | S/%7.2f | S/%8.2f",
                dia,
                fechaDelDia.format(formatoFecha),
                tarifa,
                acumulado
            )
        )
    }
    println("---------------------------------------------")
    println(String.format("MULTA TOTAL A PAGAR: S/ %.2f", acumulado))
}
