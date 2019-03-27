package edu.temple.lab7;

import java.io.Serializable;
import java.util.ArrayList;

public class arrList implements Serializable {

    String bookName;

    public static boolean isLan = false;
    public static boolean isVer = false;




    public arrList(String bookName) {
        this.bookName = bookName;

    }


    public static ArrayList<arrList> displayBooks() {

        ArrayList<arrList> books = new ArrayList<arrList>();

        books.add(new arrList("Do Androids Dream of Electric Sheep?"));
        books.add(new arrList("I Was Told There'd Be Cake"));
        books.add(new arrList("To Kill a Mockingbird"));
        books.add(new arrList("The Perks of Being a Wallflower"));
        books.add(new arrList("The Man Without Qualities"));
        books.add(new arrList("Cloudy With a Chance of Meatballs"));
        books.add(new arrList("Where the Wild Things Are"));
        books.add(new arrList("Me Talk Pretty One Day "));
        books.add(new arrList("One Hundred Years of Solitude"));
        books.add(new arrList("The Elephant Tree"));

        return books;
    }

    //public String getTitle() {
    //	return bookName;
//	}

    @Override
    public String toString() {
        return bookName;
    }

}