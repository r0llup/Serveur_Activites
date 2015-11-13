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

package protocols;

import beans.BeanDBAccessMySQL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import serveur_activites.Serveur_Activites;

/**
 * Manage a {@link ProtocolFOAMP}
 * @author Sh1fT
 */
public class ProtocolFOAMP implements interfaces.ProtocolFOAMP {
    private Serveur_Activites parent;
    private BeanDBAccessMySQL bdbam;
    public static final int RESPONSE_OK = 100;
    public static final int RESPONSE_KO = 600;

    /**
     * Create a new {@link ProtocolFOAMP} instance
     * @param parent 
     */
    public ProtocolFOAMP(Serveur_Activites parent) {
        this.setParent(parent);
        this.setBdbam(new BeanDBAccessMySQL(
                System.getProperty("file.separator") +"properties" +
                System.getProperty("file.separator") + "BeanDBAccessMySQL.properties"));
    }

    public Serveur_Activites getParent() {
        return parent;
    }

    public void setParent(Serveur_Activites parent) {
        this.parent = parent;
    }

    public BeanDBAccessMySQL getBdbam() {
        return bdbam;
    }

    public void setBdbam(BeanDBAccessMySQL bdbam) {
        this.bdbam = bdbam;
    }

    /**
     * Effectue la connexion pour un utilisateur
     * @param name
     * @param password
     * @return 
     */
    @Override
    public Integer login(String name, String password) {
        try {
            String query = "SELECT * FROM voyageurs WHERE nom LIKE ? " +
                "AND password LIKE ?;";
            PreparedStatement ps = this.getBdbam().getDBConnection().prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = this.getBdbam().executeQuery(ps);
            if (rs.next())
                return ProtocolFOAMP.RESPONSE_OK;
            return ProtocolFOAMP.RESPONSE_KO;
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            this.getBdbam().stop();
            System.exit(1);
        }
        return null;
    }

    /**
     * Inscription à une activité du jour
     * @param type
     * @param date
     * @param clientName
     * @return 
     */
    @Override
    public Integer bookingFunnyActivities(String type, Date date, String clientName) {
        try {
            String query = "SELECT idActivite FROM activites WHERE type LIKE ?;";
            PreparedStatement ps = this.getBdbam().getDBConnection().prepareStatement(query);
            ps.setString(1, type);
            ResultSet rs = this.getBdbam().executeQuery(ps);
            Integer idActivite = null;
            if (rs.next())
                idActivite = rs.getInt("idActivite");
            if (idActivite != null) {
                query = "SELECT idVoyageur FROM voyageurs WHERE nom LIKE ?;";
                ps = this.getBdbam().getDBConnection().prepareStatement(query);
                ps.setString(1, clientName);
                rs = this.getBdbam().executeQuery(ps);
                Integer idVoyageur = null;
                if (rs.next())
                    idVoyageur = rs.getInt("idVoyageur");
                if (idVoyageur != null) {
                    query = "INSERT INTO inscriptions VALUES(?, ?, ?, NULL, FALSE);";
                    ps = this.getBdbam().getDBConnection().prepareStatement(query);
                    ps.setInt(1, idActivite);
                    ps.setInt(2, idVoyageur);
                    ps.setDate(3, date);
                    Integer rss = this.getBdbam().executeUpdate(ps);
                    if (rss == 1) {
                        this.getBdbam().getDBConnection().commit();
                        return ProtocolFOAMP.RESPONSE_OK;
                    } else
                        return ProtocolFOAMP.RESPONSE_KO;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            this.getBdbam().stop();
            System.exit(1);
        }
        return null;
    }

    /**
     * Acceptation de la date proposée
     * @param type
     * @param date
     * @param clientName
     * @return 
     */
    @Override
    public Integer ackFunnyActivities(String type, Date date, String clientName) {
    try {
            String query = "SELECT idActivite FROM activites WHERE type LIKE ?;";
            PreparedStatement ps = this.getBdbam().getDBConnection().prepareStatement(query);
            ps.setString(1, type);
            ResultSet rs = this.getBdbam().executeQuery(ps);
            Integer idActivite = null;
            if (rs.next())
                idActivite = rs.getInt("idActivite");
            if (idActivite != null) {
                query = "SELECT idVoyageur FROM voyageurs WHERE nom LIKE ?;";
                ps = this.getBdbam().getDBConnection().prepareStatement(query);
                ps.setString(1, clientName);
                rs = this.getBdbam().executeQuery(ps);
                Integer idVoyageur = null;
                if (rs.next())
                    idVoyageur = rs.getInt("idVoyageur");
                if (idVoyageur != null) {
                    query = "UPDATE inscriptions SET confirmed = ? WHERE " +
                        "idActivite = ? AND idVoyageur = ? AND date = ?;";
                    ps = this.getBdbam().getDBConnection().prepareStatement(query);
                    ps.setBoolean(1, true);
                    ps.setInt(2, idActivite);
                    ps.setInt(3, idVoyageur);
                    ps.setDate(4, date);
                    Integer rss = this.getBdbam().executeUpdate(ps);
                    if (rss == 1) {
                        this.getBdbam().getDBConnection().commit();
                        return ProtocolFOAMP.RESPONSE_OK;
                    } else
                        return ProtocolFOAMP.RESPONSE_KO;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            this.getBdbam().stop();
            System.exit(1);
        }
        return null;
    }

    /**
     * Inscription à une activité d'une certaine durée
     * @param type
     * @param date
     * @param duree
     * @param clientName
     * @return 
     */
    @Override
    public Integer bookingFunnyTrek(String type, Date date, Integer duree, String clientName) {
           try {
            String query = "SELECT idActivite FROM activites WHERE type LIKE ?;";
            PreparedStatement ps = this.getBdbam().getDBConnection().prepareStatement(query);
            ps.setString(1, type);
            ResultSet rs = this.getBdbam().executeQuery(ps);
            Integer idActivite = null;
            if (rs.next())
                idActivite = rs.getInt("idActivite");
            if (idActivite != null) {
                query = "SELECT idVoyageur FROM voyageurs WHERE nom LIKE ?;";
                ps = this.getBdbam().getDBConnection().prepareStatement(query);
                ps.setString(1, clientName);
                rs = this.getBdbam().executeQuery(ps);
                Integer idVoyageur = null;
                if (rs.next())
                    idVoyageur = rs.getInt("idVoyageur");
                if (idVoyageur != null) {
                    query = "INSERT INTO inscriptions VALUES(?, ?, ?, ?, FALSE);";
                    ps = this.getBdbam().getDBConnection().prepareStatement(query);
                    ps.setInt(1, idActivite);
                    ps.setInt(2, idVoyageur);
                    ps.setDate(3, date);
                    ps.setInt(4, duree);
                    Integer rss = this.getBdbam().executeUpdate(ps);
                    if (rss == 1) {
                        this.getBdbam().getDBConnection().commit();
                        return ProtocolFOAMP.RESPONSE_OK;
                    } else
                        return ProtocolFOAMP.RESPONSE_KO;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            this.getBdbam().stop();
            System.exit(1);
        }
        return null;
    }
}