package com.pp.miro.widget.repository.impl;

import com.pp.miro.widget.bom.Widget;
import com.pp.miro.widget.repository.api.WidgetRepo;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class InMemoryWidgetRepo implements WidgetRepo<Widget> {

  private final Lock readLock;
  private final Lock writeLock;

  /**
   * Map to store Widgets [WidgetId, Widget]
   */
  private final Map<String, Widget> widgetStore = new HashMap();
  private final SortedMap<Integer, String> zIndexToWidgetStore = new TreeMap();

  public InMemoryWidgetRepo() {
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    readLock = readWriteLock.readLock();
    writeLock = readWriteLock.writeLock();
  }

  @Override
  public Widget store(Widget widget) {

    if (widget == null) {
      throw new IllegalArgumentException("[ERROR] - Widget Can't be NULL!!");
    }
    writeLock.lock();
    try {
      
      if (widget.getWidgetId() == null) {
        // First time seeing the widget. No need to check in widgetStore.
        widget.setWidgetId(UUID.randomUUID());
      } else {
        mergeWidgetWithStoreWidget(widget);
      }

      // if Z index is not set, then set it.
      if (widget.getZIndex() == null) {
        setZIndex(widget);
      }

      insertWidgetInStore(widget);
      return widget;
    } finally {
      writeLock.unlock();
    }
  }

  
  private void insertWidgetInStore(Widget widget) {
    if(isReplacingWidgetForZIndex(widget)){
       // TODO Logic to insert new widget at zIndex and then update and move existing widgets in map.
    }
    widgetStore.put(widget.getWidgetId().toString(), widget);
    zIndexToWidgetStore.put(widget.getZIndex(), widget.getWidgetId().toString());
  }
  

  private boolean isReplacingWidgetForZIndex(Widget widget){
    String widgetAtZIndexInStore = zIndexToWidgetStore.get(widget.getZIndex());
    return widgetAtZIndexInStore != null && widgetAtZIndexInStore.equals(widget.getWidgetId().toString());
  }

  private void mergeWidgetWithStoreWidget(Widget widget) {
    Optional<Widget> widgetInStore = retrieve(widget.getWidgetId().toString());
    if(widgetInStore.isPresent()) {
      if (widget.getXCoOrdinate() == null) {
        widget.setXCoOrdinate(widgetInStore.get().getXCoOrdinate());
      }
      if (widget.getYCoOrdinate() == null) {
        widget.setYCoOrdinate(widgetInStore.get().getYCoOrdinate());
      }
      if (widget.getZIndex() == null) {
        widget.setZIndex(widgetInStore.get().getZIndex());
      }
      if (widget.getWidgetWidth() == null) {
        widget.setWidgetWidth(widgetInStore.get().getWidgetWidth());
      }
      if (widget.getWidgetHeight() == null) {
        widget.setWidgetHeight(widgetInStore.get().getWidgetHeight());
      }          
    }
  }

  private void setZIndex(Widget widget) {
    if (zIndexToWidgetStore.isEmpty()) {
      widget.setZIndex(0);
    } else {
      widget.setZIndex(zIndexToWidgetStore.lastKey() + 1);
    }
  }

  @Override
  public Optional<Widget> retrieve(String id) {
    validateId(id);
    readLock.lock();
    try {
      return Optional.ofNullable(widgetStore.get(id));
    } finally {
      readLock.unlock();
    }
  }

  private void validateId(String id) {
    if (StringUtils.isEmpty(id)) {
      throw new IllegalArgumentException("[ERROR] - Can't find Widget with empty or null id!");
    }
  }

  @Override
  public boolean delete(String id) {
    validateId(id);
    writeLock.lock();
    try {
      if (widgetStore.containsKey(id)) {
        Widget removedWidget = widgetStore.remove(id);
        zIndexToWidgetStore.remove(removedWidget.getZIndex());
        return true;
      }
    } finally {
      writeLock.unlock();
    }
    return false;
  }

  @Override
  public List<Widget> getAll() {
    
    readLock.lock();
    try{
      return zIndexToWidgetStore.values().stream().map(widgetStore::get).collect(Collectors.toList());
    }finally {
      readLock.unlock();
    }
  }

}
