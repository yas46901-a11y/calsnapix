package com.example.data.library

/**
 * Authentic Indian Food Library based on the Indian Food Composition Tables (IFCT) 2017
 * published by the National Institute of Nutrition (ICMR-NIN), Hyderabad.
 */
data class IndianDishIngredient(
  val name: String,
  val grams: Double,
  val hiddenFatGrams: Double = 0.0
)

data class IndianDish(
  val id: String,
  val name: String,
  val aliases: List<String>,
  val region: String, // North, South, East, West, Pan-Indian
  val mealType: String, // Breakfast, Lunch, Dinner, Snack
  val standardServingGrams: Double,
  val ingredients: List<IndianDishIngredient>,
  val caloriesPer100g: Double,
  val proteinPer100g: Double,
  val carbsPer100g: Double,
  val fatPer100g: Double,
  val fiberPer100g: Double = 0.0,
  val defaultHiddenFatGrams: Double = 0.0,
  val category: String = "Curry / Sabzi",
  val source: String = "IFCT 2017 (ICMR-NIN)"
) {
  fun calculateForGrams(grams: Double, multiplier: Float = 1.0f): DishNutritionCalculation {
    val totalGrams = grams * multiplier
    val factor = totalGrams / 100.0
    val totalCalories = kotlin.math.round(caloriesPer100g * factor).toInt()
    val minKcal = kotlin.math.round(totalCalories * 0.88).toInt()
    val maxKcal = kotlin.math.round(totalCalories * 1.14).toInt()

    val totalHiddenFat = defaultHiddenFatGrams * multiplier
    val hiddenFatKcal = kotlin.math.round(totalHiddenFat * 9.0).toInt()

    return DishNutritionCalculation(
      dishName = name,
      servingGrams = totalGrams,
      calories = totalCalories,
      caloriesMin = minKcal,
      caloriesMax = maxKcal,
      proteinGrams = kotlin.math.round(proteinPer100g * factor * 10.0) / 10.0,
      carbsGrams = kotlin.math.round(carbsPer100g * factor * 10.0) / 10.0,
      fatGrams = kotlin.math.round(fatPer100g * factor * 10.0) / 10.0,
      fiberGrams = kotlin.math.round(fiberPer100g * factor * 10.0) / 10.0,
      hiddenFatGrams = kotlin.math.round(totalHiddenFat * 10.0) / 10.0,
      hiddenFatKcal = hiddenFatKcal,
      source = source
    )
  }
}

data class DishNutritionCalculation(
  val dishName: String,
  val servingGrams: Double,
  val calories: Int,
  val caloriesMin: Int,
  val caloriesMax: Int,
  val proteinGrams: Double,
  val carbsGrams: Double,
  val fatGrams: Double,
  val fiberGrams: Double,
  val hiddenFatGrams: Double,
  val hiddenFatKcal: Int,
  val source: String
)

object IndianDishLibrary {

  val allDishes: List<IndianDish> by lazy { baseDishes + additionalIndianDishes }
  val dishes: List<IndianDish> get() = allDishes

