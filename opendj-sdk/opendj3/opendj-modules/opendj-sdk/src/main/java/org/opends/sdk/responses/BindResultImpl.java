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
 *      Copyright 2010 Sun Microsystems, Inc.
 */

package org.opends.sdk.responses;



import org.opends.sdk.ByteString;
import org.opends.sdk.ResultCode;



/**
 * Bind result implementation.
 */
final class BindResultImpl extends AbstractResultImpl<BindResult> implements
    BindResult
{
  private ByteString credentials = null;



  /**
   * Creates a new bind result using the provided result code.
   *
   * @param resultCode
   *          The result code.
   * @throws NullPointerException
   *           If {@code resultCode} was {@code null}.
   */
  BindResultImpl(final ResultCode resultCode) throws NullPointerException
  {
    super(resultCode);
  }



  /**
   * Creates a new bind result that is an exact copy of the provided
   * result.
   *
   * @param bindResult
   *          The bind result to be copied.
   * @throws NullPointerException
   *           If {@code bindResult} was {@code null} .
   */
  BindResultImpl(final BindResult bindResult)
      throws NullPointerException
  {
    super(bindResult);
    this.credentials = bindResult.getServerSASLCredentials();
  }



  /**
   * {@inheritDoc}
   */
  public ByteString getServerSASLCredentials()
  {
    return credentials;
  }



  /**
   * {@inheritDoc}
   */
  public boolean isSASLBindInProgress()
  {
    final ResultCode code = getResultCode();
    return code.equals(ResultCode.SASL_BIND_IN_PROGRESS);
  }



  /**
   * {@inheritDoc}
   */
  public BindResult setServerSASLCredentials(final ByteString credentials)
      throws UnsupportedOperationException
  {
    this.credentials = credentials;
    return this;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("BindResult(resultCode=");
    builder.append(getResultCode());
    builder.append(", matchedDN=");
    builder.append(getMatchedDN());
    builder.append(", diagnosticMessage=");
    builder.append(getDiagnosticMessage());
    builder.append(", referrals=");
    builder.append(getReferralURIs());
    builder.append(", serverSASLCreds=");
    builder.append(getServerSASLCredentials() == null ? ByteString.empty()
        : getServerSASLCredentials());
    builder.append(", controls=");
    builder.append(getControls());
    builder.append(")");
    return builder.toString();
  }



  @Override
  BindResult getThis()
  {
    return this;
  }

}
