package com.xbsaykat.klistify.base.di

import BASE_URL
import android.content.Context
import android.util.Log
import com.xbsaykat.klistify.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun providesApiClient(builder: Retrofit.Builder): Retrofit {
        return builder
            .baseUrl(BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofitBuilder(client: OkHttpClient): Retrofit.Builder {
        return Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)

    }

    @Singleton
    @Provides
    fun providesClient(
        @Named("log_interceptor") interceptor: Interceptor,
        @Named("header_interceptor") header: Interceptor
    ): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        clientBuilder.readTimeout(30, TimeUnit.SECONDS)
        clientBuilder.writeTimeout(30, TimeUnit.SECONDS)
        try {
            clientBuilder.addInterceptor(interceptor)
            clientBuilder.addInterceptor(header)
            clientBuilder.connectionPool(ConnectionPool(0, 5, TimeUnit.MINUTES))
            clientBuilder.protocols(listOf(Protocol.HTTP_1_1)).build()
        } catch (e: Exception) {
            Log.d("NETWORK_CACHE", "HTTP_CODE providesClient: ${e.localizedMessage}")
        }
        clientBuilder.retryOnConnectionFailure(false)
        clientBuilder.callTimeout(30, TimeUnit.SECONDS)
        return clientBuilder.build()
    }

    @Singleton
    @Provides
    @Named("log_interceptor")
    fun providesInterceptor(): Interceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor { networkLog ->
            Log.d("NETWORK_", "log: $networkLog"
            )
        }
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return httpLoggingInterceptor
    }

    @Singleton
    @Provides
    fun cache(@ApplicationContext context: Context): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong() //5 MB
        return Cache(File(context.cacheDir, "network_cash"), cacheSize)
    }

    @Singleton
    @Provides
    @Named("with_cache_interceptor")
    fun provideCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response: Response = chain.proceed(chain.request())
            val cacheControl: CacheControl = CacheControl.Builder()
                .maxAge(15, TimeUnit.MINUTES)
                .build()
            response.newBuilder()
                .header("CACHE_CONTROL", cacheControl.toString())
                .build()
        }
    }
}