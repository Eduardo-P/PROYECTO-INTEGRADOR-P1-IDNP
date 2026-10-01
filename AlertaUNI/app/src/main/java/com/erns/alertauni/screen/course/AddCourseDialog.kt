package com.erns.alertauni.screen.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.erns.alertauni.data.model.StudentEnrollment
import com.erns.alertauni.ui.theme.AlertaUNITheme
import com.erns.alertauni.ui.theme.MyMutedForegroundColor
import com.erns.alertauni.ui.theme.MyPrimaryColor
import com.erns.alertauni.ui.theme.MyPrimaryForegroundColor
import com.erns.alertauni.ui.theme.MySurfaceColor


@Composable
fun AddCourseDialog(
    uiState: EnrollUiState,
    onDismiss: () -> Unit,
    onClickFindCourse: (String) -> Unit,
    onClickCourseEnroll: (String, String) -> Unit
) {
    val inputText = remember { mutableStateOf("") }

    // Sacamos el enrollment si el estado es Found
    val enrollment = if (uiState is EnrollUiState.Found) uiState.enrollment else null

    // Verificar si esta buscando o registrando para deshabilitar cosas
    val estaBuscando = uiState is EnrollUiState.Searching
    val estaRegistrando = uiState is EnrollUiState.Enrolling

    // Armar el nombre del docente
    val profesor = if (enrollment != null) {
        enrollment.firstname + " " + enrollment.surname
    } else {
        "Docente"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MySurfaceColor,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Agregar Curso",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MyPrimaryColor
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MyMutedForegroundColor
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Campo para escribir el codigo
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = inputText.value,
                    onValueChange = { inputText.value = it },
                    placeholder = { Text("Código ...") },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    enabled = !estaBuscando && !estaRegistrando,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        // Si esta buscando mostramos el spinner, sino el icono de buscar
                        if (estaBuscando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    if (inputText.value.isNotBlank()) {
                                        onClickFindCourse(inputText.value)
                                    }
                                },
                                enabled = !estaRegistrando
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )

                // Mensajes de error segun el estado
                if (uiState is EnrollUiState.NotFound) {
                    Text(
                        text = "Código no encontrado o deshabilitado. Verifica con tu docente.",
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                if (uiState is EnrollUiState.AlreadyEnrolled) {
                    Text(
                        text = "Ya estás inscrito en este curso.",
                        color = Color(0xFFFF9800), // naranja
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                if (uiState is EnrollUiState.Error) {
                    Text(
                        text = uiState.message,
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Informacion del curso (o placeholders si no se ha buscado)
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = enrollment?.courseCode ?: "Código",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (enrollment != null) Color.DarkGray else MyMutedForegroundColor
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = enrollment?.courseName ?: "Curso",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (enrollment != null) Color.DarkGray else MyMutedForegroundColor
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = "Tipo ${enrollment?.courseType ?: ""}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (enrollment != null) Color.DarkGray else MyMutedForegroundColor
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = "Grupo ${enrollment?.groupType ?: ""}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (enrollment != null) Color.DarkGray else MyMutedForegroundColor
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = profesor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (enrollment != null) Color.DarkGray else MyMutedForegroundColor
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    enabled = !estaBuscando && !estaRegistrando,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MyPrimaryColor
                    )
                ) {
                    Text("Cancelar", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        if (enrollment != null) {
                            onClickCourseEnroll(
                                enrollment.course_catalog_id,
                                enrollment.courseName
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    enabled = enrollment != null,
                    colors = ButtonDefaults.buttonColors(containerColor = MyPrimaryColor),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    // Si esta registrando mostramos spinner en el boton
                    if (estaRegistrando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MyPrimaryForegroundColor
                        )
                    } else {
                        Text(
                            "Registrarme",
                            fontWeight = FontWeight.SemiBold,
                            color = MyPrimaryForegroundColor
                        )
                    }
                }
            }
        },
        dismissButton = {}
    )
}


// =====================================================
// Previews para visualizar cada estado del dialogo
// =====================================================

// Datos de ejemplo para los previews
private val cursoEjemplo = StudentEnrollment(
    course_catalog_id = "1", courseId = "101", courseCode = "1702128",
    courseName = "Intro. Desarrollo Nuevas Plataformas", semester = "2025-B",
    courseType = "E", groupType = "A",
    firstname = "Ernesto", surname = "Suárez López",
    email = "esuarez@unsa.edu.pe"
)

@Preview(name = "01 - Idle (estado inicial)", showBackground = true)
@Composable
fun PreviewIdle() {
    AlertaUNITheme(dynamicColor = false) {
        AddCourseDialog(
            uiState = EnrollUiState.Idle,
            onDismiss = {},
            onClickFindCourse = {},
            onClickCourseEnroll = { _, _ -> }
        )
    }
}

@Preview(name = "02 - Searching (buscando)", showBackground = true)
@Composable
fun PreviewSearching() {
    AlertaUNITheme(dynamicColor = false) {
        AddCourseDialog(
            uiState = EnrollUiState.Searching,
            onDismiss = {},
            onClickFindCourse = {},
            onClickCourseEnroll = { _, _ -> }
        )
    }
}

@Preview(name = "03 - Found (curso encontrado)", showBackground = true)
@Composable
fun PreviewFound() {
    AlertaUNITheme(dynamicColor = false) {
        AddCourseDialog(
            uiState = EnrollUiState.Found(cursoEjemplo),
            onDismiss = {},
            onClickFindCourse = {},
            onClickCourseEnroll = { _, _ -> }
        )
    }
}

@Preview(name = "04 - NotFound (codigo invalido)", showBackground = true)
@Composable
fun PreviewNotFound() {
    AlertaUNITheme(dynamicColor = false) {
        AddCourseDialog(
            uiState = EnrollUiState.NotFound,
            onDismiss = {},
            onClickFindCourse = {},
            onClickCourseEnroll = { _, _ -> }
        )
    }
}

@Preview(name = "05 - Enrolling (registrando)", showBackground = true)
@Composable
fun PreviewEnrolling() {
    AlertaUNITheme(dynamicColor = false) {
        AddCourseDialog(
            uiState = EnrollUiState.Enrolling,
            onDismiss = {},
            onClickFindCourse = {},
            onClickCourseEnroll = { _, _ -> }
        )
    }
}

@Preview(name = "06 - AlreadyEnrolled (ya inscrito)", showBackground = true)
@Composable
fun PreviewAlreadyEnrolled() {
    AlertaUNITheme(dynamicColor = false) {
        AddCourseDialog(
            uiState = EnrollUiState.AlreadyEnrolled,
            onDismiss = {},
            onClickFindCourse = {},
            onClickCourseEnroll = { _, _ -> }
        )
    }
}

@Preview(name = "07 - Error (error generico)", showBackground = true)
@Composable
fun PreviewError() {
    AlertaUNITheme(dynamicColor = false) {
        AddCourseDialog(
            uiState = EnrollUiState.Error("No se pudo completar el registro"),
            onDismiss = {},
            onClickFindCourse = {},
            onClickCourseEnroll = { _, _ -> }
        )
    }
}


// =====================================================
// Dialogo de registro exitoso
// =====================================================

@Composable
fun EnrollSuccessDialog(
    courseName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MySurfaceColor,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Éxito",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "¡Registro exitoso!",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MyPrimaryColor
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Te registraste en:",
                    fontSize = 14.sp,
                    color = MyMutedForegroundColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = courseName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MyPrimaryColor),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    "Continuar",
                    fontWeight = FontWeight.SemiBold,
                    color = MyPrimaryForegroundColor
                )
            }
        }
    )
}

@Preview(name = "08 - Enrolled (registro exitoso)", showBackground = true)
@Composable
fun PreviewEnrollSuccess() {
    AlertaUNITheme(dynamicColor = false) {
        EnrollSuccessDialog(
            courseName = "Intro. Desarrollo Nuevas Plataformas",
            onDismiss = {}
        )
    }
}