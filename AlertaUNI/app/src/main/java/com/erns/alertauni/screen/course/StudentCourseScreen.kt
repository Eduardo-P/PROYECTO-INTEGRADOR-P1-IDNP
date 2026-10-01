package com.erns.alertauni.screen.course


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.erns.alertauni.data.model.StudentEnrollment
import com.erns.alertauni.screen.common.SearchBoxComponent
import com.erns.alertauni.ui.theme.AlertaUNITheme
import com.erns.alertauni.ui.theme.MyMutedForegroundColor
import com.erns.alertauni.ui.theme.MySurfaceColor


@Composable
fun StudentCourseScreen(
    viewModel: CourseViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onFabActionReady: (() -> Unit) -> Unit
) {
    val context = LocalContext.current
    val studentEnrollmentListState = viewModel.studentEnrollmentList.collectAsState()
    val enrollUiState = viewModel.enrollUiState.collectAsState()
    val showAddDialog = remember { mutableStateOf(false) }
    val showSuccessDialog = remember { mutableStateOf(false) }
    val successCourseName = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }

    // Cargar nombre del usuario
    LaunchedEffect(Unit) {
        viewModel.username.collect {
            username.value = it
        }
    }

    // Escuchar cambios en el estado de inscripcion
    LaunchedEffect(Unit) {
        viewModel.enrollUiState.collect { state ->
            if (state is EnrollUiState.Enrolled) {
                // Cerrar el dialogo de agregar y mostrar el de exito
                showAddDialog.value = false
                successCourseName.value = state.courseName
                showSuccessDialog.value = true
            }
        }
    }

    // Configurar el boton FAB
    val onClickFloatingActionButton: () -> Unit = {
        showAddDialog.value = true
    }
    onFabActionReady(onClickFloatingActionButton)

    // Mostrar el dialogo de agregar curso
    if (showAddDialog.value) {
        AddCourseDialog(
            uiState = enrollUiState.value,
            onDismiss = {
                showAddDialog.value = false
                viewModel.resetEnrollState()
            },
            onClickFindCourse = { codigo ->
                viewModel.findCourse(codigo)
            },
            onClickCourseEnroll = { id, nombre ->
                viewModel.courseEnroll(id, nombre)
            }
        )
    }

    // Mostrar dialogo de registro exitoso
    if (showSuccessDialog.value) {
        EnrollSuccessDialog(
            courseName = successCourseName.value,
            onDismiss = {
                showSuccessDialog.value = false
                viewModel.resetEnrollState()
            }
        )
    }

    // Pantalla principal con la lista de cursos
    StudentCourseScreenLayout(
        username.value,
        "Cursos",
        studentEnrollmentListState.value
    )
}

@Composable
fun StudentCourseScreenLayout(
    username: String,
    title: String,
    studentEnrollmentList: List<StudentEnrollment>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBoxComponent(username, title)
        LazyColumn {
            items(studentEnrollmentList) { studentEnrollment ->
                StudentEnrollmentCard(studentEnrollment)
            }
        }
    }
}

@Composable
fun StudentEnrollmentCard(
    studentEnrollment: StudentEnrollment
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(8.dp)
            .clickable(onClick = { }),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MySurfaceColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            )
            {
                Text(
                    text = studentEnrollment.courseCode,
                    fontSize = 18.sp,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = studentEnrollment.courseName.uppercase(),
                    fontSize = 18.sp,
                )
            }

            Text(
                text = "Tipo ${studentEnrollment.courseType}",
                color = MyMutedForegroundColor,
                fontSize = 18.sp
            )
            Text(
                text = "Grupo ${studentEnrollment.groupType}",
                color = MyMutedForegroundColor,
                fontSize = 18.sp
            )
            Text(
                text = studentEnrollment.firstname + " " + studentEnrollment.surname,
                color = MyMutedForegroundColor,
                fontSize = 16.sp,
            )
            Text(
                text = studentEnrollment.email,
                color = MyMutedForegroundColor,
                fontSize = 18.sp,
            )
        }
    }
}

// =====================================================
// Previews de la pantalla de cursos
// =====================================================

private val cursosEjemplo = listOf(
    StudentEnrollment(
        course_catalog_id = "1", courseId = "1", courseCode = "1702128",
        courseName = "Nuevas Plataformas", semester = "2025-B",
        courseType = "E", groupType = "A",
        firstname = "Ernesto", surname = "Suárez", email = "esuarez@unsa.edu.pe"
    ),
    StudentEnrollment(
        course_catalog_id = "2", courseId = "2", courseCode = "1703201",
        courseName = "Base de Datos II", semester = "2025-B",
        courseType = "T", groupType = "B",
        firstname = "María", surname = "Quispe", email = "mquispe@unsa.edu.pe"
    )
)

@Preview(name = "Cursos - con datos", showBackground = true)
@Composable
fun CursoListaPreview() {
    AlertaUNITheme(dynamicColor = false) {
        StudentCourseScreenLayout("Juan Pérez", "Cursos", cursosEjemplo)
    }
}

@Preview(name = "Cursos - sin datos", showBackground = true)
@Composable
fun CursoListaVaciaPreview() {
    AlertaUNITheme(dynamicColor = false) {
        StudentCourseScreenLayout("Juan Pérez", "Cursos", emptyList())
    }
}

@Preview(name = "Cursos - pantalla completa con FAB", showBackground = true, showSystemUi = true)
@Composable
fun CursosPantallaCompletaPreview() {
    AlertaUNITheme(dynamicColor = false) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar curso"
                    )
                }
            }
        ) { innerPadding ->
            StudentCourseScreenLayout(
                "Juan Pérez",
                "Cursos",
                cursosEjemplo
            )
        }
    }
}

@Preview(name = "Cursos - despues de registrarse (Enrolled)", showBackground = true, showSystemUi = true)
@Composable
fun CursosPostRegistroPreview() {
    // Lista con el nuevo curso que se acaba de agregar
    val listaActualizada = cursosEjemplo + StudentEnrollment(
        course_catalog_id = "3", courseId = "3", courseCode = "1702130",
        courseName = "Ing. de Software", semester = "2025-B",
        courseType = "T", groupType = "A",
        firstname = "Carlos", surname = "López", email = "clopez@unsa.edu.pe"
    )
    AlertaUNITheme(dynamicColor = false) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(shape = CircleShape, onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar curso")
                }
            }
        ) { innerPadding ->
            StudentCourseScreenLayout(
                "Juan Pérez",
                "Cursos",
                listaActualizada
            )
        }
    }
}
