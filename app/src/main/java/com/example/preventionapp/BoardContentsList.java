package com.example.preventionapp;

import java.util.ArrayList;

public class BoardContentsList extends ArrayList<BoardContentsListItem> {

    private static BoardContentsList boardContentsList = null;
    private BoardContentsList() { }

    public static BoardContentsList getboardContentsList(){
        if (boardContentsList == null) {
            boardContentsList = new BoardContentsList();
        }
        return boardContentsList;
    }


}
