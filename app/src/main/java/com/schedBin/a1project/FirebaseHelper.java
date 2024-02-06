package com.schedBin.a1project;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    public void addTextToDatabase(String text) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = "wBvgNrVFCHSSdvqhVZfSefjw8283"; // Replace this with your user ID or any unique identifier
        database.getReference("users").child(userId).child("text").setValue(text);
    }
}