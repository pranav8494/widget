package com.pp.miro.widget.services.impl;

import com.pp.miro.widget.bom.Widget;
import com.pp.miro.widget.repository.api.WidgetRepo;
import com.pp.miro.widget.services.api.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class WidgetServiceImpl implements WidgetService {

  private WidgetRepo widgetRepo;
  
  @Autowired
  public  WidgetServiceImpl(WidgetRepo widgetRepo) {
    this.widgetRepo = widgetRepo;
  }
  
  @Override
  public Widget saveWidget(Widget widget) {
    return widgetRepo.store(widget);
  }

  @Override
  public Widget updateWidget(Widget widget) {
    if (widget == null || widget.getId() == null || StringUtils.isEmpty(widget.getId().toString())) {
      throw new IllegalArgumentException("[ERROR] - Either Widget is null or Widget ID is empty!!");
    }
    return widgetRepo.store(widget);
  }

  @Override
  public Optional<Widget> retrieveWidget(String id) {
    return widgetRepo.retrieve(id);
  }

  @Override
  public List<Widget> retrieveAllWidgets() {
    return widgetRepo.retrieveAllWidgets();
  }

  @Override
  public boolean deleteWidget(String id) {
    return widgetRepo.delete(id);
  }
}
