package com.shutterfly.imagecanvas.di

import com.shutterfly.imagecanvas.data.repository.ImagesRepositoryImpl
import com.shutterfly.imagecanvas.domain.repository.ImagesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ImagesRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindImagesRepository(impl: ImagesRepositoryImpl): ImagesRepository
}