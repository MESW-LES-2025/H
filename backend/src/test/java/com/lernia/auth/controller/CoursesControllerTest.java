package com.lernia.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lernia.auth.utils.BaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CoursesControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllCoursesWithoutFilters() throws Exception {
        mockMvc.perform(get("/api/courses/search")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(Matchers.greaterThan(0)));
    }

    @Test
    void shouldFilterByName() throws Exception {
        mockMvc.perform(get("/api/courses/search")
                        .param("name", "Computing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(Matchers.containsString("Computing")));
    }

    @Test
    void shouldFilterByCourseTypes() throws Exception {
        mockMvc.perform(get("/api/courses/search")
                        .param("courseTypes", "BACHELOR")
                        .param("courseTypes", "MASTER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].courseType",
                        Matchers.everyItem(Matchers.isOneOf("BACHELOR", "MASTER"))));
    }

    @Test
    void shouldFilterByLanguage() throws Exception {
        mockMvc.perform(get("/api/courses/search")
                        .param("languages", "English"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].language", Matchers.everyItem(Matchers.is("English"))));
    }

    @Test
    void shouldFilterByCountries() throws Exception {
        mockMvc.perform(get("/api/courses/search")
                        .param("countries", "United Kingdom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].university.location.country",
                        Matchers.everyItem(Matchers.is("United Kingdom"))));
    }

    @Test
    void shouldFilterByAreasOfStudy() throws Exception {
        mockMvc.perform(get("/api/courses/search")
                        .param("areasOfStudy", "Computer Science"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].areaOfStudies[*].name",
                        Matchers.everyItem(Matchers.hasItem("Computer Science"))));
    }

    @Test
    void shouldFilterByScholarships() throws Exception {
        mockMvc.perform(get("/api/courses/search")
                        .param("hasScholarship", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
        // Optionally, assert that only courses whose university has scholarships are returned
    }

    @Test
    void shouldCombineMultipleFilters() throws Exception {
        mockMvc.perform(get("/api/courses/search")
                        .param("name", "Engineering")
                        .param("courseTypes", "MASTER")
                        .param("languages", "English")
                        .param("countries", "United Kingdom")
                        .param("areasOfStudy", "Engineering")
                        .param("hasScholarship", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/courses/search")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.content.length()").value(Matchers.lessThanOrEqualTo(5)));
    }
}
