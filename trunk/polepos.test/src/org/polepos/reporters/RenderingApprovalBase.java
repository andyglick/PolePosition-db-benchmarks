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

package org.polepos.reporters;

import org.junit.Before;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public abstract class RenderingApprovalBase {

    private BufferedImage renderTarget;
    private Graphics2D graphic;

    @Before
    public final void setup(){
        this.renderTarget = new BufferedImage(640,480,BufferedImage.TYPE_INT_ARGB);
        this.graphic = renderTarget.createGraphics();
        graphic.setClip(0,0,640,480);
        additionalSetup();
    }

    protected void additionalSetup() {

    }

    public final BufferedImage image() {
        return renderTarget;
    }

    public final Graphics2D graphic() {
        return graphic;
    }
}
