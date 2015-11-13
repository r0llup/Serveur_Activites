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

package protocols;

import beans.BeanDBAccessMySQL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import serveur_activites.Serveur_Activites;

/**
 * Manage a {@link ProtocolHIDP}
 * @author Sh1fT
 */
public class ProtocolHIDP implements interfaces.ProtocolHIDP {
    private Serveur_Activites parent;
    private BeanDBAccessMySQL bdbam;
    public static final int RESPONSE_OK = 100;
    public static final int RESPONSE_KO = 600;
    public static final int GRAPH_SECT = 10;
    public static final int GRAPH_HIST = 20;
    public static final String[] ACTIVITIES = {"cracra-hontah", "extreme trek",
        "orgiac island"};

    /**
     * Create a new {@link ProtocolHIDP} instance
     * @param parent 
     */
    public ProtocolHIDP(Serveur_Activites parent) {
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
                return ProtocolHIDP.RESPONSE_OK;
            return ProtocolHIDP.RESPONSE_KO;
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            this.getBdbam().stop();
            System.exit(1);
        }
        return null;
    }

    /**
     * Demande du nombre d'inscriptions, pour une saison donnée
     * @param year
     * @param activity
     * @return 
     */
    @Override
    public String getStatDescrActiv(Integer year, String activity) {
        try {
            String query = "SELECT idActivite FROM activites WHERE type LIKE ?;";
            PreparedStatement ps = this.getBdbam().getDBConnection().prepareStatement(query);
            ps.setString(1, activity);
            ResultSet rs = this.getBdbam().executeQuery(ps);
            Integer idActivite = null;
            if (rs.next())
                idActivite = rs.getInt("idActivite");
            if (idActivite != null) {
                query = "SELECT COUNT(*) AS cptActivities FROM inscriptions " +
                    "WHERE idActivite = ? AND YEAR(date) = ?;";
                ps = this.getBdbam().getDBConnection().prepareStatement(query);
                ps.setInt(1, idActivite);
                ps.setInt(2, year);
                rs = this.getBdbam().executeQuery(ps);
                Integer cptActivities = null;
                if (rs.next())
                    cptActivities = rs.getInt("cptActivities");
                if (cptActivities != null)
                    return String.valueOf(cptActivities);
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            this.getBdbam().stop();
            System.exit(1);
        }
        return null;
    }

    /**
     * Demande d'un graphique statistique réalisé à l'aide de la librairie
     * JFreechart
     * @param year
     * @param graphType
     * @return 
     */
    @Override
    public JFreeChart getGrActivComp(Integer year, Integer graphType) {
        Integer nbreActivities1 = Integer.parseInt(this.getStatDescrActiv(year,
            ProtocolHIDP.ACTIVITIES[0]));
        Integer nbreActivities2 = Integer.parseInt(this.getStatDescrActiv(year,
            ProtocolHIDP.ACTIVITIES[1]));
        Integer nbreActivities3 = Integer.parseInt(this.getStatDescrActiv(year,
            ProtocolHIDP.ACTIVITIES[2]));
        JFreeChart jfc = null;
        switch (graphType) {
            case ProtocolHIDP.GRAPH_SECT:
                jfc = ChartFactory.createPieChart("Nombre d'inscriptions selon les activitées",
                    this.feedPieDataset(new Integer[] {nbreActivities1, nbreActivities2, nbreActivities3}),
                    true, true, true);
                break;
            case ProtocolHIDP.GRAPH_HIST:
                jfc = ChartFactory.createBarChart("Nombre d'inscriptions selon les activitées",
                    "Activitées", "Inscriptions", this.feedCategoryDataset(
                    new Integer[] {nbreActivities1, nbreActivities2, nbreActivities3}, year),
                    PlotOrientation.VERTICAL, true, true, true);
                break;
            default:
                break;
        }
        return jfc;
    }

    /**
     * Create and add values to the dataset
     * @param vals
     * @return 
     */
    public DefaultPieDataset feedPieDataset(Integer[] vals) {
        Integer cpt = 0;
        if (vals != null) {
            DefaultPieDataset dpd = new DefaultPieDataset();
            for (Integer val : vals) {
                dpd.setValue(new StringBuilder().append(ProtocolHIDP.ACTIVITIES[cpt]).
                    append(" (").append(val).append(")").toString(), val);
                cpt++;
            }
            return dpd;
        }
        return null;
    }

    /**
     * Create and add values to the dataset
     * @param vals
     * @param category
     * @return 
     */
    public DefaultCategoryDataset feedCategoryDataset(Integer[] vals,
        Integer year) {
        Integer cpt = 0;
        if (vals != null) {
            DefaultCategoryDataset dcd = new DefaultCategoryDataset();
            for (Integer val : vals) {
                dcd.addValue(val, year, ProtocolHIDP.ACTIVITIES[cpt]);
                cpt++;
            }
            return dcd;
        }
        return null;
    }

    /**
     * Demande d'un graphique statistique réalisé à l'aide de la librairie
     * JFreechart
     * @param year
     * @return 
     */
    @Override
    public JFreeChart getGrActivEvol(Integer year) {
        Integer nbreActivities1 = Integer.parseInt(this.getStatDescrActiv(year,
            ProtocolHIDP.ACTIVITIES[0]));
        Integer nbreActivities2 = Integer.parseInt(this.getStatDescrActiv(year,
            ProtocolHIDP.ACTIVITIES[1]));
        Integer nbreActivities3 = Integer.parseInt(this.getStatDescrActiv(year,
            ProtocolHIDP.ACTIVITIES[2]));
        JFreeChart jfc = ChartFactory.createTimeSeriesChart(
            "Nombre d'inscriptions selon les activitées",
            "Activitées", "Inscriptions", this.feedXYDataset(new Integer[]
            {nbreActivities1, nbreActivities2, nbreActivities3}, year),
            true, true, true);
        return jfc;
    }

    /**
     * Create and add values to the dataset
     * @param vals
     * @param category
     * @param month
     * @return 
     */
    protected XYDataset feedXYDataset(Integer[] vals, Integer year) {
        if (vals != null) {
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            for (String activity : ProtocolHIDP.ACTIVITIES) {
                TimeSeries ts = new TimeSeries(activity);
                for (Integer val : vals)
                    ts.add(new Day(new Random().nextInt(30)+1,
                        new Random().nextInt(12)+1, year), val);
                dataset.addSeries(ts);
            }
            return dataset;
        }
        return null;
    }

    @Override
    public String getStatInferActiv(Integer year, String activity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JFreeChart getGr2dDepenses(Integer effective) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean testComp(String nationality, Integer seuil) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}