package com.example.ActualApp.controller;

import com.example.ActualApp.controller.dto.ActivityCategoryDto;
import com.example.ActualApp.controller.dto.ActivityDto;
import com.example.ActualApp.controller.dto.NewActivityCategoryDto;
import com.example.ActualApp.service.ActivityCategoryService;
import com.example.ActualApp.service.ActivityService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ActivityCategoryController.class)
@WithMockUser
@AutoConfigureMockMvc(addFilters = false)
class ActivityCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityCategoryService activityCategoryService;

    @Test
    void shouldReturnCategoryByGivenId() throws Exception {
        //Given
        UUID id = UUID.randomUUID();
        ActivityCategoryDto activityCategoryDto = Instancio.create(ActivityCategoryDto.class);
        Mockito.when(activityCategoryService.getCategoryById(id)).thenReturn(activityCategoryDto);

        //When
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories/" + id));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(activityCategoryDto.id().toString()))
                .andExpect(jsonPath("$.name").value(activityCategoryDto.name()))
                .andExpect(jsonPath("$.priority").value(activityCategoryDto.priority()))
                .andExpect(jsonPath("$.activitiesNumber").value(activityCategoryDto.activitiesNumber()));

    }

    @Test
    void shouldReturnAllActivities() throws Exception {
        //Given
        List<ActivityCategoryDto> categories = Instancio.ofList(ActivityCategoryDto.class)
                .size(1)
                .create();
        ActivityCategoryDto activityCategoryDto = categories.get(0);
        Mockito.when(activityCategoryService.getAllCategories()).thenReturn(categories);

        //When
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/categories"));

        //Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(activityCategoryDto.id().toString()))
                .andExpect(jsonPath("$[0].name").value(activityCategoryDto.name().toString()))
                .andExpect(jsonPath("$[0].priority").value(activityCategoryDto.priority()))
                .andExpect(jsonPath("$[0].activitiesNumber").value(activityCategoryDto.activitiesNumber()));
    }

    @Test
    void shouldReturnNewCategory() throws Exception {
        //Given
        NewActivityCategoryDto newActivityCategoryDto = new NewActivityCategoryDto("Test Category");
        Mockito.when(activityCategoryService.saveNewCategory(newActivityCategoryDto)).thenReturn(newActivityCategoryDto);

        //When
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                                "name" : "Test Category"
                        }
                        """));

        //Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newActivityCategoryDto.name()));

    }
}