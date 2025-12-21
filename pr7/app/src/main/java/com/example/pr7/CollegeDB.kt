package com.example.pr7

import android.content.Context
import androidx.room.*

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupID: Int = 0,
    val course: Int,
    val specializationID: Int = 0,
    val fullName: String,
    val isBuget: Boolean,
    val dataID: Int = 0,
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val totalHours: Int = 0,
    val specializationID: Int = 0,
    val dataID: Int = 0,
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val login: String,
    val password: String,
    val role: String,
)
@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupName: String,
    val count_student: Int = 0,
)
@Entity(tableName = "specializations")
data class Specialization(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val specializationName: String,
    val specializationCode: String,
    val count_student: Int = 0,
)
@Dao
interface SpecializationDao {
    @Insert
    suspend fun add(specialization: Specialization): Long

    @Update
    suspend fun update(specialization: Specialization)

    @Query("SELECT * FROM `specializations`")
    suspend fun getAll(): List<Specialization>

    @Query("SELECT * FROM `specializations` WHERE specializationName = :specializationName")
    suspend fun getSpecByName(specializationName: String): Specialization?
}

@Dao
interface GroupDao {
    @Insert
    suspend fun add(group: Group): Long

    @Update
    suspend fun update(group: Group)

    @Query("SELECT * FROM `groups`")
    suspend fun getAll(): List<Group>

    @Query("SELECT * FROM `groups` WHERE groupName = :groupName")
    suspend fun getGroupByName(groupName: String): Group?
}
@Dao
interface UserDao {
    @Insert
    suspend fun add(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM USERS")
    suspend fun getAll(): MutableList<User>

    @Delete
    suspend fun delete(user: User)
}
@Dao
interface StudentDao {
    @Insert
    suspend fun add(student: Student)

    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM students")
    suspend fun getAll(): List<Student>

    @Query("SELECT * FROM students WHERE fullName LIKE '%' || :searchQuery || '%'")
    suspend fun searchByName(searchQuery: String): List<Student>
}

@Dao
interface TeacherDao {
    @Insert
    suspend fun add(teacher: Teacher)

    @Update
    suspend fun update(teacher: Teacher)

    @Delete
    suspend fun delete(teacher: Teacher)

    @Query("SELECT * FROM teachers")
    suspend fun getAll(): List<Teacher>

    @Query("SELECT * FROM teachers WHERE fullName LIKE '%' || :query || '%'")
    suspend fun searchByName(query: String): List<Teacher>
}

@Database(entities = [Student::class, Teacher::class, User::class, Group::class, Specialization::class], version = 5)
abstract class CollegeDB : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun specializationDao(): SpecializationDao

    companion object {
        @Volatile
        private var instance: CollegeDB? = null

        fun getInstance(context: Context): CollegeDB {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    CollegeDB::class.java,
                    "college.db"
                ).build().also { instance = it }
            }
        }
    }
}