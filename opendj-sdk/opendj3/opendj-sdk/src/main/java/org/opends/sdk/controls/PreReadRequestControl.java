/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE
 * or https://OpenDS.dev.java.net/OpenDS.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2009 Sun Microsystems, Inc.
 */

package org.opends.sdk.controls;



import static org.opends.sdk.CoreMessages.ERR_PREREADREQ_CANNOT_DECODE_VALUE;
import static org.opends.sdk.CoreMessages.ERR_PREREADREQ_NO_CONTROL_VALUE;
import static org.opends.sdk.CoreMessages.ERR_PREREAD_CONTROL_BAD_OID;

import java.io.IOException;
import java.util.*;

import org.forgerock.i18n.LocalizableMessage;
import org.opends.sdk.ByteString;
import org.opends.sdk.ByteStringBuilder;
import org.opends.sdk.DecodeException;
import org.opends.sdk.DecodeOptions;
import org.opends.sdk.asn1.ASN1;
import org.opends.sdk.asn1.ASN1Reader;
import org.opends.sdk.asn1.ASN1Writer;

import com.forgerock.opendj.util.StaticUtils;
import com.forgerock.opendj.util.Validator;



/**
 * The pre-read request control as defined in RFC 4527. This control allows the
 * client to read the target entry of an update operation immediately before the
 * modifications are applied. These reads are done as an atomic part of the
 * update operation.
 *
 * @see PreReadResponseControl
 * @see <a href="http://tools.ietf.org/html/rfc4527">RFC 4527 - Lightweight
 *      Directory Access Protocol (LDAP) Read Entry Controls </a>
 */
public final class PreReadRequestControl implements Control
{
  /**
   * The IANA-assigned OID for the LDAP pre-read request control used for
   * retrieving an entry in the state it had immediately before an update was
   * applied.
   */
  public static final String OID = "1.3.6.1.1.13.1";

  // The set of raw attributes to return in the entry.
  private final Set<String> attributes;

  private final boolean isCritical;

  private static final PreReadRequestControl CRITICAL_EMPTY_INSTANCE = new PreReadRequestControl(
      true, Collections.<String> emptySet());

  private static final PreReadRequestControl NONCRITICAL_EMPTY_INSTANCE = new PreReadRequestControl(
      false, Collections.<String> emptySet());

  /**
   * A decoder which can be used for decoding the pre-read request control.
   */
  public static final ControlDecoder<PreReadRequestControl> DECODER =
    new ControlDecoder<PreReadRequestControl>()
  {

    public PreReadRequestControl decodeControl(final Control control,
        final DecodeOptions options) throws DecodeException
    {
      Validator.ensureNotNull(control);

      if (control instanceof PreReadRequestControl)
      {
        return (PreReadRequestControl) control;
      }

      if (!control.getOID().equals(OID))
      {
        final LocalizableMessage message = ERR_PREREAD_CONTROL_BAD_OID.get(
            control.getOID(), OID);
        throw DecodeException.error(message);
      }

      if (!control.hasValue())
      {
        // The control must always have a value.
        final LocalizableMessage message = ERR_PREREADREQ_NO_CONTROL_VALUE
            .get();
        throw DecodeException.error(message);
      }

      final ASN1Reader reader = ASN1.getReader(control.getValue());
      Set<String> attributes;
      try
      {
        reader.readStartSequence();
        if (reader.hasNextElement())
        {
          final String firstAttribute = reader.readOctetStringAsString();
          if (reader.hasNextElement())
          {
            attributes = new LinkedHashSet<String>();
            attributes.add(firstAttribute);
            do
            {
              attributes.add(reader.readOctetStringAsString());
            }
            while (reader.hasNextElement());
            attributes = Collections.unmodifiableSet(attributes);
          }
          else
          {
            attributes = Collections.singleton(firstAttribute);
          }
        }
        else
        {
          attributes = Collections.emptySet();
        }
        reader.readEndSequence();
      }
      catch (final Exception ae)
      {
        StaticUtils.DEBUG_LOG.throwing("PreReadRequestControl",
            "decodeControl", ae);

        final LocalizableMessage message = ERR_PREREADREQ_CANNOT_DECODE_VALUE
            .get(ae.getMessage());
        throw DecodeException.error(message, ae);
      }

      if (attributes.isEmpty())
      {
        return control.isCritical() ? CRITICAL_EMPTY_INSTANCE
            : NONCRITICAL_EMPTY_INSTANCE;
      }
      else
      {
        return new PreReadRequestControl(control.isCritical(), attributes);
      }
    }



    public String getOID()
    {
      return OID;
    }
  };



