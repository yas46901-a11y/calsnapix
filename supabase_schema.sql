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

-- 6. Row Level Security (RLS)
ALTER TABLE public.user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.meals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.scanned_meals ENABLE ROW LEVEL SECURITY;

-- Allow public access with anon key for mobile app client
CREATE POLICY "Allow anon read user_profiles" ON public.user_profiles FOR SELECT USING (true);
CREATE POLICY "Allow anon insert user_profiles" ON public.user_profiles FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow anon update user_profiles" ON public.user_profiles FOR UPDATE USING (true);
CREATE POLICY "Allow anon delete user_profiles" ON public.user_profiles FOR DELETE USING (true);

CREATE POLICY "Allow anon read meals" ON public.meals FOR SELECT USING (true);
CREATE POLICY "Allow anon insert meals" ON public.meals FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow anon update meals" ON public.meals FOR UPDATE USING (true);
CREATE POLICY "Allow anon delete meals" ON public.meals FOR DELETE USING (true);

CREATE POLICY "Allow anon read scanned_meals" ON public.scanned_meals FOR SELECT USING (true);
CREATE POLICY "Allow anon insert scanned_meals" ON public.scanned_meals FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow anon update scanned_meals" ON public.scanned_meals FOR UPDATE USING (true);
CREATE POLICY "Allow anon delete scanned_meals" ON public.scanned_meals FOR DELETE USING (true);

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
