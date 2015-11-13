/**
 * ProtocolFOAMP
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

import java.sql.Date;

/**
 * Manage a {@link ProtocolFOAMP}
 * @author Sh1fT
 */
public interface ProtocolFOAMP {
    public Integer login(String name, String password);
    public Integer bookingFunnyActivities(String type, Date date, String clientName);
    public Integer ackFunnyActivities(String type, Date date, String clientName);
    public Integer bookingFunnyTrek(String type, Date date, Integer duree, String clientName);
}