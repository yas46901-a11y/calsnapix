package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalCard
import com.example.ui.theme.CalTheme

/**
 * Data structures for Regional Diet Tips
 */
data class RegionalTipItem(
  val category: String,
  val icon: ImageVector,
  val title: String,
  val advice: String,
  val practicalSwap: String
)

data class RegionalDishNutrition(
  val dishName: String,
  val typicalKcal: Int,
  val proteinGrams: Int,
  val verdict: String,
  val optimizationTip: String
)

data class RegionalCuisineDietProfile(
  val id: String,
  val name: String,
  val stateNames: String,
  val emoji: String,
  val stapleSummary: String,
  val calorieRiskFactor: String,
  val tips: List<RegionalTipItem>,
  val topDishes: List<RegionalDishNutrition>
)

val regionalDietProfiles = listOf(
  RegionalCuisineDietProfile(
    id = "north",
    name = "North Indian",
    stateNames = "Punjab, Haryana, Delhi, UP, Kashmir, Himachal",
    emoji = "🌾",
    stapleSummary = "Whole wheat rotis, rajma, chhole, dal tadka, paneer, curd, sarson saag",
    calorieRiskFactor = "Heavy restaurant tadkas, malai cream, and multiple buttered naans",
    tips = listOf(
      RegionalTipItem(
        category = "PROTEIN STRATEGY",
        icon = Icons.Default.FitnessCenter,
        title = "Smart Dal & Legume Balancing",
        advice = "Dal Makhani is often simmered with heavy butter and cream, pushing fats up to 35g per bowl. Opt for double-boiled Dal Tadka or Moong Dal with a 1-tsp jeera ghee tempering to get 14g clean protein with 60% less saturated fat.",
        practicalSwap = "Dal Makhani (450 kcal) → Yellow Dal Tadka with jeera (190 kcal, +12g protein)"
      ),
      RegionalTipItem(
        category = "CARB & FIBER OPTIMIZATION",
        icon = Icons.Default.Restaurant,
        title = "Missi Roti & Multigrain Flour",
        advice = "Replace refined maida breads with Missi Roti (50% whole wheat + 50% roasted chickpea besan). The high resistant starch halves post-meal insulin spikes and delivers 9g protein per pair of rotis.",
        practicalSwap = "1 Butter Naan (320 kcal, 2g fiber) → 2 Missi Rotis (220 kcal, 8g fiber, 9g protein)"
      ),
      RegionalTipItem(
        category = "AYURVEDIC GUT HEALTH",
        icon = Icons.Default.Spa,
        title = "Hing & Ajwain Digestion Tempering",
        advice = "Dense legumes like rajma, chhole, and black urad dal can cause gas and bloating. Always temper with freshly roasted hing (asafoetida), grated ginger, and ajwain to kindle digestive fire (Agni).",
        practicalSwap = "Heavy cream garnish → Fresh chopped coriander, lemon juice & julienned ginger"
      ),
      RegionalTipItem(
        category = "DAIRY PORTION CONTROL",
        icon = Icons.Default.Opacity,
        title = "Paneer & Dairy Fat Management",
        advice = "Paneer is rich in casein protein and calcium but contains 20g fat per 100g. Pair fresh homemade low-fat paneer with roasted green moong sprouts or sattu to satisfy satiety without exceeding daily fat budgets.",
        practicalSwap = "Malai Kofta Gravy (540 kcal) → Char-grilled Tandoori Paneer Tikka (260 kcal)"
      )
    ),
    topDishes = listOf(
      RegionalDishNutrition(
        dishName = "Dal Tadka with 2 Phulkas",
        typicalKcal = 310,
        proteinGrams = 13,
        verdict = "Nutrient Dense",
        optimizationTip = "Classic Indian gold standard; use pure desi ghee strictly as a 1-tsp finish."
      ),
      RegionalDishNutrition(
        dishName = "Paneer Butter Masala",
        typicalKcal = 490,
        proteinGrams = 14,
        verdict = "Calorie Dense",
        optimizationTip = "Order gravy blended with onion-tomato-cashew puree without extra malai cream float."
      ),
      RegionalDishNutrition(
        dishName = "Sarson Ka Saag & Makki Roti",
        typicalKcal = 340,
        proteinGrams = 9,
        verdict = "Optimal Recovery",
        optimizationTip = "Mustard and bathua greens provide powerful glucosinolates and bioavailable iron."
      )
    )
  ),
  RegionalCuisineDietProfile(
    id = "south",
    name = "South Indian",
    stateNames = "Tamil Nadu, Kerala, Karnataka, Andhra, Telangana",
    emoji = "🥥",
    stapleSummary = "Parboiled rice, idli, dosa, sambar, rasam, curd, seafood, coconut, tamarind",
    calorieRiskFactor = "Excess carb-stacking (idli + rice + rasam rice) & large coconut chutney bowls",
    tips = listOf(
      RegionalTipItem(
        category = "PROTEIN ENHANCEMENT",
        icon = Icons.Default.FitnessCenter,
        title = "Ghana Sambar & Boiled Sundal",
        advice = "Standard watery sambar has minimal dal. Prepare 'Ghana Sambar' with double toor dal or pair 2 idlis with a bowl of boiled chickpea/white pea sundal to reach 18g+ protein instead of just 6g.",
        practicalSwap = "3 Idlis with plain coconut chutney (6g P) → 2 Idlis + Thick Sambar + 1 cup Sundal (18g P)"
      ),
      RegionalTipItem(
        category = "GLYCEMIC INDEX CONTROL",
        icon = Icons.Default.Restaurant,
        title = "Red Matta & Brown Samba Rice",
        advice = "Polished white Ponni or Sona Masoori rice has a high glycemic index (GI > 75). Swap to unpolished Kerala Matta red rice or brown Samba rice, which contain resistant starch to sustain steady energy.",
        practicalSwap = "Polished White Rice (GI 75) → Kerala Red Matta Rice (GI 52, rich in magnesium)"
      ),
      RegionalTipItem(
        category = "HEALTHY FATS & COCONUT",
        icon = Icons.Default.Opacity,
        title = "Coconut Chutney Portioning",
        advice = "Fresh coconut provides beneficial medium-chain triglycerides (MCTs), but a deep katori contains 180 kcal and 15g fat. Limit coconut chutney to 2 tablespoons and rotate with tomato-onion or roasted gram chutney.",
        practicalSwap = "Deep-Fried Medu Vada (290 kcal, 18g fat) → Steamed Moong Dal Pesarattu (140 kcal, 2g fat)"
      ),
      RegionalTipItem(
        category = "METABOLIC ACCELERATION",
        icon = Icons.Default.LocalFireDepartment,
        title = "Crushed Black Pepper Rasam",
        advice = "Traditional pepper-cumin rasam contains piperine from black pepper and curcumin from turmeric. This synergistic combination boosts antioxidant uptake by up to 2000% and aids gastric clearance.",
        practicalSwap = "Heavy cream soup → Piping hot homemade pepper rasam before dinner"
      )
    ),
    topDishes = listOf(
      RegionalDishNutrition(
        dishName = "Moong Dal Pesarattu & Upma",
        typicalKcal = 280,
        proteinGrams = 14,
        verdict = "Nutrient Dense",
        optimizationTip = "Whole green moong batter requires zero fermentation and provides rapid sustained fuel."
      ),
      RegionalDishNutrition(
        dishName = "Masala Dosa with Chutneys",
        typicalKcal = 390,
        proteinGrams = 8,
        verdict = "High GI",
        optimizationTip = "Reduce potato masala filling and pair with an extra bowl of vegetable-rich sambar."
      ),
      RegionalDishNutrition(
        dishName = "Kerala Fish Curry (Meen Curry)",
        typicalKcal = 240,
        proteinGrams = 26,
        verdict = "Optimal Recovery",
        optimizationTip = "Kodampuli (Malabar tamarind) and fresh seer fish deliver lean protein & Omega-3 EPA."
      )
    )
  ),
  RegionalCuisineDietProfile(
    id = "west",
    name = "West Indian",
    stateNames = "Maharashtra, Gujarat, Goa, Rajasthan",
    emoji = "🥜",
    stapleSummary = "Jowar & bajra bhakri, thepla, sprouted usal, pitla, peanuts, kokum, solkadhi",
    calorieRiskFactor = "Added sugar/jaggery in everyday dals & deep-fried farsans (sev, fafda, gathiya)",
    tips = listOf(
      RegionalTipItem(
        category = "MILLET POWERHOUSE",
        icon = Icons.Default.Restaurant,
        title = "Jowar & Bajra Bhakris",
        advice = "Traditional hand-patted Jowar (sorghum) and Bajra (pearl millet) bhakris are naturally gluten-free and loaded with iron, zinc, and slow-digesting complex carbs that prevent afternoon energy crashes.",
        practicalSwap = "White bread or maida pav → 1 thick Bajra/Jowar Bhakri with roasted garlic thecha"
      ),
      RegionalTipItem(
        category = "SUGAR MANAGEMENT",
        icon = Icons.Default.LocalFireDepartment,
        title = "Watch Added Jaggery in Dals",
        advice = "Traditional Gujarati dals and kadhis often add 1-2 teaspoons of jaggery or sugar per katori, stealthily contributing 60-80 extra liquid calories. Request sugar-free versions flavored with kokum and cinnamon.",
        practicalSwap = "Sweetened Dal (220 kcal) → Tangy Khatta-Meetha Dal with amchur (130 kcal, zero added sugar)"
      ),
      RegionalTipItem(
        category = "PROTEIN SPROUTS",
        icon = Icons.Default.FitnessCenter,
        title = "Matki & Moong Usal",
        advice = "Maharashtrian sprouted moth beans (matki) and sprouted moong are exceptional sources of branched-chain amino acids (BCAAs). Lightly steam them with mustard seeds and curry leaves instead of soaking in oily tari.",
        practicalSwap = "Fried Misal Pav with oily rassa (510 kcal) → Sprouted Matki Usal with curd (210 kcal, 14g P)"
      ),
      RegionalTipItem(
        category = "AYURVEDIC DIGESTION",
        icon = Icons.Default.Spa,
        title = "Kokum & Solkadhi Elixirs",
        advice = "Kokum (Garcinia indica) contains hydroxycitric acid (HCA), a natural compound shown to curb excess fatty acid synthesis. Chilled solkadhi cools high Pitta acidity after spicy meals.",
        practicalSwap = "Carbonated soda beverage → 1 glass chilled Solkadhi with roasted cumin"
      )
    ),
    topDishes = listOf(
      RegionalDishNutrition(
        dishName = "Pitla with Jowar Bhakri",
        typicalKcal = 330,
        proteinGrams = 15,
        verdict = "Nutrient Dense",
        optimizationTip = "Besan (roasted gram flour) + sorghum millet provides an optimal complete amino acid score."
      ),
      RegionalDishNutrition(
        dishName = "Methi Thepla with Plain Curd",
        typicalKcal = 240,
        proteinGrams = 9,
        verdict = "Nutrient Dense",
        optimizationTip = "Fenugreek (methi) greens improve cellular insulin sensitivity and regulate blood glucose."
      ),
      RegionalDishNutrition(
        dishName = "Fafda & Jalebi Breakfast",
        typicalKcal = 680,
        proteinGrams = 7,
        verdict = "Calorie Dense",
        optimizationTip = "High in trans-fats and refined sugar; reserve for rare festivities and opt for Khaman Dhokla."
      )
    )
  ),
  RegionalCuisineDietProfile(
    id = "east",
    name = "East & North-East",
    stateNames = "West Bengal, Odisha, Assam, Bihar, Jharkhand, Manipur",
    emoji = "🐟",
    stapleSummary = "Freshwater fish, steamed rice, dalma, sattu, greens (shak), mustard oil, pakhala",
    calorieRiskFactor = "Large portions of polished white rice (2-3 cups) & sugar-heavy sweets (mishti)",
    tips = listOf(
      RegionalTipItem(
        category = "LEAN ANIMAL PROTEIN",
        icon = Icons.Default.FitnessCenter,
        title = "Freshwater Fish (Rohu & Katla)",
        advice = "Freshwater carp like Rohu and Katla provide complete protein (19g/100g) and cardioprotective Omega-3 DHA/EPA without the heavy saturated fats of red meats. Prepare light stews (jhol) with ridge gourd.",
        practicalSwap = "Deep-Fried Fish Cutlet (360 kcal, 22g fat) → Light Macher Jhol with green papaya (190 kcal, 6g fat)"
      ),
      RegionalTipItem(
        category = "HERITAGE PROTEIN",
        icon = Icons.Default.AutoAwesome,
        title = "Roasted Bengal Gram Sattu",
        advice = "Sattu (roasted gram flour) is India's original superfood whey. Stirring 2-3 tablespoons into cold water with roasted cumin, rock salt, and lemon yields 11g clean protein and 7g soluble prebiotic fiber.",
        practicalSwap = "Sweet sherbet or packaged protein shake → Chilled Savory Sattu Ghol (130 kcal, 11g protein)"
      ),
      RegionalTipItem(
        category = "ONE-POT VEGETABLE DAL",
        icon = Icons.Default.Restaurant,
        title = "Odisha's Dalma Method",
        advice = "Cooking lentils together with raw papaya, pumpkin, drumsticks, and colocasia triples the vitamin C, potassium, and dietary fiber per serving while lowering the net carbohydrate density of the meal.",
        practicalSwap = "Plain white rice + potato aloo bhaja → Vegetable Dalma + 1 cup steamed rice + lime"
      ),
      RegionalTipItem(
        category = "NATURAL PROBIOTIC",
        icon = Icons.Default.Spa,
        title = "Fermented Pakhala / Pantabhat",
        advice = "Fermenting cooked rice overnight in water activates beneficial lactic acid bacteria and increases bioavailable micronutrients (iron availability spikes by up to 300%). It soothes gut lining in hot weather.",
        practicalSwap = "Heavy biryani → Cool Pakhala Bhata with roasted cumin, green chili, and sautéed greens"
      )
    ),
    topDishes = listOf(
      RegionalDishNutrition(
        dishName = "Macher Jhol with Steamed Rice",
        typicalKcal = 340,
        proteinGrams = 24,
        verdict = "Nutrient Dense",
        optimizationTip = "Heart-healthy gold standard; flavored with kalonji (nigella seeds) and cumin."
      ),
      RegionalDishNutrition(
        dishName = "Baked Litti with Baingan Chokha",
        typicalKcal = 390,
        proteinGrams = 16,
        verdict = "Optimal Recovery",
        optimizationTip = "Baked over charcoal rather than deep fried; sattu filling provides long-lasting stamina."
      ),
      RegionalDishNutrition(
        dishName = "Vegetable Dalma & Rice",
        typicalKcal = 310,
        proteinGrams = 14,
        verdict = "Nutrient Dense",
        optimizationTip = "Drumstick and raw papaya enzymes ease gastric emptying and reduce bloating."
      )
    )
  ),
  RegionalCuisineDietProfile(
    id = "pan",
    name = "Pan-Indian / Fusion",
    stateNames = "Metropolitan India & Global Desi Lifestyle",
    emoji = "🥗",
    stapleSummary = "Balanced combination of phulkas, brown rice, dal, paneer, chicken, salads, curd",
    calorieRiskFactor = "Irregular late-night meal windows & high-calorie restaurant oil floats",
    tips = listOf(
      RegionalTipItem(
        category = "THE THALI FORMULA",
        icon = Icons.Default.Restaurant,
        title = "50-25-25 Balanced Plate",
        advice = "Structure your plate visually: 50% fiber (raw kachumber salad, stir-fried bhindi or palak), 25% protein (dal, paneer, sattu, eggs, grilled chicken), and 25% whole grains (phulkas, red or brown rice).",
        practicalSwap = "Carb-heavy thali (70% rice/naan) → Half plate fresh salad + 1 katori dal + 2 small phulkas"
      ),
      RegionalTipItem(
        category = "CHRONO-NUTRITION",
        icon = Icons.Default.LocalFireDepartment,
        title = "Midday Agni Window (12 PM - 2 PM)",
        advice = "In sync with human circadian metabolic rhythms, insulin sensitivity and gastric enzyme secretion peak around noon. Consume 50-60% of your daily calories during lunch, and keep dinners light before 8:30 PM.",
        practicalSwap = "Heavy 10 PM dinner (750 kcal) → Substantial 1:30 PM lunch + light vegetable soup dinner (250 kcal)"
      ),
      RegionalTipItem(
        category = "EVENING SNACK CONTROL",
        icon = Icons.Default.FitnessCenter,
        title = "Tackle the 5 PM Chai Craving",
        advice = "The late afternoon tea break is where most fitness plans break down due to fried samosas, namkeens, and refined flour biscuits. Keep dry roasted makhana, roasted chana, or boiled eggs within reach.",
        practicalSwap = "2 Fried Samosas with sweet chutney (520 kcal) → 1 cup Roasted Makhana with rock salt (130 kcal)"
      ),
      RegionalTipItem(
        category = "HYDRATION TIMING",
        icon = Icons.Default.Opacity,
        title = "Pre-Meal Water Protocol",
        advice = "Drink a full glass of room-temperature water 30 minutes before your meal. Avoid drinking large amounts during the meal, which dilutes hydrochloric acid and slows protein breakdown.",
        practicalSwap = "Chilled soda during lunch → Warm jeera-infused water 30 minutes prior"
      )
    ),
    topDishes = listOf(
      RegionalDishNutrition(
        dishName = "Paneer Tikka Brown Rice Bowl",
        typicalKcal = 360,
        proteinGrams = 20,
        verdict = "Nutrient Dense",
        optimizationTip = "Modern balanced macro bowl; complex carbs paired with complete dairy proteins."
      ),
      RegionalDishNutrition(
        dishName = "Moong Dal Chilla & Mint Curd",
        typicalKcal = 240,
        proteinGrams = 18,
        verdict = "Nutrient Dense",
        optimizationTip = "Exceptional quick high-protein vegetarian breakfast or evening refuel."
      ),
      RegionalDishNutrition(
        dishName = "Butter Chicken with Garlic Naan",
        typicalKcal = 690,
        proteinGrams = 28,
        verdict = "Calorie Dense",
        optimizationTip = "Ask for Tandoori Chicken Tikka with whole wheat roomali or tandoori roti instead."
      )
    )
  )
)

