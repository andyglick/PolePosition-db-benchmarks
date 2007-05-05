/* 
This file is part of the PolePosition database benchmark
http://www.polepos.org

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

package org.polepos.reporters;

import java.awt.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.*;
import org.jfree.ui.*;
import org.polepos.framework.*;


public class ChartBuilder {
    private static final String TIME_CHART_LEGEND_TEXT = " 1  /  log(t + 2)                     better >";
    private static final String MEMORY_CHART_LEGEND_TEXT = " 1  /  log(m + 2)                     better >";
	private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font LEGEND_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font VALUE_LABEL_FONT = new Font("SansSerif", Font.ITALIC, 12);
    private static final Font VALUE_TICKLABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	private static final Font CATEGORY_LABEL_FONT = new Font("SansSerif", Font.ITALIC, 12);
	private static final Font CATEGORY_TICKLABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

	public JFreeChart createTimeChart(Graph graph) {
		CategoryDataset dataset=createTimeDataset(graph);
		return createChart(dataset, TIME_CHART_LEGEND_TEXT);
	}

	public JFreeChart createMemoryChart(Graph graph) {
		CategoryDataset dataset=createMemoryDataset(graph);
		return createChart(dataset, MEMORY_CHART_LEGEND_TEXT);
	}
	
	private JFreeChart createChart(CategoryDataset dataset, String legendText) {
		CategoryAxis categoryAxis = new CategoryAxis("");
		categoryAxis.setLabelFont(CATEGORY_LABEL_FONT);
		categoryAxis.setTickLabelFont(CATEGORY_TICKLABEL_FONT);
		String yLegendText =  legendText;
		ValueAxis valueAxis = new NumberAxis(yLegendText);
		valueAxis.setLabelFont(VALUE_LABEL_FONT);
		valueAxis.setTickLabelFont(VALUE_TICKLABEL_FONT);
		LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, false);
		CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		JFreeChart chart = new JFreeChart("", TITLE_FONT, plot, false);
		StandardLegend legend = new StandardLegend();
		legend.setItemFont(LEGEND_FONT);
		legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
		legend.setBackgroundPaint(Color.white);
		chart.setLegend(legend);
		return chart;
	}
	

	private CategoryDataset createTimeDataset(Graph graph) {
		DefaultCategoryDataset dataset=new DefaultCategoryDataset();
		for(TeamCar teamCar : graph.teamCars()) {
			for(TurnSetup setup : graph.setups()) {
                String legend = "" + setup.getMostImportantValueForGraph();
                double time = graph.timeFor(teamCar,setup);
                double logedTime = toLogedValue(time);
                dataset.addValue(logedTime,(teamCar.toString()),legend);
            }
        }
		return dataset;
	}
	
	private CategoryDataset createMemoryDataset(Graph graph) {
		DefaultCategoryDataset dataset=new DefaultCategoryDataset();
		for(TeamCar teamCar : graph.teamCars()) {
			for(TurnSetup setup : graph.setups()) {
                String legend = "" + setup.getMostImportantValueForGraph();
                double memory = graph.memoryFor(teamCar,setup);
                double logedMemory = toLogedValue(memory);
				dataset.addValue(logedMemory,(teamCar.toString()),legend);
            }
        }
		return dataset;
	}

	protected final double toLogedValue(double value) {
		double logMemory = Math.log(value + 2);
		double valForOutput = 1 /logMemory;
		return valForOutput;
	}

}
