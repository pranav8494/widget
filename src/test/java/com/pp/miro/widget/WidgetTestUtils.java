package com.pp.miro.widget;

import com.pp.miro.widget.bom.Widget;

public class WidgetTestUtils {

  public static Widget createWidget() {
    return Widget.builder().xCoOrdinate(1).yCoOrdinate(1).widgetWidth(1).widgetHeight(1).build();
  }
  
  public static Widget createWidget(Integer zIndex) {
    return Widget.builder().xCoOrdinate(1).yCoOrdinate(1).zIndex(zIndex).widgetWidth(1).widgetHeight(1).build();
  }

  public static Widget createWidgetWithDifferentXY(Widget widget, int xyIncrement) {
    return Widget.builder().xCoOrdinate(widget.getXCoOrdinate() + xyIncrement)
        .yCoOrdinate(widget.getYCoOrdinate() + xyIncrement)
        .zIndex(widget.getZIndex())
        .widgetHeight(widget.getWidgetHeight())
        .widgetWidth(widget.getWidgetWidth())
        .id(widget.getId())
        .lastModificationDate(widget.getLastModificationDate())
        .build();        
  }
}
