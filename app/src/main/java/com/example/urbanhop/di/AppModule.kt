package com.example.urbanhop.di

import com.example.urbanhop.data.stations.StationsRepository
import com.example.urbanhop.state.MapViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { StationsRepository(androidContext()) }
    viewModelOf(::MapViewModel)
}