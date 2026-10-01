package com.erns.alertauni.screen.course

import com.erns.alertauni.data.model.StudentEnrollment

// Estados del proceso de inscripcion a un curso
// Usamos sealed class para que el compilador nos obligue a manejar todos los casos
sealed class EnrollUiState {
    // Estado inicial, el dialogo esta abierto esperando que escriban un codigo
    object Idle : EnrollUiState()

    // Se esta buscando el curso en el backend
    object Searching : EnrollUiState()

    // Se encontro el curso, guardamos los datos para mostrarlos
    data class Found(val enrollment: StudentEnrollment) : EnrollUiState()

    // No se encontro el curso o esta deshabilitado
    object NotFound : EnrollUiState()

    // Se esta enviando la solicitud de inscripcion
    object Enrolling : EnrollUiState()

    // Se registro exitosamente, guardamos el nombre del curso para el mensaje
    data class Enrolled(val courseName: String) : EnrollUiState()

    // El estudiante ya estaba inscrito
    object AlreadyEnrolled : EnrollUiState()

    // Algun error (de red, servidor, etc)
    data class Error(val message: String) : EnrollUiState()
}
