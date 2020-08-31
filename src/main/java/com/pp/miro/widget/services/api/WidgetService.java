package com.pp.miro.widget.services.api;

import com.pp.miro.widget.bom.Widget;

import java.util.List;
import java.util.Optional;

/**
 * Service API for {@link Widget}.
 */
public interface WidgetService {

  /**
   * creates a new {@link Widget}
   *
   * @param widget
   * @return Widget created.
   */
  Widget saveWidget(Widget widget);

  /**
   * updates the given Widget.
   *
   * @param widget
   * @return
   */
  Widget updateWidget(Widget widget);

  /**
   * Get {@link Widget} by id.
   *
   * @param id
   * @return Optional of Widget if it exists else {@link Optional#EMPTY}.
   */
  Optional<Widget> retrieveWidget(String id);

  /**
   * Gets all the widgets from data repo.
   *
   * @return
   */
  List<Widget> retrieveAllWidgets();

  /**
   * Delete the Widget with given ID if found.
   *
   * @param id
   * @return <code>true</code> if successfully deleted, else <code>false</code>.
   */
  boolean deleteWidget(String id);

  /**
   * Gets all the widgets within the given CoOrdinate range.
   * @param fromX
   * @param fromY
   * @param toX
   * @param toY
   * @return
   */
  List<Widget> filterWidgetsByCoOrdinateRange(Integer fromX, Integer fromY, Integer toX, Integer toY);
}
