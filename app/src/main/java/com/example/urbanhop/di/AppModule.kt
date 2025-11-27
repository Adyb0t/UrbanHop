package com.example.urbanhop.di

import com.example.urbanhop.BuildConfig
import com.example.urbanhop.data.events.EventsRepository
import com.example.urbanhop.data.location.GeocodeApi
import com.example.urbanhop.data.stations.StationsRepository
import com.example.urbanhop.state.EventsViewModel
import com.example.urbanhop.state.MapViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val GOOGLE_MAPS_DI = "GoogleMapsDI"

val appModule = module {
    single { StationsRepository(androidContext()) }
    single { EventsRepository(androidContext(), get<GeocodeApi>()) }
    single(named(GOOGLE_MAPS_DI)) {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
            .client(get(named(GOOGLE_MAPS_DI)))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>(named(GOOGLE_MAPS_DI)).create(GeocodeApi::class.java) }
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
    viewModelOf(::MapViewModel)
    viewModelOf(::EventsViewModel)
}
