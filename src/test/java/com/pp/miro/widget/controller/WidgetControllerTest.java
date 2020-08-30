package com.pp.miro.widget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pp.miro.widget.WidgetTestUtils;
import com.pp.miro.widget.bom.Widget;
import com.pp.miro.widget.services.api.WidgetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(controllers = WidgetController.class)
public class WidgetControllerTest {

  private static final String BASE_URL = "/widget";

  @MockBean
  private WidgetService service;
  
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  
  private Widget testWidget;

  @BeforeEach
  void setUp() {
    testWidget = WidgetTestUtils.createWidget(0);
    testWidget.setId(UUID.randomUUID());
  }
  
  @Test
  void createWidgetWithEmptyBodyTest() throws Exception {
    Mockito.when(service.saveWidget(Mockito.any())).thenThrow(new IllegalArgumentException("[ERROR] - Widget Can't be NULL!!"));
    mockMvc.perform(post(BASE_URL))
        .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
  }
  
  @Test
  void createWidgetTest() throws Exception {
    Mockito.when(service.saveWidget(Mockito.any())).thenReturn(testWidget);
    String result = mockMvc.perform(post(BASE_URL)
        .content(objectMapper.writeValueAsBytes(WidgetTestUtils.createWidget()))
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andReturn().getResponse().getContentAsString();
    Widget widget = objectMapper.readValue(result, Widget.class);
    Assertions.assertEquals(0, widget.getZIndex());
    Assertions.assertNotNull(widget.getId());
  }
  
  @Test
  void deleteNonExistingWidgetTest() throws Exception {
    
    Mockito.when(service.deleteWidget(Mockito.any())).thenReturn(false);
    mockMvc.perform(delete(BASE_URL + "/aaaa-bbbb"))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();
  }

  @Test
  void deleteExistingWidgetTest() throws Exception {
    Mockito.when(service.deleteWidget(Mockito.any())).thenReturn(true);
    mockMvc.perform(delete(BASE_URL + "/aaaa-bbbb"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
  }
  
}
