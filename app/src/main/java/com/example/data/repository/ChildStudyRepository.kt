package com.example.data.repository

import com.example.data.local.dao.ChildStudyDao
import com.example.data.local.entities.QuizQuestion
import com.example.data.local.entities.StudyPlanItem
import kotlinx.coroutines.flow.Flow

class ChildStudyRepository(private val childStudyDao: ChildStudyDao) {
    fun getStudyPlans(userId: Long): Flow<List<StudyPlanItem>> = childStudyDao.getStudyPlans(userId)
    suspend fun insertStudyPlan(plan: StudyPlanItem): Long = childStudyDao.insertStudyPlan(plan)
    suspend fun updateStudyPlan(plan: StudyPlanItem) = childStudyDao.updateStudyPlan(plan)
    suspend fun deleteStudyPlan(plan: StudyPlanItem) = childStudyDao.deleteStudyPlan(plan)
    suspend fun toggleStudyPlan(plan: StudyPlanItem) = childStudyDao.updatePlanCompletion(plan.id, !plan.isCompleted)

    fun getAllQuizQuestions(): Flow<List<QuizQuestion>> = childStudyDao.getAllQuizQuestions()
    fun getQuizQuestionsBySubject(subject: String): Flow<List<QuizQuestion>> = childStudyDao.getQuizQuestionsBySubject(subject)
}
