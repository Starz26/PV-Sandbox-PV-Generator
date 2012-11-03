/* Class to generate Regression Analysis Plot chart image */
package com.example;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DrawRegressionChart extends HttpServlet {

	// OutputStream outputStream;
	// get Method
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String qsToken = "";
		String analyte = "";

		// if token is missing in request from SF then chart won't be drawn
		if (request.getParameter("token") != null) {
			// Token sent in request from SF
			qsToken = request.getParameter("token");

			// analyte name sent in request from SF
			analyte = request.getParameter("analyte");

			// String accToken =
			// getServletConfig().getInitParameter("accToken");

			// accToken stores in environment variables on heroku
			String accToken = System.getenv("SFTOKEN");

			// token comparison, if matched draw chart else not
			if (qsToken.trim().equals(accToken.trim())) {
				// initializing common class object and called method to draw
				// chart
				ChartGeneration objCG = new ChartGeneration();
				objCG.GenerateChartImage(request, response, "r", analyte,
						accToken);
			}
		}
	}

	// post method
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String qsToken = "";
		String analyte = "";

		// if token is missing in request from SF then chart won't be drawn
		if (request.getParameter("token") != null) {
			// Token sent in request from SF
			qsToken = request.getParameter("token");

			// analyte name sent in request from SF
			analyte = request.getParameter("analyte");

			// String accToken =
			// getServletConfig().getInitParameter("accToken");

			// accToken stores in environment variables on heroku
			String accToken = System.getenv("SFTOKEN");

			// token comparison, if matched draw chart else not
			if (qsToken.trim().equals(accToken.trim())) {
				// initializing common class object and called method to draw
				// chart
				ChartGeneration objCG = new ChartGeneration();
				objCG.GenerateChartImage(request, response, "r", analyte,
						accToken);
			}
		}
	}

}
