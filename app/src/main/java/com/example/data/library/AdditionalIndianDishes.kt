package com.example.data.library

/**
 * Additional authentic regional Indian dishes extending the IFCT 2017 library to 100+ items.
 */
val additionalIndianDishes: List<IndianDish> = listOf(
  // South Indian variants
  IndianDish(
    id = "south_rava_dosa",
    name = "Crispy Onion Rava Dosa",
    aliases = listOf("rava dosa", "onion rava dosa", "rava roast"),
    region = "South",
    mealType = "Breakfast",
    standardServingGrams = 160.0,
    ingredients = listOf(
      IndianDishIngredient("Semolina, Rice Flour & Ginger", 120.0),
      IndianDishIngredient("Onions & Green Chillies", 25.0),
      IndianDishIngredient("Ghee / Oil Roast", 15.0, 13.5)
    ),
    caloriesPer100g = 210.0,
    proteinPer100g = 4.6,
    carbsPer100g = 32.0,
    fatPer100g = 7.5,
    fiberPer100g = 2.1,
    defaultHiddenFatGrams = 11.0,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "south_pesarattu",
    name = "Andhra Moong Dal Pesarattu",
    aliases = listOf("pesarattu", "moong dosa", "mla pesarattu", "upma pesarattu"),
    region = "South",
    mealType = "Breakfast",
    standardServingGrams = 170.0,
    ingredients = listOf(
      IndianDishIngredient("Whole Green Moong Batter", 135.0),
      IndianDishIngredient("Finely Chopped Ginger Onion Topping", 25.0),
      IndianDishIngredient("Gingelly Oil on Tawa", 10.0, 9.5)
    ),
    caloriesPer100g = 168.0,
    proteinPer100g = 9.2,
    carbsPer100g = 24.5,
    fatPer100g = 4.2,
    fiberPer100g = 4.6,
    defaultHiddenFatGrams = 6.0,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "south_bisi_bele_bath",
    name = "Karnataka Bisi Bele Bath",
    aliases = listOf("bisi bele bath", "bisibelebath", "hot dal rice"),
    region = "South",
    mealType = "Lunch",
    standardServingGrams = 250.0,
    ingredients = listOf(
      IndianDishIngredient("Rice & Toor Dal", 150.0),
      IndianDishIngredient("Vegetables & Tamarind", 65.0),
      IndianDishIngredient("Copra Spiced Ghee Tadka", 35.0, 32.0)
    ),
    caloriesPer100g = 158.0,
    proteinPer100g = 5.2,
    carbsPer100g = 24.0,
    fatPer100g = 5.2,
    fiberPer100g = 3.8,
    defaultHiddenFatGrams = 12.0,
    category = "Rice"
  ),
  IndianDish(
    id = "south_avial",
    name = "Kerala Traditional Aviyal",
    aliases = listOf("avial", "aviyal", "kerala aviyal", "coconut vegetable stew"),
    region = "South",
    mealType = "Lunch",
    standardServingGrams = 200.0,
    ingredients = listOf(
      IndianDishIngredient("Mixed Country Vegetables (Plantain, Yam, Drumstick)", 120.0),
      IndianDishIngredient("Fresh Grated Coconut Cumin Paste", 60.0),
      IndianDishIngredient("Virgin Coconut Oil Drizzle", 20.0, 19.0)
    ),
    caloriesPer100g = 115.0,
    proteinPer100g = 2.4,
    carbsPer100g = 11.2,
    fatPer100g = 7.4,
    fiberPer100g = 4.5,
    defaultHiddenFatGrams = 12.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "south_thoran",
    name = "Beans Carrot Thoran (Stir Fry)",
    aliases = listOf("thoran", "poriyal", "beans thoran", "cabbage thoran"),
    region = "South",
    mealType = "Lunch",
    standardServingGrams = 150.0,
    ingredients = listOf(
      IndianDishIngredient("Green Beans & Carrots", 110.0),
      IndianDishIngredient("Fresh Coconut Shreds", 30.0),
      IndianDishIngredient("Coconut Oil Mustard Tadka", 10.0, 9.5)
    ),
    caloriesPer100g = 88.0,
    proteinPer100g = 2.8,
    carbsPer100g = 9.5,
    fatPer100g = 4.6,
    fiberPer100g = 3.9,
    defaultHiddenFatGrams = 6.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "south_chettinad_chicken",
    name = "Chettinad Pepper Chicken",
    aliases = listOf("chettinad chicken", "pepper chicken", "kozhi curry"),
    region = "South",
    mealType = "Dinner",
    standardServingGrams = 220.0,
    ingredients = listOf(
      IndianDishIngredient("Chicken on Bone", 130.0),
      IndianDishIngredient("Roasted Kalpasi, Pepper & Shallot Masala", 65.0),
      IndianDishIngredient("Gingelly Oil Cooking Base", 25.0, 23.5)
    ),
    caloriesPer100g = 185.0,
    proteinPer100g = 18.2,
    carbsPer100g = 4.2,
    fatPer100g = 11.0,
    fiberPer100g = 1.8,
    defaultHiddenFatGrams = 14.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "south_kerala_fish_curry",
    name = "Kerala Meen Curry (Cocum Fish)",
    aliases = listOf("kerala fish curry", "meen curry", "kudampuli meen curry"),
    region = "South",
    mealType = "Lunch",
    standardServingGrams = 200.0,
    ingredients = listOf(
      IndianDishIngredient("King Fish / Sardine Steaks", 110.0),
      IndianDishIngredient("Kudampuli Chili Coconut Milk Broth", 70.0),
      IndianDishIngredient("Coconut Oil Tempering", 20.0, 18.5)
    ),
    caloriesPer100g = 148.0,
    proteinPer100g = 15.6,
    carbsPer100g = 3.8,
    fatPer100g = 8.5,
    fiberPer100g = 1.2,
    defaultHiddenFatGrams = 10.5,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "south_hyderabadi_haleem",
    name = "Hyderabadi Mutton Haleem",
    aliases = listOf("haleem", "hyderabadi haleem", "mutton haleem"),
    region = "South",
    mealType = "Dinner",
    standardServingGrams = 250.0,
    ingredients = listOf(
      IndianDishIngredient("Slow-Pounded Mutton & Wheat (Daliya)", 140.0),
      IndianDishIngredient("Lentils & Fried Birista", 60.0),
      IndianDishIngredient("Pure Desi Ghee Infusion", 50.0, 48.0)
    ),
    caloriesPer100g = 225.0,
    proteinPer100g = 14.5,
    carbsPer100g = 18.0,
    fatPer100g = 11.5,
    fiberPer100g = 3.5,
    defaultHiddenFatGrams = 26.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "south_mirchi_bajji",
    name = "Andhra Mirchi Bajji (Stuffed Chili Fritters)",
    aliases = listOf("mirchi bajji", "mirchi bhajiya", "menasinakai bajji"),
    region = "South",
    mealType = "Snack",
    standardServingGrams = 120.0,
    ingredients = listOf(
      IndianDishIngredient("Large Bhavnagri Green Chillies", 50.0),
      IndianDishIngredient("Besan Tamarind Batter", 45.0),
      IndianDishIngredient("Deep Fried Oil", 25.0, 23.0)
    ),
    caloriesPer100g = 245.0,
    proteinPer100g = 5.2,
    carbsPer100g = 25.0,
    fatPer100g = 14.0,
    fiberPer100g = 3.2,
    defaultHiddenFatGrams = 15.0,
    category = "Breakfast / Snack"
  ),

  // Western & Maharashtrian
  IndianDish(
    id = "west_sabudana_khichdi",
    name = "Sabudana Khichdi (Tapioca Pearls)",
    aliases = listOf("sabudana khichdi", "sago khichdi", "vrat khichdi", "upvas khichdi"),
    region = "West",
    mealType = "Breakfast",
    standardServingGrams = 180.0,
    ingredients = listOf(
      IndianDishIngredient("Soaked Tapioca Sago", 110.0),
      IndianDishIngredient("Roasted Crushed Peanuts & Boiled Potatoes", 50.0),
      IndianDishIngredient("Pure Desi Ghee Cumin Tempering", 20.0, 19.0)
    ),
    caloriesPer100g = 215.0,
    proteinPer100g = 3.8,
    carbsPer100g = 34.5,
    fatPer100g = 7.4,
    fiberPer100g = 1.4,
    defaultHiddenFatGrams = 12.0,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "west_sabudana_vada",
    name = "Crispy Sabudana Vada",
    aliases = listOf("sabudana vada", "sago vada", "vrat vada"),
    region = "West",
    mealType = "Snack",
    standardServingGrams = 120.0,
    ingredients = listOf(
      IndianDishIngredient("Sago & Potato Mash with Peanuts", 85.0),
      IndianDishIngredient("Frying Oil Absorbed", 35.0, 32.0)
    ),
    caloriesPer100g = 310.0,
    proteinPer100g = 4.2,
    carbsPer100g = 38.0,
    fatPer100g = 16.0,
    fiberPer100g = 1.8,
    defaultHiddenFatGrams = 18.0,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "west_kothimbir_vadi",
    name = "Maharashtrian Kothimbir Vadi",
    aliases = listOf("kothimbir vadi", "coriander fritter", "kothmir vadi"),
    region = "West",
    mealType = "Snack",
    standardServingGrams = 120.0,
    ingredients = listOf(
      IndianDishIngredient("Fresh Coriander Leaves & Besan", 90.0),
      IndianDishIngredient("Shallow Frying Oil", 30.0, 27.0)
    ),
    caloriesPer100g = 220.0,
    proteinPer100g = 7.2,
    carbsPer100g = 22.0,
    fatPer100g = 11.5,
    fiberPer100g = 4.0,
    defaultHiddenFatGrams = 12.5,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "west_puran_poli",
    name = "Puran Poli with Melted Ghee",
    aliases = listOf("puran poli", "holige", "obbattu", "vedmi"),
    region = "West",
    mealType = "Lunch",
    standardServingGrams = 140.0,
    ingredients = listOf(
      IndianDishIngredient("Wheat Flour Wrap", 45.0),
      IndianDishIngredient("Sweet Chana Dal Jaggery Puran", 70.0),
      IndianDishIngredient("Generous Ghee Topping", 25.0, 24.0)
    ),
    caloriesPer100g = 295.0,
    proteinPer100g = 6.8,
    carbsPer100g = 48.0,
    fatPer100g = 9.5,
    fiberPer100g = 3.6,
    defaultHiddenFatGrams = 12.0,
    category = "Breads"
  ),
  IndianDish(
    id = "west_undhiyu",
    name = "Surti Undhiyu (Winter Vegetable Medley)",
    aliases = listOf("undhiyu", "surti undhiyu", "gujarati undhiyu"),
    region = "West",
    mealType = "Lunch",
    standardServingGrams = 220.0,
    ingredients = listOf(
      IndianDishIngredient("Surti Papdi, Sweet Potatoes & Small Brinjals", 120.0),
      IndianDishIngredient("Methi Muthia Dumplings", 60.0),
      IndianDishIngredient("Spiced Groundnut Oil Base", 40.0, 36.0)
    ),
    caloriesPer100g = 178.0,
    proteinPer100g = 4.8,
    carbsPer100g = 18.5,
    fatPer100g = 10.2,
    fiberPer100g = 4.8,
    defaultHiddenFatGrams = 18.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "west_dal_dhokli",
    name = "Gujarati Dal Dhokli",
    aliases = listOf("dal dhokli", "varan phal", "daal dhokli"),
    region = "West",
    mealType = "Lunch",
    standardServingGrams = 220.0,
    ingredients = listOf(
      IndianDishIngredient("Toor Dal Sweet-Tangy Broth", 140.0),
      IndianDishIngredient("Whole Wheat Spiced Ribbons", 60.0),
      IndianDishIngredient("Ghee Mustard Hing Tadka", 20.0, 18.0)
    ),
    caloriesPer100g = 142.0,
    proteinPer100g = 5.2,
    carbsPer100g = 22.0,
    fatPer100g = 4.2,
    fiberPer100g = 3.2,
    defaultHiddenFatGrams = 7.0,
    category = "Lentils / Dal"
  ),

  // North Indian Staples
  IndianDish(
    id = "north_kadai_paneer",
    name = "Kadai Paneer with Bell Peppers",
    aliases = listOf("kadai paneer", "kadhai paneer", "karahi paneer"),
    region = "North",
    mealType = "Dinner",
    standardServingGrams = 200.0,
    ingredients = listOf(
      IndianDishIngredient("Paneer Cubes", 70.0),
      IndianDishIngredient("Capsicum & Onion Petals", 60.0),
      IndianDishIngredient("Coriander Seed Tomato Masala & Oil", 70.0, 18.0)
    ),
    caloriesPer100g = 175.0,
    proteinPer100g = 8.2,
    carbsPer100g = 7.5,
    fatPer100g = 13.0,
    fiberPer100g = 2.4,
    defaultHiddenFatGrams = 12.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "north_sarson_saag",
    name = "Sarson ka Saag & Makki Roti",
    aliases = listOf("sarson ka saag", "sarson saag", "makki ki roti", "punjabi saag"),
    region = "North",
    mealType = "Dinner",
    standardServingGrams = 240.0,
    ingredients = listOf(
      IndianDishIngredient("Slow Cooked Mustard & Bathua Greens", 150.0),
      IndianDishIngredient("Makki Atta Kneaded with warm water", 50.0),
      IndianDishIngredient("Desi White Butter (Makhan)", 40.0, 34.0)
    ),
    caloriesPer100g = 165.0,
    proteinPer100g = 4.2,
    carbsPer100g = 16.0,
    fatPer100g = 9.5,
    fiberPer100g = 4.8,
    defaultHiddenFatGrams = 19.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "north_baingan_bharta",
    name = "Dhaba Smoked Baingan Bharta",
    aliases = listOf("baingan bharta", "eggplant mash", "roasted aubergine"),
    region = "North",
    mealType = "Dinner",
    standardServingGrams = 200.0,
    ingredients = listOf(
      IndianDishIngredient("Charcoal Roasted Mashed Eggplant", 140.0),
      IndianDishIngredient("Sauteed Onion, Tomatoes & Green Peas", 40.0),
      IndianDishIngredient("Mustard Oil Infusion", 20.0, 18.0)
    ),
    caloriesPer100g = 92.0,
    proteinPer100g = 2.2,
    carbsPer100g = 9.4,
    fatPer100g = 5.2,
    fiberPer100g = 3.6,
    defaultHiddenFatGrams = 8.5,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "north_malai_kofta",
    name = "Shahi Malai Kofta in Cashew Gravy",
    aliases = listOf("malai kofta", "paneer kofta", "shahi kofta"),
    region = "North",
    mealType = "Dinner",
    standardServingGrams = 220.0,
    ingredients = listOf(
      IndianDishIngredient("Paneer Potato Khoya Kofta Dumplings", 80.0, 18.0),
      IndianDishIngredient("Rich White Cashew Melon Seed Gravy", 110.0),
      IndianDishIngredient("Butter Cream", 30.0, 27.0)
    ),
    caloriesPer100g = 230.0,
    proteinPer100g = 6.5,
    carbsPer100g = 14.5,
    fatPer100g = 17.0,
    fiberPer100g = 2.0,
    defaultHiddenFatGrams = 24.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "north_amritsari_kulcha",
    name = "Amritsari Stuffed Aloo Kulcha",
    aliases = listOf("amritsari kulcha", "kulcha", "stuffed kulcha", "onion kulcha"),
    region = "North",
    mealType = "Lunch",
    standardServingGrams = 160.0,
    ingredients = listOf(
      IndianDishIngredient("Layered Tandoor Maida Bread", 80.0),
      IndianDishIngredient("Spiced Potato Onion Filling", 50.0),
      IndianDishIngredient("Generous White Butter (Makhan)", 30.0, 26.0)
    ),
    caloriesPer100g = 285.0,
    proteinPer100g = 6.8,
    carbsPer100g = 42.0,
    fatPer100g = 10.5,
    fiberPer100g = 2.8,
    defaultHiddenFatGrams = 14.5,
    category = "Breads"
  ),
  IndianDish(
    id = "north_dum_aloo",
    name = "Kashmiri Dum Aloo",
    aliases = listOf("dum aloo", "kashmiri dum aloo", "spicy baby potatoes"),
    region = "North",
    mealType = "Dinner",
    standardServingGrams = 200.0,
    ingredients = listOf(
      IndianDishIngredient("Pricked Fried Baby Potatoes", 110.0, 12.0),
      IndianDishIngredient("Fennel & Dry Ginger Yogurt Gravy", 70.0),
      IndianDishIngredient("Mustard Oil", 20.0, 18.0)
    ),
    caloriesPer100g = 145.0,
    proteinPer100g = 2.8,
    carbsPer100g = 18.0,
    fatPer100g = 7.0,
    fiberPer100g = 2.6,
    defaultHiddenFatGrams = 12.0,
    category = "Curry / Sabzi"
  ),

  // Chaat & Street Food Favorites
  IndianDish(
    id = "chaat_aloo_tikki",
    name = "Delhi Aloo Tikki Chaat",
    aliases = listOf("aloo tikki", "tikki chaat", "aloo tikki chaat"),
    region = "North",
    mealType = "Snack",
    standardServingGrams = 180.0,
    ingredients = listOf(
      IndianDishIngredient("Shallow Fried Potato Patties (2 pcs)", 90.0, 15.0),
      IndianDishIngredient("Warm Ragda Chana", 50.0),
      IndianDishIngredient("Curd & Sweet Tamarind Chutney", 40.0)
    ),
    caloriesPer100g = 182.0,
    proteinPer100g = 4.2,
    carbsPer100g = 26.0,
    fatPer100g = 7.0,
    fiberPer100g = 3.0,
    defaultHiddenFatGrams = 11.0,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "chaat_sev_puri",
    name = "Mumbai Sev Puri (6 pieces)",
    aliases = listOf("sev puri", "sev batata puri", "bambai sev puri"),
    region = "West",
    mealType = "Snack",
    standardServingGrams = 140.0,
    ingredients = listOf(
      IndianDishIngredient("Flat Puris (6 pcs)", 35.0),
      IndianDishIngredient("Potato Onion Raw Mango Mix", 50.0),
      IndianDishIngredient("Nylon Sev Topping", 35.0, 12.0),
      IndianDishIngredient("Trio of Chutneys", 20.0)
    ),
    caloriesPer100g = 240.0,
    proteinPer100g = 4.8,
    carbsPer100g = 34.0,
    fatPer100g = 10.0,
    fiberPer100g = 2.8,
    defaultHiddenFatGrams = 10.0,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "chaat_dahi_vada",
    name = "North Indian Dahi Bhalla / Dahi Vada",
    aliases = listOf("dahi bhalla", "dahi vada", "dahi bare", "thayir vadai"),
    region = "North",
    mealType = "Snack",
    standardServingGrams = 200.0,
    ingredients = listOf(
      IndianDishIngredient("Fried Urad Dal Bhallas soaked in water", 80.0, 8.0),
      IndianDishIngredient("Thick Sweetened Chilled Curd", 100.0),
      IndianDishIngredient("Saunth Tamarind & Roasted Jeera", 20.0)
    ),
    caloriesPer100g = 155.0,
    proteinPer100g = 5.8,
    carbsPer100g = 19.5,
    fatPer100g = 6.4,
    fiberPer100g = 2.2,
    defaultHiddenFatGrams = 8.5,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "chaat_bhel_puri",
    name = "Crispy Mumbai Bhel Puri",
    aliases = listOf("bhel puri", "bhel", "jhal muri", "sukha bhel"),
    region = "West",
    mealType = "Snack",
    standardServingGrams = 130.0,
    ingredients = listOf(
      IndianDishIngredient("Puffed Rice (Kurmura)", 50.0),
      IndianDishIngredient("Sev & Crushed Papdi", 40.0, 10.0),
      IndianDishIngredient("Boiled Potatoes, Onions & Chutney", 40.0)
    ),
    caloriesPer100g = 195.0,
    proteinPer100g = 4.5,
    carbsPer100g = 32.0,
    fatPer100g = 6.2,
    fiberPer100g = 2.4,
    defaultHiddenFatGrams = 6.0,
    category = "Breakfast / Snack"
  ),

  // East Indian Specialties
  IndianDish(
    id = "east_chingri_malai",
    name = "Bengali Chingri Malai Curry (Prawns in Coconut Milk)",
    aliases = listOf("chingri malai curry", "prawn malai curry", "bengali prawn curry"),
    region = "East",
    mealType = "Dinner",
    standardServingGrams = 220.0,
    ingredients = listOf(
      IndianDishIngredient("Golda Chingri (Prawns)", 110.0),
      IndianDishIngredient("Thick Coconut Milk & Spices", 85.0),
      IndianDishIngredient("Pure Mustard Oil & Ghee", 25.0, 23.0)
    ),
    caloriesPer100g = 180.0,
    proteinPer100g = 14.8,
    carbsPer100g = 4.2,
    fatPer100g = 11.5,
    fiberPer100g = 1.0,
    defaultHiddenFatGrams = 15.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "east_kosha_mangsho",
    name = "Bengali Kosha Mangsho (Slow Cooked Mutton)",
    aliases = listOf("kosha mangsho", "mutton kosha", "bengali mutton curry"),
    region = "East",
    mealType = "Dinner",
    standardServingGrams = 240.0,
    ingredients = listOf(
      IndianDishIngredient("Tender Goat Meat on Bone", 140.0),
      IndianDishIngredient("Caramelized Onion Ginger Garlic Masala", 65.0),
      IndianDishIngredient("Kachi Ghani Mustard Oil", 35.0, 32.0)
    ),
    caloriesPer100g = 220.0,
    proteinPer100g = 17.5,
    carbsPer100g = 5.2,
    fatPer100g = 14.5,
    fiberPer100g = 1.6,
    defaultHiddenFatGrams = 22.0,
    category = "Curry / Sabzi"
  ),
  IndianDish(
    id = "east_mishti_doi",
    name = "Baked Bengali Mishti Doi",
    aliases = listOf("mishti doi", "sweet curd", "bengali dahi", "caramel yogurt"),
    region = "East",
    mealType = "Snack",
    standardServingGrams = 150.0,
    ingredients = listOf(
      IndianDishIngredient("Reduced Whole Milk & Curd Culture", 120.0),
      IndianDishIngredient("Caramelized Sugar & Nolen Gur", 30.0)
    ),
    caloriesPer100g = 138.0,
    proteinPer100g = 4.0,
    carbsPer100g = 21.0,
    fatPer100g = 4.2,
    fiberPer100g = 0.0,
    defaultHiddenFatGrams = 4.0,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "east_rosogolla",
    name = "Spongy Kolkata Rosogolla (2 pieces)",
    aliases = listOf("rasgulla", "rosogolla", "rasgulla 2 pcs", "chhena rasgulla"),
    region = "East",
    mealType = "Snack",
    standardServingGrams = 100.0,
    ingredients = listOf(
      IndianDishIngredient("Cow Milk Fresh Chhena Balls", 50.0),
      IndianDishIngredient("Light Cardamom Sugar Syrup", 50.0)
    ),
    caloriesPer100g = 186.0,
    proteinPer100g = 4.2,
    carbsPer100g = 37.0,
    fatPer100g = 2.4,
    fiberPer100g = 0.0,
    defaultHiddenFatGrams = 1.5,
    category = "Breakfast / Snack"
  ),

  // Desserts / Traditional Sweets
  IndianDish(
    id = "sweet_gulab_jamun",
    name = "Hot Gulab Jamun (2 pieces)",
    aliases = listOf("gulab jamun", "jamun", "kala jamun"),
    region = "Pan-Indian",
    mealType = "Snack",
    standardServingGrams = 100.0,
    ingredients = listOf(
      IndianDishIngredient("Mawa / Khoya & Maida Dumplings", 50.0, 11.0),
      IndianDishIngredient("Rose Saffron Sugar Syrup", 50.0)
    ),
    caloriesPer100g = 325.0,
    proteinPer100g = 5.2,
    carbsPer100g = 52.0,
    fatPer100g = 11.0,
    fiberPer100g = 0.5,
    defaultHiddenFatGrams = 9.5,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "sweet_gajar_halwa",
    name = "Desi Ghee Gajar ka Halwa",
    aliases = listOf("gajar halwa", "gajar ka halwa", "carrot halwa"),
    region = "North",
    mealType = "Dinner",
    standardServingGrams = 120.0,
    ingredients = listOf(
      IndianDishIngredient("Grated Red Carrots & Full Cream Milk", 80.0),
      IndianDishIngredient("Mawa, Sugar & Nuts", 25.0),
      IndianDishIngredient("Pure Desi Ghee", 15.0, 14.5)
    ),
    caloriesPer100g = 245.0,
    proteinPer100g = 4.8,
    carbsPer100g = 32.0,
    fatPer100g = 11.5,
    fiberPer100g = 2.4,
    defaultHiddenFatGrams = 12.0,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "sweet_moong_dal_halwa",
    name = "Shahi Moong Dal Halwa",
    aliases = listOf("moong dal halwa", "moong halwa", "sheera"),
    region = "North",
    mealType = "Dinner",
    standardServingGrams = 120.0,
    ingredients = listOf(
      IndianDishIngredient("Pounded Yellow Moong Dal", 45.0),
      IndianDishIngredient("Sugar, Saffron & Almonds", 45.0),
      IndianDishIngredient("Equal Ratio Pure Desi Ghee", 30.0, 29.0)
    ),
    caloriesPer100g = 385.0,
    proteinPer100g = 6.5,
    carbsPer100g = 44.0,
    fatPer100g = 20.5,
    fiberPer100g = 2.8,
    defaultHiddenFatGrams = 24.0,
    category = "Breakfast / Snack"
  ),
  IndianDish(
    id = "sweet_kheer",
    name = "Rice Kheer / Payasam with Nuts",
    aliases = listOf("kheer", "rice kheer", "payasam", "paal payasam"),
    region = "Pan-Indian",
    mealType = "Lunch",
    standardServingGrams = 180.0,
    ingredients = listOf(
      IndianDishIngredient("Full Cream Milk Reduced", 130.0),
      IndianDishIngredient("Fragrant Gobindobhog / Basmati Rice", 25.0),
      IndianDishIngredient("Sugar, Ghee-roasted Cashews & Raisins", 25.0, 4.5)
    ),
    caloriesPer100g = 145.0,
    proteinPer100g = 4.2,
    carbsPer100g = 22.0,
    fatPer100g = 4.5,
    fiberPer100g = 0.4,
    defaultHiddenFatGrams = 4.0,
    category = "Breakfast / Snack"
  )
)
