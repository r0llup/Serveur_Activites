/**
 * ActivitiesWorkerRunnable
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.xml.sax.InputSource;
import protocols.ProtocolFOAMP;

/**
 * Manage a {@link ActivitiesWorkerRunnable}
 * @author Sh1fT
 */
public class ActivitiesWorkerRunnable implements Runnable {
    private Serveur_Activites parent;
    private Socket cSocket;

    /**
     * Create a new {@link ActivitiesWorkerRunnable} instance
     * @param parent
     * @param cSocket 
     */
    public ActivitiesWorkerRunnable(Serveur_Activites parent, Socket cSocket) {
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
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                    this.getcSocket().getOutputStream()), true);
            String cmd = br.readLine();
            if (cmd.contains("LOGIN")) {
                String username = cmd.split(":")[1];
                String password = cmd.split(":")[2];
                Integer res = this.getParent().getProtocolFOAMP().
                        login(username, password);
                switch (res) {
                    case ProtocolFOAMP.RESPONSE_OK:
                        pw.println("OK");
                        break;
                    case ProtocolFOAMP.RESPONSE_KO:
                        pw.println("KO");
                        break;
                    default:
                        pw.println("KO");
                        break;
                }
            } else if (cmd.contains("BACTFUN")) {
                String type = cmd.split(":")[1];
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                Date date = new Date(sdf.parse(cmd.split(":")[2]).getTime());
                String clientName = cmd.split(":")[3];
                Integer res = this.getParent().getProtocolFOAMP().
                        bookingFunnyActivities(type, date, clientName);
                switch (res) {
                    case ProtocolFOAMP.RESPONSE_OK:
                        pw.println("OK");
                        break;
                    case ProtocolFOAMP.RESPONSE_KO:
                        pw.println("KO");
                        break;
                    default:
                        pw.println("KO");
                        break;
                }
            } else if (cmd.contains("ACKACTFUN")) {
                String type = cmd.split(":")[1];
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                Date date = new Date(sdf.parse(cmd.split(":")[2]).getTime());
                String clientName = cmd.split(":")[3];
                Integer res = this.getParent().getProtocolFOAMP().
                        ackFunnyActivities(type, date, clientName);
                switch (res) {
                    case ProtocolFOAMP.RESPONSE_OK:
                        pw.println("OK");
                        break;
                    case ProtocolFOAMP.RESPONSE_KO:
                        pw.println("KO");
                        break;
                    default:
                        pw.println("KO");
                        break;
                }
            } else if (cmd.contains("BTREKFUN")) {
                String type = cmd.split(":")[1];
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                Date date = new Date(sdf.parse(cmd.split(":")[2]).getTime());
                Integer length = Integer.parseInt(cmd.split(":")[3]);
                String clientName = cmd.split(":")[4];
                Integer res = this.getParent().getProtocolFOAMP().
                        bookingFunnyTrek(type, date, length, clientName);
                switch (res) {
                    case ProtocolFOAMP.RESPONSE_OK:
                        pw.println("OK");
                        break;
                    case ProtocolFOAMP.RESPONSE_KO:
                        pw.println("KO");
                        break;
                    default:
                        pw.println("KO");
                        break;
                }
            }
            pw.close();
            br.close();
            this.getcSocket().close();
            this.getParent().getClientLabel().setText("aucun");
        } catch (IOException | ParseException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }
}