package com.attendify.app.ui.instructor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attendify.app.data.model.User
import com.attendify.app.data.model.toModel
import com.attendify.app.data.repository.EnrollmentRepository
import com.attendify.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing student enrollments
 */
@HiltViewModel
class EnrollmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val enrollmentRepository: EnrollmentRepository
) : ViewModel() {
    
    private val _allStudents = MutableStateFlow<List<User>>(emptyList())
    val allStudents: StateFlow<List<User>> = _allStudents.asStateFlow()
    
    private val _enrolledStudents = MutableStateFlow<List<User>>(emptyList())
    val enrolledStudents: StateFlow<List<User>> = _enrolledStudents.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    
    fun loadStudentsForCourse(courseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            
            try {
                // Load all students
                userRepository.getUsersByRole("student").collect { students ->
                    _allStudents.value = students.map { it.toModel() }
                }
                
                // Load enrolled students for this course
                val enrollments = enrollmentRepository.getEnrollmentsByCourse(courseId).first()
                val enrolledIds = enrollments.map { it.studentId }.toSet()
                
                _enrolledStudents.value = _allStudents.value.filter { it.id in enrolledIds }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun enrollStudent(courseId: String, studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            
            try {
                val result = enrollmentRepository.enrollStudent(courseId, studentId)
                if (result.isSuccess) {
                    _message.value = "Student enrolled successfully"
                    loadStudentsForCourse(courseId) // Refresh
                } else {
                    _message.value = "Error: ${result.exceptionOrNull()?.message}"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun unenrollStudent(courseId: String, studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            
            try {
                val result = enrollmentRepository.unenrollStudent(courseId, studentId)
                if (result.isSuccess) {
                    _message.value = "Student unenrolled successfully"
                    loadStudentsForCourse(courseId) // Refresh
                } else {
                    _message.value = "Error: ${result.exceptionOrNull()?.message}"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    fun clearMessage() {
        _message.value = null
    }
}
