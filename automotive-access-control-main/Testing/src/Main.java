import org.w3c.dom.ls.LSOutput;

import java.util.Scanner;

public class Main {


    public static char[] vowels = "aeiou".toCharArray();
    public static char[] consonants = "bcdfghjklmnpqrstvwxyz".toCharArray();

    public static void main(String[] args) {

/*
Vowels, consonants, marks
*/




                //write your code here
                Scanner readline = new Scanner(System.in);
                System.out.println("Enter a word");
                String a = readline.nextLine();

                char[] charAr = a.toCharArray();
              for(char vow : charAr){
                 //   for(char vow : a.toCharArray()){
                   if(isVowel(vow) == true){
                       System.out.println(vow);
                   } else if (isConsonant(vow) == true){
                       System.out.println(vow);
                   } else{
                       System.out.println(".");
                   }
               }

            }

            // The method checks whether a letter is a vowel
            public static boolean isVowel(char character) {
                character = Character.toLowerCase(character);  // Convert to lowercase
                for (char vowel : vowels) {  // Look for vowels in the array
                    if (character == vowel) {
                        return true;
                    }
                }
                return false;
            }

            // The method checks whether a letter is a consonant
            public static boolean isConsonant(char character) {
                character = Character.toLowerCase(character);  // Convert to lowercase
                for (char vowel : consonants) {  // Look for consonants in the array
                    if (character == vowel) {
                        return true;
                    }
                }
                return false;
            }
        }





