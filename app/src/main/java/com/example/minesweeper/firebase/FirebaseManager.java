package com.example.minesweeper.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    private static DatabaseReference db =
            FirebaseDatabase.getInstance().getReference();

    public static DatabaseReference rooms() {
        return db.child("rooms");
    }
}