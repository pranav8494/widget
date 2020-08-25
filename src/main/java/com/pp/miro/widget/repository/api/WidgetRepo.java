package com.pp.miro.widget.repository.api;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Widgets.
 */
@Repository
public interface WidgetRepo<T> {

  /**
   * Store the given widget object in db/store.
   * @param widget
   * @return
   */
  T store(T widget);

  /** 
   * Retrieve the object for the given ID from db/store.
   */
  Optional<T> retrieve(String id);

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
  List<T> getAll();
}
