package com.tecsup.navegacion_sovero.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class StudentDetail(
    val id: Int,
    val name: String,
    val career: String,
    val code: String,
    val email: String,
    val faculty: String,
    val biography: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, itemId: Int) {
    val students = listOf(
        StudentDetail(
            id = 1,
            name = "Juan León",
            career = "Ingeniería de Sistemas",
            code = "2024-0001",
            email = "juan.leon@tecsup.edu.pe",
            faculty = "Ingeniería y Tecnología",
            biography = "Estudiante de quinto ciclo apasionado por el desarrollo móvil, la arquitectura de software y las soluciones basadas en inteligencia artificial."
        ),
        StudentDetail(
            id = 2,
            name = "María García",
            career = "Arquitectura",
            code = "2024-0002",
            email = "maria.garcia@tecsup.edu.pe",
            faculty = "Arquitectura y Diseño",
            biography = "Especialista en diseño bioclimático, modelado 3D interactivo e infraestructura urbana sostenible."
        ),
        StudentDetail(
            id = 3,
            name = "Carlos Pérez",
            career = "Medicina",
            code = "2024-0003",
            email = "carlos.perez@tecsup.edu.pe",
            faculty = "Ciencias de la Salud",
            biography = "Enfocado en telemedicina, investigación clínica e innovación tecnológica aplicada al diagnóstico temprano."
        ),
        StudentDetail(
            id = 4,
            name = "Ana López",
            career = "Derecho",
            code = "2024-0004",
            email = "ana.lopez@tecsup.edu.pe",
            faculty = "Derecho y Ciencias Políticas",
            biography = "Interesada en derecho corporativo digital, regulaciones de ciberseguridad y propiedad intelectual de software."
        ),
        StudentDetail(
            id = 5,
            name = "Luis Ramírez",
            career = "Administración",
            code = "2024-0005",
            email = "luis.ramirez@tecsup.edu.pe",
            faculty = "Negocios y Gestión",
            biography = "Líder de proyectos estudiantiles en transformación digital, gestión de datos financieros y emprendimiento empresarial."
        ),
        StudentDetail(
            id = 6,
            name = "Sofía Torres",
            career = "Diseño Gráfico",
            code = "2024-0006",
            email = "sofia.torres@tecsup.edu.pe",
            faculty = "Diseño y Comunicación",
            biography = "Creadora visual centrada en experiencia de usuario (UI/UX), diseño tipográfico y branding para marcas digitales."
        ),
        StudentDetail(
            id = 7,
            name = "Diego Morales",
            career = "Marketing Digital",
            code = "2024-0007",
            email = "diego.morales@tecsup.edu.pe",
            faculty = "Marketing y Comunicaciones",
            biography = "Especialista en estrategia de contenidos, analítica de métricas web y posicionamiento orgánico SEO en redes."
        ),
        StudentDetail(
            id = 8,
            name = "Elena Mendoza",
            career = "Contabilidad y Finanzas",
            code = "2024-0008",
            email = "elena.mendoza@tecsup.edu.pe",
            faculty = "Finanzas y Contabilidad",
            biography = "Apasionada por la analítica contable, tecnologías fintech y optimización de presupuestos corporativos."
        )
    )

    // Selección segura del estudiante correspondiente según itemId
    val safeIndex = (itemId - 1).coerceIn(0, students.size - 1)
    val student = students.getOrElse(safeIndex) { students.first() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expediente Académico") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera con degradado y foto superpuesta
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Fondo con degradado usando colores del tema
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                )

                // Foto de perfil circular superpuesta
                Surface(
                    modifier = Modifier
                        .size(90.dp)
                        .offset(y = 45.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 6.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Foto de perfil",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(52.dp))

            // Nombre y Carrera
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = student.career,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta con información académica (ID, Correo, Facultad)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    DetailRow(
                        icon = Icons.Default.AccountBox,
                        label = "ID Estudiante",
                        value = student.code
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    DetailRow(
                        icon = Icons.Default.Email,
                        label = "Correo Electrónico",
                        value = student.email
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    DetailRow(
                        icon = Icons.Default.Home,
                        label = "Facultad",
                        value = student.faculty
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Secciones adicionales: Biografía
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Biografía",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = student.biography,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
