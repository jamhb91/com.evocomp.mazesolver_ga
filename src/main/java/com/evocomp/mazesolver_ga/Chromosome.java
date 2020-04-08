/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evocomp.mazesolver_ga;

import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author jamhb
 */
public class Chromosome {
    private Integer[] chromosome;
    private double distance;

    public Chromosome(Integer[] chromosome, double distance){
        this.chromosome = chromosome;
        this.distance = distance;
    }
    
    /**
     * @return the chromosome
     */
    public Integer[] getChromosome() {
        return chromosome;
    }

    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(Integer[] chromosome) {
        this.chromosome = chromosome;
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(Arrays.toString(chromosome));
        sb.append(" Distance: ");
        sb.append(distance);
        return sb.toString();
    }
}
    
class SortByDistance implements Comparator<Chromosome>{
    @Override
    public int compare(Chromosome a, Chromosome b){
        return (int) (a.getDistance() - b.getDistance());
    }
}