  /**
   * Creates a new pre-read request control.
   *
   * @param isCritical
   *          {@code true} if it is unacceptable to perform the operation
   *          without applying the semantics of this control, or {@code false}
   *          if it can be ignored
   * @param attributes
   *          The list of attributes to be included with the response control.
   *          Attributes that are sub-types of listed attributes are implicitly
   *          included. The list may be empty, indicating that all user
   *          attributes should be returned.
   * @return The new control.
   * @throws NullPointerException
   *           If {@code attributes} was {@code null}.
   */
  public static PreReadRequestControl newControl(final boolean isCritical,
      final Collection<String> attributes) throws NullPointerException
  {
    Validator.ensureNotNull(attributes);

    if (attributes.isEmpty())
    {
      return isCritical ? CRITICAL_EMPTY_INSTANCE : NONCRITICAL_EMPTY_INSTANCE;
    }
    else if (attributes.size() == 1)
    {
      return new PreReadRequestControl(isCritical, Collections
          .singleton(attributes.iterator().next()));
    }
    else
    {
      final Set<String> attributeSet = new LinkedHashSet<String>(attributes);
      return new PreReadRequestControl(isCritical, Collections
          .unmodifiableSet(attributeSet));
    }
  }



  /**
   * Creates a new pre-read request control.
   *
   * @param isCritical
   *          {@code true} if it is unacceptable to perform the operation
   *          without applying the semantics of this control, or {@code false}
   *          if it can be ignored
   * @param attributes
   *          The list of attributes to be included with the response control.
   *          Attributes that are sub-types of listed attributes are implicitly
   *          included. The list may be empty, indicating that all user
   *          attributes should be returned.
   * @return The new control.
   * @throws NullPointerException
   *           If {@code attributes} was {@code null}.
   */
  public static PreReadRequestControl newControl(final boolean isCritical,
      final String... attributes) throws NullPointerException
  {
    Validator.ensureNotNull((Object) attributes);

    if (attributes.length == 0)
    {
      return isCritical ? CRITICAL_EMPTY_INSTANCE : NONCRITICAL_EMPTY_INSTANCE;
    }
    else if (attributes.length == 1)
    {
      return new PreReadRequestControl(isCritical, Collections
          .singleton(attributes[0]));
    }
    else
    {
      final Set<String> attributeSet = new LinkedHashSet<String>(Arrays
          .asList(attributes));
      return new PreReadRequestControl(isCritical, Collections
          .unmodifiableSet(attributeSet));
    }
  }



  private PreReadRequestControl(final boolean isCritical,
      final Set<String> attributes)
  {
    this.isCritical = isCritical;
    this.attributes = attributes;
  }



  /**
   * Returns an unmodifiable set containing the names of attributes to be
   * included with the response control. Attributes that are sub-types of listed
   * attributes are implicitly included. The returned set may be empty,
   * indicating that all user attributes should be returned.
   *
   * @return An unmodifiable set containing the names of attributes to be
   *         included with the response control.
   */
  public Set<String> getAttributes()
  {
    return attributes;
  }



  /**
   * {@inheritDoc}
   */
  public String getOID()
  {
    return OID;
  }



  /**
   * {@inheritDoc}
   */
  public ByteString getValue()
  {
    final ByteStringBuilder buffer = new ByteStringBuilder();
    final ASN1Writer writer = ASN1.getWriter(buffer);
    try
    {
      writer.writeStartSequence();
      if (attributes != null)
      {
        for (final String attr : attributes)
        {
          writer.writeOctetString(attr);
        }
      }
      writer.writeEndSequence();
      return buffer.toByteString();
    }
    catch (final IOException ioe)
    {
      // This should never happen unless there is a bug somewhere.
      throw new RuntimeException(ioe);
    }
  }



  /**
   * {@inheritDoc}
   */
  public boolean hasValue()
  {
    return true;
  }



  /**
   * {@inheritDoc}
   */
  public boolean isCritical()
  {
    return isCritical;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("PreReadRequestControl(oid=");
    builder.append(getOID());
    builder.append(", criticality=");
    builder.append(isCritical());
    builder.append(", attributes=");
    builder.append(attributes);
    builder.append(")");
    return builder.toString();
  }

}
