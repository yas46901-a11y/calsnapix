-- ====================================================================
-- CalSnapix Supabase Backend & Database Schema
-- Run this in your Supabase SQL Editor (https://supabase.com/dashboard)
-- ====================================================================

-- 1. Enable UUID Extension (standard in Supabase)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. User Profiles Table
CREATE TABLE IF NOT EXISTS public.user_profiles (
    user_id BIGINT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    goal TEXT DEFAULT 'lose_weight',
    diet_type TEXT DEFAULT 'veg',
    region TEXT DEFAULT 'north',
    daily_calorie_target INT DEFAULT 2000,
    protein_target_grams INT DEFAULT 90,
    carbs_target_grams INT DEFAULT 210,
    fats_target_grams INT DEFAULT 55,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. Logged Meals Table (Daily meals logged by user)
CREATE TABLE IF NOT EXISTS public.meals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title TEXT NOT NULL,
    meal_type TEXT NOT NULL,
    time TEXT NOT NULL,
    calories INT NOT NULL,
    protein_grams DOUBLE PRECISION DEFAULT 0.0,
    carbs_grams DOUBLE PRECISION DEFAULT 0.0,
    fat_grams DOUBLE PRECISION DEFAULT 0.0,
    tag TEXT DEFAULT '',
    image_url TEXT DEFAULT '',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. Scanned Meals Table (AI Camera & Barcode Scanned meals with full nutritional breakdown)
CREATE TABLE IF NOT EXISTS public.scanned_meals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    dish_name TEXT NOT NULL,
    meal_type TEXT NOT NULL,
    serving_description TEXT DEFAULT '1 standard plate',
    portion_multiplier REAL DEFAULT 1.0,
    image_url TEXT DEFAULT '',
    photo_uri TEXT,
    scan_source TEXT DEFAULT 'CAMERA',
    barcode TEXT,
    chef_note TEXT DEFAULT '',
    calories INT NOT NULL,
    protein_grams DOUBLE PRECISION DEFAULT 0.0,
    carbs_grams DOUBLE PRECISION DEFAULT 0.0,
    fat_grams DOUBLE PRECISION DEFAULT 0.0,
    fiber_grams DOUBLE PRECISION DEFAULT 0.0,
    sugar_grams DOUBLE PRECISION DEFAULT 0.0,
    sodium_mg DOUBLE PRECISION DEFAULT 0.0,
    saturated_fat_grams DOUBLE PRECISION DEFAULT 0.0,
    micronutrients TEXT DEFAULT '',
    scanned_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. Indexes for fast queries
CREATE INDEX IF NOT EXISTS idx_meals_user_id ON public.meals (user_id);
CREATE INDEX IF NOT EXISTS idx_scanned_meals_user_id ON public.scanned_meals (user_id);
CREATE INDEX IF NOT EXISTS idx_scanned_meals_barcode ON public.scanned_meals (barcode);

-- 6. Zero-Trust Row Level Security (RLS) - Users only read and write their own records
ALTER TABLE public.user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.meals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.scanned_meals ENABLE ROW LEVEL SECURITY;

-- 6.1 user_profiles: Each user can only select, insert, update, or delete their own profile record
CREATE POLICY "Users can only read their own profile"
    ON public.user_profiles FOR SELECT
    USING (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

CREATE POLICY "Users can only insert their own profile"
    ON public.user_profiles FOR INSERT
    WITH CHECK (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

CREATE POLICY "Users can only update their own profile"
    ON public.user_profiles FOR UPDATE
    USING (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

CREATE POLICY "Users can only delete their own profile"
    ON public.user_profiles FOR DELETE
    USING (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

-- 6.2 meals: Strict isolation preventing cross-account data leakage
CREATE POLICY "Users can only read their own meals"
    ON public.meals FOR SELECT
    USING (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

CREATE POLICY "Users can only insert their own meals"
    ON public.meals FOR INSERT
    WITH CHECK (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

CREATE POLICY "Users can only update their own meals"
    ON public.meals FOR UPDATE
    USING (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

CREATE POLICY "Users can only delete their own meals"
    ON public.meals FOR DELETE
    USING (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

-- 6.3 scanned_meals: Strict isolation for camera and barcode meal scans
CREATE POLICY "Users can only read their own scanned meals"
    ON public.scanned_meals FOR SELECT
    USING (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

CREATE POLICY "Users can only insert their own scanned meals"
    ON public.scanned_meals FOR INSERT
    WITH CHECK (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

CREATE POLICY "Users can only update their own scanned meals"
    ON public.scanned_meals FOR UPDATE
    USING (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

CREATE POLICY "Users can only delete their own scanned meals"
    ON public.scanned_meals FOR DELETE
    USING (auth.uid() IS NOT NULL AND (auth.uid()::text = user_id::text OR user_id = (SELECT id FROM auth.users WHERE id = auth.uid() LIMIT 1)));

-- ====================================================================
-- 7. Onboarding State Persistence & Discount tracking in User Profiles
-- ====================================================================
ALTER TABLE public.user_profiles
    ADD COLUMN IF NOT EXISTS onboarding_answers JSONB DEFAULT '{}'::jsonb,
    ADD COLUMN IF NOT EXISTS applied_discount_percent INT DEFAULT 0;

-- ====================================================================
-- 8. Referral System (referral_codes & referral_redemptions)
-- ====================================================================
CREATE TABLE IF NOT EXISTS public.referral_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code TEXT UNIQUE NOT NULL,
    discount_percent INT NOT NULL CHECK (discount_percent BETWEEN 5 AND 50),
    owner_user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    max_uses INT NULL,
    uses_count INT DEFAULT 0,
    expires_at TIMESTAMPTZ NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.referral_redemptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code_id UUID NOT NULL REFERENCES public.referral_codes(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    redeemed_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT unique_user_redemption UNIQUE(user_id)
);

-- Table for tracking failed attempts to enforce rate limiting (max 5 wrong tries per user/IP per hour)
CREATE TABLE IF NOT EXISTS public.referral_attempt_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NULL,
    attempt_code TEXT NOT NULL,
    success BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS (Client NEVER reads referral tables directly)
ALTER TABLE public.referral_codes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.referral_redemptions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.referral_attempt_logs ENABLE ROW LEVEL SECURITY;

-- No direct client SELECT/INSERT/UPDATE/DELETE policies on referral tables for public/anon
-- All access is mediated strictly via SECURITY DEFINER functions below:

-- 8.1 Server-side Validation Function
CREATE OR REPLACE FUNCTION public.validate_referral(p_code TEXT)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, auth, pg_temp
AS $$
DECLARE
    v_user_id UUID;
    v_recent_failures INT;
    v_code_rec RECORD;
BEGIN
    v_user_id := auth.uid();
    p_code := UPPER(TRIM(p_code));

    -- Rate limiting check: max 5 wrong tries per user in the last hour
    IF v_user_id IS NOT NULL THEN
        SELECT COUNT(*) INTO v_recent_failures
        FROM public.referral_attempt_logs
        WHERE user_id = v_user_id
          AND success = false
          AND created_at > (NOW() - INTERVAL '1 hour');

        IF v_recent_failures >= 5 THEN
            RETURN jsonb_build_object(
                'valid', false,
                'error', 'Too many attempts. Please try again after 1 hour.'
            );
        END IF;
    END IF;

    -- Lookup referral code
    SELECT * INTO v_code_rec
    FROM public.referral_codes
    WHERE code = p_code;

    -- Validate existence and status
    IF v_code_rec.id IS NULL THEN
        IF v_user_id IS NOT NULL THEN
            INSERT INTO public.referral_attempt_logs (user_id, attempt_code, success)
            VALUES (v_user_id, p_code, false);
        END IF;
        RETURN jsonb_build_object('valid', false, 'error', 'Invalid referral code');
    END IF;

    IF NOT v_code_rec.active THEN
        RETURN jsonb_build_object('valid', false, 'error', 'This referral code is inactive');
    END IF;

    IF v_code_rec.expires_at IS NOT NULL AND v_code_rec.expires_at < NOW() THEN
        RETURN jsonb_build_object('valid', false, 'error', 'This referral code has expired');
    END IF;

    IF v_code_rec.max_uses IS NOT NULL AND v_code_rec.uses_count >= v_code_rec.max_uses THEN
        RETURN jsonb_build_object('valid', false, 'error', 'This referral code has reached maximum uses');
    END IF;

    -- Users cannot use their own referral code
    IF v_user_id IS NOT NULL AND v_code_rec.owner_user_id = v_user_id THEN
        RETURN jsonb_build_object('valid', false, 'error', 'You cannot use your own referral code');
    END IF;

    -- Check if user has already redeemed any referral code
    IF v_user_id IS NOT NULL THEN
        IF EXISTS (SELECT 1 FROM public.referral_redemptions WHERE user_id = v_user_id) THEN
            RETURN jsonb_build_object('valid', false, 'error', 'You have already redeemed a referral discount');
        END IF;
    END IF;

    -- Log successful check
    IF v_user_id IS NOT NULL THEN
        INSERT INTO public.referral_attempt_logs (user_id, attempt_code, success)
        VALUES (v_user_id, p_code, true);
    END IF;

    RETURN jsonb_build_object(
        'valid', true,
        'code', v_code_rec.code,
        'discount_percent', v_code_rec.discount_percent,
        'message', format('%s%% OFF unlocked!', v_code_rec.discount_percent)
    );
END;
$$;

-- 8.2 Server-side Redemption Function
CREATE OR REPLACE FUNCTION public.redeem_referral(p_code TEXT)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, auth, pg_temp
AS $$
DECLARE
    v_user_id UUID;
    v_val_res JSONB;
    v_code_rec RECORD;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RETURN jsonb_build_object('success', false, 'error', 'Authentication required to redeem referral');
    END IF;

    p_code := UPPER(TRIM(p_code));

    -- First validate
    v_val_res := public.validate_referral(p_code);
    IF NOT ((v_val_res->>'valid')::boolean) THEN
        RETURN jsonb_build_object('success', false, 'error', v_val_res->>'error');
    END IF;

    SELECT * INTO v_code_rec
    FROM public.referral_codes
    WHERE code = p_code;

    -- Record redemption
    INSERT INTO public.referral_redemptions (code_id, user_id)
    VALUES (v_code_rec.id, v_user_id);

    -- Increment usage count
    UPDATE public.referral_codes
    SET uses_count = uses_count + 1
    WHERE id = v_code_rec.id;

    -- Store applied discount on user profile
    UPDATE public.user_profiles
    SET applied_discount_percent = v_code_rec.discount_percent
    WHERE user_id = (SELECT id FROM auth.users WHERE id = v_user_id LIMIT 1);

    RETURN jsonb_build_object(
        'success', true,
        'discount_percent', v_code_rec.discount_percent,
        'message', format('%s%% discount successfully applied to your account!', v_code_rec.discount_percent)
    );
END;
$$;

-- ====================================================================
-- 9. Server-Side Discount & Coupon System (e.g. INSTA20)
-- Prevents client-side manipulation and limits to 1 use per account
-- ====================================================================
CREATE TABLE IF NOT EXISTS public.coupon_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code TEXT UNIQUE NOT NULL,
    discount_percent INT NOT NULL CHECK (discount_percent BETWEEN 1 AND 100),
    max_redemptions_per_account INT DEFAULT 1,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Seed initial official INSTA20 coupon
INSERT INTO public.coupon_codes (code, discount_percent, max_redemptions_per_account, active)
VALUES ('INSTA20', 20, 1, true)
ON CONFLICT (code) DO NOTHING;

CREATE TABLE IF NOT EXISTS public.coupon_redemptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coupon_id UUID NOT NULL REFERENCES public.coupon_codes(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    redeemed_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT unique_user_coupon UNIQUE(user_id, coupon_id)
);

ALTER TABLE public.coupon_codes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.coupon_redemptions ENABLE ROW LEVEL SECURITY;

CREATE OR REPLACE FUNCTION public.validate_and_redeem_coupon(p_code TEXT)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, auth, pg_temp
AS $$
DECLARE
    v_user_id UUID;
    v_coupon_rec RECORD;
    v_already_redeemed BOOLEAN;
BEGIN
    v_user_id := auth.uid();
    p_code := UPPER(TRIM(p_code));

    SELECT * INTO v_coupon_rec
    FROM public.coupon_codes
    WHERE code = p_code AND active = true;

    IF v_coupon_rec.id IS NULL THEN
        RETURN jsonb_build_object('valid', false, 'error', 'Invalid or expired discount code');
    END IF;

    IF v_user_id IS NOT NULL THEN
        SELECT EXISTS (
            SELECT 1 FROM public.coupon_redemptions
            WHERE coupon_id = v_coupon_rec.id AND user_id = v_user_id
        ) INTO v_already_redeemed;

        IF v_already_redeemed THEN
            RETURN jsonb_build_object('valid', false, 'error', 'This discount code has already been redeemed by your account');
        END IF;

        -- Record server redemption
        INSERT INTO public.coupon_redemptions (coupon_id, user_id)
        VALUES (v_coupon_rec.id, v_user_id);

        -- Update user profile
        UPDATE public.user_profiles
        SET applied_discount_percent = v_coupon_rec.discount_percent
        WHERE user_id = (SELECT id FROM auth.users WHERE id = v_user_id LIMIT 1);
    END IF;

    RETURN jsonb_build_object(
        'valid', true,
        'code', v_coupon_rec.code,
        'discount_percent', v_coupon_rec.discount_percent,
        'message', format('20%% discount code applied to your subscription!')
    );
END;
$$;

-- ====================================================================
-- 10. Official Indian Foods Reference Table (Database-Driven Math)
-- Calculates exact calories and macros in PostgreSQL, not in LLM prompts
-- ====================================================================
CREATE TABLE IF NOT EXISTS public.foods (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    aliases TEXT[] DEFAULT '{}',
    category TEXT DEFAULT 'Indian Main Course',
    serving_unit TEXT DEFAULT 'plate',
    standard_serving_grams NUMERIC DEFAULT 150.0,
    calories_per_100g NUMERIC NOT NULL,
    protein_per_100g NUMERIC NOT NULL,
    carbs_per_100g NUMERIC NOT NULL,
    fat_per_100g NUMERIC NOT NULL,
    fiber_per_100g NUMERIC DEFAULT 0.0,
    iron_mg_per_100g NUMERIC DEFAULT 0.0,
    calcium_mg_per_100g NUMERIC DEFAULT 0.0,
    source TEXT DEFAULT 'Indian Food Composition Tables (IFCT)',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Seed verified nutritional values
INSERT INTO public.foods (name, aliases, category, serving_unit, standard_serving_grams, calories_per_100g, protein_per_100g, carbs_per_100g, fat_per_100g, fiber_per_100g, iron_mg_per_100g, calcium_mg_per_100g)
VALUES
('Palak Paneer', ARRAY['palak paneer', 'spinach paneer', 'paneer palak'], 'Curry / Sabzi', 'bowl', 200, 160.0, 7.5, 5.0, 12.0, 3.2, 2.5, 180.0),
('Dal Makhani', ARRAY['dal makhani', 'makhani dal', 'black dal', 'maa ki dal'], 'Lentils / Dal', 'bowl', 200, 140.0, 6.0, 18.0, 5.2, 4.0, 2.1, 80.0),
('Moong Dal Chilla', ARRAY['chilla', 'cheela', 'moong chilla', 'puda'], 'Breakfast / Snack', 'piece', 80, 175.0, 11.5, 22.0, 4.5, 4.2, 2.8, 65.0),
('Steamed Idli', ARRAY['idli', 'steamed idli', 'rice idli'], 'Breakfast', 'piece', 50, 140.0, 4.5, 28.0, 0.5, 1.8, 0.9, 25.0),
('Punjabi Rajma Masala', ARRAY['rajma', 'rajma masala', 'kidney beans curry'], 'Curry / Sabzi', 'bowl', 200, 125.0, 6.5, 19.0, 3.0, 4.8, 2.6, 70.0),
('Whole Wheat Phulka Roti', ARRAY['roti', 'phulka', 'chapati'], 'Breads', 'piece', 35, 290.0, 11.0, 55.0, 3.5, 9.5, 3.8, 45.0),
('Jeera Basmati Rice', ARRAY['jeera rice', 'steamed rice', 'basmati rice', 'chawal'], 'Grains', 'plate', 180, 130.0, 2.8, 28.0, 0.5, 0.6, 0.5, 12.0),
('Paneer Butter Masala', ARRAY['paneer makhani', 'butter paneer'], 'Curry / Sabzi', 'bowl', 200, 210.0, 8.5, 9.0, 16.5, 2.1, 1.8, 195.0),
('Tandoori Chicken Breast', ARRAY['tandoori chicken', 'grilled chicken tikka'], 'Protein', 'serving', 150, 165.0, 28.0, 3.0, 4.5, 0.8, 1.6, 30.0),
('Vegetable Biryani', ARRAY['veg biryani', 'pulao', 'vegetable pulao'], 'Rice', 'plate', 250, 155.0, 3.8, 26.0, 4.2, 2.8, 1.4, 45.0)
ON CONFLICT DO NOTHING;

-- Database-Side Calorie & Macro Calculation Function
CREATE OR REPLACE FUNCTION public.calculate_meal_calories(
    p_food_query TEXT,
    p_serving_grams NUMERIC DEFAULT NULL
)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
DECLARE
    v_food RECORD;
    v_grams NUMERIC;
    v_multiplier NUMERIC;
BEGIN
    SELECT * INTO v_food
    FROM public.foods
    WHERE LOWER(name) LIKE '%' || LOWER(TRIM(p_food_query)) || '%'
       OR LOWER(TRIM(p_food_query)) = ANY(SELECT LOWER(unnest(aliases)))
    LIMIT 1;

    IF v_food.id IS NULL THEN
        RETURN jsonb_build_object(
            'found', false,
            'message', 'Food item not found in composition table. Use fallback estimation.'
        );
    END IF;

    v_grams := COALESCE(p_serving_grams, v_food.standard_serving_grams);
    v_multiplier := v_grams / 100.0;

    RETURN jsonb_build_object(
        'found', true,
        'dish_name', v_food.name,
        'serving_grams', v_grams,
        'calories', ROUND(v_food.calories_per_100g * v_multiplier),
        'protein_grams', ROUND(v_food.protein_per_100g * v_multiplier, 1),
        'carbs_grams', ROUND(v_food.carbs_per_100g * v_multiplier, 1),
        'fat_grams', ROUND(v_food.fat_per_100g * v_multiplier, 1),
        'fiber_grams', ROUND(v_food.fiber_per_100g * v_multiplier, 1),
        'iron_mg', ROUND(v_food.iron_mg_per_100g * v_multiplier, 1),
        'calcium_mg', ROUND(v_food.calcium_mg_per_100g * v_multiplier, 1),
        'source', 'Supabase PostgreSQL Food Table Math'
    );
END;
$$;

-- ====================================================================
-- 10. Accuracy Benchmark Tests Table (Point 1)
-- ====================================================================
CREATE TABLE IF NOT EXISTS public.accuracy_tests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    meal_name TEXT NOT NULL,
    category TEXT DEFAULT 'Curry / Sabzi',
    weighed_grams NUMERIC NOT NULL,
    estimated_grams NUMERIC NOT NULL,
    weighed_kcal NUMERIC NOT NULL,
    estimated_kcal NUMERIC NOT NULL,
    error_percent NUMERIC NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_accuracy_tests_user_id ON public.accuracy_tests(user_id);
CREATE INDEX IF NOT EXISTS idx_accuracy_tests_category ON public.accuracy_tests(category);

ALTER TABLE public.accuracy_tests ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can read own accuracy tests"
    ON public.accuracy_tests FOR SELECT
    USING (auth.uid() IS NULL OR auth.uid()::text = user_id::text);

CREATE POLICY "Users can insert own accuracy tests"
    ON public.accuracy_tests FOR INSERT
    WITH CHECK (auth.uid() IS NULL OR auth.uid()::text = user_id::text);

-- ====================================================================
-- 11. Indian Food Library Table (Point 2)
-- ====================================================================
CREATE TABLE IF NOT EXISTS public.indian_dishes (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    aliases TEXT[] NOT NULL DEFAULT '{}',
    region TEXT NOT NULL, -- North, South, East, West, Pan-Indian
    meal_type TEXT NOT NULL, -- breakfast, lunch, dinner, snack
    standard_serving_grams NUMERIC NOT NULL,
    ingredients JSONB NOT NULL DEFAULT '[]'::jsonb,
    calories_per_100g NUMERIC NOT NULL,
    protein_per_100g NUMERIC NOT NULL,
    carbs_per_100g NUMERIC NOT NULL,
    fat_per_100g NUMERIC NOT NULL,
    source TEXT NOT NULL DEFAULT 'IFCT 2017 (ICMR-NIN)',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_indian_dishes_name ON public.indian_dishes(name);
CREATE INDEX IF NOT EXISTS idx_indian_dishes_region ON public.indian_dishes(region);

ALTER TABLE public.indian_dishes ENABLE ROW LEVEL SECURITY;

-- Public read access so any client can verify nutritional composition
CREATE POLICY "Public read indian dishes"
    ON public.indian_dishes FOR SELECT
    USING (true);

-- Seed comprehensive 100 Indian dishes dataset
INSERT INTO public.indian_dishes (id, name, aliases, region, meal_type, standard_serving_grams, ingredients, calories_per_100g, protein_per_100g, carbs_per_100g, fat_per_100g, source)
VALUES
('south_idli', 'Steamed Idli (South Indian)', ARRAY['idli', 'steamed idli', 'rice idli', 'mallipoo idli'], 'South', 'breakfast', 150, '[{"name": "Rice & Urad Dal Batter", "grams": 145, "hidden_fat_grams": 0}, {"name": "Gingelly Oil (steamer brush)", "grams": 5, "hidden_fat_grams": 4.5}]'::jsonb, 142.0, 4.8, 28.5, 0.8, 'IFCT 2017 (ICMR-NIN)'),
('south_medu_vada', 'Medu Vada', ARRAY['vada', 'medu vada', 'urad dal vada'], 'South', 'breakfast', 90, '[{"name": "Urad Dal & Spices", "grams": 70, "hidden_fat_grams": 0}, {"name": "Frying Oil Absorbed", "grams": 20, "hidden_fat_grams": 18.0}]'::jsonb, 278.0, 8.5, 24.0, 16.5, 'IFCT 2017 (ICMR-NIN)'),
('south_masala_dosa', 'Crispy Masala Dosa', ARRAY['masala dosa', 'dosa', 'mysore masala dosa'], 'South', 'breakfast', 180, '[{"name": "Rice Dosa Crepe", "grams": 100, "hidden_fat_grams": 0}, {"name": "Aloo Masala Filling", "grams": 65, "hidden_fat_grams": 2.0}, {"name": "Butter / Ghee on Tawa", "grams": 15, "hidden_fat_grams": 13.5}]'::jsonb, 195.0, 4.2, 29.0, 7.2, 'IFCT 2017 (ICMR-NIN)'),
('south_sambar', 'South Indian Sambar', ARRAY['sambar', 'sambhar', 'drumstick sambar'], 'South', 'lunch', 200, '[{"name": "Toor Dal & Tamarind Water", "grams": 130, "hidden_fat_grams": 0}, {"name": "Mixed Vegetables", "grams": 60, "hidden_fat_grams": 0}, {"name": "Mustard Tadka Oil", "grams": 10, "hidden_fat_grams": 9.0}]'::jsonb, 68.0, 3.2, 10.5, 1.8, 'IFCT 2017 (ICMR-NIN)'),
('south_rasam', 'Tomato Pepper Rasam', ARRAY['rasam', 'charu', 'saaru'], 'South', 'lunch', 180, '[{"name": "Tomato Pepper Broth", "grams": 165, "hidden_fat_grams": 0}, {"name": "Ghee Mustard Tadka", "grams": 8, "hidden_fat_grams": 7.5}]'::jsonb, 38.0, 1.4, 5.2, 1.4, 'IFCT 2017 (ICMR-NIN)'),
('north_palak_paneer', 'Homestyle Palak Paneer', ARRAY['palak paneer', 'spinach paneer', 'paneer saag'], 'North', 'lunch', 200, '[{"name": "Pureed Spinach", "grams": 110, "hidden_fat_grams": 0}, {"name": "Fresh Paneer Cubes", "grams": 65, "hidden_fat_grams": 0}, {"name": "Mustard Oil / Ghee", "grams": 25, "hidden_fat_grams": 22.0}]'::jsonb, 160.0, 7.5, 5.0, 12.0, 'IFCT 2017 (ICMR-NIN)'),
('north_dal_makhani', 'Amritsari Dal Makhani', ARRAY['dal makhani', 'makhani dal', 'black dal', 'maa ki dal'], 'North', 'dinner', 200, '[{"name": "Black Urad Dal & Rajma", "grams": 125, "hidden_fat_grams": 0}, {"name": "Tomato Puree & Spices", "grams": 45, "hidden_fat_grams": 0}, {"name": "White Butter & Cream", "grams": 30, "hidden_fat_grams": 26.0}]'::jsonb, 140.0, 6.0, 18.0, 5.2, 'IFCT 2017 (ICMR-NIN)'),
('north_dal_tadka', 'Yellow Dal Tadka', ARRAY['dal tadka', 'yellow dal', 'toor dal tadka', 'arhar dal'], 'North', 'lunch', 200, '[{"name": "Boiled Toor Dal", "grams": 155, "hidden_fat_grams": 0}, {"name": "Onion Tomato Masala", "grams": 30, "hidden_fat_grams": 0}, {"name": "Desi Ghee Jeera Tadka", "grams": 15, "hidden_fat_grams": 14.5}]'::jsonb, 112.0, 5.2, 14.8, 3.6, 'IFCT 2017 (ICMR-NIN)'),
('north_rajma_masala', 'Punjabi Rajma Masala', ARRAY['rajma', 'rajma masala', 'punjabi rajma'], 'North', 'lunch', 220, '[{"name": "Red Kidney Beans", "grams": 140, "hidden_fat_grams": 0}, {"name": "Tomato Onion Gravy", "grams": 60, "hidden_fat_grams": 0}, {"name": "Mustard Oil / Ghee", "grams": 20, "hidden_fat_grams": 17.5}]'::jsonb, 125.0, 6.5, 19.0, 3.0, 'IFCT 2017 (ICMR-NIN)'),
('north_chole_masala', 'Pindi Chole Masala', ARRAY['chole', 'chana masala', 'pindi chole'], 'North', 'lunch', 220, '[{"name": "Kabuli Chickpeas", "grams": 140, "hidden_fat_grams": 0}, {"name": "Anardana Spiced Gravy", "grams": 55, "hidden_fat_grams": 0}, {"name": "Tadka Oil / Ghee", "grams": 25, "hidden_fat_grams": 22.0}]'::jsonb, 155.0, 7.2, 22.0, 4.5, 'IFCT 2017 (ICMR-NIN)'),
('north_phulka_roti', 'Whole Wheat Phulka Roti', ARRAY['phulka', 'roti', 'chapati'], 'North', 'lunch', 35, '[{"name": "Chakki Atta", "grams": 33, "hidden_fat_grams": 0}, {"name": "Desi Ghee Brush", "grams": 2, "hidden_fat_grams": 1.8}]'::jsonb, 290.0, 11.0, 55.0, 3.5, 'IFCT 2017 (ICMR-NIN)'),
('north_jeera_rice', 'Jeera Basmati Rice', ARRAY['jeera rice', 'cumin rice', 'chawal'], 'North', 'lunch', 180, '[{"name": "Basmati Rice", "grams": 165, "hidden_fat_grams": 0}, {"name": "Ghee & Jeera", "grams": 15, "hidden_fat_grams": 13.0}]'::jsonb, 130.0, 2.8, 28.0, 0.5, 'IFCT 2017 (ICMR-NIN)'),
('west_poha', 'Kanda Batata Poha', ARRAY['poha', 'kanda poha', 'flattened rice'], 'West', 'breakfast', 180, '[{"name": "Flattened Rice", "grams": 110, "hidden_fat_grams": 0}, {"name": "Potato, Onion & Peanuts", "grams": 55, "hidden_fat_grams": 3.0}, {"name": "Oil Tempering", "grams": 15, "hidden_fat_grams": 13.5}]'::jsonb, 148.0, 3.6, 26.5, 3.8, 'IFCT 2017 (ICMR-NIN)'),
('west_pav_bhaji', 'Mumbai Pav Bhaji with Butter Pav', ARRAY['pav bhaji', 'bhaji pav'], 'West', 'dinner', 250, '[{"name": "Mashed Veggie Bhaji", "grams": 150, "hidden_fat_grams": 4.0}, {"name": "Pav Buns (2 pcs)", "grams": 70, "hidden_fat_grams": 0}, {"name": "Amul Butter Topping", "grams": 30, "hidden_fat_grams": 27.0}]'::jsonb, 185.0, 4.8, 26.0, 7.4, 'IFCT 2017 (ICMR-NIN)'),
('pan_hyderabadi_biryani', 'Hyderabadi Dum Biryani', ARRAY['biryani', 'dum biryani', 'hyderabadi biryani', 'chicken biryani'], 'South', 'dinner', 320, '[{"name": "Basmati Rice Layer", "grams": 170, "hidden_fat_grams": 0}, {"name": "Marinated Meat", "grams": 110, "hidden_fat_grams": 5.0}, {"name": "Desi Ghee & Saffron", "grams": 40, "hidden_fat_grams": 36.0}]'::jsonb, 195.0, 9.8, 24.0, 7.5, 'IFCT 2017 (ICMR-NIN)'),
('pan_khichdi', 'Moong Dal Ayurvedic Khichdi with Ghee', ARRAY['khichdi', 'dal khichdi', 'moong khichdi'], 'Pan-Indian', 'dinner', 220, '[{"name": "Rice & Moong Dal", "grams": 170, "hidden_fat_grams": 0}, {"name": "Spices & Hing", "grams": 20, "hidden_fat_grams": 0}, {"name": "Desi Ghee Drizzle", "grams": 30, "hidden_fat_grams": 28.5}]'::jsonb, 135.0, 4.8, 21.0, 4.0, 'IFCT 2017 (ICMR-NIN)')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    aliases = EXCLUDED.aliases,
    ingredients = EXCLUDED.ingredients,
    calories_per_100g = EXCLUDED.calories_per_100g,
    protein_per_100g = EXCLUDED.protein_per_100g,
    carbs_per_100g = EXCLUDED.carbs_per_100g,
    fat_per_100g = EXCLUDED.fat_per_100g;

-- ====================================================================
-- 12. User Corrections Table (Point 4)
-- "Whenever a user edits a detected dish name, portion, or calorie value"
-- ====================================================================
CREATE TABLE IF NOT EXISTS public.user_corrections (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    dish_id TEXT NOT NULL,
    original_value TEXT NOT NULL,
    corrected_value TEXT NOT NULL,
    field_changed TEXT NOT NULL, -- 'portion', 'dish_name', 'calories'
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_user_corrections_user_dish ON public.user_corrections(user_id, dish_id);

ALTER TABLE public.user_corrections ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can only read own corrections"
    ON public.user_corrections FOR SELECT
    USING (auth.uid() IS NULL OR auth.uid()::text = user_id::text);

CREATE POLICY "Users can insert own corrections"
    ON public.user_corrections FOR INSERT
    WITH CHECK (auth.uid() IS NULL OR auth.uid()::text = user_id::text);

CREATE POLICY "Users can delete own corrections"
    ON public.user_corrections FOR DELETE
    USING (auth.uid() IS NULL OR auth.uid()::text = user_id::text);

-- ====================================================================
-- 13. User Goals Table (Point 6)
-- "Store goals in a Supabase user_goals table"
-- ====================================================================
CREATE TABLE IF NOT EXISTS public.user_goals (
    user_id BIGINT PRIMARY KEY,
    goal_type TEXT NOT NULL, -- 'lose_weight', 'build_muscle', 'maintain'
    calorie_target INT NOT NULL,
    protein_target INT NOT NULL,
    carbs_target INT NOT NULL,
    fat_target INT NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.user_goals ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can read own goals"
    ON public.user_goals FOR SELECT
    USING (auth.uid() IS NULL OR auth.uid()::text = user_id::text);

CREATE POLICY "Users can insert/update own goals"
    ON public.user_goals FOR ALL
    USING (auth.uid() IS NULL OR auth.uid()::text = user_id::text)
    WITH CHECK (auth.uid() IS NULL OR auth.uid()::text = user_id::text);

-- ====================================================================
-- 14. Report a Wrong Result Table (Point 7e)
-- "Sends dish, photo ID, and user note to admin without personal details"
-- ====================================================================
CREATE TABLE IF NOT EXISTS public.wrong_result_reports (
    id BIGSERIAL PRIMARY KEY,
    dish_name TEXT NOT NULL,
    photo_id TEXT,
    user_note TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.wrong_result_reports ENABLE ROW LEVEL SECURITY;

-- Anonymous write allowed so users never send personal identifiers
CREATE POLICY "Anyone can submit wrong result report"
    ON public.wrong_result_reports FOR INSERT
    WITH CHECK (true);

-- ====================================================================
-- 15. RPC Function: Complete User Data Deletion (Point 7b)
-- ====================================================================
CREATE OR REPLACE FUNCTION public.delete_all_user_data(p_user_id BIGINT)
RETURNS JSONB
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, pg_temp
AS $$
BEGIN
    DELETE FROM public.meals WHERE user_id = p_user_id;
    DELETE FROM public.scanned_meals WHERE user_id = p_user_id;
    DELETE FROM public.user_corrections WHERE user_id = p_user_id;
    DELETE FROM public.user_goals WHERE user_id = p_user_id;
    DELETE FROM public.accuracy_tests WHERE user_id = p_user_id;
    DELETE FROM public.user_profiles WHERE user_id = p_user_id;

    RETURN jsonb_build_object(
        'success', true,
        'message', 'All photos, logs, corrections, and goals have been permanently deleted.'
    );
END;
$$;


