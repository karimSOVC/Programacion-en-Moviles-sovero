package com.sovero.murocampus.data.remote

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * El cliente de Retrofit. Es un object, así se crea una sola vez
 * para toda la app.
 */
object RetrofitClient {

    // ignoreUnknownKeys: si el JSON trae un campo que el modelo no tiene,
    // lo ignora en vez de fallar
    private val json = Json { ignoreUnknownKeys = true }

    // OkHttp hace la conexión; el interceptor muestra en Logcat (filtro
    // "OkHttp") cada petición y su respuesta. Solo para desarrollo.
    // Mejora (Fase 2): un Interceptor agrega "Accept: application/json" a
    // TODAS las peticiones, así no hay que ponerlo en cada función de PostApi.
    // Va antes del de registro para que el encabezado se vea en Logcat.
    private val encabezadoAccept = Interceptor { chain ->
        val peticion = chain.request().newBuilder()
            .header("Accept", "application/json")
            .build()
        chain.proceed(peticion)
    }

    private val cliente = OkHttpClient.Builder()
        .addInterceptor(encabezadoAccept)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/") // termina con /
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // by lazy: se crea la primera vez que se usa, y solo esa vez
    val api: PostApi by lazy { retrofit.create(PostApi::class.java) }

    // Ejemplo resuelto [E]: la API de usuarios usa el mismo cliente
    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
}
