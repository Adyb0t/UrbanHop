package com.example.urbanhop.di

import com.example.urbanhop.BuildConfig
import com.example.urbanhop.data.events.EventsRepository
import com.example.urbanhop.data.location.GeocodeApi
import com.example.urbanhop.data.event_stations.StationsRepository
import com.example.urbanhop.data.events.SerpApi
import com.example.urbanhop.data.navigation_stations.TrainNavigationDataSource
import com.example.urbanhop.state.MapViewModel
import com.example.urbanhop.state.TrainNavViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val GOOGLE_MAPS_DI = "GoogleMapsDI"
const val SERP_API_DI = "SerpApiDI"

val appModule = module {
    single(named(GOOGLE_MAPS_DI)) {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
            .client(get(named(GOOGLE_MAPS_DI)))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single(named(SERP_API_DI)) {
        Retrofit.Builder()
            .baseUrl("https://serpapi.com/")
            .client(get(named(SERP_API_DI)))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>(named(GOOGLE_MAPS_DI)).create(GeocodeApi::class.java) }
    single { get<Retrofit>(named(SERP_API_DI)).create(SerpApi::class.java) }
    single(named(GOOGLE_MAPS_DI)) {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("key", BuildConfig.MAPS_API_KEY)
                    .build()
                val request = original.newBuilder().url(url).build()
                chain.proceed(request)
            }
            .build()
    }
    single(named(SERP_API_DI)) {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.SERP_API_KEY)
                    .build()
                val request = original.newBuilder().url(url).build()
                chain.proceed(request)
            }
            .build()
    }

    single { TrainNavigationDataSource(androidContext()) }
    single { StationsRepository(androidContext()) }
    single { EventsRepository(get<GeocodeApi>(), get<SerpApi>()) }
    viewModelOf(::MapViewModel)
    viewModelOf(::TrainNavViewModel)
}
