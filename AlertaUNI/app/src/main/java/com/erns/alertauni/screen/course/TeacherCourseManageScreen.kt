package com.erns.alertauni.screen.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erns.alertauni.screen.common.SearchBoxComponent
import com.erns.alertauni.ui.theme.AlertaUNITheme
import com.erns.alertauni.ui.theme.MyMutedForegroundColor
import com.erns.alertauni.ui.theme.MyPrimaryColor
import com.erns.alertauni.ui.theme.MyPrimaryForegroundColor
import com.erns.alertauni.ui.theme.MySurfaceColor

// Datos que necesita cada curso del docente
data class TeacherCourseInfo(
    val courseCatalogId: String,
    val courseCode: String,
    val courseName: String,
    val courseType: String,
    val groupType: String,
    val classCode: String,
    val classCodeEnabled: Boolean
)

// Pantalla completa del docente para gestionar codigos
@Composable
fun TeacherCourseManageLayout(
    username: String,
    title: String,
    courses: List<TeacherCourseInfo>,
    onToggleCode: (String, Boolean) -> Unit,
    onRegenerateCode: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBoxComponent(username, title)
        LazyColumn {
            items(courses) { course ->
                TeacherCourseCard(
                    course = course,
                    onToggleCode = onToggleCode,
                    onRegenerateCode = onRegenerateCode
                )
            }
        }
    }
}

// Card de un curso con su codigo y controles
@Composable
fun TeacherCourseCard(
    course: TeacherCourseInfo,
    onToggleCode: (String, Boolean) -> Unit,
    onRegenerateCode: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MySurfaceColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Nombre y codigo del curso
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = course.courseCode,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = course.courseName.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tipo y grupo
            Text(
                text = "Tipo ${course.courseType} - Grupo ${course.groupType}",
                color = MyMutedForegroundColor,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Codigo de clase (lo que comparten con los estudiantes)
            Text(
                text = "Código de clase:",
                fontSize = 13.sp,
                color = MyMutedForegroundColor
            )
            Text(
                text = course.classCode,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (course.classCodeEnabled) MyPrimaryColor else Color.Gray,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Controles: switch para habilitar/deshabilitar + boton regenerar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Switch para habilitar o deshabilitar el codigo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = course.classCodeEnabled,
                        onCheckedChange = { nuevoValor ->
                            onToggleCode(course.courseCatalogId, nuevoValor)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MyPrimaryForegroundColor,
                            checkedTrackColor = MyPrimaryColor
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (course.classCodeEnabled) "Habilitado" else "Deshabilitado",
                        fontSize = 14.sp,
                        color = if (course.classCodeEnabled) MyPrimaryColor else Color.Gray
                    )
                }

                // Boton para regenerar el codigo
                Button(
                    onClick = { onRegenerateCode(course.courseCatalogId) },
                    colors = ButtonDefaults.buttonColors(containerColor = MyPrimaryColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerar",
                        tint = MyPrimaryForegroundColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Regenerar",
                        color = MyPrimaryForegroundColor,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}


// Datos de ejemplo para los previews

private val cursosDocenteEjemplo = listOf(
    TeacherCourseInfo(
        courseCatalogId = "1",
        courseCode = "1702128",
        courseName = "Nuevas Plataformas",
        courseType = "E",
        groupType = "A",
        classCode = "NP2025A",
        classCodeEnabled = true
    ),
    TeacherCourseInfo(
        courseCatalogId = "2",
        courseCode = "1703201",
        courseName = "Base de Datos II",
        courseType = "T",
        groupType = "B",
        classCode = "BD2025B",
        classCodeEnabled = false
    )
)

// Previews

@Preview(name = "01 - Lista cursos docente", showBackground = true)
@Composable
fun TeacherCourseManagePreview() {
    AlertaUNITheme(dynamicColor = false) {
        TeacherCourseManageLayout(
            username = "Ernesto Suárez",
            title = "Mis Cursos",
            courses = cursosDocenteEjemplo,
            onToggleCode = { _, _ -> },
            onRegenerateCode = {}
        )
    }
}

@Preview(name = "02 - Card codigo habilitado", showBackground = true)
@Composable
fun TeacherCourseCardEnabledPreview() {
    AlertaUNITheme(dynamicColor = false) {
        TeacherCourseCard(
            course = cursosDocenteEjemplo[0], // codigo habilitado
            onToggleCode = { _, _ -> },
            onRegenerateCode = {}
        )
    }
}

@Preview(name = "03 - Card codigo deshabilitado", showBackground = true)
@Composable
fun TeacherCourseCardDisabledPreview() {
    AlertaUNITheme(dynamicColor = false) {
        TeacherCourseCard(
            course = cursosDocenteEjemplo[1], // codigo deshabilitado
            onToggleCode = { _, _ -> },
            onRegenerateCode = {}
        )
    }
}

@Preview(name = "04 - Lista vacia", showBackground = true)
@Composable
fun TeacherCourseManageEmptyPreview() {
    AlertaUNITheme(dynamicColor = false) {
        TeacherCourseManageLayout(
            username = "Ernesto Suárez",
            title = "Mis Cursos",
            courses = emptyList(),
            onToggleCode = { _, _ -> },
            onRegenerateCode = {}
        )
    }
}
