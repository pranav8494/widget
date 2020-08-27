package com.pp.miro.widget.repository.impl;

import com.pp.miro.widget.bom.Widget;
import com.pp.miro.widget.repository.api.WidgetRepo;
import com.pp.miro.widget.WidgetTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class InMemoryWidgetRepoTest {
  
  private WidgetRepo repo = new InMemoryWidgetRepo();

  private Widget widget1, widget2, widget3;

  @BeforeEach
  void setUp(){
    repo.wipeRepo();
    widget1 = repo.store(WidgetTestUtils.createWidget());
    widget2 = repo.store(WidgetTestUtils.createWidget(2));
    widget3 = repo.store(WidgetTestUtils.createWidget());
    
  }
  
  @Test
  void testSaveNullWidget(){
    
    Assertions.assertThrows(IllegalArgumentException.class, () -> repo.store(null));
  }

  @Test
  void testSaveFirstWithNoZIndex(){
    Assertions.assertEquals(0, widget1.getZIndex());
    testCommonProperties(widget1);
  }

  @Test
  void testSaveLastNoZIndex(){
    Assertions.assertEquals(3, widget3.getZIndex());
    testCommonProperties(widget3);
  }
  
  @Test
  void testGetAll(){

    List<Widget> widgets = repo.retrieveAllWidgets();
    Assertions.assertEquals(3, widgets.size());
    Assertions.assertEquals(widget1.getId(), widgets.get(0).getId());
    Assertions.assertEquals(widget2.getId(), widgets.get(1).getId());
    Assertions.assertEquals(widget3.getId(), widgets.get(2).getId());
  }

  @Test
  void testSaveWithZIndexInMiddle(){
    Widget temp = repo.store(WidgetTestUtils.createWidget(1));
    Assertions.assertEquals(1, temp.getZIndex());
  }
  
  @Test
  void testSaveWithOverriddenZIndex(){
    Widget temp = repo.store(WidgetTestUtils.createWidget(2));
    Assertions.assertEquals(2, temp.getZIndex());
    List<Widget> widgets = repo.retrieveAllWidgets();
    Assertions.assertEquals(4, widgets.size());
    Assertions.assertEquals(widget1.getId(), widgets.get(0).getId());
    Assertions.assertEquals(temp.getId(), widgets.get(1).getId());
    Assertions.assertEquals(widget2.getId(), widgets.get(2).getId());
    Assertions.assertEquals(3, widgets.get(2).getZIndex());
    Assertions.assertEquals(widget3.getId(), widgets.get(3).getId());
    Assertions.assertEquals(4, widgets.get(3).getZIndex());
  }
  
  @Test
  void testChangingZIndex(){
    Widget temp = WidgetTestUtils.createWidgetWithDifferentXY(widget1, 0);
    temp.setZIndex(2);
    repo.store(temp);
    List<Widget> widgets = repo.retrieveAllWidgets();
    Assertions.assertEquals(3, widgets.size());
    Assertions.assertEquals(widget1.getId(), widgets.get(0).getId());
    Assertions.assertEquals(2, widgets.get(0).getZIndex());
    Assertions.assertEquals(widget2.getId(), widgets.get(1).getId());
    Assertions.assertEquals(3, widgets.get(1).getZIndex());
    Assertions.assertEquals(widget3.getId(), widgets.get(2).getId());
    Assertions.assertEquals(4, widgets.get(2).getZIndex());
  }

  @Test
  void testChangingZIndexToLast(){
    Widget temp = WidgetTestUtils.createWidgetWithDifferentXY(widget3, 0);
    temp.setZIndex(6);
    repo.store(temp);
    List<Widget> widgets = repo.retrieveAllWidgets();
    Assertions.assertEquals(3, widgets.size());
    Assertions.assertEquals(widget3.getId(), widgets.get(2).getId());
    Assertions.assertEquals(6, widgets.get(2).getZIndex());
  }

  @Test
  void testSaveWithOverriddenZIndexWithGap(){
    Widget temp = repo.store(WidgetTestUtils.createWidget(0));
    Assertions.assertEquals(0, temp.getZIndex());
    List<Widget> widgets = repo.retrieveAllWidgets();
    Assertions.assertEquals(4, widgets.size());
    Assertions.assertEquals(widget1.getId(), widgets.get(1).getId());
    Assertions.assertEquals(1, widgets.get(1).getZIndex());
    Assertions.assertEquals(temp.getId(), widgets.get(0).getId());
    Assertions.assertEquals(widget2.getId(), widgets.get(2).getId());
    Assertions.assertEquals(2, widgets.get(2).getZIndex());
    Assertions.assertEquals(widget3.getId(), widgets.get(3).getId());
    Assertions.assertEquals(3, widgets.get(3).getZIndex());
  }
  
  @Test
  void testUpdateExistingWidget(){
    Widget temp = repo.store(WidgetTestUtils.createWidgetWithDifferentXY(widget1, 10));
    Assertions.assertEquals(temp.getXCoOrdinate(), widget1.getXCoOrdinate() + 10);
    Assertions.assertEquals(temp.getYCoOrdinate(), widget1.getYCoOrdinate() + 10);
    Assertions.assertEquals(temp.getZIndex(), widget1.getZIndex());
    Assertions.assertEquals(temp.getWidgetWidth(), widget1.getWidgetWidth());
    Assertions.assertEquals(temp.getWidgetHeight(), widget1.getWidgetHeight());
    Assertions.assertEquals(temp.getId(), widget1.getId());
  }
  
  @Test
  void testGetAndDelete(){
    Optional<Widget> temp = repo.retrieve(widget1.getId().toString());
    Assertions.assertTrue(temp.isPresent() && temp.get().equals(widget1));
    Assertions.assertTrue(repo.delete(widget1.getId().toString()));
    Assertions.assertFalse(repo.delete(widget1.getId().toString()));
  }

  @Test
  void testStoreConcurrency() throws Exception {
    List<CompletableFuture> asyncResults = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      CompletableFuture<Void> result = CompletableFuture.runAsync(() -> {
        for (int j = 0; j < 10; j++) {
          Widget widgetMock = WidgetTestUtils.createWidget(j);
          repo.store(widgetMock);
        }
      });
      asyncResults.add(result);
    }

    CompletableFuture.allOf(asyncResults.toArray(new CompletableFuture[]{})).get(5, TimeUnit.SECONDS);
    Assertions.assertEquals(53, repo.retrieveAllWidgets().size());
  }
  

  private void testCommonProperties(Widget widget) {
    Assertions.assertTrue(widget.getLastModificationDate().isBefore(LocalDateTime.now()) ||
        widget.getLastModificationDate().isEqual(LocalDateTime.now()));
    Assertions.assertTrue(widget.getId() != null);
  }  
}
