package com.chinarrental.app.data.repository

import com.chinarrental.app.data.dao.ItemDao
import com.chinarrental.app.data.model.Item
import com.chinarrental.app.data.model.ItemCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val itemDao: ItemDao
) {

    fun getAllItems(): Flow<List<Item>> = itemDao.getAllItems()

    fun getItemById(id: Long): Flow<Item?> = itemDao.getItemById(id)

    fun getItemsByCategory(category: ItemCategory): Flow<List<Item>> =
        itemDao.getItemsByCategory(category.name)

    fun getAvailableItems(): Flow<List<Item>> = itemDao.getAvailableItems()

    fun searchItems(query: String): Flow<List<Item>> = itemDao.searchItems(query)

    fun getLowStockItems(threshold: Int = 5): Flow<List<Item>> =
        itemDao.getLowStockItems(threshold)

    fun getTotalItemsCount(): Flow<Int> = itemDao.getTotalItemsCount()

    fun getTotalItemsValue(): Flow<Double> = itemDao.getTotalItemsValue()

    suspend fun insertItem(item: Item): Result<Long> {
        return try {
            val id = itemDao.insertItem(item)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItem(item: Item): Result<Unit> {
        return try {
            itemDao.updateItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteItem(item: Item): Result<Unit> {
        return try {
            itemDao.deleteItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun increaseQuantity(itemId: Long, quantity: Int): Result<Unit> {
        return try {
            itemDao.increaseAvailableQuantity(itemId, quantity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun decreaseQuantity(itemId: Long, quantity: Int): Result<Unit> {
        return try {
            itemDao.decreaseAvailableQuantity(itemId, quantity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}