package com.example.myapplication.model

import java.sql.Timestamp

class Booking constructor(
    var id: String,
    nightClub: NightClub,
    user: User,
    date: Timestamp,
    review: Review
) {
}