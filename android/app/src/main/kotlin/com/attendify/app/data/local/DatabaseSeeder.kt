package com.attendify.app.data.local

import com.attendify.app.data.model.User
import com.attendify.app.data.model.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds the database with initial data on first run
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val database: AttendifyDatabase
) {
    fun seedDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val userDao = database.userDao()
            if (userDao.getUserCount() == 0) {
                // Add default users
                userDao.insertUser(User(
                    id = "1",
                    username = "admin",
                    email = "admin@attendify.com",
                    firstName = "Admin",
                    lastName = "User",
                    role = "admin",
                    profileImageUrl = null,
                    studentId = null,
                    department = null
                ).toEntity())

                userDao.insertUser(User(
                    id = "2",
                    username = "instructor1",
                    email = "instructor1@attendify.com",
                    firstName = "John",
                    lastName = "Doe",
                    role = "instructor",
                    department = "Computer Science",
                    profileImageUrl = null,
                    studentId = null
                ).toEntity())

                userDao.insertUser(User(
                    id = "3",
                    username = "student1",
                    email = "student1@attendify.com",
                    firstName = "Jane",
                    lastName = "Smith",
                    role = "student",
                    studentId = "S001",
                    department = "Computer Science",
                    profileImageUrl = null
                ).toEntity())
            }
        }
    }
}
