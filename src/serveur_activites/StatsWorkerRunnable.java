/**
 * StatsWorkerRunnable
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

package serveur_activites;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.ImageIcon;
import org.jfree.chart.JFreeChart;
import org.xml.sax.InputSource;
import protocols.ProtocolFOAMP;

/**
 * Manage a {@link ActivitiesWorkerRunnable}
 * @author Sh1fT
 */
public class StatsWorkerRunnable implements Runnable {
    private Serveur_Activites parent;
    private Socket cSocket;

    /**
     * Create a new {@link ActivitiesWorkerRunnable} instance
     * @param parent
     * @param cSocket 
     */
    public StatsWorkerRunnable(Serveur_Activites parent, Socket cSocket) {
        this.setParent(parent);
        this.setcSocket(cSocket);
    }

    public Serveur_Activites getParent() {
        return parent;
    }

    public void setParent(Serveur_Activites parent) {
        this.parent = parent;
    }

    public Socket getcSocket() {
        return cSocket;
    }

    public void setcSocket(Socket cSocket) {
        this.cSocket = cSocket;
    }

    public void run() {
        try {
            this.getParent().getClientLabel().setText(
                    this.getcSocket().getInetAddress().getHostAddress());
            InputSource is = new InputSource(new InputStreamReader(
                    this.getcSocket().getInputStream()));
            BufferedReader br = new BufferedReader(is.getCharacterStream());
            ObjectOutputStream oos = new ObjectOutputStream(this.getcSocket().getOutputStream());
            String cmd = br.readLine();
            if (cmd.contains("LOGIN")) {
                String username = cmd.split(":")[1];
                String password = cmd.split(":")[2];
                Integer res = this.getParent().getProtocolHIDP().
                    login(username, password);
                switch (res) {
                    case ProtocolFOAMP.RESPONSE_OK:
                        oos.writeObject("OK");
                        break;
                    case ProtocolFOAMP.RESPONSE_KO:
                        oos.writeObject("KO");
                        break;
                    default:
                        oos.writeObject("KO");
                        break;
                }
            } else if (cmd.contains("GET_STAT_DESCR_ACTIV")) {
                String year = cmd.split(":")[1];
                String activity = cmd.split(":")[2];
                String res = this.getParent().getProtocolHIDP().
                    getStatDescrActiv(Integer.parseInt(year), activity);
                oos.writeObject(res);
            } else if (cmd.contains("GET_GR_ACTIV_COMP")) {
                String year = cmd.split(":")[1];
                String graphType = cmd.split(":")[2];
                JFreeChart res = this.getParent().getProtocolHIDP().
                    getGrActivComp(Integer.parseInt(year),
                    Integer.parseInt(graphType));
                oos.writeObject(new ImageIcon(res.createBufferedImage(500, 300)));
            } else if (cmd.contains("GET_GR_ACTIV_EVOL")) {
                String year = cmd.split(":")[1];
                JFreeChart res = this.getParent().getProtocolHIDP().
                    getGrActivEvol(Integer.parseInt(year));
                oos.writeObject(new ImageIcon(res.createBufferedImage(500, 300)));
            }
            oos.close();
            br.close();
            this.getcSocket().close();
            this.getParent().getClientLabel().setText("aucun");
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }
}