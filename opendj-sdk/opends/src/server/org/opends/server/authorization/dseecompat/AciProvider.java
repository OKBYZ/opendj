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
 *      Portions Copyright 2007 Sun Microsystems, Inc.
 */

package org.opends.server.authorization.dseecompat;

import org.opends.server.admin.std.server.DseeCompatAccessControlHandlerCfg;
import org.opends.server.api.AccessControlHandler;
import org.opends.server.api.AccessControlProvider;
import org.opends.server.config.ConfigException;
import org.opends.server.types.InitializationException;

/**
 * This class is the provider class for the dseecompt ACI.
 */
public class AciProvider implements
    AccessControlProvider<DseeCompatAccessControlHandlerCfg>
{

    /*
     * The AciHandler does all the work in this package.
     */
    private static AciHandler instance = null;

    /**
     * Create an aci provider.
     */
    public AciProvider() {
        super();
    }


    /**
     * Creates the AciHandler class and calls its initialization
     * method.
     *
     * @param configuration
     *          The entry containing the configuration Access Control
     *          entry.
     * @throws ConfigException
     *           If the initialization fails.
     * @throws InitializationException
     *           If the initialization fails.
     */
    public void initializeAccessControlHandler(
      DseeCompatAccessControlHandlerCfg configuration)
    throws ConfigException, InitializationException {
         instance=new AciHandler(configuration);
    }

    /**
     * Returns a new AciHandler instance. There can be only one active.
     * @return  A new AciHandler instance.
     */
    public  AccessControlHandler getInstance() {
        return instance;
    }

    /**
     * Not used at this time.
     */
    public void finalizeAccessControlHandler() {

    }
}
