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
 *      Copyright 2008 Sun Microsystems, Inc.
 *      Portions Copyright 2014-2015 ForgeRock AS
 */

package org.opends.guitools.controlpanel.ui;

import static org.opends.messages.AdminToolMessages.*;

import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.opends.guitools.controlpanel.util.Utilities;
import org.forgerock.i18n.LocalizableMessage;

/**
 * The panel to create a domain.
 *
 */
public class NewDomainPanel extends NewOrganizationPanel
{
  private static final long serialVersionUID = -595396547491445219L;

  /** {@inheritDoc} */
  public LocalizableMessage getTitle()
  {
    return INFO_CTRL_NEW_DOMAIN_PANEL_TITLE.get();
  }

  /** {@inheritDoc} */
  protected LocalizableMessage getProgressDialogTitle()
  {
    return INFO_CTRL_NEW_DOMAIN_PANEL_TITLE.get();
  }

  /** {@inheritDoc} */
  protected void checkSyntax(ArrayList<LocalizableMessage> errors)
  {
    for (JLabel label : labels)
    {
      setPrimaryValid(label);
    }

    JTextField[] requiredFields = {name};
    LocalizableMessage[] msgs = {ERR_CTRL_PANEL_NAME_OF_DOMAIN_REQUIRED.get()};
    for (int i=0; i<requiredFields.length; i++)
    {
      String v = requiredFields[i].getText().trim();
      if (v.length() == 0)
      {
        errors.add(msgs[i]);
      }
    }
  }

  /** {@inheritDoc} */
  protected void updateDNValue()
  {
    String value = name.getText().trim();
    if (value.length() > 0)
    {
       String rdn = Utilities.getRDNString("dc", value);
          dn.setText(rdn+","+parentNode.getDN());
    }
    else
    {
      dn.setText(","+parentNode.getDN());
    }
  }

  /** {@inheritDoc} */
  protected String getLDIF()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("dn: ").append(dn.getText()).append("\n");
    String[] attrNames = {"dc", "description"};
    JTextField[] textFields = {name, description};
    sb.append("objectclass: top\n");
    sb.append("objectclass: domain\n");
    for (int i=0; i<attrNames.length; i++)
    {
      String value = textFields[i].getText().trim();
      if (value.length() > 0)
      {
        sb.append(attrNames[i]).append(": ").append(value).append("\n");
      }
    }
    return sb.toString();
  }
}
