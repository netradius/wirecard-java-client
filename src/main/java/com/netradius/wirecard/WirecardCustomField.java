package com.netradius.wirecard;

import com.netradius.wirecard.schema.CustomField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static com.netradius.wirecard.util.ValidationUtils.checkForNull;
import static com.netradius.wirecard.util.ValidationUtils.checkMaxLength;

/**
 * Wirecard’s Payment Gateway also permits the storage and later retrieval of additional
 * information. The use of ‘Custom Fields’ permits the client application to store
 * key-value pairs with each transaction.
 *
 * @author Erik R. Jensen
 */
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class WirecardCustomField implements Validated {

  /**
   * The name of the custom field.
   * This field is optional and limited to 36 characters.
   */
  protected String name;

  /**
   * The value of the custom field.
   * This field is optional and limited to 256 characters.
   */
  protected String value;

  @Override
  public void validate() {
    checkForNull(name);
    checkMaxLength(name, 36);
    checkForNull(value);
    checkMaxLength(value, 256);
  }

  protected CustomField getCustomField() {
    validate();
    CustomField customField = new CustomField();
    customField.setFieldName(name);
    customField.setFieldValue(value);
    return customField;
  }
}
