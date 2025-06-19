package com.xbsaykat.klistify.home.di

import com.xbsaykat.klistify.home.data.repository.HomeRepoImp
import com.xbsaykat.klistify.home.domain.repository.HomeRepo
import com.xbsaykat.klistify.home.repo.HomeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import kotlin.jvm.java

@Module
@InstallIn(ViewModelComponent::class)
object HomeViewModelComponent {
    @Provides
    fun providesHomeRepo(homeService: HomeService): HomeRepo {
        return HomeRepoImp(homeService)
    }

    @Provides
    @ViewModelScoped
    fun providesHomeService(retrofit: Retrofit): HomeService {
        return retrofit.create(HomeService::class.java)
    }


}