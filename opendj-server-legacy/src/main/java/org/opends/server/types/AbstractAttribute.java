/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at legal-notices/CDDLv1_0.txt
 * or http://forgerock.org/license/CDDLv1.0.html.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at legal-notices/CDDLv1_0.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2006-2008 Sun Microsystems, Inc.
 *      Portions Copyright 2014-2015 ForgeRock AS
 */
package org.opends.server.types;

import java.util.Collection;
import java.util.Set;

import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.DecodeException;
import org.forgerock.opendj.ldap.schema.MatchingRule;

/** An abstract base class for implementing new types of {@link Attribute}. */
@org.opends.server.types.PublicAPI(
    stability = org.opends.server.types.StabilityLevel.UNCOMMITTED,
    mayInstantiate = false,
    mayExtend = false,
    mayInvoke = true)
public abstract class AbstractAttribute implements Attribute
{
  /** Creates a new abstract attribute. */
  protected AbstractAttribute()
  {
    // No implementation required.
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation iterates through each attribute value in the
   * provided collection, checking to see if this attribute contains
   * the value using {@link #contains(ByteString)}.
   */
  @Override
  public boolean containsAll(Collection<ByteString> values)
  {
    for (ByteString value : values)
    {
      if (!contains(value))
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public final boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof Attribute))
    {
      return false;
    }

    Attribute a = (Attribute) o;
    return getAttributeType().equals(a.getAttributeType())
        && valuesEqual(a)
        && optionsEqual(a.getOptions());
  }

  private boolean valuesEqual(Attribute a)
  {
    if (size() != a.size())
    {
      return false;
    }

    for (ByteString v : a)
    {
      if (!contains(v))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation returns the primary name associated with this
   * attribute's attribute type or, if there is no primary name, the
   * attribute type's OID.
   */
  @Override
  public String getName()
  {
    return getAttributeType().getNameOrOID();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation returns this attribute's name if there are no
   * attribute options, otherwise it constructs a string comprising of
   * this attribute's name followed by a semi-colon and a semi-colon
   * separated list of its attribute options.
   */
  @Override
  public String getNameWithOptions()
  {
    if (!hasOptions())
    {
      return getName();
    }

    StringBuilder buffer = new StringBuilder();
    buffer.append(getName());
    for (String option : getOptions())
    {
      buffer.append(';');
      buffer.append(option);
    }
    return buffer.toString();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation returns {@code true} if the provided
   * collection of options is {@code null} or empty. If the
   * collection is non-empty and this attribute does not have any
   * options then it returns {@code false}. Otherwise, {@code true} is
   * returned if all the provided options are present.
   */
  @Override
  public boolean hasAllOptions(Collection<String> options)
  {
    return AttributeDescription.containsAllOptions(getOptions(), options);
  }

  @Override
  public int hashCode()
  {
    int hashCode = getAttributeType().hashCode();
    for (ByteString value : this)
    {
      try
      {
        MatchingRule eqRule = getAttributeType().getEqualityMatchingRule();
        hashCode += eqRule.normalizeAttributeValue(value).hashCode();
      }
      catch (DecodeException e)
      {
        hashCode += value.hashCode();
      }
    }
    return hashCode;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation calls {@link #getOptions()} to
   * retrieve this attribute's set of options and then compares them
   * one at a time against the provided option. All comparisons are
   * case insensitive.
   */
  @Override
  public boolean hasOption(String option)
  {
    return AttributeDescription.containsOption(getOptions(), option);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation retrieves the set of options associated with
   * this attribute and tests to see if it is empty.
   */
  @Override
  public boolean hasOptions()
  {
    return !getOptions().isEmpty();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation returns <code>true</code> if the
   * {@link #size()} of this attribute is zero.
   */
  @Override
  public boolean isEmpty()
  {
    return size() == 0;
  }

  @Override
  public boolean isReal()
  {
    return !isVirtual();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation returns !{@link #hasOptions()} if the
   * provided set of options is <code>null</code>. Otherwise it
   * checks that the size of the provided set of options is equal to
   * the size of this attribute's options, return <code>false</code>
   * if the sizes differ. If the sizes are the same then each option
   * in the provided set is checked using
   * {@link #hasOption(String)} and <code>true</code> is
   * returned if all the provided options are present.
   */
  @Override
  public boolean optionsEqual(Set<String> options)
  {
    if (options != null)
    {
      return getOptions().size() == options.size()
          && hasAllOptions(options);
    }
    return !hasOptions();
  }

  @Override
  public final String toString()
  {
    StringBuilder buffer = new StringBuilder();
    toString(buffer);
    return buffer.toString();
  }
}
