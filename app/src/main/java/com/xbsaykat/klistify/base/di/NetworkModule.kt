package com.xbsaykat.klistify.base.di

import BASE_URL
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.xbsaykat.klistify.App
import com.xbsaykat.klistify.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
        @Named("header_interceptor") header: Interceptor,
        @Named("with_cache_interceptor") cacheInterceptor: Interceptor,
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
            clientBuilder.cache(cacheInfo())
            clientBuilder.addInterceptor(cacheInterceptor)
        } catch (e: Exception) {
            Log.d("NETWORK_", "HTTP_CODE providesClient: ${e.localizedMessage}")
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
            Log.d("NETWORK_", "log: $networkLog")
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
    fun cacheInfo(): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong() //5 MB
        return Cache(App.instance.cacheDir, cacheSize)
    }


    @Singleton
    @Provides
    @Named("with_cache_interceptor")
    fun provideCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            request = if (hasNetwork()) request.newBuilder()
                .cacheControl(CacheControl.Builder().maxAge(30, TimeUnit.MINUTES).build()).build()
            else request.newBuilder()
                .cacheControl(CacheControl.Builder().maxStale(1, TimeUnit.DAYS).build()).build()
            chain.proceed(request)
        }
    }

    @Singleton
    @Provides
    @Named("header_interceptor")
    fun providesHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                .method(original.method, original.body)
                .build()

            val finalRequest = chain.proceed(request)
            try {
                if (finalRequest.code == 401) {
                    //Any EventManager to addEvent(REFRESH_TOKEN)
                }
                if (BuildConfig.DEBUG) {
                    Log.d(
                        "HTTP_CODE:",
                        "intercept: ${finalRequest.code} ${finalRequest.request.url}\n"
                    )
                }
            } catch (e: Exception) {
                Log.e("HTTP_CODE", "intercept:exception ${e.localizedMessage}")
            }
            finalRequest
        }
    }

    @Singleton
    @Provides
    fun hasNetwork(): Boolean {
        val connectivityManager =
            App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}