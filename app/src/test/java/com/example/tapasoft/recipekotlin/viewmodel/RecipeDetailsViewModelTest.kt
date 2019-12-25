package com.example.tapasoft.recipekotlin.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.tapasoft.recipekotlin.repository.RecipeRepository
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeDetailsViewModelTest {
    @get:Rule
    //заменяет этот executor синхронным
    val taskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: RecipeRepository
    private lateinit var viewModel: RecipeDetailsViewModel
    private lateinit var deletingObserver: Observer<Boolean>

    @Before
    fun setup() {
        repository = mock()
        viewModel = RecipeDetailsViewModel(repository)

        deletingObserver = mock()

        viewModel.areStepsDeleted().observeForever(deletingObserver)
    }

    @Test
    fun init_shouldShowDeleting() {
        viewModel.deleteCookingSteps(17)

        verify(deletingObserver).onChanged(eq(false))
    }

}