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
 *      Copyright 2009-2010 Sun Microsystems, Inc.
 */

package org.opends.sdk.requests;



import java.util.List;

import org.opends.sdk.DecodeException;
import org.opends.sdk.DecodeOptions;
import org.opends.sdk.controls.Control;
import org.opends.sdk.controls.ControlDecoder;



/**
 * The base class of all Requests provides methods for querying and manipulating
 * the set of Controls included with a Request.
 */
public interface Request
{

  /**
   * Adds the provided control to this request.
   *
   * @param control
   *          The control to be added to this request.
   * @return This request.
   * @throws UnsupportedOperationException
   *           If this request does not permit controls to be added.
   * @throws NullPointerException
   *           If {@code control} was {@code null}.
   */
  Request addControl(Control control) throws UnsupportedOperationException,
      NullPointerException;



  /**
   * Decodes and returns the first control in this request having an OID
   * corresponding to the provided control decoder.
   *
   * @param <C>
   *          The type of control to be decoded and returned.
   * @param decoder
   *          The control decoder.
   * @param options
   *          The set of decode options which should be used when decoding the
   *          control.
   * @return The decoded control, or {@code null} if the control is not included
   *         with this request.
   * @throws DecodeException
   *           If the control could not be decoded because it was malformed in
   *           some way (e.g. the control value was missing, or its content
   *           could not be decoded).
   * @throws NullPointerException
   *           If {@code decoder} or {@code options} was {@code null}.
   */
  <C extends Control> C getControl(ControlDecoder<C> decoder,
      DecodeOptions options) throws DecodeException, NullPointerException;



  /**
   * Returns a {@code List} containing the controls included with this request.
   * The returned {@code List} may be modified if permitted by this request.
   *
   * @return A {@code List} containing the controls.
   */
  List<Control> getControls();

}
