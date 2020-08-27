package com.pp.miro.widget.services.impl;

import com.pp.miro.widget.bom.Widget;
import com.pp.miro.widget.repository.api.WidgetRepo;
import com.pp.miro.widget.WidgetTestUtils;
import com.pp.miro.widget.services.api.WidgetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
public class WidgetServiceImplTest {

  @Mock
  private WidgetRepo repo;

  private WidgetService service;

  @BeforeEach
  void setUp() {
    
    service = new WidgetServiceImpl(repo);
  }

  @Test
  void testCreateWidget(){
    Widget widget = WidgetTestUtils.createWidget();
    Mockito.when(repo.store(Mockito.any())).thenReturn(widget);
    Assertions.assertNotNull(service.saveWidget(widget));
  }

  @Test
  void testUpdateWidgetWithNull(){
    Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateWidget(null));
    Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateWidget(WidgetTestUtils.createWidget()));
  }

  @Test
  void testUpdateWidget(){
    Widget widget = WidgetTestUtils.createWidget();
    widget.setId(UUID.randomUUID());
    Mockito.when(repo.store(Mockito.any())).thenReturn(widget);
    Assertions.assertNotNull(service.updateWidget(widget));
  }
  
}
