package com.pp.miro.widget.repository.impl;

import com.pp.miro.widget.bom.Widget;
import com.pp.miro.widget.repository.api.WidgetRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryWidgetRepo implements WidgetRepo {

  private final Lock readLock;
  private final Lock writeLock;

  /**
   * Map to store Widgets [WidgetId, Widget]
   */
  private final Map<String, Widget> widgetStore = new HashMap();
  private final SortedMap<Integer, String> zIndexToWidgetStore = new TreeMap();
  private final SortedMap<Integer, SortedMap<Integer, Set<String>>> widgetsSortedByCoOrdinateMap = new TreeMap();

  public InMemoryWidgetRepo() {
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    readLock = readWriteLock.readLock();
    writeLock = readWriteLock.writeLock();
  }

  @Override
  public Widget store(Widget widget) {

    if (widget == null) {
      log.debug("[REPO] - Input error: Widget Can't be NULL!!");
      throw new IllegalArgumentException("[ERROR] - Widget Can't be NULL!!");
    }
    writeLock.lock();
    try {

      if (widget.getId() == null) {
        // First time seeing the widget. No need to check in widgetStore.
        widget.setId(UUID.randomUUID());
      } else {
        mergeWithInStoreWidget(widget);
        removeWidgetFromCoOrdinateMap(widget);
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
    widget.setLastModificationDate(LocalDateTime.now());
    if(isReplacingWidgetForZIndex(widget)){

      log.debug("[REPO] - Overwrite detected.");

      removeWidgetFromZIndexMap(widget);

      // insert new widget at zIndex and then update and move existing widgets in map.
      Set<Integer> zIndexKeysToReplace = zIndexToWidgetStore.tailMap(widget.getZIndex()).keySet();
      Widget newWidget = widget;

      for(Integer key : zIndexKeysToReplace) {
        if(key == newWidget.getZIndex()){
          Integer newZIndexForOldWidget = key + 1;
          Widget oldWidget = widgetStore.get(zIndexToWidgetStore.get(key));
          oldWidget.setZIndex(newZIndexForOldWidget);
          zIndexToWidgetStore.put(key, newWidget.getId().toString());
          newWidget = oldWidget;
        }
        else{
          zIndexToWidgetStore.put(newWidget.getZIndex(), newWidget.getId().toString());
          break;
        }
      }

      // To cover the last item in the map if need to move until end.
      if(zIndexToWidgetStore.lastKey() < newWidget.getZIndex()){
        zIndexToWidgetStore.put(newWidget.getZIndex(), newWidget.getId().toString());
      }
    }
    else {
      if(zIndexToWidgetStore.values().contains(widget.getId().toString())){
        removeWidgetFromZIndexMap(widget);
      }
      zIndexToWidgetStore.put(widget.getZIndex(), widget.getId().toString());
    }
    widgetStore.put(widget.getId().toString(), widget);
    putWidgetInCoOrdinateMap(widget);
  }

  private void putWidgetInCoOrdinateMap(Widget widget) {
    SortedMap<Integer, Set<String>> yCoOrdinateMap =
        Optional.ofNullable(widgetsSortedByCoOrdinateMap.get(widget.getXCoOrdinate())).orElse(new TreeMap());
    Set<String> widgetIds = Optional.ofNullable(yCoOrdinateMap.get(widget.getYCoOrdinate())).orElse(new HashSet<String>());
    widgetIds.add(widget.getId().toString());
    yCoOrdinateMap.put(widget.getYCoOrdinate(), widgetIds);
    widgetsSortedByCoOrdinateMap.put(widget.getXCoOrdinate(), yCoOrdinateMap);
  }

  private void removeWidgetFromCoOrdinateMap(Widget widget) {

    Optional.ofNullable(widgetStore.get(widget.getId().toString())).ifPresent(
        w -> Optional.ofNullable(widgetsSortedByCoOrdinateMap.get(w.getXCoOrdinate())).ifPresent(
            yCoOrdinateMap -> Optional.ofNullable(yCoOrdinateMap.get(w.getYCoOrdinate())).ifPresent(
                widgetIds -> {
                  widgetIds.remove(w.getId().toString());
                } 
        )
    ));
  }

  private void removeWidgetFromZIndexMap(Widget widget){
    Optional.ofNullable(widgetStore.get(widget.getId().toString())).map(oldWidgetInStore -> {
      zIndexToWidgetStore.remove(oldWidgetInStore.getZIndex());
      return oldWidgetInStore;
    });
  }

  private boolean isReplacingWidgetForZIndex(Widget widget){
    String widgetAtZIndexInStore = zIndexToWidgetStore.get(widget.getZIndex());
    return widgetAtZIndexInStore != null && !widgetAtZIndexInStore.equals(widget.getId().toString());
  }

  private void mergeWithInStoreWidget(Widget widget) {
    Optional<Widget> widgetInStore = retrieve(widget.getId().toString());
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
    else {
      // The ID doesnt exist in storage, hence a new widget, Resetting ID.
      widget.setId(UUID.randomUUID());
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
      log.debug("[REPO] - Input error: Can't find Widget with empty or null id!");
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
  public List<Widget> retrieveAllWidgets() {

    readLock.lock();
    try{
      return zIndexToWidgetStore.values().stream().map(widgetStore::get).collect(Collectors.toList());
    }finally {
      readLock.unlock();
    }
  }

  @Override
  public List<Widget> filterWidgetsByCoOrdinateRange(Integer fromX, Integer fromY, Integer toX, Integer toY) {

    // Using Binary search to find the lower bound giving a complexity of O(log NX) + O(log NY) as its a map within map.
    
    List<Widget> result = new ArrayList();
    readLock.lock();
    try{
      Integer[] xKeys = widgetsSortedByCoOrdinateMap.keySet().toArray(new Integer[0]);
      int xStartPosition = Arrays.binarySearch(xKeys, fromX);
      for(int i = xStartPosition >= 0 ? xStartPosition : (xStartPosition * (-1))-1; i < xKeys.length; i++){
        if(fromX <= xKeys[i] && xKeys[i] <= toX) {
          SortedMap<Integer, Set<String>> yCoOrdinateMap = widgetsSortedByCoOrdinateMap.get(xKeys[i]);
          Integer[] yKeys = yCoOrdinateMap.keySet().toArray(new Integer[0]);
          int yStartPosition = Arrays.binarySearch(yKeys, fromY);
          for(int j = yStartPosition >= 0 ? yStartPosition : (yStartPosition * (-1))-1; j < yKeys.length; j++){
            if(fromY <= yKeys[j] && yKeys[j] <= toY) {
              yCoOrdinateMap.get(yKeys[j]).forEach(value ->
                  // Check if the widget is within range.
                  Optional.ofNullable(widgetStore.get(value)).ifPresent(widget -> {
                    Integer widthX = widget.getXCoOrdinate() + widget.getWidgetWidth();
                    Integer widthY = widget.getYCoOrdinate() + widget.getWidgetHeight();
                    if(fromX <= widthX && widthX <= toX && fromY <= widthY && widthY <= toY) {
                      result.add(widget);
                    }
                  }));
            }
            else if(yKeys[j] > toY){
              break;
            }
          }
        }
        else if(xKeys[i] > toX){
          break;              
        }        
      }
    }finally {
      readLock.unlock();
    }
    return result;
  }

  @Override
  public void wipeRepo() {
    widgetStore.clear();
    zIndexToWidgetStore.clear();
  }
}
