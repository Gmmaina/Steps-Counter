package com.example.dailyburn.repositories


import com.example.dailyburn.R
import com.example.dailyburn.models.FitnessTip

class TipsRepository {
    // List of all available categories
    val categories = listOf(
        "All",
        "Nutrition",
        "Cardio",
        "Strength Training",
        "Recovery",
        "Mental Wellness"
    )

    // Get all tips or filter by category
    fun getFitnessTips(category: String? = null): List<FitnessTip> {
        return if (category == null || category == "All") {
            allTips
        } else {
            allTips.filter { it.category == category }
        }
    }

    // Predefined list of fitness tips
    private val allTips = listOf(
        // Nutrition Tips
        FitnessTip(
            id = 1,
            title = "Stay Hydrated",
            content = "Drink at least 8 glasses of water daily. Proper hydration improves performance, prevents cramps, and helps with recovery.",
            category = "Nutrition",
            iconResId = R.drawable.ic_water
        ),
        FitnessTip(
            id = 2,
            title = "Protein Timing",
            content = "Consume 20-30g of protein within 30 minutes after your workout to maximize muscle recovery and growth.",
            category = "Nutrition",
            iconResId = R.drawable.ic_protein
        ),
        FitnessTip(
            id = 3,
            title = "Pre-Workout Nutrition",
            content = "Eat a balanced meal with carbs and protein 2-3 hours before workout. If short on time, try a banana or small smoothie 30 minutes before.",
            category = "Nutrition",
            iconResId = R.drawable.ic_food
        ),
        FitnessTip(
            id = 4,
            title = "Limit Processed Foods",
            content = "Focus on whole foods like vegetables, fruits, lean proteins, and whole grains. These provide better nutrition for recovery and performance.",
            category = "Nutrition",
            iconResId = R.drawable.ic_vegetables
        ),

        // Cardio Tips
        FitnessTip(
            id = 5,
            title = "High-Intensity Intervals",
            content = "Mix short bursts of intense activity (30 sec) with recovery periods (90 sec) to burn more calories and improve cardiovascular fitness faster.",
            category = "Cardio",
            iconResId = R.drawable.ic_steps
        ),
        FitnessTip(
            id = 6,
            title = "Zone 2 Training",
            content = "Train at 60-70% of your max heart rate for longer periods to build aerobic base and improve fat burning.",
            category = "Cardio",
            iconResId = R.drawable.ic_heart_rate
        ),
        FitnessTip(
            id = 7,
            title = "Morning Cardio",
            content = "Consider fasted morning cardio sessions for improved fat burning. Keep intensity moderate and sessions under 45 minutes.",
            category = "Cardio",
            iconResId = R.drawable.ic_sunrise
        ),
        FitnessTip(
            id = 8,
            title = "Track Progress",
            content = "Record distance, time, and heart rate to see improvements. Aim to increase distance or decrease time gradually week by week.",
            category = "Cardio",
            iconResId = R.drawable.ic_chart
        ),

        // Strength Training Tips
        FitnessTip(
            id = 9,
            title = "Progressive Overload",
            content = "Gradually increase weight, reps, or sets over time to continually challenge your muscles and promote growth.",
            category = "Strength Training",
            iconResId = R.drawable.ic_dumbbell
        ),
        FitnessTip(
            id = 10,
            title = "Compound Exercises",
            content = "Focus on multi-joint movements like squats, deadlifts, bench press, and pull-ups for maximum muscle activation and hormone response.",
            category = "Strength Training",
            iconResId = R.drawable.ic_barbell
        ),
        FitnessTip(
            id = 11,
            title = "Mind-Muscle Connection",
            content = "Focus on feeling the target muscle working during each rep. This conscious connection can improve results by up to 60%.",
            category = "Strength Training",
            iconResId = R.drawable.ic_brain
        ),
        FitnessTip(
            id = 12,
            title = "Rest Between Sets",
            content = "For strength, rest 3-5 minutes between sets. For muscle growth, keep rest periods between 60-90 seconds.",
            category = "Strength Training",
            iconResId = R.drawable.ic_timer
        ),

        // Recovery Tips
        FitnessTip(
            id = 13,
            title = "Quality Sleep",
            content = "Aim for 7-9 hours of quality sleep. Most muscle recovery and growth happens during sleep when growth hormone levels peak.",
            category = "Recovery",
            iconResId = R.drawable.ic_sleep
        ),
        FitnessTip(
            id = 14,
            title = "Active Recovery",
            content = "Light activity like walking, swimming, or yoga on rest days increases blood flow to muscles and speeds up recovery.",
            category = "Recovery",
            iconResId = R.drawable.ic_meditation
        ),
        FitnessTip(
            id = 15,
            title = "Foam Rolling",
            content = "Spend 10 minutes foam rolling tight muscles to improve blood flow, reduce soreness, and maintain muscle function.",
            category = "Recovery",
            iconResId = R.drawable.ic_barbell
        ),
        FitnessTip(
            id = 16,
            title = "Cold Therapy",
            content = "Try ice baths or cold showers after intense workouts to reduce inflammation and speed up recovery times.",
            category = "Recovery",
            iconResId = R.drawable.ic_snowflake
        ),

        // Mental Wellness Tips
        FitnessTip(
            id = 17,
            title = "Mindful Workouts",
            content = "Practice mindfulness during exercise by focusing on your breathing and movement. This improves performance and reduces stress.",
            category = "Mental Wellness",
            iconResId = R.drawable.ic_meditation
        ),
        FitnessTip(
            id = 18,
            title = "Set SMART Goals",
            content = "Create Specific, Measurable, Achievable, Relevant, and Time-bound fitness goals to stay motivated and track progress.",
            category = "Mental Wellness",
            iconResId = R.drawable.ic_goals
        ),
        FitnessTip(
            id = 19,
            title = "Celebrate Small Wins",
            content = "Acknowledge and celebrate small achievements in your fitness journey to maintain motivation and build confidence.",
            category = "Mental Wellness",
            iconResId = R.drawable.ic_trophy
        ),
        FitnessTip(
            id = 20,
            title = "Regular Social Support",
            content = "Exercise with friends or join group classes. Social connection during fitness activities boosts adherence and enjoyment.",
            category = "Mental Wellness",
            iconResId = R.drawable.ic_group
        )
    )
}