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

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectSet;
import org.polepos.framework.TeamCar;
import org.polepos.framework.TurnSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public abstract class CustomBarRendererBase implements ChartRenderer{

	private Graphics graphics;
	private GraphData graphData;
	private List<TurnData> runs;
	protected final Graph graph;

	public CustomBarRendererBase(Graph graph) {
		this.graph = graph;
	}
	
	protected static class TurnData {
		
			public TurnData(TurnSetup turn, long best) {
				this.turn = turn;
				this.best = best;
			}
			
			TurnSetup turn;
			
			long best;
			
			public List<TeamData> teams = new ArrayList<TeamData>();
		}

	protected static class TeamData {
			public TeamData(TeamCar team, double orderOfMagnitude, long val) {
				this.teamCar = team;
				this.orderOfMagnitude = orderOfMagnitude;
				this.val = val;
			}
			TeamCar teamCar;
			double orderOfMagnitude;
			long val;
			
			public Color barColor() {
				int rgb = teamCar.color();
				return new Color(rgb);
			}
			
			
		}

	protected static class GraphData {
			
			double maxTeamNameWidth = 0;
			double barHeight = 15;
			Font teamNameFont;
			Font turnLegendFont;
			
			double paddingX = 3;
			
			double maxMagnitude = 5;
			
			double paddingY = 2;
			
			double markerHeight = 5;
			
		}

	public static void main(String[] args) {
		
		EmbeddedObjectContainer db = Db4oEmbedded.openFile("graph.db4o");
		
		int circuitNo = 5;
		ObjectSet<Graph> result = db.query(Graph.class);
		// Graph graph = result.iterator().next();
		Graph graph = result.get(circuitNo);
		
		
		db.activate(graph, Integer.MAX_VALUE);
		db.close();
		
		
		// final CustomBarRendererBase renderer = (graph.circuit().isFixedTime() ? new FixedTimeCustomBarRenderer(graph) : new LogarithmicTimedLapsCustomBarRenderer(graph));
		final CustomBarRendererBase renderer = (graph.circuit().isFixedTime() ? new LinearFixedTimeCustomBarRenderer(graph) : new LinearTimedLapsCustomBarRenderer(graph));

		JFrame w = new JFrame("Polepos preview");
		JPanel contentPane = new JPanel()
			{
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				renderer.render((Graphics2D)g);
			}
			
		};
		w.setContentPane(contentPane);
		contentPane.setBackground(Color.WHITE);
		w.setSize(522, 600);
		w.setVisible(true);
		
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	double stride() {
		return graphData().barHeight + graphData().paddingY;
	}

	double barX() {
		return graphData().maxTeamNameWidth+1;
	}

	double maxBarWidthWithLegend() {
		return maxBarWidth()- textWidth("10000x")/2;
	}
	
	double maxBarWidth(){
		return graphics.getClipBounds().getWidth() - barX();
	}

	protected double widthPerOrderOfMagnitude() {
		return maxBarWidthWithLegend() / graphData().maxMagnitude;
	}

	public int render(Graphics2D graphics) {
		this.graphics = graphics;
	
		graphics.setFont(graphData().teamNameFont);
	
		double y = 0;
		
		for (TurnData turnData : runs()) {
			if (y > 0) {
				y += stride();
			}
			y = renderTurn(graphics, turnData, y);
			
		}
		
		y = renderAxis(graphics, (int) y);
		
		return (int) y;
	
	}

	private int renderAxis(Graphics graphics, int y) {
		int axisX = (int) graphData().maxTeamNameWidth;
		graphics.setColor(Color.BLACK);
		graphics.drawLine(axisX, 0, axisX, y);
		graphics.drawLine(axisX, y, (int) graphics.getClipBounds().getWidth(), y);
		return renderXLegend(graphics, y, axisX);
	}

	protected int renderXLegend(Graphics graphics, int y, int axisX) {
		for(int i=0;i<=graphData().maxMagnitude;i++) {
			
			int x = (int) (i * widthPerOrderOfMagnitude()) + axisX;
			
			if(doDrawXAxisMarker(i)){
				graphics.drawLine(x, y, x, (int) (y+graphData().markerHeight));
			}
	
			String legend;
			
			if (i == 0) {
				graphics.setFont(graphData().turnLegendFont);
				legend = "magnitude";
			} else {
				graphics.setFont(graphData().teamNameFont);
				legend = magnitudeAxisLegend(i);
			}
			
			graphics.drawString(legend, (int)(x-textWidth(legend)/2.), (int)(y+graphData().markerHeight+textHeight(legend)));
		}
		
		return (int) (y + stride()*2);
	}

	protected abstract boolean doDrawXAxisMarker(int i);

	protected abstract String magnitudeAxisLegend(int i);

	private double renderTurn(Graphics graphics, TurnData turnData, double y) {
	
		String legend = CustomBarPDFReporter.legend(turnData.turn);
		
		graphics.setFont(graphData().turnLegendFont);
		graphics.setColor(Color.BLACK);
		graphics.drawString(legend, (int) (barX()+graphData().paddingX), (int) (y + graphData().barHeight-graphData().paddingY));
	
	
		y += stride();
	
		for (TeamData teamData : turnData.teams) {
	
			graphics.setFont(graphData().teamNameFont);
			graphics.setColor(Color.BLACK);
			
			
			int textY = (int) (graphData.paddingY+(float) (y + graphData().barHeight - textHeight(teamData.teamCar.name())/2));
			graphics.drawString(teamData.teamCar.name(), (int) (graphData().maxTeamNameWidth-textWidth(teamData.teamCar.name())-graphData().paddingX), textY);
	
			graphics.setColor(teamData.barColor());
	
			
			int barWidth = barWidth(teamData);
	
			graphics.fillRect((int)barX(), (int) y, barWidth, (int) graphData().barHeight);
			
			graphics.setColor(Color.BLACK);
			graphics.drawString(legendOnRightOfBar(teamData), (int)(barX()+barWidth+graphData().paddingX), textY);
	
			if (teamData.val > 0) {
				String legendInsideBar = legendInsideBar(teamData);
				graphics.setColor(Color.WHITE);
				graphics.drawString(legendInsideBar, (int)(barX()+barWidth-textWidth(legendInsideBar)-graphData().paddingX), textY);
			}
			
			y += stride();
		}
		return y;
	
	}

	protected abstract String legendInsideBar(TeamData teamData);

	protected abstract String legendOnRightOfBar(TeamData teamData);

	protected abstract int barWidth(TeamData teamData);

	protected double textWidth(String string) {
		return textBounds(graphics, string, graphics.getFont()).getWidth();
	}

	protected double textHeight(String string) {
		return textBounds(graphics, string, graphics.getFont()).getHeight();
	}

	protected double barUnits(double orderOfMagnitude) {
		if (orderOfMagnitude == 0) {
			return 0;
		}
		return Math.log10(orderOfMagnitude) + 1;
	}

	private Rectangle2D textBounds(Graphics g, String string, Font font) {
		
	    FontMetrics metrics = g.getFontMetrics(font);
	    int hgt = metrics.getHeight();
	    int adv = metrics.stringWidth(string);
	    Dimension size = new Dimension(adv+2, hgt+2);
	
	    return new Rectangle(size);
	}

	private GraphData prepareGraphData(Graphics graphics, final Graph graph) {
	
		GraphData graphData = new GraphData();
	
		graphData.teamNameFont = new Font("Helvetica", Font.PLAIN, 9);
		graphData.turnLegendFont = new Font("Helvetica", Font.BOLD, 9);
	
		for (TeamCar teamCar : graph.teamCars()) {
			Rectangle2D textBounds = textBounds(graphics, teamCar.name(), graphData.teamNameFont);
			graphData.maxTeamNameWidth = Math.max(graphData.maxTeamNameWidth, textBounds.getWidth()+graphData.paddingX*2);
			graphData.barHeight = Math.max(graphData.barHeight, textBounds.getHeight());
		}
	
		return graphData;
	}

	protected GraphData graphData() {
		if (graphData == null) {
			this.graphData = prepareGraphData(graphics, graph);
		}
		return graphData;
	}

	private List<TurnData> runs() {
		if (runs == null) {
			this.runs = prepareTurnsData(graph);
		}
		return runs;
	}

	protected abstract List<TurnData> prepareTurnsData(final Graph graph);

}