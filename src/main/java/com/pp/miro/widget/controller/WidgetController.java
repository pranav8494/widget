package com.pp.miro.widget.controller;

import com.pp.miro.widget.bom.Widget;
import com.pp.miro.widget.services.api.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping(value = "/widget")
public class WidgetController {

  private WidgetService widgetService;

  @Autowired
  public WidgetController(WidgetService widgetService) {

    this.widgetService = widgetService;
  }

  @PostMapping
  @ResponseBody
  public ResponseEntity<Widget> create(@RequestBody Widget widget) {

    Widget savedWidget = widgetService.saveWidget(widget);
    return new ResponseEntity<>(savedWidget, HttpStatus.OK);
  }

  @PutMapping
  @ResponseBody
  public ResponseEntity update(@RequestBody Widget widget) {
    Widget updatedWidget = widgetService.updateWidget(widget);
    return new ResponseEntity<>(updatedWidget, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Widget> retrieve(@PathVariable("id") String id) {

    return widgetService.retrieveWidget(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/all")
  public Collection<Widget> getAll() {

    return widgetService.retrieveAllWidgets();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity delete(@PathVariable("id") String id) {

    return widgetService.deleteWidget(id) ? 
        ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }

  @GetMapping("/filter/{fromX}/{fromY}/{toX}/{toY}")
  public Collection<Widget> filterWidgetsByCoOrdinateRange(@PathVariable("fromX") Integer fromX, 
                                                           @PathVariable("fromY") Integer fromY,
                                                           @PathVariable("toX") Integer toX,
                                                           @PathVariable("toY") Integer toY) {

    return widgetService.filterWidgetsByCoOrdinateRange(fromX, fromY, toX, toY);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public final String illegalArgumentExceptionHandler(final IllegalArgumentException e) {

    return '"' + e.getMessage() + '"';
  }

}
