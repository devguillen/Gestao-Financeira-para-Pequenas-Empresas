package com.gestao.financas;

import com.gestao.financas.entity.Category;
import com.gestao.financas.repository.CategoryRepository;
import com.gestao.financas.service.CategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void createCategory_savesAndReturnsCategory() {
        Category category = new Category();
        category.setName("Alimentação");

        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals("Alimentação", result.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void getCategoriesByUser_returnsList() {
        Long userId = 1L;
        Category c1 = new Category();
        c1.setName("Alimentação");
        Category c2 = new Category();
        c2.setName("Transporte");

        when(categoryRepository.findByUserId(userId)).thenReturn(Arrays.asList(c1, c2));

        List<Category> result = categoryService.getCategoriesByUser(userId);

        assertEquals(2, result.size());
        assertEquals("Alimentação", result.get(0).getName());
        assertEquals("Transporte", result.get(1).getName());
        verify(categoryRepository).findByUserId(userId);
    }

    @Test
    void updateCategory_updatesAndReturnsCategory() {
        Long categoryId = 1L;
        Category existing = new Category();
        existing.setName("Antigo");

        Category updateDetails = new Category();
        updateDetails.setName("Novo");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        Category result = categoryService.updateCategory(categoryId, updateDetails);

        assertEquals("Novo", result.getName());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(existing);
    }

    @Test
    void updateCategory_categoryNotFound_throwsException() {
        Long categoryId = 1L;
        Category updateDetails = new Category();
        updateDetails.setName("Novo");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.updateCategory(categoryId, updateDetails));

        assertEquals("Categoria não encontrada", exception.getMessage());
    }

    @Test
    void deleteCategory_callsRepository() {
        Long categoryId = 1L;

        doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).deleteById(categoryId);
    }
}
