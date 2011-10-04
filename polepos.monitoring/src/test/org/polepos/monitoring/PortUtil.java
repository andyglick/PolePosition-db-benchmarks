/*
 * This file is part of the PolePosition database benchmark
 * http://www.polepos.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA  02111-1307, USA.MA  02111-1307, USA.
 */

package org.polepos.monitoring;

import java.io.IOException;
import java.net.ServerSocket;

import static org.polepos.util.JavaLangUtils.rethrow;

/**
 * @author roman.stoffel@gamlor.info
 * @since 28.09.11
 */
public final class PortUtil {


    public static int getFreePort(){
        ServerSocket s = null;
        try {
            s = new ServerSocket(0);
            int port = s.getLocalPort();
            s.close();
            return port;
        } catch (IOException e) {
            throw rethrow(e);
        }
    }

}
