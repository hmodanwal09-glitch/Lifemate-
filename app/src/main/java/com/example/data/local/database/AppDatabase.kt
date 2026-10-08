package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ChildStudyDao
import com.example.data.local.dao.ExpenseDao
import com.example.data.local.dao.FamilyDao
import com.example.data.local.dao.NoteDao
import com.example.data.local.dao.ReminderDao
import com.example.data.local.dao.ShoppingDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.ExpenseItem
import com.example.data.local.entities.FamilyMember
import com.example.data.local.entities.NoteItem
import com.example.data.local.entities.QuizQuestion
import com.example.data.local.entities.ReminderItem
import com.example.data.local.entities.ShoppingItem
import com.example.data.local.entities.StudyPlanItem
import com.example.data.local.entities.TaskItem
import com.example.data.local.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        TaskItem::class,
        NoteItem::class,
        ReminderItem::class,
        ExpenseItem::class,
        FamilyMember::class,
        ShoppingItem::class,
        StudyPlanItem::class,
        QuizQuestion::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun reminderDao(): ReminderDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun familyDao(): FamilyDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun childStudyDao(): ChildStudyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifemate_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialDemoData(database)
                    }
                }
            }
        }

        suspend fun populateInitialDemoData(database: AppDatabase) {
            val userDao = database.userDao()
            val taskDao = database.taskDao()
            val noteDao = database.noteDao()
            val reminderDao = database.reminderDao()
            val expenseDao = database.expenseDao()
            val familyDao = database.familyDao()
            val shoppingDao = database.shoppingDao()
            val childStudyDao = database.childStudyDao()

            // Prepopulate 3 default users (one for each mode)
            // Password: "Password123" -> SHA-256 hash
            val defaultPasswordHash = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"

            val adultId = userDao.insertUser(
                User(
                    name = "Sarah Miller",
                    email = "sarah@lifemate.com",
                    password = defaultPasswordHash,
                    userType = "ADULT"
                )
            )

            val childId = userDao.insertUser(
                User(
                    name = "Leo Miller",
                    email = "leo@lifemate.com",
                    password = defaultPasswordHash,
                    userType = "CHILD"
                )
            )

            val seniorId = userDao.insertUser(
                User(
                    name = "Grandpa Robert",
                    email = "robert@lifemate.com",
                    password = defaultPasswordHash,
                    userType = "SENIOR"
                )
            )

            // Prepopulate Tasks for Adult
            taskDao.insertTask(
                TaskItem(
                    title = "Submit quarterly family budget report",
                    description = "Review utilities, groceries, and savings for Q4",
                    priority = "HIGH",
                    status = "PENDING",
                    dueDate = "2026-10-12",
                    category = "Finance",
                    userId = adultId
                )
            )
            taskDao.insertTask(
                TaskItem(
                    title = "Pick up dry cleaning",
                    description = "Main street laundromat before 6 PM",
                    priority = "LOW",
                    status = "COMPLETED",
                    dueDate = "2026-10-09",
                    category = "Errands",
                    userId = adultId
                )
            )
            taskDao.insertTask(
                TaskItem(
                    title = "Organize weekend family picnic",
                    description = "Prepare sandwiches, frisbee, and blanket",
                    priority = "MEDIUM",
                    status = "PENDING",
                    dueDate = "2026-10-14",
                    category = "Family",
                    isFamilyShared = true,
                    userId = adultId
                )
            )

            // Prepopulate Tasks for Child
            taskDao.insertTask(
                TaskItem(
                    title = "Science Project: Solar System model",
                    description = "Paint Mars and Jupiter with glow paint",
                    priority = "HIGH",
                    status = "PENDING",
                    dueDate = "2026-10-11",
                    category = "Homework",
                    userId = childId
                )
            )
            taskDao.insertTask(
                TaskItem(
                    title = "Read Chapter 4 of Treasure Island",
                    description = "Summarize the chapter in 5 bullet points",
                    priority = "MEDIUM",
                    status = "PENDING",
                    dueDate = "2026-10-10",
                    category = "Reading",
                    userId = childId
                )
            )

            // Prepopulate Reminders
            reminderDao.insertReminder(
                ReminderItem(
                    title = "Family Dinner & Movie Night",
                    date = "2026-10-10",
                    time = "07:30 PM",
                    status = "PENDING",
                    reminderType = "GENERAL",
                    userId = adultId
                )
            )
            reminderDao.insertReminder(
                ReminderItem(
                    title = "Blood Pressure Medication (Lisinopril 10mg)",
                    date = "2026-10-09",
                    time = "08:00 AM",
                    status = "PENDING",
                    reminderType = "MEDICINE",
                    dosageOrDetails = "Take 1 tablet with a full glass of water after breakfast",
                    userId = seniorId
                )
            )
            reminderDao.insertReminder(
                ReminderItem(
                    title = "Cardiologist Checkup at Dr. Davis Clinic",
                    date = "2026-10-15",
                    time = "10:30 AM",
                    status = "PENDING",
                    reminderType = "APPOINTMENT",
                    dosageOrDetails = "Suite 402 - Bring health card and previous ECG report",
                    userId = seniorId
                )
            )
            reminderDao.insertReminder(
                ReminderItem(
                    title = "Evening Walk & Light Stretch",
                    date = "2026-10-09",
                    time = "05:00 PM",
                    status = "PENDING",
                    reminderType = "GENERAL",
                    dosageOrDetails = "20 mins walk in the community garden",
                    userId = seniorId
                )
            )

            // Prepopulate Notes
            noteDao.insertNote(
                NoteItem(
                    title = "Home Maintenance Checklist",
                    content = "1. Replace HVAC filter\n2. Clean gutters before rain\n3. Check smoke detector batteries\n4. Garden sprinkler timer check",
                    colorTag = "#8B5CF6",
                    userId = adultId
                )
            )
            noteDao.insertNote(
                NoteItem(
                    title = "Fun Science Facts to Remember",
                    content = "- Venus spins clockwise unlike other planets!\n- Honey never spoils in thousands of years.\n- Bananas are curved because they grow towards the sun.",
                    colorTag = "#10B981",
                    userId = childId
                )
            )

            // Prepopulate Expenses
            expenseDao.insertExpense(
                ExpenseItem(
                    amount = 84.50,
                    category = "Food",
                    date = "2026-10-07",
                    description = "Weekly organic groceries & fruits",
                    userId = adultId
                )
            )
            expenseDao.insertExpense(
                ExpenseItem(
                    amount = 45.00,
                    category = "Travel",
                    date = "2026-10-06",
                    description = "Gas station refill",
                    userId = adultId
                )
            )
            expenseDao.insertExpense(
                ExpenseItem(
                    amount = 32.20,
                    category = "Medicine",
                    date = "2026-10-05",
                    description = "Pharmacy vitamins & elder prescription",
                    userId = adultId
                )
            )
            expenseDao.insertExpense(
                ExpenseItem(
                    amount = 29.99,
                    category = "Education",
                    date = "2026-10-04",
                    description = "School stationery and geometry box",
                    userId = adultId
                )
            )

            // Prepopulate Family Contacts
            familyDao.insertFamilyMember(
                FamilyMember(
                    name = "Sarah Miller (Mom)",
                    relation = "Daughter",
                    phone = "+1 (555) 234-5678",
                    email = "sarah@lifemate.com",
                    isEmergencyContact = true,
                    notes = "Available all day, lives 10 mins away",
                    userId = seniorId
                )
            )
            familyDao.insertFamilyMember(
                FamilyMember(
                    name = "Dr. Arthur Davis (Cardiologist)",
                    relation = "Primary Doctor",
                    phone = "+1 (555) 987-6543",
                    email = "dr.davis@metroclinic.org",
                    isEmergencyContact = true,
                    notes = "Clinic hours 9 AM - 5 PM",
                    userId = seniorId
                )
            )
            familyDao.insertFamilyMember(
                FamilyMember(
                    name = "Leo Miller (Grandson)",
                    relation = "Grandson",
                    phone = "+1 (555) 345-6789",
                    email = "leo@lifemate.com",
                    isEmergencyContact = false,
                    notes = "School day ends at 3:30 PM",
                    userId = seniorId
                )
            )

            // Prepopulate Shopping Items
            shoppingDao.insertShoppingItem(
                ShoppingItem(title = "Whole Milk & Greek Yogurt", isPurchased = false, category = "Dairy", userId = adultId)
            )
            shoppingDao.insertShoppingItem(
                ShoppingItem(title = "Fresh Apples & Bananas", isPurchased = true, category = "Produce", userId = adultId)
            )
            shoppingDao.insertShoppingItem(
                ShoppingItem(title = "Sourdough Bread", isPurchased = false, category = "Bakery", userId = adultId)
            )

            // Prepopulate Study Plans for Child
            childStudyDao.insertStudyPlan(
                StudyPlanItem(
                    subject = "Mathematics",
                    topic = "Fractions and Decimal conversions",
                    targetDate = "2026-10-10",
                    durationMinutes = 40,
                    isCompleted = false,
                    notes = "Review practice set page 52",
                    userId = childId
                )
            )
            childStudyDao.insertStudyPlan(
                StudyPlanItem(
                    subject = "Science",
                    topic = "Photosynthesis & Plant Biology",
                    targetDate = "2026-10-11",
                    durationMinutes = 45,
                    isCompleted = false,
                    notes = "Draw chlorophyll leaf diagram",
                    userId = childId
                )
            )

            // Prepopulate Quiz Questions
            childStudyDao.insertAllQuizQuestions(
                listOf(
                    QuizQuestion(
                        subject = "Science",
                        question = "What gas do plants absorb from the air during photosynthesis?",
                        optionA = "Oxygen",
                        optionB = "Carbon Dioxide",
                        optionC = "Nitrogen",
                        optionD = "Helium",
                        correctIndex = 1,
                        explanation = "Plants take in Carbon Dioxide (CO2) and release Oxygen (O2) using sunlight energy."
                    ),
                    QuizQuestion(
                        subject = "Science",
                        question = "Which planet in our solar system is known as the Red Planet?",
                        optionA = "Venus",
                        optionB = "Jupiter",
                        optionC = "Mars",
                        optionD = "Saturn",
                        correctIndex = 2,
                        explanation = "Mars is called the Red Planet because iron minerals in its soil oxidize (rust), creating a reddish hue."
                    ),
                    QuizQuestion(
                        subject = "Mathematics",
                        question = "What is 3/4 converted into a percentage?",
                        optionA = "25%",
                        optionB = "50%",
                        optionC = "75%",
                        optionD = "80%",
                        correctIndex = 2,
                        explanation = "3 divided by 4 equals 0.75, which multiplied by 100 is 75%."
                    ),
                    QuizQuestion(
                        subject = "General Knowledge",
                        question = "Which is the largest ocean on Earth?",
                        optionA = "Atlantic Ocean",
                        optionB = "Indian Ocean",
                        optionC = "Arctic Ocean",
                        optionD = "Pacific Ocean",
                        correctIndex = 3,
                        explanation = "The Pacific Ocean covers more than 30% of the Earth's surface area."
                    ),
                    QuizQuestion(
                        subject = "Computer Science",
                        question = "In Java and Kotlin, what is an array or list index started from?",
                        optionA = "0",
                        optionB = "1",
                        optionC = "-1",
                        optionD = "Any number",
                        correctIndex = 0,
                        explanation = "Standard programming lists and arrays use zero-based indexing."
                    )
                )
            )
        }
    }
}
