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

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.polepos.reporters.TestDataFactory.createEmptyGraph;

/**
 * @author roman.stoffel@gamlor.info
 * @since 16.09.11
 */
public class TestLoadGraphRenderer {

    @Test
    public void canDealWithEmptyMonitoringResults(){
        Graph emptyGraph = createEmptyGraph();

        LoadGraphRenderer toTest = new LoadGraphRenderer(emptyGraph);


        final Graphics graphics = createStubGraphics();
        toTest.render(graphics);

    }

    private Graphics createStubGraphics() {
        final BufferedImage img = new BufferedImage(500, 400, BufferedImage.TYPE_INT_RGB);
        final Graphics graphics = img.getGraphics();
        graphics.setClip(0,0,500,400);
        return graphics;
    }
}
