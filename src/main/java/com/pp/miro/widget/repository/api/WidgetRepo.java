package com.pp.miro.widget.repository.api;

import com.pp.miro.widget.bom.Widget;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Widgets.
 */
public interface WidgetRepo {

  /**
   * Store the given widget object in db/store.
   * @param widget
   * @return
   */
  Widget store(Widget widget);

  /** 
   * Retrieve the object for the given ID from db/store.
   */
  Optional<Widget> retrieve(String id);

  /**
   * Delete the object for the given ID from db/store.
   * @param id
   * @return
   */
  boolean delete(String id);

  /**
   * Gets all the widgets from the db/store.
   * @return
   */
  List<Widget> retrieveAllWidgets();

  /**
   * To wipeout all the data in repo. Mainly needed for testing.
   */
  void wipeRepo();

  /**
   * Gets all the widgets within the given CoOrdinate range.
   * @param fromX
   * @param toX
   * @param fromY
   * @param toY
   * @return
   */
  List<Widget> filterWidgetsByCoOrdinateRange(Integer fromX, Integer fromY, Integer toX, Integer toY);
}
