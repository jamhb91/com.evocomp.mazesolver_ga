/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evocomp.mazesolver_ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author jamhb
 */
public class MazeSolver {
    
    private int MAX_CHROMOSOME_SIZE = 0;
    private int POPULATION = 1000;
    private int MATCH_SIZE = 5;
    private int MATRIX_SIZE = 10;
    private final Position INITIAL_POSITION;
    private final Position TARGET_POSITION;
    
    private final List<Position> Walls;
    
    private final int MOVEMENTS = 5;
    
    public MazeSolver(int ChromosomeSize, int population, int matchSize, Position initialPos, Position target, List<Position> walls, int matrixSize){
        this.MAX_CHROMOSOME_SIZE = ChromosomeSize;
        this.POPULATION = population;
        this.INITIAL_POSITION = initialPos;
        this.TARGET_POSITION = target;
        this.MATCH_SIZE = matchSize;
        this.Walls = walls;
        this.MATRIX_SIZE = matrixSize;
    }
    
    public List<Chromosome> GetInitialPopulation() throws Exception{
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < POPULATION; i++) {
            Integer [] chromosome = GetRandomChromosome();
            population.add(new Chromosome(chromosome,CalculateFitness(chromosome)));
        }
        return population;
    }
    
    private Integer[] GetRandomChromosome(){
        Integer [] chromosome = new Integer[MAX_CHROMOSOME_SIZE];
        Random random = new Random();
        for (int i = 0; i < chromosome.length; i++) {
            chromosome[i] = random.nextInt(MOVEMENTS);
        }
        return chromosome;
    }
    
    private double CalculateFitness(Integer [] chromosome) throws Exception{
        double result = 0;
        Position currentPosition = INITIAL_POSITION;
        boolean targetReach = false;
        for (Integer gen : chromosome) {
            currentPosition = MazeSolverUtilities.GetNextPosition(currentPosition, gen);
            result = result + CalculateDistance(currentPosition);
            result = result + GetPenalty(currentPosition);
            if(currentPosition.equals(TARGET_POSITION)){
                targetReach=true;
                break;
            }
        }
        
        Set duplicates = findDuplicates(Arrays.asList(chromosome));
        if(!targetReach){result = result + MAX_CHROMOSOME_SIZE*10;}
        result = result + duplicates.size()*MAX_CHROMOSOME_SIZE*3;
        return result;
    }
    
    private double CalculateDistance(Position position){
        return (
            Math.abs(TARGET_POSITION.getX() - position.getX()) + 
            Math.abs(TARGET_POSITION.getY() - position.getY())
        );
    }
    
    private int GetPenalty(Position position){
        if(Walls.contains(position) || IsPositionOutsideMap(position) ){
            return MAX_CHROMOSOME_SIZE*10;
        }
        else{
            return 0;
        }
    }
    
    private boolean IsPositionOutsideMap(Position position){
        return position.getX()<0 || position.getY()<0 || position.getX()>(MATRIX_SIZE-1) || position.getY()> (MATRIX_SIZE-1);
    }
    
    public Chromosome GetBestChromosome(List<Chromosome> population){
        Collections.sort(population, new SortByDistance());
        return population.get(0);
    }
    
    
    public List<Chromosome> MatchPopulation(List<Chromosome> population){
        List<Chromosome> generation = new ArrayList<>();
        for (int i = 0; i < POPULATION; i++) {
            Collections.shuffle(population);
            generation.add(GetMatchWinner(population.subList(0, MATCH_SIZE)));
        }
        return generation;
    }
    
    private Chromosome GetMatchWinner(List<Chromosome> population){
        int indexOfWinner=0;
        double bestDistance= Double.MAX_VALUE;
        for (int i = 0; i < population.size(); i++) {
            if(population.get(i).getDistance()<bestDistance){
                bestDistance = population.get(i).getDistance();
                indexOfWinner=i;
            }
        }
        return population.get(indexOfWinner);
    }
    
    
    public List<Chromosome> GetCrossedChildren(List<Chromosome> parents){
        Collections.shuffle(parents);
        Random r = new Random();
        
        List<Chromosome> children = new ArrayList<>();
        int arrayHalf = parents.size()/2;
        
        for (int i = 0; i < arrayHalf; i++) {
            Integer [] parent1 = parents.get(i).getChromosome();
            Integer [] parent2 = parents.get(i+arrayHalf).getChromosome();
            int crossPosition = r.nextInt(MAX_CHROMOSOME_SIZE-2)+1;
            
            Integer [] x = ArrayUtils.subarray(parent1, 0, crossPosition);
            Integer [] xx = ArrayUtils.subarray(parent1, crossPosition, MAX_CHROMOSOME_SIZE);
            Integer [] y = ArrayUtils.subarray(parent2, 0, crossPosition);
            Integer [] yy = ArrayUtils.subarray(parent2, crossPosition, MAX_CHROMOSOME_SIZE);
            
            Integer [] child1 = ArrayUtils.addAll(x, yy);
            Integer [] child2 = ArrayUtils.addAll(y, xx);
            
            children.add(new Chromosome(child1,0));
            children.add(new Chromosome(child2,0));
            
//            System.out.println("Parent1: " + Arrays.toString(parent1));
//            System.out.println("Parent2: " + Arrays.toString(parent2));
//            System.out.println("Cross Position: " + crossPosition);
//            System.out.println("Children1: " + Arrays.toString(child1));
//            System.out.println("Children2: " + Arrays.toString(child2));
        }
        
        return children;
    }
    
    public void MuteGeneration(List<Chromosome> parents, int maxMute){
        Collections.shuffle(parents);
        for (int i = 0; i < maxMute; i++) {
            parents.set(i, new Chromosome(makeMute(parents.get(i).getChromosome()),0));
        }
    }
    
    public void CalculateDistances(List<Chromosome> parents) throws Exception{
        for (int i = 0; i < parents.size(); i++) {
            Chromosome chromosome = parents.get(i);
            chromosome.setDistance(CalculateFitness(chromosome.getChromosome()));
            parents.set(i,chromosome);
        }
    }
    
    public Integer [] makeMute(Integer [] array){
        Random r = new Random();
        Integer [] muted = Arrays.copyOf(array, array.length);
        int position = r.nextInt(MAX_CHROMOSOME_SIZE);
        muted[position] = r.nextInt(MOVEMENTS);
        position = r.nextInt(MAX_CHROMOSOME_SIZE);
        muted[position] = r.nextInt(MOVEMENTS);
        return muted;
        //System.out.println(">Original: " + Arrays.toString(array));
        //System.out.println(">   Muted: " + Arrays.toString(muted));
    }
    
    private <T> Set<T> findDuplicates(Collection<T> collection) {
        Set<T> uniques = new HashSet<>();
        return collection.stream()
            .filter(e -> !uniques.add(e))
            .collect(Collectors.toSet());
    }
}