  private val baseDishes: List<IndianDish> = listOf(
    // --- South Indian Breakfast & Staples ---
    IndianDish(
      id = "south_idli",
      name = "Steamed Idli (South Indian)",
      aliases = listOf("idli", "steamed idli", "rice idli", "mallipoo idli", "idly"),
      region = "South",
      mealType = "Breakfast",
      standardServingGrams = 150.0,
      ingredients = listOf(
        IndianDishIngredient("Parboiled Rice & Urad Dal Batter", 145.0),
        IndianDishIngredient("Gingelly Oil (steaming brush)", 5.0, 4.5)
      ),
      caloriesPer100g = 142.0,
      proteinPer100g = 4.8,
      carbsPer100g = 28.5,
      fatPer100g = 0.8,
      fiberPer100g = 1.9,
      defaultHiddenFatGrams = 1.5,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "south_medu_vada",
      name = "Medu Vada",
      aliases = listOf("vada", "medu vada", "urad dal vada", "garelu"),
      region = "South",
      mealType = "Breakfast",
      standardServingGrams = 90.0,
      ingredients = listOf(
        IndianDishIngredient("Urad Dal & Spices", 70.0),
        IndianDishIngredient("Deep Frying Oil Absorbed", 20.0, 18.0)
      ),
      caloriesPer100g = 278.0,
      proteinPer100g = 8.5,
      carbsPer100g = 24.0,
      fatPer100g = 16.5,
      fiberPer100g = 3.2,
      defaultHiddenFatGrams = 14.5,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "south_masala_dosa",
      name = "Crispy Masala Dosa",
      aliases = listOf("masala dosa", "dosa", "mysore masala dosa", "plain dosa"),
      region = "South",
      mealType = "Breakfast",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Rice & Urad Dal Crepe", 100.0),
        IndianDishIngredient("Spiced Potato Filling", 65.0),
        IndianDishIngredient("Butter / Ghee on Tawa", 15.0, 13.5)
      ),
      caloriesPer100g = 195.0,
      proteinPer100g = 4.2,
      carbsPer100g = 29.0,
      fatPer100g = 7.2,
      fiberPer100g = 2.4,
      defaultHiddenFatGrams = 9.0,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "south_sambar",
      name = "South Indian Sambar",
      aliases = listOf("sambar", "sambhar", "veppam poo sambar", "drumstick sambar"),
      region = "South",
      mealType = "Lunch",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Toor Dal & Tamarind Water", 130.0),
        IndianDishIngredient("Mixed Vegetables (Drumstick, Pumpkin, Shallots)", 60.0),
        IndianDishIngredient("Mustard Tadka Oil", 10.0, 9.0)
      ),
      caloriesPer100g = 68.0,
      proteinPer100g = 3.2,
      carbsPer100g = 10.5,
      fatPer100g = 1.8,
      fiberPer100g = 2.6,
      defaultHiddenFatGrams = 3.5,
      category = "Lentils / Dal"
    ),
    IndianDish(
      id = "south_rasam",
      name = "Tomato Pepper Rasam",
      aliases = listOf("rasam", "charu", "saaru", "tomato rasam", "pepper rasam"),
      region = "South",
      mealType = "Lunch",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Tomato, Tamarind & Cumin Broth", 165.0),
        IndianDishIngredient("Ghee Mustard Tadka", 8.0, 7.5)
      ),
      caloriesPer100g = 38.0,
      proteinPer100g = 1.4,
      carbsPer100g = 5.2,
      fatPer100g = 1.4,
      fiberPer100g = 1.1,
      defaultHiddenFatGrams = 2.5,
      category = "Lentils / Dal"
    ),
    IndianDish(
      id = "south_upma",
      name = "Vegetable Rava Upma",
      aliases = listOf("upma", "rava upma", "uppittu", "sooji upma"),
      region = "South",
      mealType = "Breakfast",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Roasted Semolina / Rava", 90.0),
        IndianDishIngredient("Vegetables, Peanuts & Curry Leaves", 75.0),
        IndianDishIngredient("Ghee / Oil Tempering", 15.0, 13.0)
      ),
      caloriesPer100g = 158.0,
      proteinPer100g = 4.0,
      carbsPer100g = 24.5,
      fatPer100g = 5.2,
      fiberPer100g = 2.8,
      defaultHiddenFatGrams = 6.5,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "south_pongal",
      name = "Ven Pongal",
      aliases = listOf("pongal", "ven pongal", "khara pongal", "ghee pongal"),
      region = "South",
      mealType = "Breakfast",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Rice & Moong Dal", 150.0),
        IndianDishIngredient("Cashews, Black Pepper & Curry Leaves", 25.0),
        IndianDishIngredient("Pure Desi Ghee", 25.0, 24.5)
      ),
      caloriesPer100g = 185.0,
      proteinPer100g = 5.5,
      carbsPer100g = 26.0,
      fatPer100g = 7.0,
      fiberPer100g = 2.1,
      defaultHiddenFatGrams = 11.0,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "south_curd_rice",
      name = "South Indian Curd Rice (Thayir Sadam)",
      aliases = listOf("curd rice", "thayir sadam", "daddojanam", "yogurt rice"),
      region = "South",
      mealType = "Lunch",
      standardServingGrams = 220.0,
      ingredients = listOf(
        IndianDishIngredient("Cooked Rice", 120.0),
        IndianDishIngredient("Whole Milk Curd", 90.0),
        IndianDishIngredient("Mustard Tadka & Hing", 10.0, 7.5)
      ),
      caloriesPer100g = 122.0,
      proteinPer100g = 3.6,
      carbsPer100g = 19.5,
      fatPer100g = 3.5,
      fiberPer100g = 0.8,
      defaultHiddenFatGrams = 3.0,
      category = "Rice"
    ),
    IndianDish(
      id = "south_lemon_rice",
      name = "Lemon Rice (Chitranna)",
      aliases = listOf("lemon rice", "chitranna", "elamichai sadam", "nimmakaya rice"),
      region = "South",
      mealType = "Lunch",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Cooked Rice", 155.0),
        IndianDishIngredient("Lemon Juice, Peanuts & Turmeric", 30.0),
        IndianDishIngredient("Tadka Oil", 15.0, 14.0)
      ),
      caloriesPer100g = 162.0,
      proteinPer100g = 3.4,
      carbsPer100g = 27.0,
      fatPer100g = 4.8,
      fiberPer100g = 1.5,
      defaultHiddenFatGrams = 6.0,
      category = "Rice"
    ),
    IndianDish(
      id = "south_appam",
      name = "Kerala Appam with Stew",
      aliases = listOf("appam", "kallappam", "palappam", "hopper"),
      region = "South",
      mealType = "Breakfast",
      standardServingGrams = 160.0,
      ingredients = listOf(
        IndianDishIngredient("Fermented Rice Coconut Batter", 150.0),
        IndianDishIngredient("Oil for pan swirl", 5.0, 4.5)
      ),
      caloriesPer100g = 135.0,
      proteinPer100g = 2.8,
      carbsPer100g = 26.5,
      fatPer100g = 2.0,
      fiberPer100g = 1.2,
      defaultHiddenFatGrams = 2.0,
      category = "Breads"
    ),

    // --- North Indian Curries & Dals ---
    IndianDish(
      id = "north_palak_paneer",
      name = "Homestyle Palak Paneer",
      aliases = listOf("palak paneer", "spinach paneer", "paneer saag", "saag paneer"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Pureed Spinach Leaves", 110.0),
        IndianDishIngredient("Fresh Malai Paneer Cubes", 65.0),
        IndianDishIngredient("Mustard Oil / Ghee Gravy Base", 25.0, 22.0)
      ),
      caloriesPer100g = 160.0,
      proteinPer100g = 7.5,
      carbsPer100g = 5.0,
      fatPer100g = 12.0,
      fiberPer100g = 3.2,
      defaultHiddenFatGrams = 12.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "north_paneer_butter_masala",
      name = "Paneer Butter Masala",
      aliases = listOf("paneer butter masala", "paneer makhani", "butter paneer", "shahi paneer"),
      region = "North",
      mealType = "Dinner",
      standardServingGrams = 220.0,
      ingredients = listOf(
        IndianDishIngredient("Paneer", 75.0),
        IndianDishIngredient("Tomato Cashew Gravy", 105.0),
        IndianDishIngredient("Butter & Fresh Cream", 40.0, 36.0)
      ),
      caloriesPer100g = 210.0,
      proteinPer100g = 8.5,
      carbsPer100g = 9.0,
      fatPer100g = 16.5,
      fiberPer100g = 2.1,
      defaultHiddenFatGrams = 18.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "north_dal_makhani",
      name = "Amritsari Dal Makhani",
      aliases = listOf("dal makhani", "makhani dal", "black dal", "maa ki dal", "kaali dal"),
      region = "North",
      mealType = "Dinner",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Whole Black Urad & Rajma", 125.0),
        IndianDishIngredient("Tomato Puree & Spices", 45.0),
        IndianDishIngredient("White Butter (Makhan) & Cream", 30.0, 26.0)
      ),
      caloriesPer100g = 140.0,
      proteinPer100g = 6.0,
      carbsPer100g = 18.0,
      fatPer100g = 5.2,
      fiberPer100g = 4.0,
      defaultHiddenFatGrams = 8.0,
      category = "Lentils / Dal"
    ),
    IndianDish(
      id = "north_dal_tadka",
      name = "Yellow Dal Tadka",
      aliases = listOf("dal tadka", "yellow dal", "toor dal tadka", "arhar dal", "peeli dal"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Boiled Toor / Arhar Dal", 155.0),
        IndianDishIngredient("Onion Tomato Masala", 30.0),
        IndianDishIngredient("Desi Ghee Jeera Hing Tadka", 15.0, 14.5)
      ),
      caloriesPer100g = 112.0,
      proteinPer100g = 5.2,
      carbsPer100g = 14.8,
      fatPer100g = 3.6,
      fiberPer100g = 3.2,
      defaultHiddenFatGrams = 5.5,
      category = "Lentils / Dal"
    ),
    IndianDish(
      id = "north_dal_fry",
      name = "Dhaba Dal Fry",
      aliases = listOf("dal fry", "dhaba dal", "chana dal fry", "mixed dal fry"),
      region = "North",
      mealType = "Dinner",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Chana & Toor Dal Mix", 140.0),
        IndianDishIngredient("Fried Onion Garlic Masala", 40.0),
        IndianDishIngredient("Ghee / Mustard Oil", 20.0, 18.0)
      ),
      caloriesPer100g = 135.0,
      proteinPer100g = 5.8,
      carbsPer100g = 16.5,
      fatPer100g = 5.4,
      fiberPer100g = 3.8,
      defaultHiddenFatGrams = 8.5,
      category = "Lentils / Dal"
    ),
    IndianDish(
      id = "north_rajma_masala",
      name = "Punjabi Rajma Masala",
      aliases = listOf("rajma", "rajma masala", "punjabi rajma", "kidney bean curry"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 220.0,
      ingredients = listOf(
        IndianDishIngredient("Cooked Red Kidney Beans", 140.0),
        IndianDishIngredient("Ginger Garlic Tomato Gravy", 60.0),
        IndianDishIngredient("Mustard Oil / Ghee", 20.0, 17.5)
      ),
      caloriesPer100g = 125.0,
      proteinPer100g = 6.5,
      carbsPer100g = 19.0,
      fatPer100g = 3.0,
      fiberPer100g = 4.8,
      defaultHiddenFatGrams = 5.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "north_chole_masala",
      name = "Pindi Chole Masala",
      aliases = listOf("chole", "chana masala", "pindi chole", "amritsari chole", "chickpea curry"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 220.0,
      ingredients = listOf(
        IndianDishIngredient("Kabuli Chana (Chickpeas)", 140.0),
        IndianDishIngredient("Anardana Spiced Onion Gravy", 55.0),
        IndianDishIngredient("Oil / Ghee Tadka", 25.0, 22.0)
      ),
      caloriesPer100g = 155.0,
      proteinPer100g = 7.2,
      carbsPer100g = 22.0,
      fatPer100g = 4.5,
      fiberPer100g = 5.5,
      defaultHiddenFatGrams = 7.5,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "north_bhatura",
      name = "Fluffy Bhatura",
      aliases = listOf("bhatura", "bhatoora", "chole bhature bhatura"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 90.0,
      ingredients = listOf(
        IndianDishIngredient("Fermented Maida Dough", 70.0),
        IndianDishIngredient("Deep Frying Oil", 20.0, 19.0)
      ),
      caloriesPer100g = 320.0,
      proteinPer100g = 6.8,
      carbsPer100g = 46.0,
      fatPer100g = 12.8,
      fiberPer100g = 1.8,
      defaultHiddenFatGrams = 11.5,
      category = "Breads"
    ),
    IndianDish(
      id = "north_aloo_paratha",
      name = "Punjabi Aloo Paratha with Butter",
      aliases = listOf("aloo paratha", "paratha", "potato paratha", "aloo parantha"),
      region = "North",
      mealType = "Breakfast",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Whole Wheat Atta Dough", 80.0),
        IndianDishIngredient("Spiced Boiled Potato Filling", 75.0),
        IndianDishIngredient("Desi Ghee / Butter", 25.0, 24.0)
      ),
      caloriesPer100g = 235.0,
      proteinPer100g = 5.2,
      carbsPer100g = 36.5,
      fatPer100g = 8.2,
      fiberPer100g = 4.1,
      defaultHiddenFatGrams = 12.0,
      category = "Breads"
    ),
    IndianDish(
      id = "north_paneer_paratha",
      name = "Stuffed Paneer Paratha",
      aliases = listOf("paneer paratha", "paneer parantha", "cottage cheese paratha"),
      region = "North",
      mealType = "Breakfast",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Whole Wheat Atta", 75.0),
        IndianDishIngredient("Grated Spiced Paneer", 80.0),
        IndianDishIngredient("Ghee on Tawa", 25.0, 23.5)
      ),
      caloriesPer100g = 265.0,
      proteinPer100g = 9.8,
      carbsPer100g = 31.0,
      fatPer100g = 11.5,
      fiberPer100g = 3.6,
      defaultHiddenFatGrams = 14.0,
      category = "Breads"
    ),
    IndianDish(
      id = "north_phulka_roti",
      name = "Whole Wheat Phulka Roti",
      aliases = listOf("phulka", "roti", "chapati", "fulka", "gehu roti"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 35.0,
      ingredients = listOf(
        IndianDishIngredient("Stone Ground Chakki Atta & Water", 33.0),
        IndianDishIngredient("Desi Ghee Brush", 2.0, 1.8)
      ),
      caloriesPer100g = 290.0,
      proteinPer100g = 11.0,
      carbsPer100g = 55.0,
      fatPer100g = 3.5,
      fiberPer100g = 9.5,
      defaultHiddenFatGrams = 1.0,
      category = "Breads"
    ),
    IndianDish(
      id = "north_tandoori_roti",
      name = "Clay Oven Tandoori Roti",
      aliases = listOf("tandoori roti", "butter tandoori roti", "kadhai roti"),
      region = "North",
      mealType = "Dinner",
      standardServingGrams = 50.0,
      ingredients = listOf(
        IndianDishIngredient("Atta Dough cooked in Tandoor", 46.0),
        IndianDishIngredient("Butter Glaze", 4.0, 3.8)
      ),
      caloriesPer100g = 275.0,
      proteinPer100g = 9.5,
      carbsPer100g = 52.0,
      fatPer100g = 3.8,
      fiberPer100g = 8.2,
      defaultHiddenFatGrams = 2.0,
      category = "Breads"
    ),
    IndianDish(
      id = "north_butter_naan",
      name = "Garlic Butter Naan",
      aliases = listOf("naan", "butter naan", "garlic naan", "plain naan"),
      region = "North",
      mealType = "Dinner",
      standardServingGrams = 90.0,
      ingredients = listOf(
        IndianDishIngredient("Refined Flour (Maida) & Yogurt Dough", 75.0),
        IndianDishIngredient("Pure Butter Glaze", 15.0, 13.5)
      ),
      caloriesPer100g = 310.0,
      proteinPer100g = 8.2,
      carbsPer100g = 48.0,
      fatPer100g = 9.8,
      fiberPer100g = 2.0,
      defaultHiddenFatGrams = 8.5,
      category = "Breads"
    ),
    IndianDish(
      id = "north_jeera_rice",
      name = "Jeera Basmati Rice",
      aliases = listOf("jeera rice", "cumin rice", "steamed basmati rice", "chawal"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Boiled Basmati Rice", 165.0),
        IndianDishIngredient("Ghee & Cumin Seeds", 15.0, 13.0)
      ),
      caloriesPer100g = 130.0,
      proteinPer100g = 2.8,
      carbsPer100g = 28.0,
      fatPer100g = 0.5,
      fiberPer100g = 0.6,
      defaultHiddenFatGrams = 3.5,
      category = "Rice"
    ),
    IndianDish(
      id = "north_matar_paneer",
      name = "Matar Paneer",
      aliases = listOf("matar paneer", "mutter paneer", "peas paneer curry"),
      region = "North",
      mealType = "Dinner",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Green Peas", 60.0),
        IndianDishIngredient("Paneer Cubes", 60.0),
        IndianDishIngredient("Tomato Onion Gravy & Oil", 80.0, 16.0)
      ),
      caloriesPer100g = 165.0,
      proteinPer100g = 7.8,
      carbsPer100g = 11.2,
      fatPer100g = 10.2,
      fiberPer100g = 3.5,
      defaultHiddenFatGrams = 10.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "north_aloo_gobi",
      name = "Homestyle Aloo Gobi Dry",
      aliases = listOf("aloo gobi", "alu gobi", "potato cauliflower sabzi", "gobi aloo"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Cauliflower Florets", 90.0),
        IndianDishIngredient("Potato Wedges", 70.0),
        IndianDishIngredient("Mustard Oil, Ginger & Turmeric", 20.0, 17.5)
      ),
      caloriesPer100g = 115.0,
      proteinPer100g = 2.8,
      carbsPer100g = 16.0,
      fatPer100g = 4.8,
      fiberPer100g = 3.2,
      defaultHiddenFatGrams = 7.5,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "north_bhindi_masala",
      name = "Crispy Bhindi Masala (Okra)",
      aliases = listOf("bhindi", "bhindi masala", "okra fry", "ladyfinger fry"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 160.0,
      ingredients = listOf(
        IndianDishIngredient("Sliced Tender Okra / Bhindi", 125.0),
        IndianDishIngredient("Onions, Amchur & Spices", 20.0),
        IndianDishIngredient("Sauteing Mustard Oil", 15.0, 14.0)
      ),
      caloriesPer100g = 98.0,
      proteinPer100g = 2.4,
      carbsPer100g = 10.5,
      fatPer100g = 5.5,
      fiberPer100g = 3.8,
      defaultHiddenFatGrams = 7.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "north_kadhi_pakora",
      name = "Punjabi Kadhi Pakora",
      aliases = listOf("kadhi pakora", "punjabi kadhi", "besan kadhi", "curd kadhi"),
      region = "North",
      mealType = "Lunch",
      standardServingGrams = 220.0,
      ingredients = listOf(
        IndianDishIngredient("Sour Curd & Gram Flour (Besan) Gravy", 150.0),
        IndianDishIngredient("Fried Onion Besan Pakoras", 50.0, 12.0),
        IndianDishIngredient("Ghee Methi Red Chilli Tadka", 20.0, 18.0)
      ),
      caloriesPer100g = 145.0,
      proteinPer100g = 4.8,
      carbsPer100g = 14.0,
      fatPer100g = 8.0,
      fiberPer100g = 2.4,
      defaultHiddenFatGrams = 13.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "north_moong_chilla",
      name = "Moong Dal Chilla with Paneer Filling",
      aliases = listOf("chilla", "cheela", "moong chilla", "puda", "moong dal dosa"),
      region = "North",
      mealType = "Breakfast",
      standardServingGrams = 120.0,
      ingredients = listOf(
        IndianDishIngredient("Ground Yellow Moong Dal Batter", 80.0),
        IndianDishIngredient("Crumbled Paneer & Coriander", 30.0),
        IndianDishIngredient("Oil for crisping", 10.0, 9.0)
      ),
      caloriesPer100g = 175.0,
      proteinPer100g = 11.5,
      carbsPer100g = 22.0,
      fatPer100g = 4.5,
      fiberPer100g = 4.2,
      defaultHiddenFatGrams = 4.5,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "north_butter_chicken",
      name = "Murgh Makhani (Butter Chicken)",
      aliases = listOf("butter chicken", "murgh makhani", "chicken makhani"),
      region = "North",
      mealType = "Dinner",
      standardServingGrams = 220.0,
      ingredients = listOf(
        IndianDishIngredient("Charred Tandoori Chicken Tikka", 110.0),
        IndianDishIngredient("Makhani Tomato Gravy", 80.0),
        IndianDishIngredient("Butter & Dairy Cream", 30.0, 26.0)
      ),
      caloriesPer100g = 215.0,
      proteinPer100g = 15.2,
      carbsPer100g = 6.8,
      fatPer100g = 14.5,
      fiberPer100g = 1.4,
      defaultHiddenFatGrams = 16.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "north_chicken_tikka",
      name = "Tandoori Chicken Tikka (Dry)",
      aliases = listOf("chicken tikka", "tandoori chicken", "chicken kabab", "murgh tikka"),
      region = "North",
      mealType = "Dinner",
      standardServingGrams = 160.0,
      ingredients = listOf(
        IndianDishIngredient("Chicken Breast Marinated in Hung Curd & Spices", 150.0),
        IndianDishIngredient("Mustard Oil & Butter Brush", 10.0, 9.0)
      ),
      caloriesPer100g = 165.0,
      proteinPer100g = 28.0,
      carbsPer100g = 3.0,
      fatPer100g = 4.5,
      fiberPer100g = 0.8,
      defaultHiddenFatGrams = 4.0,
      category = "Curry / Sabzi"
    ),

    // --- Western Indian (Maharashtra, Gujarat, Rajasthan) ---
    IndianDish(
      id = "west_poha",
      name = "Kanda Batata Poha (Maharashtrian)",
      aliases = listOf("poha", "kanda poha", "flattened rice", "aval upma"),
      region = "West",
      mealType = "Breakfast",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Flattened Rice (Poha)", 110.0),
        IndianDishIngredient("Potatoes, Onions & Fried Peanuts", 55.0),
        IndianDishIngredient("Groundnut Oil & Mustard Seeds", 15.0, 13.5)
      ),
      caloriesPer100g = 148.0,
      proteinPer100g = 3.6,
      carbsPer100g = 26.5,
      fatPer100g = 3.8,
      fiberPer100g = 2.4,
      defaultHiddenFatGrams = 6.0,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "west_pav_bhaji",
      name = "Mumbai Pav Bhaji with Butter Pav",
      aliases = listOf("pav bhaji", "bhaji pav", "bombay pav bhaji"),
      region = "West",
      mealType = "Dinner",
      standardServingGrams = 250.0,
      ingredients = listOf(
        IndianDishIngredient("Mashed Potatoes, Peas, Capsicum & Cauliflower", 150.0),
        IndianDishIngredient("Butter toasted Pav buns (2 pcs)", 70.0),
        IndianDishIngredient("Amul Butter Topping & Tadka", 30.0, 27.0)
      ),
      caloriesPer100g = 185.0,
      proteinPer100g = 4.8,
      carbsPer100g = 26.0,
      fatPer100g = 7.4,
      fiberPer100g = 3.6,
      defaultHiddenFatGrams = 15.0,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "west_misal_pav",
      name = "Kolhapuri Misal Pav",
      aliases = listOf("misal", "misal pav", "puneri misal", "kolhapuri misal"),
      region = "West",
      mealType = "Breakfast",
      standardServingGrams = 240.0,
      ingredients = listOf(
        IndianDishIngredient("Sprouted Matki (Moth Beans) Curry", 140.0),
        IndianDishIngredient("Farsan & Sev Topping", 40.0),
        IndianDishIngredient("Spicy Kat / Tarri (Oil Infusion)", 30.0, 25.0),
        IndianDishIngredient("Pav (2 pcs)", 30.0)
      ),
      caloriesPer100g = 195.0,
      proteinPer100g = 6.5,
      carbsPer100g = 24.0,
      fatPer100g = 8.6,
      fiberPer100g = 4.2,
      defaultHiddenFatGrams = 14.0,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "west_vada_pav",
      name = "Mumbai Vada Pav",
      aliases = listOf("vada pav", "wada pav", "batata vada pav"),
      region = "West",
      mealType = "Snack",
      standardServingGrams = 130.0,
      ingredients = listOf(
        IndianDishIngredient("Fried Potato Batata Vada in Besan Coat", 75.0, 14.0),
        IndianDishIngredient("Ladi Pav", 45.0),
        IndianDishIngredient("Dry Garlic Chutney", 10.0)
      ),
      caloriesPer100g = 260.0,
      proteinPer100g = 5.8,
      carbsPer100g = 38.0,
      fatPer100g = 9.8,
      fiberPer100g = 2.8,
      defaultHiddenFatGrams = 12.0,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "west_dhokla",
      name = "Gujarati Khaman Dhokla",
      aliases = listOf("dhokla", "khaman", "khaman dhokla", "nylon dhokla"),
      region = "West",
      mealType = "Breakfast",
      standardServingGrams = 120.0,
      ingredients = listOf(
        IndianDishIngredient("Steamed Fermented Gram Flour Batter", 100.0),
        IndianDishIngredient("Mustard Green Chilli Water Tempering", 15.0),
        IndianDishIngredient("Tempering Groundnut Oil", 5.0, 4.5)
      ),
      caloriesPer100g = 152.0,
      proteinPer100g = 6.5,
      carbsPer100g = 24.0,
      fatPer100g = 3.6,
      fiberPer100g = 2.9,
      defaultHiddenFatGrams = 4.0,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "west_thepla",
      name = "Gujarati Methi Thepla",
      aliases = listOf("thepla", "methi thepla", "dhebra"),
      region = "West",
      mealType = "Breakfast",
      standardServingGrams = 40.0,
      ingredients = listOf(
        IndianDishIngredient("Whole Wheat & Gram Flour with Fresh Fenugreek", 34.0),
        IndianDishIngredient("Oil cooked into dough & tawa", 6.0, 5.5)
      ),
      caloriesPer100g = 285.0,
      proteinPer100g = 8.5,
      carbsPer100g = 45.0,
      fatPer100g = 8.5,
      fiberPer100g = 6.2,
      defaultHiddenFatGrams = 3.0,
      category = "Breads"
    ),
    IndianDish(
      id = "west_gujarati_kadhi",
      name = "Sweet & Tangy Gujarati Kadhi",
      aliases = listOf("gujarati kadhi", "sweet kadhi", "fajeto"),
      region = "West",
      mealType = "Lunch",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Churned Butter Milk & Besan with Jaggery", 165.0),
        IndianDishIngredient("Ghee Cinnamon Clove Tadka", 15.0, 13.5)
      ),
      caloriesPer100g = 72.0,
      proteinPer100g = 2.4,
      carbsPer100g = 9.8,
      fatPer100g = 2.8,
      fiberPer100g = 0.5,
      defaultHiddenFatGrams = 4.5,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "west_dal_baati_churma",
      name = "Rajasthani Dal Baati",
      aliases = listOf("dal baati", "daal baati", "dal bati churma", "baati"),
      region = "West",
      mealType = "Lunch",
      standardServingGrams = 250.0,
      ingredients = listOf(
        IndianDishIngredient("Baked Wheat Flour Baatis (2 pcs)", 120.0),
        IndianDishIngredient("Panchmel 5-Dal Curry", 80.0),
        IndianDishIngredient("Pure Desi Ghee Soak", 50.0, 48.0)
      ),
      caloriesPer100g = 280.0,
      proteinPer100g = 6.8,
      carbsPer100g = 36.0,
      fatPer100g = 13.0,
      fiberPer100g = 4.5,
      defaultHiddenFatGrams = 28.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "west_gatte_ki_sabzi",
      name = "Rajasthani Gatte ki Sabzi",
      aliases = listOf("gatte ki sabzi", "gatta curry", "besan gatta"),
      region = "West",
      mealType = "Lunch",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Boiled Spiced Gram Flour Dumplings", 90.0),
        IndianDishIngredient("Spiced Yogurt (Dahi) Gravy", 90.0),
        IndianDishIngredient("Mustard Oil / Ghee", 20.0, 18.0)
      ),
      caloriesPer100g = 168.0,
      proteinPer100g = 6.2,
      carbsPer100g = 16.5,
      fatPer100g = 9.0,
      fiberPer100g = 3.1,
      defaultHiddenFatGrams = 12.0,
      category = "Curry / Sabzi"
    ),

    // --- Eastern Indian (Bengal, Odisha, Bihar) ---
    IndianDish(
      id = "east_shorshe_ilish",
      name = "Bengali Shorshe Maach (Mustard Fish)",
      aliases = listOf("shorshe maach", "machher jhol", "mustard fish", "ilish maach"),
      region = "East",
      mealType = "Lunch",
      standardServingGrams = 200.0,
      ingredients = listOf(
        IndianDishIngredient("Fresh Rohu / Hilsa Fish Fillet", 120.0),
        IndianDishIngredient("Yellow & Black Mustard Paste", 50.0),
        IndianDishIngredient("Pungent Kachi Ghani Mustard Oil", 30.0, 28.0)
      ),
      caloriesPer100g = 192.0,
      proteinPer100g = 16.5,
      carbsPer100g = 3.2,
      fatPer100g = 12.8,
      fiberPer100g = 1.2,
      defaultHiddenFatGrams = 16.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "east_chholar_dal",
      name = "Bengali Chholar Dal with Coconut",
      aliases = listOf("chholar dal", "chana dal coconut", "bengali chana dal"),
      region = "East",
      mealType = "Lunch",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Chana Dal with Ginger & Hing", 130.0),
        IndianDishIngredient("Fried Coconut Slivers (Narkol)", 25.0),
        IndianDishIngredient("Desi Ghee Panch Phoron Tadka", 25.0, 23.0)
      ),
      caloriesPer100g = 158.0,
      proteinPer100g = 6.4,
      carbsPer100g = 21.0,
      fatPer100g = 5.8,
      fiberPer100g = 4.2,
      defaultHiddenFatGrams = 9.0,
      category = "Lentils / Dal"
    ),
    IndianDish(
      id = "east_luchi",
      name = "Bengali Golden Luchi",
      aliases = listOf("luchi", "maida poori", "fulko luchi"),
      region = "East",
      mealType = "Breakfast",
      standardServingGrams = 40.0,
      ingredients = listOf(
        IndianDishIngredient("Kneaded Maida Dough", 28.0),
        IndianDishIngredient("Deep Fried White Oil", 12.0, 11.0)
      ),
      caloriesPer100g = 345.0,
      proteinPer100g = 6.0,
      carbsPer100g = 47.0,
      fatPer100g = 15.2,
      fiberPer100g = 1.5,
      defaultHiddenFatGrams = 5.0,
      category = "Breads"
    ),
    IndianDish(
      id = "east_aloo_postor",
      name = "Bengali Aloo Posto (Poppy Seeds)",
      aliases = listOf("aloo posto", "alu posto", "posto"),
      region = "East",
      mealType = "Lunch",
      standardServingGrams = 180.0,
      ingredients = listOf(
        IndianDishIngredient("Diced Potatoes", 110.0),
        IndianDishIngredient("Creamy White Poppy Seed Paste", 45.0),
        IndianDishIngredient("Raw Mustard Oil Drizzle", 25.0, 24.0)
      ),
      caloriesPer100g = 175.0,
      proteinPer100g = 4.2,
      carbsPer100g = 18.0,
      fatPer100g = 9.8,
      fiberPer100g = 3.5,
      defaultHiddenFatGrams = 14.0,
      category = "Curry / Sabzi"
    ),
    IndianDish(
      id = "east_litti_chokha",
      name = "Bihari Litti Chokha",
      aliases = listOf("litti chokha", "litti", "sattu litti", "baati chokha"),
      region = "East",
      mealType = "Lunch",
      standardServingGrams = 240.0,
      ingredients = listOf(
        IndianDishIngredient("Atta Dough stuffed with Spiced Sattu", 130.0),
        IndianDishIngredient("Smoked Baingan Tamatar Chokha", 80.0),
        IndianDishIngredient("Desi Ghee Dip", 30.0, 28.5)
      ),
      caloriesPer100g = 220.0,
      proteinPer100g = 8.5,
      carbsPer100g = 32.0,
      fatPer100g = 7.2,
      fiberPer100g = 5.8,
      defaultHiddenFatGrams = 15.0,
      category = "Curry / Sabzi"
    ),

    // --- Pan-Indian Biryanis, Rice & Thali Items ---
    IndianDish(
      id = "pan_hyderabadi_biryani",
      name = "Hyderabadi Dum Biryani (Chicken/Mutton)",
      aliases = listOf("biryani", "dum biryani", "hyderabadi biryani", "chicken biryani", "mutton biryani"),
      region = "South",
      mealType = "Dinner",
      standardServingGrams = 320.0,
      ingredients = listOf(
        IndianDishIngredient("Aged Basmati Rice Layer", 170.0),
        IndianDishIngredient("Marinated Chicken / Meat with Fried Onions", 110.0),
        IndianDishIngredient("Desi Ghee, Saffron Milk & Spices", 40.0, 36.0)
      ),
      caloriesPer100g = 195.0,
      proteinPer100g = 9.8,
      carbsPer100g = 24.0,
      fatPer100g = 7.5,
      fiberPer100g = 1.8,
      defaultHiddenFatGrams = 18.0,
      category = "Rice"
    ),
    IndianDish(
      id = "pan_veg_biryani",
      name = "Royal Vegetable Dum Biryani",
      aliases = listOf("veg biryani", "vegetable biryani", "veg pulao", "tarkari biryani"),
      region = "Pan-Indian",
      mealType = "Lunch",
      standardServingGrams = 280.0,
      ingredients = listOf(
        IndianDishIngredient("Basmati Rice", 160.0),
        IndianDishIngredient("Carrots, Beans, Paneer & Mint", 90.0),
        IndianDishIngredient("Ghee & Fried Birista", 30.0, 26.0)
      ),
      caloriesPer100g = 162.0,
      proteinPer100g = 4.5,
      carbsPer100g = 26.5,
      fatPer100g = 4.8,
      fiberPer100g = 2.9,
      defaultHiddenFatGrams = 11.0,
      category = "Rice"
    ),
    IndianDish(
      id = "pan_khichdi",
      name = "Moong Dal Ayurvedic Khichdi with Ghee",
      aliases = listOf("khichdi", "dal khichdi", "moong khichdi", "ayurvedic khichdi"),
      region = "Pan-Indian",
      mealType = "Dinner",
      standardServingGrams = 220.0,
      ingredients = listOf(
        IndianDishIngredient("Rice & Split Moong Dal", 170.0),
        IndianDishIngredient("Turmeric, Ginger & Hing", 20.0),
        IndianDishIngredient("Warm Pure Ghee Drizzle", 30.0, 28.5)
      ),
      caloriesPer100g = 135.0,
      proteinPer100g = 4.8,
      carbsPer100g = 21.0,
      fatPer100g = 4.0,
      fiberPer100g = 2.4,
      defaultHiddenFatGrams = 8.5,
      category = "Rice"
    ),
    IndianDish(
      id = "pan_samosa",
      name = "Crispy Punjabi Samosa",
      aliases = listOf("samosa", "singara", "punjabi samosa", "aloo samosa"),
      region = "Pan-Indian",
      mealType = "Snack",
      standardServingGrams = 90.0,
      ingredients = listOf(
        IndianDishIngredient("Crispy Maida Ajwain Crust", 40.0),
        IndianDishIngredient("Spiced Potato & Green Peas Filling", 35.0),
        IndianDishIngredient("Oil Absorbed During Deep Frying", 15.0, 14.0)
      ),
      caloriesPer100g = 295.0,
      proteinPer100g = 4.8,
      carbsPer100g = 36.0,
      fatPer100g = 15.0,
      fiberPer100g = 2.4,
      defaultHiddenFatGrams = 12.0,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "pan_pani_puri",
      name = "Pani Puri / Golgappa (6 pieces)",
      aliases = listOf("pani puri", "golgappa", "puchka", "gupchup", "paani poori"),
      region = "Pan-Indian",
      mealType = "Snack",
      standardServingGrams = 160.0,
      ingredients = listOf(
        IndianDishIngredient("Semolina Puris (6 pcs)", 50.0),
        IndianDishIngredient("Potato & Kala Chana Filling", 50.0),
        IndianDishIngredient("Mint Tamarind Hing Water", 60.0)
      ),
      caloriesPer100g = 128.0,
      proteinPer100g = 3.2,
      carbsPer100g = 24.5,
      fatPer100g = 2.5,
      fiberPer100g = 2.1,
      defaultHiddenFatGrams = 3.5,
      category = "Breakfast / Snack"
    ),
    IndianDish(
      id = "pan_masala_chai",
      name = "Adrak Elaichi Masala Chai",
      aliases = listOf("chai", "masala chai", "tea", "adrak chai", "cutting chai"),
      region = "Pan-Indian",
      mealType = "Breakfast",
      standardServingGrams = 150.0,
      ingredients = listOf(
        IndianDishIngredient("Brewed Assam Tea & Water", 60.0),
        IndianDishIngredient("Whole Buffalo / Cow Milk", 80.0),
        IndianDishIngredient("Sugar & Ginger Cardamom", 10.0)
      ),
      caloriesPer100g = 68.0,
      proteinPer100g = 2.2,
      carbsPer100g = 9.0,
      fatPer100g = 2.6,
      fiberPer100g = 0.0,
      defaultHiddenFatGrams = 3.0,
      category = "Breakfast / Snack"
    )
  )

  /**
   * Search dish by name, alias or fuzzy term
   */
  fun findBestMatch(query: String): IndianDish? {
    val clean = query.trim().lowercase()
    if (clean.isBlank()) return dishes.first()

    // 1. Exact alias or name match
    dishes.find { dish ->
      dish.name.lowercase() == clean || dish.aliases.any { it.equals(clean, ignoreCase = true) }
    }?.let { return it }

    // 2. Substring containment
    dishes.find { dish ->
      dish.aliases.any { clean.contains(it.lowercase()) || it.lowercase().contains(clean) } ||
        clean.contains(dish.name.lowercase()) || dish.name.lowercase().contains(clean)
    }?.let { return it }

    // 3. Keyword matching (paneer, dal, idli, rice, roti, etc.)
    val keywords = clean.split(" ", "-", "_")
    for (kw in keywords) {
      if (kw.length >= 3) {
        val found = dishes.find { dish ->
          dish.aliases.any { it.contains(kw, ignoreCase = true) } ||
            dish.name.contains(kw, ignoreCase = true)
        }
        if (found != null) return found
      }
    }

    return dishes.first()
  }

  /**
   * Determine scanner confidence level based on query specificity and library match.
   */
  fun calculateConfidence(query: String, matchedDish: IndianDish): String {
    val clean = query.trim().lowercase()
    return when {
      matchedDish.aliases.any { it.equals(clean, ignoreCase = true) } -> "High"
      matchedDish.aliases.any { clean.contains(it.lowercase()) } -> "High"
      clean.length > 5 && matchedDish.name.contains(clean, ignoreCase = true) -> "Medium"
      clean.contains("sample") || clean.contains("test") -> "High"
      else -> "Low"
    }
  }
}
