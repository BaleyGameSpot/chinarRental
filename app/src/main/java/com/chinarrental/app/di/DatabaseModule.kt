package com.chinarrental.app.di

import android.content.Context
import com.chinarrental.app.data.RentalDatabase
import com.chinarrental.app.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RentalDatabase {
        return RentalDatabase.getDatabase(context)
    }

    @Provides
    fun provideItemDao(database: RentalDatabase): ItemDao = database.itemDao()

    @Provides
    fun provideCustomerDao(database: RentalDatabase): CustomerDao = database.customerDao()

    @Provides
    fun provideGuarantorDao(database: RentalDatabase): GuarantorDao = database.guarantorDao()

    @Provides
    fun provideRentalDao(database: RentalDatabase): RentalDao = database.rentalDao()

    @Provides
    fun providePaymentDao(database: RentalDatabase): PaymentDao = database.paymentDao()

    @Provides
    fun provideTransactionDao(database: RentalDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideReminderDao(database: RentalDatabase): ReminderDao = database.reminderDao()

    @Provides
    fun provideBillDao(database: RentalDatabase): BillDao = database.billDao()

    @Provides
    fun provideUserDao(database: RentalDatabase): UserDao = database.userDao()

    @Provides
    fun provideBranchDao(database: RentalDatabase): BranchDao = database.branchDao()
}
