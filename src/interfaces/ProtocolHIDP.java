/**
 * ProtocolHIDP
 *
 * Copyright (C) 2012 Sh1fT
 *
 * This file is part of Serveur_Activites.
 *
 * Serveur_Activites is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * Serveur_Activites is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Serveur_Activites; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package interfaces;

import org.jfree.chart.JFreeChart;

/**
 * Manage a {@link ProtocolHIDP}
 * @author Sh1fT
 */
public interface ProtocolHIDP {
    public Integer login(String name, String password);
    public String getStatDescrActiv(Integer year, String activity);
    public JFreeChart getGrActivComp(Integer year, Integer graphType);
    public JFreeChart getGrActivEvol(Integer year);
    public String getStatInferActiv(Integer year, String activity);
    public JFreeChart getGr2dDepenses(Integer effective);
    public Boolean testComp(String nationality, Integer seuil);
}