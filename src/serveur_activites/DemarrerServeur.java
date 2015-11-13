/**
 * DemarrerServeur
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

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Manage a {@link DemarrerServeur}
 * @author Sh1fT
 */
public class DemarrerServeur extends Thread {
    private Serveur_Activites parent;
    private ServerSocket sSocket;
    private Socket cSocket;
    private Boolean stop;

    /**
     * Create a new {@link DemarrerServeur} instance
     * @param parent 
     */
    public DemarrerServeur(Serveur_Activites parent) {
        this.setParent(parent);
        this.setsSocket(null);
        this.setcSocket(null);
        this.setStop(false);
    }

    public Serveur_Activites getParent() {
        return this.parent;
    }

    public void setParent(Serveur_Activites parent) {
        this.parent = parent;
    }

    public ServerSocket getsSocket() {
        return sSocket;
    }

    public void setsSocket(ServerSocket sSocket) {
        this.sSocket = sSocket;
    }

    public Socket getcSocket() {
        return cSocket;
    }

    public void setcSocket(Socket cSocket) {
        this.cSocket = cSocket;
    }

    public Boolean getStop() {
        return stop;
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        try {
            this.setsSocket(new ServerSocket(this.getParent().getServerPort()));
            while (!this.getStop()) {
                this.setcSocket(this.getsSocket().accept());
                new Thread(
                    new ActivitiesWorkerRunnable(
                        this.getParent(), this.getcSocket())
                ).start();
            }
        } catch (BindException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            this.setStop(true);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            this.setStop(true);
            System.exit(1);
        }
    }
}