package com.example.assignment_1booktracker.data

object PresetBooks {
    val list = listOf(
        PresetBook(
            name = "A Sea of Unspoken Things",
            author = "Adrienne Young",
            image = "android.resource://com.example.assignment_1booktracker/drawable/a_sea_of_unspoken_things_mystery",
            category = "Mystery",
            totalPages = 328,
            criticalPoints = listOf(DataCriticalPoint(id = 1, text = "The Silent Chapter", page = 0)),
            review = "A gripping mystery novel that explores the depths of human memory and connection."
        ),
        PresetBook(
            name = "All the Water in the World",
            author = "Karen Ranney",
            image = "android.resource://com.example.assignment_1booktracker/drawable/all_the_water_in_the_world_science_fiction",
            category = "Science Fiction",
            totalPages = 456,
            criticalPoints = listOf(DataCriticalPoint(id = 2, text = "The Drought Paradox", page = 100)),
            review = "A thought-provoking science fiction novel about water scarcity and survival."
        ),
        PresetBook(
            name = "Homeseeking",
            author = "Chanel Miller",
            image = "android.resource://com.example.assignment_1booktracker/drawable/homeseeking_historycal_fiction",
            category = "Historical Fiction", // 修正类别错误
            totalPages = 234,
            criticalPoints = listOf(DataCriticalPoint(id = 3, text = "Finding Belonging", page = 50)),
            review = "A memoir of resilience and the journey to find one's place in the world."
        ),
        PresetBook(
            name = "The Note",
            author = "Unknown",
            image = "android.resource://com.example.assignment_1booktracker/drawable/the_note_mystery",
            category = "Mystery",
            totalPages = 320,
            criticalPoints = listOf(DataCriticalPoint(id = 4, text = "The Hidden Message", page = 150)),
            review = "A puzzling mystery that unfolds gradually."
        ),
        PresetBook(
            name = "The Stolen Queen",
            author = "Unknown",
            image = "android.resource://com.example.assignment_1booktracker/drawable/the_stolen_queen_historical_fiction",
            category = "Historical Fiction",
            totalPages = 400,
            criticalPoints = listOf(DataCriticalPoint(id = 5, text = "A Royal Secret", page = 200)),
            review = "An epic tale of power and betrayal in ancient times."
        ),
        PresetBook(
            name = "The Three-Body Problem",
            author = "Liu Cixin",
            image = "android.resource://com.example.assignment_1booktracker/drawable/the_three_body_problem_science_fiction",
            category = "Science Fiction",
            totalPages = 400,
            criticalPoints = listOf(DataCriticalPoint(id = 6, text = "Cosmic Mystery", page = 300)),
            review = "A groundbreaking science fiction novel that explores the universe."
        ),
        PresetBook(
            name = "Gone Girl",
            author = "Gillian Flynn",
            image = "android.resource://com.example.assignment_1booktracker/drawable/gone_girl_mystery",
            category = "Mystery",
            totalPages = 432,
            criticalPoints = listOf(DataCriticalPoint(id = 7, text = "Unreliable Narration", page = 50)),
            review = "A thrilling psychological mystery with unexpected twists."
        ),
        PresetBook(
            name = "Memoirs of a Geisha",
            author = "Arthur Golden",
            image = "android.resource://com.example.assignment_1booktracker/drawable/memoirs_of_a_geisha_historical_fiction",
            category = "Historical Fiction",
            totalPages = 448,
            criticalPoints = listOf(DataCriticalPoint(id = 8, text = "Cultural Secrets", page = 100)),
            review = "A moving historical saga set in pre-war Japan."
        ),
        PresetBook(
            name = "The Nightingale",
            author = "Kristin Hannah",
            image = "android.resource://com.example.assignment_1booktracker/drawable/the_nightingal",
            category = "Historical Fiction",
            totalPages = 448,
            criticalPoints = listOf(DataCriticalPoint(id = 9, text = "Resilience in War", page = 200)),
            review = "An inspiring story of survival and courage during wartime."
        )
    )

    data class PresetBook(
        val name: String,
        val author: String,
        val image: String,
        val category: String,
        val totalPages: Int,
        val criticalPoints: List<DataCriticalPoint>, // 修改为List<DataCriticalPoint>
        val review: String
    )
}
