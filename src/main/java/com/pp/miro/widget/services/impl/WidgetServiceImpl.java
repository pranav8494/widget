package com.pp.miro.widget.services.impl;

import com.pp.miro.widget.bom.Widget;
import com.pp.miro.widget.repository.api.WidgetRepo;
import com.pp.miro.widget.services.api.WidgetService;

import java.util.List;
import java.util.Optional;

public class WidgetServiceImpl implements WidgetService {

  // TODO add the logic here.
  private WidgetRepo widgetRepo;

  public WidgetServiceImpl(WidgetRepo widgetRepo) {
    this.widgetRepo = widgetRepo;
  }

  @Override
  public Widget createWidget(Widget widget) {
    return null;
  }

  @Override
  public Widget updateWidget(Widget widget) {
    return null;
  }

  @Override
  public Optional<Widget> getWidgetById(String id) {
    return Optional.empty();
  }

  @Override
  public List<Widget> getAllWidgets() {
    return null;
  }

  @Override
  public boolean deleteWidget(String id) {
    return false;
  }
}
