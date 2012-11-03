/* Common class to generate Difference Plot and Regression Plot chart image
 * Jfree chart library version used is 1.0.14  
 * Jcommon library version used is 1.0.17
 * */
package com.example;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

public class ChartGeneration {

	OutputStream outputStream; // to draw chart
	/* parameters passed in SF request for heroku */
	String xValues;
	String yValues;
	String xOutlier;
	String yOutlier;
	String xRange;
	String xInterval;
	String yRange;
	String yInterval;
	String typeCht;
	String graphTitle;
	String xAxisTitle;
	String yAxisTitle;
	String x1y1;
	String x2y2;
	/* parameters passed in SF request for heroku */

	/*
	 * token value to replace by linebreak charaters for chart headers only used
	 * in case of pvid does receive in querystring
	 */
	String tknLineBrk = "[linebreak]";

	// will create chart image using Jfree chart
	public void GenerateChartImage(HttpServletRequest request,
			HttpServletResponse response, String chartType, String analyte,
			String accToken) throws ServletException, IOException {
		try {

			// parameters passed in SF request for heroku
			// in case, length of image url is greater then 2000 characters, SF
			// will pass pvid in rquest for heroku
			String spvId = request.getParameter("pvId");
			if (spvId != null) {
				// getting values for pvid from SF and stores in variables
				// '!@' separated string will be written from SF public web
				// service
				String strFromSF = GetDataFromSF(spvId, accToken, chartType,
						analyte);
				if (strFromSF != null && strFromSF != "") {
					String[] sStr = strFromSF.split("!@");
					for (int isStr = 0; isStr <= sStr.length - 1; isStr++) {
						switch (isStr) {
						case 0:
							xValues = sStr[isStr];
							break;
						case 1:
							yValues = sStr[isStr];
							break;
						case 2:
							xOutlier = sStr[isStr];
							break;
						case 3:
							yOutlier = sStr[isStr];
							break;
						case 4:
							xRange = sStr[isStr];
							break;
						case 5:
							xInterval = sStr[isStr];
							break;
						case 6:
							yRange = sStr[isStr];
							break;
						case 7:
							yInterval = sStr[isStr];
							break;
						case 8:
							typeCht = sStr[isStr];
							break;
						case 9:
							graphTitle = sStr[isStr];
							graphTitle = graphTitle.replace(tknLineBrk, "\n");
							break;
						case 10:
							xAxisTitle = sStr[isStr];
							break;
						case 11:
							yAxisTitle = sStr[isStr];
							break;
						case 12:
							x1y1 = sStr[isStr];
							break;
						case 13:
							x2y2 = sStr[isStr];
							break;
						}
					}
				}
				// getting values for pvid from SF and stores in variables
			}

			// getting values for querystring parameters in variables
			if (request.getParameter("xval") != null) {
				xValues = request.getParameter("xval");
			}
			if (request.getParameter("yval") != null) {
				yValues = request.getParameter("yval");
			}
			if (request.getParameter("xo") != null) {
				xOutlier = request.getParameter("xo");
			}
			if (request.getParameter("yo") != null) {
				yOutlier = request.getParameter("yo");
			}
			if (request.getParameter("xrange") != null) {
				xRange = request.getParameter("xrange");
			}
			if (request.getParameter("xinterval") != null) {
				xInterval = request.getParameter("xinterval");
			}
			if (request.getParameter("yrange") != null) {
				yRange = request.getParameter("yrange");
			}
			if (request.getParameter("yinterval") != null) {
				yInterval = request.getParameter("yinterval");
			}
			if (request.getParameter("charttype") != null) {
				typeCht = request.getParameter("charttype");
			}
			if (request.getParameter("graphtitle") != null) {
				graphTitle = request.getParameter("graphtitle");
			}
			if (request.getParameter("xaxistitle") != null) {
				xAxisTitle = request.getParameter("xaxistitle");
			}
			if (request.getParameter("yaxistitle") != null) {
				yAxisTitle = request.getParameter("yaxistitle");
			}
			if (request.getParameter("x1y1") != null) {
				x1y1 = request.getParameter("x1y1");
			}
			if (request.getParameter("x2y2") != null) {
				x2y2 = request.getParameter("x2y2");
			}
			// getting values for querystring parameters in variables

			outputStream = response.getOutputStream();
			// fill dataset which is used to draw a chart and will be passed in
			// jfree chart library's method
			XYSeriesCollection dataset = GetDataSetForChart(xValues, yValues,
					xOutlier, yOutlier);
			response.setContentType("image/png"); // Image type
			// getting chart using jfree chart library
			JFreeChart chart = getChart(dataset, graphTitle, xAxisTitle,
					yAxisTitle, chartType, x1y1, x2y2, xRange, xInterval,
					yRange, yInterval);
			// hxw of image to be generated for chart
			int width = 550;
			int height = 450;
			// jfree chart library method to generate image for chart
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		} catch (Exception e) {
			/*
			 * if anything goes wrong for creating chart image below error image
			 * will be returned with error message
			 */
			e.printStackTrace();
			response.setContentType("image/png");
			// Create an image 200 x 200
			BufferedImage bufferedImage = new BufferedImage(500, 350,
					BufferedImage.TYPE_INT_RGB);
			// Draw an oval
			Graphics g = bufferedImage.getGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, 500, 350);
			g.setColor(Color.black);
			g.drawString("There is an error in generating chart image :", 10,
					175);
			g.drawString(e.toString(), 10, 195);
			// Free graphic resources
			g.dispose();
			// Write the image as a jpg
			ImageIO.write(bufferedImage, "png", response.getOutputStream());
			/*
			 * if anything goes wrong for creating chart image above error image
			 * will be returned with error message
			 */
		}
	}

	// fill dataset with two series one is for xval, yval and other is for
	// xoutliers, youtliers to draw chart
	public XYSeriesCollection GetDataSetForChart(String xVls, String yVls,
			String xOutl, String yOutl) {
		XYSeriesCollection dataset = null;

		// generating normal series
		if (xVls != null && xVls != "" && yVls != null & yVls != "") {
			String[] xvals = xVls.split(",");
			String[] yvals = yVls.split(",");
			if (xvals.length == yvals.length) {
				XYSeries data = new XYSeries("data", false);
				dataset = new XYSeriesCollection();
				for (int i = 0; i <= xvals.length - 1; i++) {
					if (xvals[i] != null) {
						if (yvals[i] != null) {
							data.add(Double.parseDouble(xvals[i]),
									Double.parseDouble(yvals[i]));
						}
					}
				}
				dataset.addSeries(data);
			}
		}

		// generating outlier series
		if (xOutl != null && !xOutl.equals("null") && yOutl != null
				&& !yOutl.equals("null")) {
			String[] arrxOutlier = xOutl.trim().split(",");
			String[] arryOutlier = yOutl.trim().split(",");
			if (arrxOutlier.length == arryOutlier.length
					&& arrxOutlier.length > 0 && arryOutlier.length > 0) {
				XYSeries outlier = new XYSeries("Outlier", false);
				for (int i = 0; i <= arrxOutlier.length - 1; i++) {
					if (arrxOutlier[i] != null) {
						if (arryOutlier[i] != null) {
							outlier.add(Double.parseDouble(arrxOutlier[i]),
									Double.parseDouble(arryOutlier[i]));
						}
					}
				}
				dataset.addSeries(outlier);
			}
		}

		return dataset;
	}

	// generate chart using dataset and other necessary details using jfree
	// chart library
	public JFreeChart getChart(XYSeriesCollection dataset, String grphTitle,
			String xHdr, String yHdr, String chrtType, String x1y1,
			String x2y2, String xAxisRange, String xAxisInterval,
			String yAxisRange, String yAxisInterval) {
		boolean legend = true;
		boolean tooltips = true;
		boolean urls = false;
		JFreeChart chart = ChartFactory
				.createScatterPlot(grphTitle, xHdr, yHdr, dataset,
						PlotOrientation.VERTICAL, legend, tooltips, urls);

		chart.setBackgroundPaint(Color.white); // setting background color for
												// chart

		XYPlot plot = (XYPlot) chart.getXYPlot();
		plot.setBackgroundPaint(Color.white); // setting background color for
												// Plot area
		plot.setDomainGridlinePaint(Color.lightGray); // setting horizontal grid
														// line color
		plot.setDomainGridlinesVisible(true); // setting horizontal grid line
												// visibility
		// setting stroke for visibility of horizontal grid lines
		plot.setDomainGridlineStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 1f, new float[] { 5.0f, 5.0f }, 0.0f));

		plot.setRangeGridlinePaint(Color.lightGray); // setting vertical grid
														// line color
		plot.setRangeGridlinesVisible(true); // setting horizontal grid line
												// visibility
		// setting stroke for visibility of horizontal grid lines
		plot.setRangeGridlineStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 1f, new float[] { 5.0f, 5.0f }, 0.0f));

		plot.setOutlineVisible(true); // to show horizontal last grid line
		plot.setOutlineStroke(new BasicStroke(1f)); // to show horizontal last
													// grid line
		plot.setOutlinePaint(Color.black); // to show horizontal last grid line

		float xminRange = 0;
		float xmaxRange = 0;
		float x_Interval = 0;
		float yminRange = 0;
		float ymaxRange = 0;
		float y_Interval = 0;

		// set ranges and intervals for xaxis if range is not null
		if (xAxisRange != null && xAxisRange != "" && xAxisInterval != null
				&& xAxisInterval != "") {
			/* xaxis range and interval */
			xminRange = Float.parseFloat(xAxisRange.split(",")[0]);
			xmaxRange = Float.parseFloat(xAxisRange.split(",")[1]);
			x_Interval = Float.parseFloat(xAxisInterval);
			plot.setDomainCrosshairVisible(true);
			NumberAxis domain = (NumberAxis) plot.getDomainAxis();
			domain.setRange(xminRange, xmaxRange);
			domain.setTickUnit(new NumberTickUnit(x_Interval));
			domain.setLabelPaint(Color.black);
			domain.setTickLabelPaint(Color.black);
			/* xaxis range and interval end */
		}

		// set ranges and intervals for yaxis if range is not null
		if (yAxisRange != null && yAxisRange != "" && yAxisInterval != null
				&& yAxisInterval != "") {
			/* yaxis range and interval */
			yminRange = Float.parseFloat(yAxisRange.split(",")[0]);
			ymaxRange = Float.parseFloat(yAxisRange.split(",")[1]);
			y_Interval = Float.parseFloat(yAxisInterval);
			plot.setRangeCrosshairVisible(true);
			plot.setRangeCrosshairStroke(new BasicStroke(0.5f));
			plot.setRangeCrosshairPaint(Color.black);
			NumberAxis range = (NumberAxis) plot.getRangeAxis();
			range.setRange(yminRange, ymaxRange);
			range.setTickUnit(new NumberTickUnit(y_Interval));
			range.setLabelPaint(Color.black);
			range.setTickLabelPaint(Color.black);
			/* yaxis range and interval end */
		}

		// set shape and color for plots in case of normal
		Shape cross = ShapeUtilities.createDiamond(1.5f);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setBaseSeriesVisibleInLegend(false);
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShape(0, cross);
		plot.setRenderer(0, renderer);

		if (plot.getSeriesCount() > 1) {
			// set shape and color for plots in case of outlier
			Shape circle = new Ellipse2D.Double(2f, 2f, 4f, 4f);
			renderer.setSeriesPaint(1, Color.RED);
			renderer.setSeriesLinesVisible(1, false);
			renderer.setSeriesShape(1, circle);
			plot.setRenderer(1, renderer);
		}

		// regression line for regression chart with x1y1 and x2y2 values
		if (chrtType == "r") {
			double x1 = 0;
			double y1 = 0;
			double x2 = 0;
			double y2 = 0;

			if (x1y1 != null && x1y1 != "") {
				String arrx1y1[] = x1y1.split(",");
				for (int ix1y1 = 0; ix1y1 <= arrx1y1.length - 1; ix1y1++) {
					if (ix1y1 == 0) {
						x1 = Double.valueOf(arrx1y1[ix1y1]).doubleValue();
					} else {
						y1 = Double.valueOf(arrx1y1[ix1y1]).doubleValue();
					}
				}
			}

			if (x2y2 != null && x2y2 != "") {
				String arrx2y2[] = x2y2.split(",");
				for (int ix2y2 = 0; ix2y2 <= arrx2y2.length - 1; ix2y2++) {
					if (ix2y2 == 0) {
						x2 = Double.valueOf(arrx2y2[ix2y2]).doubleValue();
					} else {
						y2 = Double.valueOf(arrx2y2[ix2y2]).doubleValue();
					}
				}

			}

			XYLineAnnotation line = new XYLineAnnotation(x1, y1, x2, y2,
					new BasicStroke(0.5f), Color.black);
			plot.addAnnotation(line);
		}
		// regression line for regression chart

		return chart;
	}

	// if pvid does get in parameter (in case of image url length > 2000
	// characters)
	// this method will use and will get '!@' seperated string which contains
	// all values used to generate chart image
	// for this scenario SF public web service will be used
	public String GetDataFromSF(String pvId, String accToken, String chartType,
			String analyte) {
		String line = "";
		String authKey = accToken; // "1111";
		try {
			// parameters passed in SF public web service to get data
			String data = URLEncoder.encode("pvId", "UTF-8") + "="
					+ URLEncoder.encode(pvId, "UTF-8");
			data += "&" + URLEncoder.encode("authKey", "UTF-8") + "="
					+ URLEncoder.encode(authKey, "UTF-8");
			data += "&" + URLEncoder.encode("chartType", "UTF-8") + "="
					+ URLEncoder.encode(chartType, "UTF-8");
			data += "&" + URLEncoder.encode("analyte", "UTF-8") + "="
					+ URLEncoder.encode(analyte, "UTF-8");
			// parameters passed in SF public web service to get data

			// url call for SF public web service
			URL url = new URL(
					"https://abbott-integration.fbd.cs11.force.com/PV_GenerateChartImageForPvId");
			//URLConnection conn = url.openConnection();
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			line = rd.readLine();
			wr.close();
			rd.close();
			// url call for SF public web service

		} catch (Exception e) {
		}
		return line; // returs '!@' seperated string (values) to generate chart
						// image
	}

}
