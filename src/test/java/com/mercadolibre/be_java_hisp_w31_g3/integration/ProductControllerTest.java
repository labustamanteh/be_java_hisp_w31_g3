package com.mercadolibre.be_java_hisp_w31_g3.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
        @Autowired
        private MockMvc mockMvc;
        @Autowired
         UserRepository userRepository;

         
        @Test
        public void getPromoPosts_validId_returnPromoList() throws Exception {
                // arrange
                String payLoadJson = CustomFactory.createPromoPost();
                String expectedResponse = CustomFactory.promoListResponse();

                // act & assert
                 this.mockMvc.perform(post("/products/promo-post").contentType(MediaType.APPLICATION_JSON)
                                 .content(payLoadJson))
                                .andDo(print()).andExpect(status().isCreated());
                MvcResult response = this.mockMvc.perform(get("/products/promo-post/list").param("user_id", "2"))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
                Assertions.assertEquals(expectedResponse, response.getResponse().getContentAsString());
        }

        @Test
        public void getPromoPostTest_invalidId_throwsNotFound() throws Exception {
                // arrange
                String userId = "220";
                String expectedErrorMessage = "No se encontró el usuario con el id ingresado";

                // act & assert
                this.mockMvc.perform(get("/products/promo-post/list").param("user_id", userId))
                                .andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                                                result.getResolvedException()))
                                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
        }

        @Test
        public void getPromoTest_isEmptyPromo_throwsNotFound() throws Exception {
                // arrange
                String userId = "3";
                String expectedErrorMessage = "No hay Productos en promoción";

                // act & assert
                this.mockMvc.perform(get("/products/promo-post/list").param("user_id", userId))
                                .andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                                                result.getResolvedException()))
                                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
        }

        @Test
        public void getPostListWithFiltersTest_validFilters_returnFilteredList() throws Exception {
                // arrange
                String discount = "0.15";
                String category = "100";
                String color = "Black";
                String has_promo = "true";
                String payLoad = CustomFactory.createPromoPost();
                String expectedResponse = CustomFactory.promoListWithFiltersResponse();

                // act & assert
                this.mockMvc.perform(
                                post("/products/promo-post").contentType(MediaType.APPLICATION_JSON).content(payLoad))
                                .andDo(print()).andExpect(status().isCreated());

                MvcResult result = this.mockMvc.perform(get("/products/post/list").param("discount", discount)
                                .param("category", category).param("color", color).param("hasPromo", has_promo))
                                .andDo(print())
                                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andReturn();
                Assertions.assertEquals(expectedResponse, result.getResponse().getContentAsString());
        }

        @Test
        public void getPostList_NotFoundPromoWithFilters_returnNotFoundException() throws Exception {
                // arrange
                String discount = "0.155";
                String category = "10034";
                String color = "Blacksss";
                String has_promo = "false";
                String expectedMessageResult = "No se encontraron publicaciones con el filtro proporcionado.";

                // act & assert
                this.mockMvc.perform(get("/products/post/list").param("discount", discount)
                                .param("category", category).param("color", color).param("hasPromo", has_promo))
                                .andDo(print())
                                .andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                                                result.getResolvedException()))
                                .andExpect(jsonPath("$.message").value(expectedMessageResult));

        }

        @Test
        public void getPostListTest_discountInvalid_returnBadRequestException() throws Exception {
                String discount = "-3";
                String expectedResultMessage = "El valor del descuento no es válido";

                // act & assert
                this.mockMvc.perform(get("/products/post/list").param("discount", discount))
                                .andDo(print()).andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(result -> assertInstanceOf(BadRequestException.class,
                                                result.getResolvedException()))
                                .andExpect(jsonPath("$.message").value(expectedResultMessage));
        }
}
