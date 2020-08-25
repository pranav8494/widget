package com.pp.miro.widget.bom;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Class representing Widget.
 */
@Data
@Builder
public class Widget implements Serializable {

  private UUID widgetId;
  private Integer xCoOrdinate;
  private Integer yCoOrdinate;
  private Integer zIndex;
  private Integer widgetWidth;
  private Integer widgetHeight;
  private LocalDateTime lastModificationDate;
}
