package com.example.data.repository

import com.example.data.local.dao.FamilyDao
import com.example.data.local.dao.ShoppingDao
import com.example.data.local.entities.FamilyMember
import com.example.data.local.entities.ShoppingItem
import kotlinx.coroutines.flow.Flow

class FamilyRepository(
    private val familyDao: FamilyDao,
    private val shoppingDao: ShoppingDao
) {
    fun getFamilyMembers(userId: Long): Flow<List<FamilyMember>> = familyDao.getFamilyMembersByUser(userId)
    fun getEmergencyContacts(userId: Long): Flow<List<FamilyMember>> = familyDao.getEmergencyContacts(userId)
    suspend fun insertFamilyMember(member: FamilyMember): Long = familyDao.insertFamilyMember(member)
    suspend fun updateFamilyMember(member: FamilyMember) = familyDao.updateFamilyMember(member)
    suspend fun deleteFamilyMember(member: FamilyMember) = familyDao.deleteFamilyMember(member)

    // Shopping List for Family
    fun getShoppingItems(userId: Long): Flow<List<ShoppingItem>> = shoppingDao.getShoppingItemsByUser(userId)
    suspend fun insertShoppingItem(item: ShoppingItem): Long = shoppingDao.insertShoppingItem(item)
    suspend fun updateShoppingItem(item: ShoppingItem) = shoppingDao.updateShoppingItem(item)
    suspend fun deleteShoppingItem(item: ShoppingItem) = shoppingDao.deleteShoppingItem(item)
    suspend fun toggleShoppingItem(item: ShoppingItem) = shoppingDao.updatePurchasedState(item.id, !item.isPurchased)
}
