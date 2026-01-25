package love.yuzi.contact.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import love.yuzi.contact.local.ContactDao
import love.yuzi.contact.local.ContactDatabase
import love.yuzi.contact.local.ContactRepository
import love.yuzi.contact.system.SystemContactRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ContactModule {

    @Provides
    @Singleton
    fun provideContactDatabase(@ApplicationContext context: Context): ContactDatabase {
        return Room.databaseBuilder(
            context,
            ContactDatabase::class.java,
            "contact-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideContactDao(database: ContactDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    @Singleton
    fun provideContactRepository(contactDao: ContactDao): ContactRepository {
        return ContactRepository(contactDao)
    }

    @Provides
    @Singleton
    fun provideSystemContactRepository(@ApplicationContext context: Context): SystemContactRepository {
        return SystemContactRepository(context)
    }
}
