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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.polepos.framework.SetupProperty;
import org.polepos.framework.TeamCar;
import org.polepos.framework.TurnSetup;
import org.polepos.util.MathUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public abstract class RenderingReporter extends GraphReporter{
    
    private static final int MAX_PROPERTIES_TO_DISPLAY = 6;

	public RenderingReporter(String path) {
		super(path);
		
	}

	protected final DefaultCategoryDataset _overviewTimeDataset = new DefaultCategoryDataset();
	protected final DefaultCategoryDataset _overviewMemoryDataset = new DefaultCategoryDataset();
	protected final DefaultCategoryDataset _overviewSizeDataset = new DefaultCategoryDataset();
	
	protected void render() {
		List<TeamCar> cars = null;
        if(mGraphs != null){
            OverViewChartBuilder overViewChartBuilder = new OverViewChartBuilder();
            for (Graph graph : mGraphs.values()) {
                if(graph != null){
                	if(cars == null) {
                		cars = graph.teamCars();
                	}
                    report(graph);
                    reportOverviewDatabaseSize(graph);
                    overViewChartBuilder.report(graph);
                }
			}
            overViewChartBuilder.createJPGs(path());
			finish(cars);
        }
	}
    
	protected List<JFreeChart> createTimeChart(Graph graph) {
		List<JFreeChart> list = new ArrayList<JFreeChart>();
		// list.add(createChart(createInverseLogTimeDataset(graph), ReporterConstants.OLD_LOGARITHMIC_TIME_CHART_LEGEND));
		
		boolean bestOnTop = true;
		String legendText = bestOnTop ? ReporterConstants.TIME_CHART_LEGEND_BEST_ON_TOP : ReporterConstants.TIME_CHART_LEGEND_BEST_BELOW;
		
		// list.add(createOrderOfMagnitudeChart(createBaseLineTimeDataset(graph, bestOnTop), legendText, bestOnTop));
		
		list.add(
			createBestAsBaseLineChart(
					createBestAsBaseLineTimeDataset(graph, bestOnTop), 
					ReporterConstants.BEST_AS_BASELINE_CHART_LEGEND, 
					bestOnTop));
		
		return list;
	}
	
	protected JFreeChart createMemoryChart(Graph graph) {
		CategoryDataset dataset = createMemoryDataset(graph);
		return createChart(dataset, ReporterConstants.MEMORY_CHART_LEGEND);
	}
	
	public CategoryDataset createInverseLogTimeDataset(Graph graph) {
		DefaultCategoryDataset dataset=new DefaultCategoryDataset();
		int currentTimeIndex = timeIndex;
		for(TeamCar teamCar : graph.teamCars()) {
			timeIndex = currentTimeIndex;
			for(TurnSetup setup : graph.setups()) {
				String legend = "" + setup.getMostImportantValueForGraph();
	            double time = graph.timeFor(teamCar,setup);
	            double logedTime = MathUtil.toLogedValue(time);
	            dataset.addValue(logedTime,teamCar.toString(),legend);
	            String xName = ""+ ++timeIndex;
				_overviewTimeDataset.addValue(logedTime,teamCar.toString(),xName);
	        }
	    }
		return dataset;
	}
	
	public CategoryDataset createBaseLineTimeDataset(final Graph graph, boolean bestOnTop) {
		DefaultCategoryDataset dataset=new DefaultCategoryDataset();
		
		int setupCount = graph.setups().size();
		double[] averages = new double[setupCount];
		
		int i = 0;
		for(TurnSetup setup : graph.setups()) {
			double sum = 0;
			for(TeamCar teamCar : graph.teamCars()) {
	            double time = graph.timeFor(teamCar,setup);
	            sum+=time;
	        }
			averages[i] = sum/graph.teamCars().size();
			i++;
	    }
		
		for(TeamCar teamCar : graph.teamCars()) {
			i = 0;
			for(TurnSetup setup : graph.setups()) {
				String legend = "" + setup.getMostImportantValueForGraph();
	            double time = graph.timeFor(teamCar,setup);
	            double graphValue = logarithmicMagnitudeGraphValue(averages[i], time, bestOnTop);
	            dataset.addValue(graphValue,teamCar.toString(),legend);
	            i++;
	        }
	    }
		return dataset;
	}
	
	public CategoryDataset createBestAsBaseLineTimeDataset(final Graph graph, boolean bestOnTop) {
		DefaultCategoryDataset dataset=new DefaultCategoryDataset();
		
		int setupCount = graph.setups().size();
		double[] best = new double[setupCount];
		
		int i = 0;
		for(TurnSetup setup : graph.setups()) {
			best[i] = Double.MAX_VALUE;
			for(TeamCar teamCar : graph.teamCars()) {
	            double time = graph.timeFor(teamCar,setup);
	            if(time < best[i]){
	            	best[i] = time;
	            }
	        }
			i++;
	    }
		
		for(TeamCar teamCar : graph.teamCars()) {
			i = 0;
			for(TurnSetup setup : graph.setups()) {
				String legend = "" + setup.getMostImportantValueForGraph();
	            double time = graph.timeFor(teamCar,setup);
	            double graphValue = logarithmicMagnitudeGraphValue(best[i], time, bestOnTop);
	            dataset.addValue(graphValue,teamCar.toString(),legend);
	            i++;
	        }
	    }
		return dataset;
	}

	

	protected static String legend(TurnSetup setup) {
		String legend = "";
		boolean first = true;
		int count = 0;
		for (SetupProperty sp : setup.properties()) {
			if (!first) {
				legend += ", ";
			}
			String name = sp.name();
			if (!name.equals("commitinterval")) {
				legend += name + "=" + sp.value();
				first = false;
			}
			count++;
			if(count >= MAX_PROPERTIES_TO_DISPLAY){
				return legend;
			}
		}

		return legend;
	}

	
	double logarithmicMagnitudeGraphValue(double average, double time, boolean bestOnTop){
		if(average == time){
			return 0;
		}
		if(time > average){
			double magnitude = time / average;
			magnitude = Math.log10(magnitude);
			return bestOnTop ? -magnitude : magnitude;
		}
		double magnitude = average / time;
		magnitude = Math.log10(magnitude);
		return bestOnTop ? magnitude : -magnitude;
	}

	private CategoryDataset createMemoryDataset(Graph graph) {
		DefaultCategoryDataset dataset=new DefaultCategoryDataset();
		int currentMemoryIndex = memoryIndex;
		for(TeamCar teamCar : graph.teamCars()) {
			memoryIndex = currentMemoryIndex;
			for(TurnSetup setup : graph.setups()) {
	            String legend = "" + setup.getMostImportantValueForGraph();
	            double memory = graph.memoryFor(teamCar,setup);
	            double logedMemory = MathUtil.toLogedValue(memory);
				dataset.addValue(logedMemory,(teamCar.toString()),legend);
				String xName = ""+ ++memoryIndex;
				_overviewMemoryDataset.addValue(logedMemory,(teamCar.toString()),xName);
	        }
	    }
		return dataset;
	}

	private void reportOverviewDatabaseSize(Graph graph) {
		int currentSizeIndex = sizeIndex;
		for(TeamCar teamCar : graph.teamCars()) {
			sizeIndex = currentSizeIndex;
			for(TurnSetup setup : graph.setups()) {
	            double databaseSize = graph.sizeFor(teamCar,setup);
	            double logedSize = MathUtil.toLogedValue(databaseSize);
	            String xName = "" + ++sizeIndex;
				_overviewSizeDataset.addValue(logedSize,(teamCar.toString()),xName);
	        }
	    }
	}
	
	public JFreeChart createChart(CategoryDataset dataset, String legendText) {
		CategoryAxis categoryAxis = new CategoryAxis("");
		categoryAxis.setLabelFont(ReporterConstants.CATEGORY_LABEL_FONT);
		categoryAxis.setTickLabelFont(ReporterConstants.CATEGORY_TICKLABEL_FONT);
        ValueAxis valueAxis = new NumberAxis(legendText);
		valueAxis.setLabelFont(ReporterConstants.VALUE_LABEL_FONT);
		valueAxis.setTickLabelFont(ReporterConstants.VALUE_TICKLABEL_FONT);
		LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, false);
		CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		JFreeChart chart = new JFreeChart("", ReporterConstants.TITLE_FONT, plot, true);
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(ReporterConstants.LEGEND_FONT);
		legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
		legend.setBackgroundPaint(Color.white);
		return chart;
	}
	
	public JFreeChart createOrderOfMagnitudeChart(CategoryDataset dataset, String legendText, boolean bestOnTop) {
		CategoryAxis categoryAxis = new CategoryAxis("");
		categoryAxis.setLabelFont(ReporterConstants.CATEGORY_LABEL_FONT);
		categoryAxis.setTickLabelFont(ReporterConstants.CATEGORY_TICKLABEL_FONT);
		String yLegendText =  legendText;
		ValueAxis valueAxis = new NumberAxis(yLegendText);
		valueAxis.setLabelFont(ReporterConstants.VALUE_LABEL_FONT);
		valueAxis.setTickLabelFont(ReporterConstants.VALUE_TICKLABEL_FONT);
		LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, false);
		CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.getRangeAxis().centerRange(0);
		plot.getRangeAxis().setRange(-3, 3);
		JFreeChart chart = new JFreeChart("", ReporterConstants.TITLE_FONT, plot, true);
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(ReporterConstants.LEGEND_FONT);
		legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
		legend.setBackgroundPaint(Color.white);
		return chart;
	}
	
	public JFreeChart createBestAsBaseLineChart(CategoryDataset dataset, String legendText, boolean bestOnTop) {
		CategoryAxis categoryAxis = new CategoryAxis("");
		categoryAxis.setLabelFont(ReporterConstants.CATEGORY_LABEL_FONT);
		categoryAxis.setTickLabelFont(ReporterConstants.CATEGORY_TICKLABEL_FONT);

		String yLegendText =  legendText;
		ValueAxis valueAxis = new NumberAxis(yLegendText);
		valueAxis.setLabelFont(ReporterConstants.VALUE_LABEL_FONT);
		valueAxis.setTickLabelFont(ReporterConstants.VALUE_TICKLABEL_FONT);
		LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, false);
		CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.getRangeAxis().centerRange(-2);
		plot.getRangeAxis().setRange(-4, 1);
		
		JFreeChart chart = new JFreeChart("", ReporterConstants.TITLE_FONT, plot, true);
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(ReporterConstants.LEGEND_FONT);
		legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
		legend.setBackgroundPaint(Color.white);
		return chart;
	}

	@Override
	public void endSeason() {
		List<TeamCar> cars = null;
        OverViewChartBuilder overViewChartBuilder = new OverViewChartBuilder();
        for (Graph graph : mGraphs.values()) {
            if(graph != null){
                if(cars == null) {
                    cars = graph.teamCars();
                }
                report(graph);
                reportOverviewDatabaseSize(graph);
                overViewChartBuilder.report(graph);
            }
        }
        overViewChartBuilder.createJPGs(path());
        finish(cars);
	}
	
	protected abstract void finish(List <TeamCar> cars);


}
