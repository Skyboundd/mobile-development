package com.skybound.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skybound.data.local.entity.RoadmapEntity
import com.skybound.data.local.entity.UserEntity
import com.skybound.data.remote.response.RoadmapResponse
import com.skybound.data.remote.response.UserResponse
import com.skybound.data.remote.response.UserStatusResponse
import com.skybound.data.user.User
import com.skybound.data.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<UserResponse>()
    val user: LiveData<UserResponse> get() = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getSession(): Flow<User> {
        return userRepository.getSession()
    }

    fun mergeAndSaveUserLocally(newUser: UserEntity) {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getUserFromDatabase(newUser.id)
                val mergedUser = currentUser?.copy(
                    username = if (newUser.username.isNotBlank()) newUser.username else currentUser.username,
                    email = if (newUser.email.isNotBlank()) newUser.email else currentUser.email,
                    phoneNumber = if (newUser.phoneNumber.isNotBlank()) newUser.phoneNumber else currentUser.phoneNumber,
                    dateOfBirth = if (newUser.dateOfBirth.isNotBlank()) newUser.dateOfBirth else currentUser.dateOfBirth,
                    userStreak = if (newUser.userStreak != 0) newUser.userStreak else currentUser.userStreak,
                    userPercentage = if (newUser.userPercentage != 0) newUser.userPercentage else currentUser.userPercentage,
                    gender = if (newUser.gender.isNotBlank()) newUser.gender else currentUser.gender,
                    userPoint = if (newUser.userPoint != 0) newUser.userPoint else currentUser.userPoint,
                    onCourse = if (newUser.onCourse.isNotBlank()) newUser.onCourse else currentUser.onCourse,
                    courseStatus = if (newUser.courseStatus.isNotBlank()) newUser.courseStatus else currentUser.courseStatus,
                    deadlineLeft = newUser.deadlineLeft ?: currentUser.deadlineLeft,
                    status = if (newUser.status.isNotBlank()) newUser.status else currentUser.status
                ) ?: newUser

                userRepository.saveUserToDatabase(mergedUser)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getUserLocally(userId: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserFromDatabase(userId)
                _user.value = user?.let {
                    UserResponse(
                        username = it.username,
                        userPoint = it.userPoint,
                        email = it.email,
                        phoneNumber = it.phoneNumber,
                        dateOfBirth = it.dateOfBirth,
                        gender = it.gender,
                        status = it.courseStatus,
                        roadmaps = emptyList()
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun fetchUser(token: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(token)
                _user.value = user
            } catch (e: Exception) {
                _error.value = "Failed Fetch, Try Access local data."
                try {
                    val localUserWithRoadmaps = userRepository.getUserWithRoadmapsFromDatabase("user_id_example")
                    _user.value = localUserWithRoadmaps?.let {
                        UserResponse(
                            username = it.user.username,
                            email = it.user.email,
                            status = it.user.status,
                            gender = it.user.gender,
                            phoneNumber = it.user.phoneNumber,
                            dateOfBirth = it.user.dateOfBirth,
                            userPoint = it.user.userPoint,
                            roadmaps = it.roadmaps.map { roadmap ->
                                RoadmapResponse(
                                    id = roadmap.id,
                                    roadmapName = roadmap.roadmapName,
                                    addedAt = roadmap.addedAt
                                )
                            }
                        )
                    }
                } catch (localError: Exception) {
                    _error.value = "Gagal memuat data lokal: ${localError.message}"
                }
            }
        }
    }




    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
