package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.QuizQuestion
import com.example.data.local.entities.StudyPlanItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildStudyDao {
    @Query("SELECT * FROM study_plans WHERE userId = :userId ORDER BY targetDate ASC, id ASC")
    fun getStudyPlans(userId: Long): Flow<List<StudyPlanItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyPlan(item: StudyPlanItem): Long

    @Update
    suspend fun updateStudyPlan(item: StudyPlanItem)

    @Delete
    suspend fun deleteStudyPlan(item: StudyPlanItem)

    @Query("UPDATE study_plans SET isCompleted = :completed WHERE id = :id")
    suspend fun updatePlanCompletion(id: Long, completed: Boolean)

    // Quiz Questions
    @Query("SELECT * FROM quiz_questions WHERE subject = :subject")
    fun getQuizQuestionsBySubject(subject: String): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions")
    fun getAllQuizQuestions(): Flow<List<QuizQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestion(question: QuizQuestion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuizQuestions(questions: List<QuizQuestion>)
}
