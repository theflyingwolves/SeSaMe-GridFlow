package scatterPlotter;

import java.awt.RenderingHints;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ScatterPlot extends ApplicationFrame{
	private static final long serialVersionUID = 1L;
	private static String applicationTitleBase = "Standard Deviation Plot";
	private static String applicationTitle;
	
	private float[][] dataSource;
	
	public ScatterPlot(float[][] data){
		super(applicationTitle);
		this.dataSource = data;
		init();
	}
	
	public void setPlotIndex(int count){
		applicationTitle = applicationTitle + " "+count;
		super.setTitle(applicationTitle);
	}
	
	private void init(){
		final NumberAxis domainAxis = new NumberAxis("Pixel Index");
        domainAxis.setAutoRangeIncludesZero(false);
        final NumberAxis rangeAxis = new NumberAxis("Standard Deviation");
        rangeAxis.setAutoRangeIncludesZero(false);
        final FastScatterPlot plot = new FastScatterPlot(this.dataSource, domainAxis, rangeAxis);
        final JFreeChart chart = new JFreeChart(applicationTitle, plot);

        // force aliasing of the rendered content..
        chart.getRenderingHints().put
            (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final ChartPanel panel = new ChartPanel(chart, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        panel.setMinimumDrawHeight(10);
        panel.setMaximumDrawHeight(2000);
        panel.setMinimumDrawWidth(20);
        panel.setMaximumDrawWidth(2000);
        
        setContentPane(panel);
	}
	
	public void plot(){
		this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}
	
//	public static void main(String[] args){
//		final int count = 50;
//		float[][] data = new float[2][count];
//		for(int i=0;i<count;i++){
//			data[0][i] = i;
//			data[1][i] = (float) (Math.random())*100;
//		}
//		
//		ScatterPlot plot = new ScatterPlot(data);
//		plot.plot();
//	}
}