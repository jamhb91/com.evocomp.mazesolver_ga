/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evocomp.mazesolver_ga;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jamhb
 */
public class Test extends JFrame{

    public static XYSeriesCollection solutionCollection = null;
    public static XYSeries solutionSeries = null;
    public static JPanel chartSolutionPanel = null;
    
    
    public static XYSeriesCollection distanceCollection = null;
    public static XYSeries distanceSeries = null;
    public static JPanel chartDistancePanel = null;
    
    public static JPanel mainPanel=null;
    
    //Program Variables
    public static List<Chromosome> output = null;
    public static List<Chromosome>  previousOutput = null;
    public static List<Position> walls = null;
    public static MazeSolver mSolver;
    public static int generation = 0;
    public static int interval = 5;
    public static Timer timer = new Timer();
    
    //GA Configuration
    public static Position initial = new Position(2,2);
    public static Position target = new Position(30,16);
    public static int matrixSize = 40;
    public static int chromosomeSize = 40*3;
    public static int generationSize = 400;
    public static int maximumGenerations = 3000;
    public static int maximumMutation = 10;
    public static int matchSize = 5;
    public static int elitism = 0;
    public static String mazeFile = "C:\\Users\\jamhb\\Documents\\NetBeansProjects\\MazeSolver_GA\\src\\main\\java\\hard.txt";
    
    
    public Test() {
        super("Maze Solver Genetic Algorithm");
        
        initializeOutput();
 
        solutionCollection = new XYSeriesCollection();
        solutionSeries = new XYSeries("Maze");
        
        chartSolutionPanel = createPathChartPanel();
        
         
        distanceCollection = new XYSeriesCollection();
        distanceSeries = new XYSeries("Best Fitness VS Generation");
        
        chartDistancePanel = createDistanceChartPanel();
        
        mainPanel = new JPanel();
        mainPanel.setSize(1120,480);
        
        mainPanel.add(chartSolutionPanel);
        mainPanel.add(chartDistancePanel);
        add(mainPanel);
 
        setSize(1160,540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    runAlgorithm();
                    if(generation>maximumGenerations){
                        timer.cancel();
                    }
                } catch (Exception ex) {
                    //Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
          }, interval, interval);
    }
     
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Test().setVisible(true);
        });
    }
    
    private static void initializeOutput(){
        try {
            walls = MazeSolverUtilities.GetWallsFromFile(mazeFile);
            mSolver = new MazeSolver(
                    chromosomeSize, //Number of coeficients
                    generationSize, //Number of children
                    matchSize, //Match Size
                    initial, //Initial Position
                    target, //Target Position
                    walls, // Replace for filename
                    matrixSize
            );
            
            System.out.println("Initial Random Matrix... ");
            output = mSolver.GetInitialPopulation();
            output.forEach((is) -> {
                System.out.println(Arrays.toString(is.getChromosome()) + " - D: " + is.getDistance());
            });
        } catch (Exception ex) {
            //Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void runAlgorithm() throws Exception {
//        System.out.println("Running New Generation...");
        Chromosome generationBest = mSolver.GetBestChromosome(output);
        
        //Replacing every 100 generations
        if(generation%100==0){
            List<Chromosome> newbies = mSolver.GetInitialPopulation();
            Collections.sort(output, new SortByDistance());
            for (int i = generationSize/10; i < generationSize; i++) {
                output.set(i, newbies.get(i));
            }
            output.set(0, generationBest);
            Collections.shuffle(output);
        }
//        System.out.println("Generation Winner");
//        System.out.println(generationBest.toString());
       
        //System.out.println("Matching matrix... ");
        List<Chromosome> matchedOutput = mSolver.MatchPopulation(output);
        
        //System.out.println("Crossing generation...");
        List<Chromosome> mutated = mSolver.GetCrossedChildren(matchedOutput);
        
        //System.out.println("Muting generation...");
        mSolver.MuteGeneration(mutated,maximumMutation);
        mSolver.CalculateDistances(mutated);
        
        //graph every 10 gen to reduce overload
        if(generation%20==0){
            solutionCollection.removeAllSeries();
            solutionCollection.addSeries(getCurrentPathSeries(MazeSolverUtilities.GetPath(initial, target, generationBest.getChromosome())));
            solutionCollection.addSeries(getInitialSeries());
            solutionCollection.addSeries(getTargetSeries());
            solutionCollection.addSeries(getWallsSeries());

            distanceSeries.add(generation, generationBest.getDistance());
            distanceCollection.removeAllSeries();
            distanceCollection.addSeries(distanceSeries);
        }
        
        previousOutput = output;
        output = mutated;
        
        generation++;
    }
    
    private JPanel createPathChartPanel() {
        String chartTitle = "Path";
        String xAxisLabel = "X";
        String yAxisLabel = "Y";

        JFreeChart chart = ChartFactory.createScatterPlot(chartTitle,
                xAxisLabel, yAxisLabel, solutionCollection, PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );
        
        //Renderer
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesPaint(0, Color.MAGENTA);
        renderer.setSeriesStroke(0, new BasicStroke(3));
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesPaint(1, Color.YELLOW  );
        renderer.setSeriesShape(1, new Rectangle(10,10));
        renderer.setSeriesLinesVisible(2, false);
        renderer.setSeriesPaint(2, Color.GREEN);
        renderer.setSeriesShape(2, new Rectangle(10,10));
        renderer.setSeriesLinesVisible(3, false);
        renderer.setSeriesPaint(3, Color.BLACK);
        renderer.setSeriesShape(3, new Rectangle(6,6));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRenderer(renderer);
        
        //Axis
        NumberAxis xAxis =(NumberAxis)plot.getDomainAxis();
        xAxis.setTickUnit(new NumberTickUnit(1));
        xAxis.setTickLabelsVisible(false);
        xAxis.setLowerBound(-1);
        xAxis.setUpperBound(40);
        xAxis.setAxisLinePaint(Color.GRAY);
        NumberAxis yAxis =(NumberAxis)plot.getRangeAxis();
        yAxis.setTickUnit(new NumberTickUnit(1));
        yAxis.setTickLabelsVisible(false);
        yAxis.setLowerBound(-1);
        yAxis.setUpperBound(40);
        yAxis.setAxisLinePaint(Color.GRAY);
        
        ChartPanel cp = new ChartPanel(chart);
        cp.setPreferredSize(new Dimension(480,480));
        return cp;
    }
    
    private static XYSeries getCurrentPathSeries(List<Position> positions){
        XYSeries series = new XYSeries("Current Path", false);
        positions.forEach((position) -> {
            series.add(position.getX(),position.getY());
        });
        return series;
    }
    
    private static XYSeries getWallsSeries(){
        XYSeries series = new XYSeries("Walls", false);
        walls.forEach((position) -> {
            series.add(position.getX(),position.getY());
        });
        return series;
    }
    
    private static XYSeries getInitialSeries(){
        XYSeries series = new XYSeries("Initial Position", false);
        series.add(initial.getX(),initial.getY());
        return series;
    }
    
    private static XYSeries getTargetSeries(){
        XYSeries series = new XYSeries("Target Position", false);
        series.add(target.getX(),target.getY());
        return series;
    }
    
    private JPanel createDistanceChartPanel() {
        String chartTitle = "Best Fitness vs Generation";
        String xAxisLabel = "Generation";
        String yAxisLabel = "Fitness";

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, distanceCollection);
        ChartPanel cp = new ChartPanel(chart);
        
        
        cp.setPreferredSize(new Dimension(640,480));
        return cp;
    }
}
