package com.manisoft.scraprushapp.mvvm.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.manisoft.scraprushapp.mvvm.repositories.LoginRepository
import com.manisoft.scraprushapp.mvvm.repositories.ScrapRepository
import com.manisoft.scraprushapp.mvvm.viewmodel.LoginViewModel
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.utils.Constants

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.manisoft.scraprushapp.network.ApiService
import java.util.concurrent.TimeUnit


val viewModelModule = module {
    viewModel {
        LoginViewModel(get())
    }
    viewModel {
        ScrapViewModel(get())
    }

}

val repositoryModule = module {
    single {
        LoginRepository(get())
    }
    single {
        ScrapRepository(get())
    }
}

val apiModule = module {
    fun provideUseApi(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    single { provideUseApi(get()) }
}

val retrofitModule = module {

    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create()
    }

    fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()

        return okHttpClientBuilder.build()
    }

    fun provideRetrofit(factory: Gson): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder().addInterceptor(interceptor).connectTimeout(90, TimeUnit.SECONDS).writeTimeout(90, TimeUnit.SECONDS).readTimeout(90, TimeUnit.SECONDS).build()

        return Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create(factory)).client(httpClient).build()
    }

    single { provideGson() }
    single { provideHttpClient() }
    single { provideRetrofit(get()) }
}