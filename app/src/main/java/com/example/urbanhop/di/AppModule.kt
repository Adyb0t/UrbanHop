package com.example.urbanhop.di

import com.example.urbanhop.data.events.EventsRepository
import com.example.urbanhop.data.stations.StationsRepository
import com.example.urbanhop.state.EventsViewModel
import com.example.urbanhop.state.MapViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { StationsRepository(androidContext()) }
    single { EventsRepository(androidContext()) }
    viewModelOf(::MapViewModel)
    viewModelOf(::EventsViewModel)
}