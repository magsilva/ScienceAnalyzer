package com.ironiacorp.scienceanalyzer;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;


/**
 *
 * @author Aretha
 */
public class Graphic extends ApplicationFrame {

    private String chart_title;

    public Graphic(String title, String chart_title) {
        super(title);
        this.chart_title = chart_title;
    }

    public void generateGraphic(TreeMap<Integer, HashMap<String, ArrayList<Person>>> defesasPorAno, boolean show_legend) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int index = 1;
        for (Integer ano : defesasPorAno.keySet()) {
            dataset.addValue(defesasPorAno.get(ano).get("ME").size() + defesasPorAno.get(ano).get("DO").size(), "id", String.valueOf(ano));
            index++;
        }

        JFreeChart chart = ChartFactory.createBarChart(
                chart_title, // chart title
                "Ano", // domain axis label
                "Número de Defesas", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                show_legend, // include legend
                true, // tooltips?
                false // URLs?
                );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis xAxis = (CategoryAxis) plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);


        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(750, 500));
        setContentPane(chartPanel);
    }

    public void generateGraphic2(TreeMap<Integer, HashMap<String, ArrayList<Person>>> defesasPorAno, boolean show_legend) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int index = 1;
        for (Integer ano : defesasPorAno.keySet()) {
            dataset.addValue(defesasPorAno.get(ano).get("ME").size(), "ME", String.valueOf(ano));
            dataset.addValue(defesasPorAno.get(ano).get("DO").size(), "DO", String.valueOf(ano));
            index++;
        }

        JFreeChart chart = ChartFactory.createBarChart(
                chart_title, // chart title
                "Ano", // domain axis label
                "Número de Defesas", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                show_legend, // include legend
                true, // tooltips?
                false // URLs?
                );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.0);
        CategoryAxis xAxis = (CategoryAxis) plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);


        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(750, 500));
        setContentPane(chartPanel);
    }

    public void generateGraphic3(TreeMap<Integer, HashMap<String, ArrayList<Person>>> defesasPorAno, boolean show_legend) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        int valor_mestrado = 0, valor_doutorado = 0;
        for (HashMap<String, ArrayList<Person>> entry : defesasPorAno.values()) {
            valor_mestrado += entry.get("ME").size();
            valor_doutorado += entry.get("DO").size();
        }
        dataset.setValue("Mestrado", valor_mestrado);
        dataset.setValue("Doutoado", valor_doutorado);

        JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                true, // legend?
                true, // tooltips?
                false // URLs?
                );
        PiePlot plot = (PiePlot) chart.getPlot();
        ChartPanel chartPanel = new ChartPanel(chart, false);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        chartPanel.setPreferredSize(new Dimension(750, 500));
        setContentPane(chartPanel);
    }
}
