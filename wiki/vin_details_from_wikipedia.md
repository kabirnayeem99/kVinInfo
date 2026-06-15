# Vehicle Identification Number (VIN)

## Table of Contents

- [Vehicle Identification Number (VIN)](#vehicle-identification-number-vin)
  - [Table of Contents](#table-of-contents)
  - [1. Overview](#1-overview)
  - [2. History](#2-history)
    - [2.1 Early Standards (1954-1981)](#21-early-standards-1954-1981)
    - [2.2 Modern Standards (1981+)](#22-modern-standards-1981)
  - [3. VIN Structure](#3-vin-structure)
    - [3.1 Regional Standards](#31-regional-standards)
    - [3.2 Position 1-3: World Manufacturer Identifier (WMI)](#32-position-1-3-world-manufacturer-identifier-wmi)
      - [WMI Regional Prefixes](#wmi-regional-prefixes)
    - [3.3 Position 4-9: Vehicle Descriptor Section (VDS)](#33-position-4-9-vehicle-descriptor-section-vds)
      - [Position 9: Check Digit](#position-9-check-digit)
    - [3.4 Position 10-17: Vehicle Identifier Section (VIS)](#34-position-10-17-vehicle-identifier-section-vis)
      - [Position 10: Model Year](#position-10-model-year)
    - [April 30, 2008 Rule Changes](#april-30-2008-rule-changes)
      - [Position 11: Assembly Plant](#position-11-assembly-plant)
      - [Positions 12-17: Serial Number](#positions-12-17-serial-number)
  - [4. Check Digit Validation](#4-check-digit-validation)
    - [4.1 Transliteration](#41-transliteration)
    - [4.2 Weight Factor Table](#42-weight-factor-table)
    - [4.3 Example Check Digit Calculation](#43-example-check-digit-calculation)
    - [4.4 Special Case: Straight Ones](#44-special-case-straight-ones)
  - [5. VIN Locations](#5-vin-locations)
  - [6. World Manufacturer Identifiers (WMI) by Region](#6-world-manufacturer-identifiers-wmi-by-region)
    - [6.1 Africa](#61-africa)
      - [South Africa](#south-africa)
      - [Other African Countries](#other-african-countries)
    - [6.2 Asia](#62-asia)
      - [Japan](#japan)
      - [China](#china)
      - [South Korea](#south-korea)
      - [India](#india)
      - [Other Asian Countries](#other-asian-countries)
    - [6.3 Europe](#63-europe)
      - [United Kingdom](#united-kingdom)
      - [Germany](#germany)
      - [France](#france)
      - [Spain](#spain)
      - [Italy](#italy)
      - [Other European Countries](#other-european-countries)
    - [6.4 North America](#64-north-america)
      - [United States - Major Manufacturers](#united-states---major-manufacturers)
      - [Canada](#canada)
      - [Mexico](#mexico)
    - [6.5 South America](#65-south-america)
      - [Brazil](#brazil)
      - [Argentina](#argentina)
      - [Other South American Countries](#other-south-american-countries)
    - [6.6 Oceania](#66-oceania)
      - [Australia](#australia)
      - [New Zealand](#new-zealand)
  - [7. Quick Reference](#7-quick-reference)
    - [VIN Structure at a Glance](#vin-structure-at-a-glance)
    - [Key Forbidden Characters](#key-forbidden-characters)
    - [Check Digit Rules (North America/China)](#check-digit-rules-north-americachina)
    - [Model Year Codes (Quick Lookup)](#model-year-codes-quick-lookup)
    - [Regional WMI Prefixes](#regional-wmi-prefixes)

---

## 1. Overview

A **vehicle identification number (VIN)**, also called a **chassis number** or **frame number**, is a unique code including a serial number used by the automotive industry to identify individual motor vehicles, towed vehicles, motorcycles, scooters, and mopeds. VINs are defined by the International Organization for Standardization in ISO 3779 (content and structure) and ISO 4030 (location and attachment).

There are vehicle history services in several countries that help potential car owners use VINs to find vehicles that are defective or have been written off.

## 2. History

### 2.1 Early Standards (1954-1981)

VINs were first used in 1954 in the United States. From 1954 to 1965, there was no accepted standard for these numbers, so different manufacturers and even divisions within a manufacturer used different formats. Many were little more than a serial number.

Starting in January 1966, the US government mandated that a 13-character VIN be used. This specification was phased in over several years:

- US manufacturers used them starting in January 1966
- By January 1, 1969, all cars sold in the US were required to have the 13-character VIN

The 1966 US specification stated only that the year of manufacture, the engine type, and a unique six-digit number (making up the last six characters) were required. Individual manufacturers could use the remaining five spaces for whatever they liked. This was not much better than what was in use by some US manufacturers before 1966.

### 2.2 Modern Standards (1981+)

In 1981, the National Highway Traffic Safety Administration of the United States standardized the format. It required all on-road vehicles sold to contain a **17-character VIN**, which does not include the letters **O (o), I (i), and Q (q)** to avoid confusion with numerals 0, 1, and 9. This was largely based on the ISO 3779 standard, but is more stringent.

After the introduction of the ISO standard, manufacturers which produced vehicles for the American market very quickly adjusted to this standard. ISO introduced recommendations for applying the VIN standard and its structure, and the VIN was also used in Europe. However, the sets of information contained in it were introduced gradually:

- Volkswagen started encoding bigger chunks of information during 1995–1997
- The control digit was encoded during 2009–2015 for selected models from the group
- The VIN control digit is also used, although not in all brand-models (e.g., Audi A1 in European vehicles)

There are at least four competing standards used to calculate the VIN.

Modern VINs are based on two related standards, originally issued by the International Organization for Standardization (ISO) in 1979 and 1980:

- ISO 3779
- ISO 3780

Compatible but different implementations of these ISO standards have been adopted by the European Union and the United States.

## 3. VIN Structure

The VIN consists of 17 characters, using only capital letters (excluding I, O, and Q) and digits (0-9).

### 3.1 Regional Standards

The VIN structure varies slightly by region:

| Positions | Metric                              |
| --------- | ----------------------------------- |
| 1–3       | World Manufacturer Identifier (WMI) |
| 4–9       | Vehicle Descriptor Section (VDS)    |
| 10–17     | Vehicle Identifier Section (VIS)    |

**European Union (>500 vehicles/year):**

- Positions 1–3: World manufacturer identifier
- Positions 4–9: General characteristics of the vehicle
- Positions 10–17: Clear identification of a particular vehicle

**North America (>2,000 vehicles/year):**

- Positions 1–3: World manufacturer identifier
- Positions 4–8: Vehicle attributes
- Position 9: Check digit
- Position 10: Model year
- Position 11: Plant code
- Positions 12–17: Sequential number

### 3.2 Position 1-3: World Manufacturer Identifier (WMI)

The first three characters uniquely identify the manufacturer of the vehicle using the world manufacturer identifier or WMI code.

A manufacturer who builds fewer than 1,000 vehicles per year uses a **9 as the third digit**, and positions 12–14 of the VIN are used for a second part of the identification.

Some manufacturers use the third character as a code for:

- A vehicle category (e.g., bus or truck)
- A division within a manufacturer
- Or both

**Example (General Motors in the US):**

- 1G1 = Chevrolet passenger cars
- 1G2 = Pontiac passenger cars
- 1GC = Chevrolet trucks

The Society of Automotive Engineers (SAE) in the US assigns WMIs to countries and manufacturers.

#### WMI Regional Prefixes

The first character of the WMI is typically the region in which the manufacturer is located:

| Region        | Codes  |
| ------------- | ------ |
| Africa        | A–C    |
| Asia          | H–R    |
| Europe        | E, S–Z |
| North America | 1–5, 7 |
| Oceania       | 6      |
| South America | 8–9    |

**Important Notes:**

- In practice, each is assigned to a country of manufacture
- In Europe, the country where the continental headquarters is located can assign the WMI to all vehicles produced in that region
- Company mergers and acquisitions can lead to seemingly confusing allocations
- Assignment policies are sometimes for unpublished reasons

### 3.3 Position 4-9: Vehicle Descriptor Section (VDS)

The fourth to ninth positions are the vehicle descriptor section. This is used, according to local regulations, to identify:

- Vehicle type
- Automobile platform used
- Model
- Body style

Each manufacturer has a unique system for using this field.

Most manufacturers since the 1980s have used the eighth digit to identify the engine type whenever there is more than one engine choice for the vehicle.

**Example (2007 Chevrolet Corvette C6):**

- U = 6.0-liter V8 engine
- E = 7.0-liter V8

#### Position 9: Check Digit

One element that is inconsistent is the use of position nine as a check digit. It is:

- **Compulsory** for vehicles in North America and China
- **Not required** in Europe

### 3.4 Position 10-17: Vehicle Identifier Section (VIS)

The 10th to 17th positions are used as the vehicle identifier section. This is used by the manufacturer to identify the individual vehicle in question. This may include:

- Information on options installed
- Engine and transmission choices
- Often just a simple sequential number

#### Position 10: Model Year

The North American implementation uses the 10th digit to encode the **model year** of the vehicle.

For the model year code:

- Besides the three letters not allowed in a VIN itself (I, O, Q), the letters **U and Z** and the digit **0** are not used for the model year code
- Outside of North America, the 10th digit is usually 0

**Model Year Code Table:**

| Year | Code | Year | Code | Year | Code | Year | Code |
| ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
| 1980 | A    | 1990 | L    | 2000 | Y    | 2010 | A    |
| 1981 | B    | 1991 | M    | 2001 | 1    | 2011 | B    |
| 1982 | C    | 1992 | N    | 2002 | 2    | 2012 | C    |
| 1983 | D    | 1993 | P    | 2003 | 3    | 2013 | D    |
| 1984 | E    | 1994 | R    | 2004 | 4    | 2014 | E    |
| 1985 | F    | 1995 | S    | 2005 | 5    | 2015 | F    |
| 1986 | G    | 1996 | T    | 2006 | 6    | 2016 | G    |
| 1987 | H    | 1997 | V    | 2007 | 7    | 2017 | H    |
| 1988 | J    | 1998 | W    | 2008 | 8    | 2018 | J    |
| 1989 | K    | 1999 | X    | 2009 | 9    | 2019 | K    |
| 2020 | L    | 2030 | Y    |
| 2021 | M    | 2031 | 1    |
| 2022 | N    | 2032 | 2    |
| 2023 | P    | 2033 | 3    |
| 2024 | R    | 2034 | 4    |
| 2025 | S    | 2035 | 5    |
| 2026 | T    | 2036 | 6    |
| 2027 | V    | 2037 | 7    |
| 2028 | W    | 2038 | 8    |
| 2029 | X    | 2039 | 9    |

**History Note:** The year 1980 was encoded by some manufacturers (especially General Motors and Chrysler) as "A" (since the 17-digit VIN was not mandatory until 1981, and the "A" or zero was in the manufacturer's pre-1981 placement in the VIN). However, Ford and AMC still used a zero for 1980. Subsequent years increment through the allowed letters, so "Y" represents the year 2000. 2001 to 2009 are encoded as digits 1 to 9, and subsequent years are encoded as "A", "B", "C", etc.

### April 30, 2008 Rule Changes

On April 30, 2008, the US National Highway Traffic Safety Administration adopted a final rule amending 49 CFR Part 565, allowing the 17-character VIN system to continue in use for at least another 30 years.

**Three notable changes to VIN structure that affect VIN deciphering systems:**

1. **Make identification:** The make may only be identified after looking at positions one through three and another position, as determined by the manufacturer in the second section (positions 4–8) of the VIN.

2. **Model year identification:** To identify the exact year in passenger cars and multipurpose passenger vehicles with a GVWR of 10,000 or less, one must read position 7 as well as position 10.
   - For passenger cars and MPVs/trucks with GVWR ≤ 10,000 lbs
   - If position 7 is numeric: model year in position 10 refers to 1980–2009
   - If position 7 is alphabetic: model year in position 10 refers to 2010–2039

3. **Heavy vehicles:** The model year for vehicles with GVWR >10,000 lbs, as well as buses, motorcycles, trailers, and low-speed vehicles, may no longer be identified within a 30-year range. VIN characters 1–8 and 10 that were assigned from 1980 to 2009 can be repeated beginning with the 2010 model year.

#### Position 11: Assembly Plant

Compulsory in North America and China is the use of the 11th character to identify the **assembly plant** at which the vehicle was built. Each manufacturer has its own set of plant codes.

#### Positions 12-17: Serial Number

In the United States and China, the 12th to 17th digits are the vehicle's serial or production number. This is unique to each vehicle, and every manufacturer uses its own sequence.

## 4. Check Digit Validation

A check-digit validation is used for all road vehicles sold in the United States and Canada.

When trying to validate a VIN with a check digit:

1. Either (a) remove the check digit for the purpose of calculation, or (b) use a weight of zero to cancel it out
2. Compare the original value of the check digit with the calculated value
3. **If the calculated value is 0–9:** the check digit must match the calculated value
4. **If the calculated value is 10:** the check digit must be X
5. **If the two values do not match:** there is a mistake in the VIN

**Important:** A match does not prove the VIN is correct, because there is still a 1/11 chance that any two distinct VINs have a matching check digit.

### 4.1 Transliteration

Transliteration consists of removing all of the letters and replacing them with their appropriate numerical counterparts based on IBM's EBCDIC:

| A   | 1   | B   | 2   | C   | 3   | D   | 4   | E   | 5   | F   | 6   | G   | 7   | H   | 8   |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| J   | 1   | K   | 2   | L   | 3   | M   | 4   | N   | 5   | P   | 7   | R   | 9   |
| S   | 2   | T   | 3   | U   | 4   | V   | 5   | W   | 6   | X   | 7   | Y   | 8   | Z   | 9   |

**Notes:**

- I, O, and Q are not allowed in a valid VIN
- Numerical digits use their own values
- S is 2, and not 1
- There is no left-alignment linearity

### 4.2 Weight Factor Table

The following is the weight factor for each position in the VIN. The 9th position is that of the check digit. It has been substituted with a 0, which will cancel it out in the multiplication step.

| Position | 1   | 2   | 3   | 4   | 5   | 6   | 7   | 8   | 9   | 10  | 11  | 12  | 13  | 14  | 15  | 16  | 17  |
| -------- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Weight   | 8   | 7   | 6   | 5   | 4   | 3   | 2   | 10  | 0   | 9   | 8   | 7   | 6   | 5   | 4   | 3   | 2   |

### 4.3 Example Check Digit Calculation

Consider the hypothetical VIN **1M8GDM9A_KP042788**, where the underscore will be the check digit.

| Position     | 1   | M   | 8   | G   | D   | M   | 9   | A   | K   | P   | 0   | 4   | 2   | 7   | 8   | 8   | \_  |
| ------------ | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| **Value**    | 1   | 4   | 8   | 7   | 4   | 4   | 9   | 1   | 0   | 2   | 7   | 0   | 4   | 2   | 7   | 8   | 8   |
| **Weight**   | 8   | 7   | 6   | 5   | 4   | 3   | 2   | 10  | 0   | 9   | 8   | 7   | 6   | 5   | 4   | 3   | 2   |
| **Products** | 8   | 28  | 48  | 35  | 16  | 12  | 18  | 10  | 0   | 18  | 56  | 0   | 24  | 10  | 28  | 24  | 16  |

**Calculation Steps:**

1. The VIN's value is calculated from the transliteration table. This number is used in the rest of the calculation.
2. Copy the weights from the weight factor row.
3. The products row is the result of the multiplication of the columns in the Value and Weight rows.
4. The products (8, 28, 48, 35 ... 24, 16) are all added together to yield a sum: **351**
5. Find the remainder after dividing by 11:
   - 351 ÷ 11 = 31 with remainder 10
   - **351 MOD 11 = 10**
6. The remainder is the check digit. If the remainder is 10, the check digit is **X**. In this example, the remainder is 10, so the check digit is X.

**Result:** With a check digit of X, the VIN **1M8GDM9A_KP042788** is written as **1M8GDM9AXKP042788**.

### 4.4 Special Case: Straight Ones

A VIN with straight-ones (seventeen consecutive 1s) has the nice feature that its check digit 1 matches the calculated value 1:

- A value of one multiplied by 89 (sum of weights) is 89
- 89 divided by 11 is 8 with remainder 1
- Thus, 1 is the check digit

This is a useful way to test a VIN-check algorithm.

## 5. VIN Locations

The VIN is marked in multiple locations:

- **Lower corner of the windscreen** on the driver's side (normally)
- **Under the bonnet** next to the latch
- **Front end of the vehicle frame**
- **Inside the door pillar** on the driver's side

On newer vehicles, VINs may be:

- **Optically read** with barcode scanners or digital cameras
- **Digitally read** via OBD-II
- **Accessed** through smartphone applications that pass the VIN to websites to decode it

## 6. World Manufacturer Identifiers (WMI) by Region

The Society of Automotive Engineers (SAE) assigns the WMI to countries and manufacturers. Below is a comprehensive list of world manufacturer codes organized by region.

### 6.1 Africa

#### South Africa

- **AAA:** Audi South Africa (made by Volkswagen of South Africa)
- **AAK:** FAW Vehicle Manufacturers SA
- **AAM:** MAN Automotive (South Africa)
- **AAV:** Volkswagen of South Africa
- **ABJ:** Mitsubishi (made by Mercedes-Benz/Daimler)
- **ABM:** BMW Southern Africa
- **ACV:** Isuzu Motors South Africa
- **AC5:** Hyundai Automotive South Africa
- **ADD:** UD Trucks Southern Africa
- **ADM:** General Motors South Africa
- **ADN:** Nissan South Africa
- **ADR:** Renault Sandero (made by Nissan South Africa)
- **ADX:** Tata Automobile Corporation

#### Other African Countries

- **BAB:** BMW (Kenya)
- **BF9:** KIBO Motorcycles (Kenya)
- **CL9:** Wallyscar (Tunisia)
- **CAG/CAH:** MAC for Mobility Manufacturing (Egypt)
- **DF9:** Laraki (Morocco)
- **GA1:** Renault/SOMACOA (Madagascar)

### 6.2 Asia

#### Japan

Japan has an extensive manufacturer list starting with **J** prefix codes.

#### China

China has the largest automotive manufacturing base with **L** prefix codes.

#### South Korea

- **KLA:** Daewoo/GM Korea
- **KMA:** Hyundai Motor
- **KNA/KNC/KNE:** Kia
- **KPA:** KG Mobility

#### India

- **MA1:** Mahindra
- **MA3:** Maruti Suzuki
- **MA6:** General Motors India
- **MAT:** Tata Motors

#### Other Asian Countries

- **MH1:** Honda (Indonesia)
- **ML0:** Ducati Motor (Thailand)
- **MNA:** Ford (Malaysia export)
- **PL1:** Proton (Malaysia)
- **RF3:** Aeon Motor (Taiwan)
- **RL0:** Ford (Vietnam)

### 6.3 Europe

#### United Kingdom

- **SAA:** Austin vehicles
- **SAJ:** Jaguar
- **SAL:** Land Rover
- **SAR:** Rover/MG
- **SCC:** Lotus Cars
- **SCF:** Aston Martin
- **SDB:** Talbot
- **SFA:** Ford of Britain
- **SHH:** Honda UK Manufacturing

#### Germany

- **WAC:** Audi/Porsche RS 2
- **WAG:** Neoplan
- **WBA:** BMW
- **WDB:** Mercedes-Benz
- **WVW:** Volkswagen

#### France

- **VF1:** Renault
- **VF3:** Peugeot
- **VF7:** Citroën
- **VFA:** Alpine

#### Spain

- **VS1:** Pegaso
- **VS5:** Renault España
- **VS6:** Ford España
- **VS7:** Citroën
- **VS8:** Peugeot
- **VSS:** SEAT

#### Italy

- **ZAF:** Fiat
- **ZAM:** Maserati
- **ZFF:** Ferrari
- **ZLA:** Lancia

#### Other European Countries

- **TA9:** SAE (Switzerland)
- **TMB:** Škoda (Czech Republic)
- **TRA:** Ikarus (Hungary)
- **UU1:** Dacia (Romania)
- **VAG:** Steyr (Austria)
- **VBK:** KTM (Austria)
- **XBB:** Great Wall Motors (Bulgaria)
- **XTA:** AvtoVAZ (Russia)
- **YAR:** Toyota (Belgium)
- **YH2:** Lynx (Finland)
- **YS2:** Scania (Sweden)
- **YV1:** Volvo (Sweden)

### 6.4 North America

#### United States - Major Manufacturers

- **1A4/1A8:** Chrysler MPV/SUV
- **1B3:** Dodge car
- **1B7:** Dodge truck
- **1C3:** Chrysler car
- **1F:** Ford
- **1G:** General Motors
- **1H:** Honda (motorcycle/ATV)
- **1N:** Nissan
- **1V:** Volkswagen
- **1Y:** Chevrolet/Geo

#### Canada

- **2A3:** Imperial
- **2B3:** Dodge car
- **2C3:** Chrysler car
- **2F:** Ford
- **2G:** General Motors
- **2H:** Honda

#### Mexico

- **3A4:** Chrysler MPV
- **3B3:** Dodge car
- **3C3:** Chrysler car
- **3F:** Ford
- **3G:** General Motors

### 6.5 South America

#### Brazil

- **9BD:** Fiat
- **9BF:** Ford
- **9BG:** General Motors (Chevrolet)
- **9BR:** Toyota
- **9BW:** Volkswagen

#### Argentina

- **8AC:** Mercedes-Benz
- **8AD:** Peugeot
- **8AF:** Ford
- **8AG:** General Motors

#### Other South American Countries

- **8F9:** Reborn (Chile)
- **8L4:** Great Wall Motors (Ecuador)
- **8XD:** Ford (Venezuela)
- **829:** Quantum Motors (Bolivia)

### 6.6 Oceania

#### Australia

- **6F1:** Ford
- **6G:** General Motors/Holden
- **6MM:** Mitsubishi
- **6T1:** Toyota

#### New Zealand

- **7A1:** Mitsubishi
- **7A3:** Honda
- **7A4:** Toyota
- **7A5:** Ford

## 7. Quick Reference

### VIN Structure at a Glance

| Section     | Positions | Purpose            | Example     |
| ----------- | --------- | ------------------ | ----------- |
| WMI         | 1-3       | Manufacturer ID    | 1G1 (Chevy) |
| VDS         | 4-9       | Vehicle attributes | GDM9A       |
| Check Digit | 10 (NA)   | Validation         | X           |
| Model Year  | 10        | Year code          | P (2023)    |
| Plant Code  | 11 (NA)   | Assembly plant     | K           |
| Serial      | 12-17     | Unique ID          | P042788     |

### Key Forbidden Characters

- **I, O, Q** - Never appear in VINs (confusion with 1, 0, 9)

### Check Digit Rules (North America/China)

- **0-9:** Must match calculated value
- **10:** Check digit is **X**
- 1/11 false positive chance even if valid

### Model Year Codes (Quick Lookup)

| 2020-2029                                | 2030-2039               |
| ---------------------------------------- | ----------------------- |
| L(2020), M, N, P, R, S, T, V, W, X(2029) | Y(2030), 1-9(2031-2039) |

### Regional WMI Prefixes

- **1-5, 7:** North America
- **A-C:** Africa
- **H-R:** Asia
- **E, S-Z:** Europe
- **6:** Oceania
- **8-9:** South America
