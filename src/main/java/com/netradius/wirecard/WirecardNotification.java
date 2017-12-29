package com.netradius.wirecard;

import com.netradius.wirecard.schema.Notification;
import com.netradius.wirecard.schema.TransactionState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static com.netradius.wirecard.util.ValidationUtils.checkForEmpty;

/**
 * Holds the Wirecard notification data.
 *
 * @author Erik R. Jensen
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class WirecardNotification implements Validated {

  /**
   * Optional stat on which to be notified.
   */
  protected TransactionState transactionState;

  /**
   * Required https:// url or mailto: link.
   */
  protected String url;

  public void validate() {
    checkForEmpty(url);
    if (!(url.startsWith("https://") || url.startsWith("mailto:"))) {
      throw new IllegalArgumentException("Notification URL must start with https:// or mailto:");
    }
  }

  protected Notification getNotification() {
    validate();
    Notification notification = new Notification();
    notification.setUrl(url);
    if (transactionState != null) {
      notification.setTransactionState(transactionState);
    }
    return notification;
  }

}
