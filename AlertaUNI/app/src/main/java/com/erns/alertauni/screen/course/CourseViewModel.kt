package com.erns.alertauni.screen.course

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erns.alertauni.data.model.ClassCodeRequest
import com.erns.alertauni.data.model.CourseEnrollRequest
import com.erns.alertauni.data.model.StudentEnrollment
import com.erns.alertauni.data.repository.StudentRepository
import com.erns.alertauni.domain.manager.DataStoreHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val studentRepository: StudentRepository,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {
    private val TAG = "CourseViewModel"

    // Estado de la UI para el proceso de inscripcion (ahora con 8 estados)
    private val _enrollUiState = MutableStateFlow<EnrollUiState>(EnrollUiState.Idle)
    val enrollUiState: StateFlow<EnrollUiState> = _enrollUiState.asStateFlow()

    // Lista de cursos en los que ya esta inscrito
    private val _studentEnrollmentList = MutableStateFlow<List<StudentEnrollment>>(emptyList())
    val studentEnrollmentList: StateFlow<List<StudentEnrollment>> =
        _studentEnrollmentList.asStateFlow()

    // Nombre del usuario para mostrar arriba
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    init {
        loadUserInfo()
        getCourses()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            _username.value = dataStoreHelper.getFirstname() + " " + dataStoreHelper.getSurname()
        }
    }

    private fun getCourses() {
        viewModelScope.launch {
            studentRepository.getStudentEnrollment()
                .onSuccess {
                    _studentEnrollmentList.value = it
                }.onFailure {
                    Log.d(TAG, it.message.toString())
                }
        }
    }

    // Busca un curso por codigo alfanumerico
    fun findCourse(classCode: String) {
        // Validar que no este vacio
        if (classCode.isBlank()) {
            _enrollUiState.value = EnrollUiState.Error("Ingrese un código válido")
            return
        }

        _enrollUiState.value = EnrollUiState.Searching
        Log.d(TAG, "Buscando curso con codigo: $classCode")

        viewModelScope.launch {
            studentRepository.findCourse(ClassCodeRequest(classCode = classCode))
                .onSuccess {
                    Log.d(TAG, "Curso encontrado: ${it.courseName}")
                    _enrollUiState.value = EnrollUiState.Found(it)
                }
                .onFailure {
                    Log.d(TAG, "No se encontro: ${it.message}")
                    _enrollUiState.value = EnrollUiState.NotFound
                }
        }
    }

    // Registra al estudiante en el curso
    fun courseEnroll(courseCatalogId: String, courseName: String) {
        _enrollUiState.value = EnrollUiState.Enrolling
        Log.d(TAG, "Registrando en curso: $courseName")

        viewModelScope.launch {
            studentRepository.courseEnroll(CourseEnrollRequest(courseCatalogId = courseCatalogId))
                .onSuccess {
                    Log.d(TAG, "Registro exitoso: ${it.created}")
                    getCourses()
                    _enrollUiState.value = EnrollUiState.Enrolled(courseName)
                }
                .onFailure {
                    Log.d(TAG, "Error al registrar: ${it.message}")
                    // Verificar si ya esta registrado
                    if (it.message != null && it.message!!.contains("duplicate", ignoreCase = true)) {
                        _enrollUiState.value = EnrollUiState.AlreadyEnrolled
                    } else {
                        _enrollUiState.value = EnrollUiState.Error("No se pudo completar el registro")
                    }
                }
        }
    }

    // Reiniciar el estado cuando se cierra el dialogo
    fun resetEnrollState() {
        _enrollUiState.value = EnrollUiState.Idle
    }
}