/**
 * Modern CalSnapix Regional Diet Tips Component.
 * Minimalist, high contrast, clean whitespace, single emerald accent.
 */
@Composable
fun RegionalDietTipsCard(
  currentRegionId: String,
  dailyCalorieTarget: Int,
  onSelectRegion: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = CalTheme.colors
  val view = LocalView.current

  var selectedRegionId by remember(currentRegionId) { mutableStateOf(currentRegionId) }
  var showDishesBreakdown by remember { mutableStateOf(false) }

  val activeProfile = remember(selectedRegionId) {
    regionalDietProfiles.find { it.id == selectedRegionId } ?: regionalDietProfiles.first()
  }

  CalCard(
    modifier = modifier.testTag("regional_diet_tips_card"),
    backgroundColor = colors.surface
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(colors.accentSoft),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = colors.accent,
              modifier = Modifier.size(16.dp)
            )
          }
          Column {
            Text(
              text = "REGIONAL DIET TIPS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink2,
              letterSpacing = 0.8.sp
            )
            Text(
              text = "${activeProfile.emoji} ${activeProfile.name} Cuisine",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = colors.ink
            )
          }
        }

        // Calorie context badge
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = colors.accentSoft
        ) {
          Text(
            text = "For ${dailyCalorieTarget} kcal",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.accent,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
          )
        }
      }

      // 2. Region Switcher Chips Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        regionalDietProfiles.forEach { profile ->
          val isSelected = profile.id == selectedRegionId
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = if (isSelected) colors.ink else colors.surface2,
            border = BorderStroke(
              width = 1.dp,
              color = if (isSelected) colors.ink else colors.line
            ),
            modifier = Modifier
              .clickable {
                if (selectedRegionId != profile.id) {
                  selectedRegionId = profile.id
                  onSelectRegion(profile.id)
                  view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
              }
              .testTag("region_chip_${profile.id}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(profile.emoji, fontSize = 13.sp)
              Text(
                text = profile.name.replace(" Indian", "").replace(" / Fusion", ""),
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) colors.bg else colors.ink
              )
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = colors.bg,
                  modifier = Modifier.size(12.dp)
                )
              }
            }
          }
        }
      }

      // 3. Cultural Baseline & Watchout Box
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surface2,
        border = BorderStroke(1.dp, colors.line)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text("📍", fontSize = 12.sp)
            Text(
              text = activeProfile.stateNames,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = colors.ink2
            )
          }

          Text(
            text = "Staples: ${activeProfile.stapleSummary}",
            fontSize = 12.sp,
            color = colors.ink,
            lineHeight = 16.sp
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = colors.danger,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Watch Out: ${activeProfile.calorieRiskFactor}",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = colors.danger
            )
          }
        }
      }

      // 4. Actionable Tips with AnimatedContent transition
      AnimatedContent(
        targetState = activeProfile,
        transitionSpec = {
          fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
        },
        label = "regional_tips_transition"
      ) { profile ->
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          profile.tips.forEach { tip ->
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = colors.surface,
              border = BorderStroke(1.dp, colors.line)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                // Category & Icon Row
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(colors.accentSoft),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = tip.icon,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(13.dp)
                      )
                    }
                    Text(
                      text = tip.category,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = colors.accent,
                      letterSpacing = 0.7.sp
                    )
                  }
                }

                // Title
                Text(
                  text = tip.title,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = colors.ink
                )

                // Advice body
                Text(
                  text = tip.advice,
                  fontSize = 12.sp,
                  color = colors.ink2,
                  lineHeight = 17.sp
                )

                // Smart Swap Banner
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = colors.accentSoft.copy(alpha = 0.5f),
                  border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.2f)),
                  modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.SwapHoriz,
                      contentDescription = null,
                      tint = colors.accent,
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = tip.practicalSwap,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = colors.accent,
                      lineHeight = 15.sp
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 5. Expandable Regional Dish Nutrition Breakdown
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surface2,
        border = BorderStroke(1.dp, colors.line),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            showDishesBreakdown = !showDishesBreakdown
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
          }
          .testTag("toggle_regional_dishes_button")
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text("🍽️", fontSize = 14.sp)
              Text(
                text = "Popular ${activeProfile.name} Dishes Breakdown",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.ink
              )
            }
            Icon(
              imageVector = if (showDishesBreakdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
              contentDescription = if (showDishesBreakdown) "Collapse" else "Expand",
              tint = colors.ink2,
              modifier = Modifier.size(18.dp)
            )
          }

          AnimatedVisibility(
            visible = showDishesBreakdown,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              activeProfile.topDishes.forEach { dish ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = colors.surface,
                  border = BorderStroke(1.dp, colors.line),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = dish.dishName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.ink
                      )
                      Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = when (dish.verdict) {
                          "Nutrient Dense", "Optimal Recovery" -> colors.accentSoft
                          "Calorie Dense" -> colors.danger.copy(alpha = 0.12f)
                          else -> colors.warn.copy(alpha = 0.15f)
                        }
                      ) {
                        Text(
                          text = dish.verdict,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = when (dish.verdict) {
                            "Nutrient Dense", "Optimal Recovery" -> colors.accent
                            "Calorie Dense" -> colors.danger
                            else -> colors.warn
                          },
                          modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                      }
                    }

                    Row(
                      horizontalArrangement = Arrangement.spacedBy(10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(vertical = 3.dp)
                    ) {
                      Text(
                        text = "${dish.typicalKcal} kcal",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.ink
                      )
                      Text("•", fontSize = 11.sp, color = colors.line)
                      Text(
                        text = "${dish.proteinGrams}g Protein",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.protein
                      )
                    }

                    Text(
                      text = dish.optimizationTip,
                      fontSize = 11.sp,
                      color = colors.ink2,
                      lineHeight = 15.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
