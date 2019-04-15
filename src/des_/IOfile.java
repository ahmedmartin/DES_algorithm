/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package des_;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ahmed_Martin
 */
public class IOfile {
    
    String read_file(String file_name){
      File f = new File(file_name);
      String world="";
        try {
          try (Scanner read = new Scanner(f)) {
              while (read.hasNext()) {
                  world +=read.nextLine()+"\n";
              }
          }
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
        return world;
    }
    
    boolean write_file(String file_name,String message){
        File f = new File(file_name);
        try {
            try (PrintWriter pr = new PrintWriter(f)) {
                pr.write(message);
            }            
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
    boolean write_file_append(String file_name,String message){
        File f = new File(file_name);
        try {
            try (PrintWriter pr = new PrintWriter(f)) {
                pr.append(message+"\n");  
            }            
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
}
