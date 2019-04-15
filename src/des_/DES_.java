/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package des_;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;

/**
 *
 * @author Ahmed_Martin
 */
public class DES_ {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       int test = 1;
        try {
            byte[] theKey = null;
            byte[] theMsg = null;
            byte[] theExp = null;
            if (test == 1) {
                theKey = hexToBytes("133457799BBCDFF1");
                theMsg = hexToBytes("0123456789ABCDEF");
                theExp = hexToBytes("85E813540F0AB405");
            } else if (test == 2) {
                theKey = hexToBytes("38627974656B6579"); // "8bytekey"
                theMsg = hexToBytes("6D6573736167652E"); // "message."
                theExp = hexToBytes("7CF45E129445D451");
            } else {
                return;
            }

            byte[][] subKeys = getSubkeys(theKey);
            IOfile fi = new IOfile();
            String s ="";
            File f = new File("key.txt");
            
                try {
            try (PrintWriter pr = new PrintWriter(f)) {
                for(int i = 0 ; i < 16; i ++){
                  s+= convert_byt_to_bit(subKeys[i])+"   \n";
                fi.write_file_append("keys.txt", convert_byt_to_bit(subKeys[i]));
                pr.println(convert_byt_to_bit(subKeys[i]));
            }
            }            
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
            
        }
             
            fi.write_file("keys.txt", s);
            byte[] theCph = encrypt(theMsg, subKeys);
            byte[] thePlain = decrypt(theCph, subKeys);

            File ff = new File("output.txt");
           try( PrintWriter p = new PrintWriter(ff)){
            p.println("Key     : " + bytesToHex(theKey));
            p.println("Message : " + bytesToHex(theMsg));
            p.println("Cipher  : " + bytesToHex(theCph));
            p.println("Expected: " + bytesToHex(theExp));
            p.println("Plain   : " + bytesToHex(thePlain));
           }
            
            System.out.println("Key     : " + bytesToHex(theKey));
            System.out.println("Message : " + bytesToHex(theMsg));
            System.out.println("Cipher  : " + bytesToHex(theCph));
            System.out.println("Expected: " + bytesToHex(theExp));
            System.out.println("Plain   : " + bytesToHex(thePlain));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static byte[][] getSubkeys(byte[] key) {
        String keyString = convert_byt_to_bit(key);

        byte[][] PC_1 = {{57, 49, 41, 33, 25, 17, 9},
        {1, 58, 50, 42, 34, 26, 18},
        {10, 2, 59, 51, 43, 35, 27},
        {19, 11, 3, 60, 52, 44, 36},
        {63, 55, 47, 39, 31, 23, 15},
        {7, 62, 54, 46, 38, 30, 22},
        {14, 6, 61, 53, 45, 37, 29},
        {21, 13, 5, 28, 20, 12, 4}};

        String key56 = "";

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                key56 += keyString.charAt(PC_1[i][j] - 1);
            }
        }

        byte[] leftShiftArray = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

        String[] Lkeys = new String[16];
        String[] Rkeys = new String[16];

        String l = key56.substring(0, 28);
        String r = key56.substring(28, key56.length());

        Lkeys[0] = shiftChars(l, leftShiftArray[0]);
        Rkeys[0] = shiftChars(r, leftShiftArray[0]);

        for (int i = 1; i < leftShiftArray.length; i++) {
            Lkeys[i] = shiftChars(Lkeys[i - 1], leftShiftArray[i]);
            Rkeys[i] = shiftChars(Rkeys[i - 1], leftShiftArray[i]);

        }
        String[] concKeys = new String[16];
        for (int i = 0; i < leftShiftArray.length; i++) {
            concKeys[i] = Lkeys[i] + Rkeys[i];

        }
        int[][] PC_2 = {{14, 17, 11, 24, 1, 5},
        {3, 28, 15, 6, 21, 10},
        {23, 19, 12, 4, 26, 8},
        {16, 7, 27, 20, 13, 2},
        {41, 52, 31, 37, 47, 55},
        {30, 40, 51, 45, 33, 48},
        {44, 49, 39, 56, 34, 53},
        {46, 42, 50, 36, 29, 32}};

        String[] permKeys = new String[16];
         
