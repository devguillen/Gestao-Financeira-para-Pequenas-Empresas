package com.gestao.financas.service;

import com.gestao.financas.entity.Category;
import com.gestao.financas.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category1 = new Category();
        category1.setId(1L);
        category1.setName("Alimentação");

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Transporte");
    }

    @Test
    void testCreateCategory() {
        when(categoryRepository.save(category1)).thenReturn(category1);

        Category created = categoryService.createCategory(category1);

        assertNotNull(created);
        assertEquals("Alimentação", created.getName());
        verify(categoryRepository, times(1)).save(category1);
    }

    @Test
    void testGetCategoriesByUser() {
        when(categoryRepository.findByUserId(1L)).thenReturn(Arrays.asList(category1, category2));

        List<Category> categories = categoryService.getCategoriesByUser(1L);

        assertNotNull(categories);
        assertEquals(2, categories.size());
        verify(categoryRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testUpdateCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any(Category.class))).thenReturn(category1);

        Category updatedDetails = new Category();
        updatedDetails.setName("Alimentação Atualizada");

        Category updated = categoryService.updateCategory(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Alimentação Atualizada", updated.getName());
        verify(categoryRepository, times(1)).save(category1);
    }

    @Test
    void testDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
