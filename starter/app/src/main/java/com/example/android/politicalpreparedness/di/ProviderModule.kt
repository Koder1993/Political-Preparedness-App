package com.example.android.politicalpreparedness.di

import android.app.Application
import androidx.room.Room
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.repository.ElectionNetworkDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {
    @Provides
    @Singleton
    fun provideElectionDao(application: Application): ElectionDao {
        return ElectionDatabase.getInstance(application).electionDao
    }
//
//    @Provides
//    fun provideElectionNetworkDataSource() = ElectionNetworkDataSource(provideRetrofitService())

    @Provides
    fun provideDispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun provideRetrofitService(): CivicsApiService {
        return CivicsApi.retrofitService
    }
}