        for (int k = 0; k < 16; k++) {
            permKeys[k] = "";
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 6; j++) {
                    permKeys[k] += concKeys[k].charAt(PC_2[i][j] - 1);
                }
               
            }
        }
        byte[][] result = new byte[16][];
        for (int i = 0; i < 16; i++) {
            
            result[i] = new BigInteger(permKeys[i], 2).toByteArray();
        }

        result = delZerros(result);

        return result;
    }

    public static byte[] encrypt(byte[] theMsg, byte[][] subKeys) {
        return des(theMsg, subKeys, true);
    }

    public static byte[] decrypt(byte[] theMsg, byte[][] subKeys) {
        return des(theMsg, subKeys, false);
    }

    static byte[] des(byte[] theMsg, byte[][] subKeys, boolean isEncrypting) {

        byte[][] IP = {{58, 50, 42, 34, 26, 18, 10, 2},
        {60, 52, 44, 36, 28, 20, 12, 4},
        {62, 54, 46, 38, 30, 22, 14, 6},
        {64, 56, 48, 40, 32, 24, 16, 8},
        {57, 49, 41, 33, 25, 17, 9, 1},
        {59, 51, 43, 35, 27, 19, 11, 3},
        {61, 53, 45, 37, 29, 21, 13, 5},
        {63, 55, 47, 39, 31, 23, 15, 7}};
        byte[][] Ipinv = {{40, 8, 48, 16, 56, 24, 64, 32},
        {39, 7, 47, 15, 55, 23, 63, 31},
        {38, 6, 46, 14, 54, 22, 62, 30},
        {37, 5, 45, 13, 53, 21, 61, 29},
        {36, 4, 44, 12, 52, 20, 60, 28},
        {35, 3, 43, 11, 51, 19, 59, 27},
        {34, 2, 42, 10, 50, 18, 58, 26},
        {33, 1, 41, 9, 49, 17, 57, 25}};
        byte [][][] perm = new byte[2][][];
        if(isEncrypting){
            perm[0] = IP;
            perm[1] = Ipinv;
            
        }else{
            perm[0] = IP;
            perm[1] = Ipinv;
            byte [][] subkes2 = new byte[subKeys.length][subKeys[0].length];
            for(int i = 0 ; i < subKeys.length; i++){
                subkes2[i] = subKeys[subKeys.length-i-1];
            }
            subKeys = subkes2;
        }
        String[] s = new String[subKeys.length];

        for (int i = 0; i < 16; i++) {
            s[i] = convert_byt_to_bit(subKeys[i]);
        }

        String temp_message = convert_byt_to_bit(theMsg);
        String message = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                message += temp_message.charAt(perm[0][i][j] - 1);
            }
        }

        String[] r = new String[17];
        String[] l = new String[17];
        l[0] = message.substring(0, 32);
        r[0] = message.substring(32, message.length());
        File f = new File("rounds.txt");
        try {
            try (PrintWriter pr = new PrintWriter(f)) {
                for (int k = 1; k < 17; k++) {
            l[k] = r[k - 1];
            r[k] = xor(l[k - 1], fun(r[k - 1], s[k - 1]));
            //System.out.println(r[k]);
            pr.println(l[k]+r[k]);  
        }
                
            }            
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
           
        }
        
        String rl = r[r.length - 1] + l[l.length - 1];
        //System.out.println(rl);
        //System.out.println(l[l.length-1]);

        String m = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                m += rl.charAt(perm[1][i][j] - 1);
            }
        }
        byte[] theMsg2 = new BigInteger(m, 2).toByteArray();
        if (m.charAt(0) == '1') {
            theMsg = new byte[theMsg2.length - 1];
            for (int i = 0; i < theMsg2.length - 1; i++) {
                theMsg[i] = theMsg2[i + 1];
            }
            return theMsg;
        } else {

            return theMsg2;
        }
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(
                        str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }

    }

    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        } else {
            int len = data.length;
            String str = "";
            for (int i = 0; i < len; i++) {
                if ((data[i] & 0xFF) < 16) {
                    str = str + "0"
                            + java.lang.Integer.toHexString(data[i] & 0xFF);
                } else {
                    str = str
                            + java.lang.Integer.toHexString(data[i] & 0xFF);
                }
            }
            return str.toUpperCase();
        }
    }

    public static String shiftChars(String string, byte shift) {
        return string.substring(shift, string.length()) + string.substring(0, shift);
    }

    public static String convert_byt_to_bit(byte[] thekey) {
        String result = "";
        for (int i = 0; i < thekey.length; i++) {
            byte b1 = thekey[i];
            result += String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
        }
        return result;
    }

    private static byte[][] delZerros(byte[][] result) {
        byte[][] res = new byte[16][];
        for (int i = 0; i < result.length; i++) {
            if (result[i].length > 6 && result[i][0] == 0) {
                res[i] = new byte[result[i].length - 1];
                for (int j = 1; j < result[i].length; j++) {
                    res[i][j - 1] = result[i][j];
                }
            } else {
                res[i] = new byte[result[i].length];
                for (int j = 0; j < result[i].length; j++) {
                    res[i][j] = result[i][j];
                }
            }
        }
        return res;
    }

    private static String fun(String string, String key) {
        byte[][] expantion = {{32, 1, 2, 3, 4, 5},
        {4, 5, 6, 7, 8, 9},
        {8, 9, 10, 11, 12, 13},
        {12, 13, 14, 15, 16, 17},
        {16, 17, 18, 19, 20, 21},
        {20, 21, 22, 23, 24, 25},
        {24, 25, 26, 27, 28, 29},
        {28, 29, 30, 31, 32, 1}};
        String holder = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                holder += string.charAt(expantion[i][j] - 1);
            }
        }
        holder = xor(holder, key);
        //System.out.println(holder);
        holder = applySboxes(holder);
        //System.out.println(holder);
        byte[][] p = {{16, 7, 20, 21},
        {29, 12, 28, 17},
        {1, 15, 23, 26},
        {5, 18, 31, 10},
        {2, 8, 24, 14},
        {32, 27, 3, 9},
        {19, 13, 30, 6},
        {22, 11, 4, 25}};
        String result = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                result += holder.charAt(p[i][j] - 1);
            }
        }
        return result; //To change body of generated methods, choose Tools | Templates.
    }

    private static String xor(String string, String fun) {
        String result = "";
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == fun.charAt(i)) {
                result += "0";
            } else {
                result += "1";
            }
        }
        return result;
    }

    private static String applySboxes(String holder) {
        String[] holders = new String[8];
        String[] rows = new String[8];
        String[] cols = new String[8];
        //System.out.println(holder);
        int row = 0;
        int col = 0;
        for (int i = 0; i < 8; i++) {
            holders[i] = holder.substring(i * 6, (i + 1) * 6);
            //System.out.println(holders[i]);
            rows[i] = "" + holders[i].charAt(0) + holders[i].charAt(holders[i].length() - 1);
            //System.out.println(rows[i] + " " + new BigInteger(rows[i], 2).toByteArray()[0]);
            cols[i] = holders[i].substring(1, holders[i].length() - 1);
            //System.out.println(cols[i] + " " + new BigInteger(cols[i], 2).toByteArray()[0]);
        }
        byte[][][] sBoxes
                = {{{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7}, {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8}, {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0}, {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}},
                {{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10}, {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5}, {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15}, {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}},
                {{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8}, {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1}, {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7}, {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}},
                {{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15}, {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9}, {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4}, {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}},
                {{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9}, {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6}, {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14}, {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}},
                {{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11}, {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8}, {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6}, {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}},
                {{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1}, {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6}, {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2}, {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}},
                {{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7}, {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2}, {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8}, {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}}};
        holder = "";
        String s = "";
        for (int i = 0; i < 8; i++) {

            s += bin(sBoxes[i][new BigInteger(rows[i], 2).toByteArray()[0]][new BigInteger(cols[i], 2).toByteArray()[0]]);
            //System.out.println(sBoxes[i][new BigInteger(rows[i], 2).toByteArray()[0]][new BigInteger(cols[i], 2).toByteArray()[0]]);

            //System.out.println(" "+  new BigInteger(rows[i], 2).toByteArray()[0]+" "+new BigInteger(cols[i], 2).toByteArray()[0]);
            while (s.length() % 4 != 0 && s.length() < 4 || s.equals("")) {
                s = "0" + s;
            }
            //System.out.println(s);
            holder += s;
            s = "";
        }

        return holder; //To change body of generated methods, choose Tools | Templates.
    }

    public static String bin(byte n) {
        String s = "";
        int a = 0;
        while (n > 0) {
            a = n % 2;
            s = a + "" + s;
            n = (byte) (n / 2);
        }
        return s;
    }

}

