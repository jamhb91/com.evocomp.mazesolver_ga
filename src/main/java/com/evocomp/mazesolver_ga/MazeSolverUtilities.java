/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evocomp.mazesolver_ga;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jamhb
 */
public abstract class MazeSolverUtilities {
    
    public static List<Position> GetPath(Position initialPosition, Position targetPosition, Integer [] movements) throws Exception{
        List<Position> positions = new ArrayList<>();
        Position currentPosition = initialPosition;
        positions.add(currentPosition);
        for (Integer movement : movements) {
            currentPosition = GetNextPosition(currentPosition,movement);
            positions.add(currentPosition);
            if(currentPosition.equals(targetPosition)){
                break;
            }
        }
        return positions;
    }
    
    public static Position GetNextPosition(Position position, int movement) throws Exception{
        switch(movement){
            case 0:
                return position;
            case 1:
                return new Position(position.getX(),position.getY()+1);
            case 2:
                return new Position(position.getX()+1,position.getY());
            case 3:
                return new Position(position.getX(),position.getY()-1);
            case 4:
                return new Position(position.getX()-1,position.getY());
            default:
                throw new Exception("Movement not recognized");
        }
    }

    
    public static List<Position> GetWallsFromFile(String FilePath) throws FileNotFoundException, IOException{
        List<Position> walls;
        try (BufferedReader br = new BufferedReader(new FileReader(FilePath))) {
            String currentLine;
            List<String[]> map = new ArrayList<>();
            while(br.ready()){
                currentLine = br.readLine();
                map.add(currentLine.split(","));
            }   
            
            Collections.reverse(map);
            walls = new ArrayList<>();
            for (int i = 0; i < map.size(); i++) {
                String [] strings = map.get(i);
                for (int j = 0; j < strings.length; j++) {
                    String string = strings[j];
                    
                    if(string.equals("1")){
                        walls.add(new Position(j,i));
                    }
                }
            }
        }
        return walls;
    }
    
    
    private List<Position> GetDefaultWalls(){
        List<Position> position = new ArrayList<>();
        position.add(new Position(0,-1));
        position.add(new Position(1,-1));
        position.add(new Position(2,-1));
        position.add(new Position(3,-1));
        position.add(new Position(4,-1));
        position.add(new Position(5,-1));
        position.add(new Position(6,-1));
        position.add(new Position(7,-1));
        position.add(new Position(8,-1));
        position.add(new Position(9,-1));
        position.add(new Position(10,-1));
        position.add(new Position(0,11));
        position.add(new Position(1,11));
        position.add(new Position(2,11));
        position.add(new Position(3,11));
        position.add(new Position(4,11));
        position.add(new Position(5,11));
        position.add(new Position(6,11));
        position.add(new Position(7,11));
        position.add(new Position(8,11));
        position.add(new Position(9,11));
        position.add(new Position(10,11));
        position.add(new Position(-1,0));
        position.add(new Position(-1,1));
        position.add(new Position(-1,2));
        position.add(new Position(-1,3));
        position.add(new Position(-1,4));
        position.add(new Position(-1,5));
        position.add(new Position(-1,6));
        position.add(new Position(-1,7));
        position.add(new Position(-1,8));
        position.add(new Position(-1,9));
        position.add(new Position(-1,10));
        position.add(new Position(11,0));
        position.add(new Position(11,1));
        position.add(new Position(11,2));
        position.add(new Position(11,3));
        position.add(new Position(11,4));
        position.add(new Position(11,5));
        position.add(new Position(11,6));
        position.add(new Position(11,7));
        position.add(new Position(11,8));
        position.add(new Position(11,9));
        position.add(new Position(11,10));
        return position;
    }
}
