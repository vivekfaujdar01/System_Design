# 🔗 System Design: URL Shortener (TinyURL / bit.ly)

> **Real-world examples**: TinyURL, bit.ly, short.io, rebrandly.com

---

## 📋 Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Requirements Gathering](#2-requirements-gathering)
3. [Capacity Estimation](#3-capacity-estimation)
4. [High-Level Design](#4-high-level-design)
5. [Core Design Decisions](#5-core-design-decisions)
   - [Short URL Generation Strategies](#51-short-url-generation-strategies)
   - [Database Design](#52-database-design)
   - [Caching Layer](#53-caching-layer)
   - [Redirection Mechanism](#54-redirection-mechanism)
6. [Detailed Component Design](#6-detailed-component-design)
7. [Data Flow Walkthrough](#7-data-flow-walkthrough)
8. [Scalability & Deep Dives](#8-scalability--deep-dives)
9. [Edge Cases & Failure Scenarios](#9-edge-cases--failure-scenarios)
10. [Monitoring & Observability](#10-monitoring--observability)
11. [Trade-offs Summary](#11-trade-offs-summary)
12. [Interview Tips](#12-interview-tips)

---

## 1. Problem Statement

Design a URL shortening service that:
- Takes a **long URL** as input and returns a unique **short URL** (e.g., `https://short.ly/aB3kQ2`)
- When a user visits the short URL, they are **instantly redirected** to the original long URL
- The service must work **reliably at massive scale** (billions of redirects per day)

### Real-World Motivation

Long URLs are:
- Hard to share on platforms with character limits (Twitter/X: 280 chars)
- Ugly and non-memorable in print/ads
- Difficult to track engagement

Short URLs solve all three problems + provide analytics.

---

## 2. Requirements Gathering

> In an interview, always start by clarifying requirements before jumping into design.

### ✅ Functional Requirements

| # | Requirement | Priority |
|---|---|---|
| 1 | Given a long URL, generate a unique short URL | **Core** |
| 2 | Given a short URL, redirect to the original long URL | **Core** |
| 3 | Custom aliases (e.g., `short.ly/my-product`) | Optional |
| 4 | Link expiry / TTL (e.g., expire after 30 days) | Optional |
| 5 | Analytics: click count, geolocation, device type, referrer | Optional |
| 6 | User accounts to manage their links | Optional |
| 7 | QR code generation for short URLs | Optional |

### ❌ Non-Functional Requirements

| # | Requirement | Target |
|---|---|---|
| 1 | **High Availability** — system must always redirect | 99.99% uptime |
| 2 | **Low Latency** — redirects must be very fast | < 10ms p99 |
| 3 | **Scalability** — handle millions of new URLs + billions of redirects | See estimates below |
| 4 | **Durability** — short URLs must not be lost | No data loss |
| 5 | **Consistency** — same short URL should always resolve to the same original URL | Eventual is OK for reads |
| 6 | **Security** — no malicious URLs; no unauthorized deletion | Basic validation |

### 🚫 Out of Scope (for this discussion)

- Full analytics dashboard (complex ML/OLAP)
- A/B testing with URLs
- Team/workspace management
- Browser extensions

---

## 3. Capacity Estimation

> Back-of-envelope estimation helps justify design choices and set scale expectations.

### Assumptions

| Parameter | Value |
|---|---|
| New URLs created per day | **100 Million** |
| Read:Write ratio | **100:1** (mostly reads/redirects) |
| Average long URL length | ~200 bytes |
| Average metadata per record | ~500 bytes total |
| Data retention | **5 years** |
| Short code length | **7 characters** |

### Write QPS (URL Creation)

```
100 Million URLs/day ÷ 86,400 sec/day ≈ 1,160 writes/sec
Peak writes (10x) ≈ 11,600 writes/sec
```

### Read QPS (Redirects)

```
Redirects = 100:1 ratio × 100M = 10 Billion/day
10 Billion ÷ 86,400 ≈ 115,000 reads/sec
Peak reads (10x) ≈ 1,150,000 reads/sec
```

### Storage Estimation

```
Per URL record:
  - short_code: 7 bytes
  - long_url: ~200 bytes
  - metadata (user_id, created_at, expiry, clicks): ~300 bytes
  Total: ~500 bytes/record

Over 5 years:
  100M URLs/day × 365 days × 5 years = 182.5 Billion URLs
  182.5B × 500 bytes ≈ 91.25 TB of raw storage
```

### Bandwidth Estimation

```
Write bandwidth:
  1,160 writes/sec × 500 bytes ≈ 580 KB/s

Read bandwidth (response = HTTP 301/302 redirect ~500 bytes):
  115,000 reads/sec × 500 bytes ≈ 57.5 MB/s
```

### URL Namespace Estimation

```
Base62 charset: a-z (26) + A-Z (26) + 0-9 (10) = 62 characters
6-character code: 62^6 = 56.8 Billion unique codes
7-character code: 62^7 = 3.5 Trillion unique codes  ← Sufficient for 5 years
```

**Decision**: Use **7-character Base62 codes** to ensure we never run out.

---

## 4. High-Level Design

### Architecture Diagram

```
                         ┌────────────────────────────────────────────────┐
                         │                  CLIENT                         │
                         │  (Browser / Mobile App / API Consumer)         │
                         └──────────────────┬─────────────────────────────┘
                                            │ HTTPS
                                            ▼
                         ┌──────────────────────────────────────────────┐
                         │              LOAD BALANCER                    │
                         │         (Nginx / AWS ALB / HAProxy)           │
                         └──────┬─────────────────────────┬─────────────┘
                                │                         │
                    ┌───────────▼───────────┐  ┌──────────▼──────────────┐
                    │  URL CREATION API     │  │  URL REDIRECT API        │
                    │  (Write Service)      │  │  (Read Service)          │
                    └───────────┬───────────┘  └──────────┬──────────────┘
                                │                         │
                    ┌───────────▼──────┐       ┌──────────▼──────────────┐
                    │ KEY GENERATION   │       │  CACHE LAYER             │
                    │ SERVICE (KGS)    │       │  (Redis Cluster)         │
                    └───────────┬──────┘       └──────────┬──────────────┘
                                │                         │ (cache miss)
                    ┌───────────▼─────────────────────────▼──────────────┐
                    │                   DATABASE                          │
                    │         (Cassandra / DynamoDB / PostgreSQL)         │
                    └─────────────────────────────────────────────────────┘
                                            │
                    ┌───────────────────────▼─────────────────────────────┐
                    │               ANALYTICS SERVICE (Optional)           │
                    │          (Kafka → ClickHouse / BigQuery)             │
                    └─────────────────────────────────────────────────────┘
```

### Core API Endpoints

#### POST /api/shorten
Creates a new short URL.

```
Request:
POST /api/shorten
Content-Type: application/json
Authorization: Bearer <token>  (optional)

{
  "long_url": "https://www.example.com/some/very/long/path?utm_source=newsletter",
  "custom_alias": "my-promo",    // optional
  "expiry_days": 30              // optional
}

Response: 201 Created
{
  "short_url": "https://short.ly/aB3kQ2m",
  "short_code": "aB3kQ2m",
  "long_url": "https://www.example.com/...",
  "created_at": "2026-07-19T00:00:00Z",
  "expires_at": "2026-08-18T00:00:00Z"
}
```

#### GET /{short_code}
Resolves a short URL and redirects.

```
Request:
GET /aB3kQ2m
Host: short.ly

Response: 301 Moved Permanently  (or 302 Found for tracking)
Location: https://www.example.com/some/very/long/path?utm_source=newsletter
```

#### DELETE /api/shorten/{short_code}
Deletes a short URL (owner only).

```
Request:
DELETE /api/shorten/aB3kQ2m
Authorization: Bearer <token>

Response: 204 No Content
```

#### GET /api/shorten/{short_code}/stats
Returns analytics for a short URL.

```
Response: 200 OK
{
  "short_code": "aB3kQ2m",
  "total_clicks": 15204,
  "unique_visitors": 10843,
  "clicks_by_country": { "IN": 5000, "US": 4000, ... },
  "clicks_by_device": { "mobile": 8000, "desktop": 7000 },
  "clicks_last_7_days": [...]
}
```

---

## 5. Core Design Decisions

### 5.1 Short URL Generation Strategies

This is the **most critical** design decision. There are four main approaches:

---

#### Approach 1: Hash-Based (MD5/SHA-256)

**How it works:**
1. Take the long URL as input.
2. Compute `MD5(long_url)` → 128-bit hash → 32 hex characters.
3. Take the first 7 characters as the short code.
4. Base62-encode it.

```
long_url = "https://www.example.com/path"
MD5 → "5f4dcc3b5aa765d61d8327deb882cf99"
Take first 7 → "5f4dcc3"
                ↓
Short code: "5f4dcc3"
```

**Pros:**
- Simple, stateless — no coordination required
- Same long URL always produces same hash (natural deduplication)

**Cons:**
- **Hash collisions** — two different URLs may produce same 7-char prefix
- Collision handling requires DB lookup on every creation (slow under heavy load)
- Not randomized — predictable short codes

**Collision Resolution:**
```
If short_code already exists in DB:
  → Append a salt (e.g., incrementing counter) to the long URL
  → Re-hash until a unique code is found
```

**Verdict**: Simple, but collision risk grows as namespace fills. Not recommended at massive scale.

---

#### Approach 2: Counter-Based (Auto-Increment ID → Base62)

**How it works:**
1. Use a globally auto-incrementing counter (or DB primary key).
2. Convert the integer to Base62.

```
ID: 1,000,000
Base62 → "4c92" (4 characters)

ID: 1,000,000,000
Base62 → "15ftgG" (6 characters)
```

**Base62 Encoding Algorithm:**
```python
CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

def encode_base62(n):
    result = []
    while n > 0:
        result.append(CHARSET[n % 62])
        n //= 62
    return ''.join(reversed(result))
```

**Pros:**
- No collisions — each ID is unique
- Simple and fast

**Cons:**
- **Single point of failure**: If the counter service goes down, URL creation stops
- **Sequential codes** — predictable, easy to enumerate (security concern)
- Distributed counter is complex (ZooKeeper or DB needed)

**Distributed Counter Options:**
- Use database `AUTO_INCREMENT` with a single-writer DB
- Use **range-based partitioning** (Server 1 gets 1–10M, Server 2 gets 10M–20M)
- Use **Twitter Snowflake** (64-bit timestamp + machine ID + sequence = unique IDs)

**Verdict**: Good approach but needs careful distributed ID generation.

---

#### Approach 3: Key Generation Service (KGS) ⭐ Recommended

**How it works:**
1. A dedicated **Key Generation Service (KGS)** pre-generates millions of random 7-char Base62 keys.
2. Keys are stored in a database with two states: `unused` and `used`.
3. When a new URL needs to be created:
   - Application requests a key from KGS.
   - KGS returns a pre-generated unused key.
   - KGS atomically marks it as `used`.
4. KGS can also load keys into in-memory buffers for faster serving.

```
KGS Database Schema:
┌─────────────┬──────────┐
│  short_code │  status  │
├─────────────┼──────────┤
│  aB3kQ2m    │  unused  │
│  xY9pL4n    │  unused  │
│  mK7rZ1q    │  used    │
│  ...        │  ...     │
└─────────────┴──────────┘
```

**KGS Memory Buffer:**
- KGS pre-loads **batch of keys** (e.g., 1000 at a time) into RAM.
- Any unused keys in memory at the time of a crash are lost, but that's acceptable.
- If KGS dies, a backup KGS takes over (no keys are double-assigned since DB is the source of truth).

**Pros:**
- No collisions — keys are pre-validated as unique
- Very fast — just a memory lookup
- Decoupled — URL service doesn't need to think about key generation

**Cons:**
- KGS is a potential single point of failure → mitigate with KGS replicas
- Pre-generated keys take storage space (~7 bytes × 3.5T = ~24.5 TB)
- Keys used in memory may be wasted on crash

**Verdict**: Best approach for high-scale systems.

---

#### Approach 4: UUID-Based (Random 128-bit UUID)

**How it works:**
1. Generate a random UUID (e.g., `550e8400-e29b-41d4-a716-446655440000`).
2. Take the first 7 characters after Base62-encoding.

**Pros:**
- No centralized service needed
- Near-zero collision probability (2^128 space)

**Cons:**
- UUID strings are long (36 chars) and not human-friendly
- Probability of collision, while tiny, is non-zero
- Not deduplication-friendly (same long URL → different UUIDs each time)

**Verdict**: Acceptable but KGS is cleaner.

---

### Strategy Comparison Table

| Strategy | Collision Risk | Performance | Complexity | Predictable URLs |
|---|---|---|---|---|
| MD5 Hash | **Medium** (prefix collision) | Fast | Low | Yes (bad) |
| Counter + Base62 | **None** | Very Fast | Medium | Yes (bad) |
| KGS (Pre-gen Keys) | **None** | Very Fast | High | No ✅ |
| UUID | **Near-zero** | Fast | Low | No ✅ |

---

### 5.2 Database Design

#### Which Database to Use?

The URL shortener is **read-heavy** (100:1 ratio). The primary access pattern is:

```
Key-value lookup: short_code → long_url
```

This is a perfect use case for a **NoSQL key-value / wide-column store**.

##### Option A: Relational DB (PostgreSQL / MySQL)

```sql
CREATE TABLE urls (
    id              BIGSERIAL PRIMARY KEY,
    short_code      VARCHAR(10) UNIQUE NOT NULL,
    long_url        TEXT NOT NULL,
    user_id         BIGINT REFERENCES users(id),
    created_at      TIMESTAMP DEFAULT NOW(),
    expires_at      TIMESTAMP,
    is_active       BOOLEAN DEFAULT TRUE,
    click_count     BIGINT DEFAULT 0
);

CREATE INDEX idx_short_code ON urls(short_code);
```

✅ ACID transactions, easy to query, good for custom aliases  
❌ Harder to scale horizontally, row-level locking issues at high write load

##### Option B: Cassandra / DynamoDB (Recommended for Scale)

```
Table: url_mappings
Partition Key: short_code (VARCHAR)
Columns:
  - long_url (TEXT)
  - user_id (UUID)
  - created_at (TIMESTAMP)
  - expires_at (TIMESTAMP)
  - is_active (BOOLEAN)
```

CQL (Cassandra Query Language):
```sql
CREATE TABLE url_mappings (
    short_code  TEXT PRIMARY KEY,
    long_url    TEXT,
    user_id     UUID,
    created_at  TIMESTAMP,
    expires_at  TIMESTAMP,
    is_active   BOOLEAN
);
```

✅ Linear horizontal scaling, excellent read throughput, replication built-in  
❌ No ACID, limited query flexibility, eventual consistency

#### Recommendation

- **Primary store**: Cassandra (or DynamoDB) for the `short_code → long_url` mapping
- **SQL (PostgreSQL)**: For user accounts, custom aliases, and metadata that needs relational queries
- **Redis**: As a caching layer in front of Cassandra

---

### 5.3 Caching Layer

**The 80/20 Rule (Pareto Principle):**  
> 20% of URLs generate 80% of traffic.

So we should aggressively cache the hot 20%.

#### Cache Design

- **Technology**: Redis Cluster (in-memory key-value store)
- **Data Structure**: Simple string key-value: `short_code → long_url`
- **TTL**: Set cache TTL equal to the URL's expiry (or 24h for permanent URLs)
- **Eviction Policy**: `allkeys-lru` — evict least recently used keys when memory is full

#### Cache Size Estimation

```
Hot URLs: 20% of daily active URLs
If 100M URLs stored, hot = 20M URLs
Each entry: 7-byte key + 200-byte value ≈ 210 bytes
20M × 210 bytes ≈ 4.2 GB

→ A single Redis node with 8GB RAM can cache all hot URLs easily.
→ Use a Redis Cluster for redundancy and geographic distribution.
```

#### Cache Update Strategy

- **Cache-Aside (Lazy Loading)**: Look up Redis first; on miss, read from DB and populate cache.
- On URL deletion or expiry, actively **invalidate** the cache entry.

```
Redirect Flow with Cache:
┌────────┐           ┌───────────┐     Hit    ┌────────────┐
│ Client │ ─ GET ──▶ │  API Srv  │ ─────────▶ │ Redis Cache │
└────────┘           └─────┬─────┘            └─────┬──────┘
                           │ Miss                    │
                           │ ◀───────────────────────┘
                           ▼
                     ┌───────────┐
                     │ Cassandra │
                     └─────┬─────┘
                           │
                           ▼
                  Populate cache, return URL
```

---

### 5.4 Redirection Mechanism

When a user visits `https://short.ly/aB3kQ2m`, the server must redirect them. There are two HTTP options:

#### 301 Permanent Redirect

```
HTTP/1.1 301 Moved Permanently
Location: https://www.example.com/...
```

- Browser **caches** this permanently.
- Subsequent visits by the same user bypass the server entirely.
- ✅ Reduces server load dramatically
- ❌ Cannot track repeated clicks (analytics are incomplete)
- ❌ If the long URL changes, the browser's cached redirect breaks

**Use when**: Long URLs are immutable and analytics are not required.

#### 302 Temporary Redirect

```
HTTP/1.1 302 Found
Location: https://www.example.com/...
```

- Browser does **NOT cache** this.
- Every click goes through the server.
- ✅ Accurate click analytics on every visit
- ❌ Higher server load (but mitigated by Redis cache)

**Use when**: Analytics are required (which is most of the time for a URL shortener business).

#### Recommendation

Use **302** for most cases, especially when analytics are a feature. Use **301** for internal links where analytics don't matter.

---

## 6. Detailed Component Design

### 6.1 URL Creation Service (Write Path)

```
Step-by-step for POST /api/shorten:

1. Receive request with long_url
2. Validate URL format (regex check, DNS resolution optional)
3. Check for malicious URLs (blacklist or VirusTotal API)
4. Check if long_url already exists in DB (deduplication — optional)
   a. If YES → return existing short_code
   b. If NO  → continue
5. Request a unique short_code from Key Generation Service (KGS)
6. Store mapping in Cassandra:
   { short_code, long_url, user_id, created_at, expires_at }
7. Store in Redis cache (pre-warm the cache)
8. Return the short URL to the client
```

#### Deduplication Consideration

If the same long URL is submitted twice, should it get the same short code?

- **Yes (dedup ON)**: Store a reverse index `long_url → short_code`. Same URL = same short link. Saves storage but two users get the same short URL.
- **No (dedup OFF)**: Each submission always gets a fresh short code. Users get unique links, better for individual analytics. Default for most services.

---

### 6.2 URL Redirect Service (Read Path)

```
Step-by-step for GET /{short_code}:

1. Extract short_code from URL path
2. Validate format (7 alphanumeric chars)
3. Look up short_code in Redis cache
   a. Cache HIT → return 302 redirect immediately (< 1ms)
   b. Cache MISS → continue
4. Look up short_code in Cassandra
   a. Found → populate Redis cache, return 302 redirect
   b. Not found → return 404 Not Found
5. (Async) Publish click event to Kafka for analytics
```

---

### 6.3 Key Generation Service (KGS)

```
KGS Architecture:

┌─────────────────────────────────────────────────┐
│               Key Generation Service             │
│                                                  │
│  ┌────────────────────────────────────────────┐  │
│  │           In-Memory Key Buffer             │  │
│  │  [aB3kQ2m, xY9pL4n, mK7rZ1q, ...]        │  │
│  └────────────────────────────────────────────┘  │
│                     ↑ replenish when low          │
│  ┌────────────────────────────────────────────┐  │
│  │           KGS Database (MySQL)             │  │
│  │  Table: keys(short_code, status)           │  │
│  │  Atomically marks keys as "used"           │  │
│  └────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
        ↑ requests for keys
┌─────────────────────────────────────────────────┐
│              URL Creation Service                │
└─────────────────────────────────────────────────┘
```

**KGS Fault Tolerance:**
- Run **two KGS instances** (primary + standby).
- If primary crashes, standby immediately takes over.
- Keys in memory at time of crash are lost but it's a tiny fraction (~1000 keys).

---

### 6.4 Analytics Service (Optional Advanced Feature)

**Design Pattern**: Write-Async via Message Queue

```
On every redirect:
1. API Server publishes an event to Kafka:
   {
     "short_code": "aB3kQ2m",
     "timestamp": "2026-07-19T00:00:00Z",
     "ip": "203.0.113.10",
     "user_agent": "Mozilla/5.0...",
     "referrer": "https://twitter.com"
   }

2. Analytics Consumer reads from Kafka:
   - Resolves IP → country, city
   - Parses user_agent → device, browser, OS
   - Writes to ClickHouse (OLAP columnar DB) or BigQuery

3. Aggregation jobs run hourly/daily to compute:
   - Total clicks per short_code
   - Clicks by country, device, referrer
   - Time-series click trends
```

**Why Kafka?**
- Decouples analytics from the critical redirect path (no latency impact)
- Handles burst traffic (Kafka buffers spikes)
- Allows multiple consumers (analytics, fraud detection, etc.)

---

## 7. Data Flow Walkthrough

### 7.1 Creating a Short URL

```
User                   API Server           KGS            Cassandra          Redis
 │                         │                 │                  │                │
 │── POST /api/shorten ───▶│                 │                  │                │
 │                         │── Validate URL  │                  │                │
 │                         │                 │                  │                │
 │                         │─── Get key() ──▶│                  │                │
 │                         │◀── "aB3kQ2m" ───│                  │                │
 │                         │                 │                  │                │
 │                         │── INSERT (aB3kQ2m, long_url) ─────▶│                │
 │                         │                 │         ◀─ OK ───│                │
 │                         │                 │                  │                │
 │                         │── SET aB3kQ2m → long_url ─────────────────────────▶│
 │                         │                 │                  │      ◀─ OK ────│
 │                         │                 │                  │                │
 │◀── 201 {short_url} ─────│                 │                  │                │
```

### 7.2 Redirecting a Short URL (Cache Hit)

```
User              Load Balancer        API Server             Redis
 │                    │                    │                     │
 │── GET /aB3kQ2m ───▶│                   │                     │
 │                    │─── Route ─────────▶│                    │
 │                    │                    │── GET aB3kQ2m ─────▶│
 │                    │                    │◀── long_url ────────│
 │                    │                    │                     │
 │◀─── 302 Redirect ──────────────────────│                     │
 │                    │                    │                     │
 │── GET long_url ───▶│ (original website)                      │
```

### 7.3 Redirecting a Short URL (Cache Miss)

```
User              API Server             Redis            Cassandra
 │                    │                     │                  │
 │── GET /aB3kQ2m ───▶│                    │                  │
 │                    │── GET aB3kQ2m ─────▶│                 │
 │                    │◀── (nil) ───────────│                 │
 │                    │                     │                  │
 │                    │── SELECT WHERE short_code='aB3kQ2m' ──▶│
 │                    │◀── long_url ─────────────────────────── │
 │                    │                     │                  │
 │                    │── SET aB3kQ2m=long_url, TTL=86400s ───▶│
 │                    │                     │                  │
 │◀─── 302 Redirect ──│                     │                  │
```

---

## 8. Scalability & Deep Dives

### 8.1 Database Sharding

As URL data grows to 91+ TB, a single database node is not feasible.

**Sharding Strategy: By Short Code**

```
Shard Key: first character of short_code

shard_0 → short codes starting with [a-g]
shard_1 → short codes starting with [h-p]
shard_2 → short codes starting with [q-z]
shard_3 → short codes starting with [A-G]
...etc.
```

**Consistent Hashing** is preferred to minimize data rebalancing when adding/removing shard nodes.

```
Hash(short_code) → position on ring → assigned shard node
```

---

### 8.2 Multi-Region / Global Distribution

For a global service, redirects should be served from geographically close servers.

```
Architecture:

┌──────────────────────────────────────────────────────┐
│                   Global DNS (AWS Route 53)           │
│      Latency-based routing → closest region          │
└───────────────────────┬──────────────────────────────┘
                        │
        ┌───────────────┼────────────────┐
        │               │                │
  ┌─────▼──────┐  ┌─────▼──────┐  ┌─────▼──────┐
  │ US Region  │  │ EU Region  │  │ Asia Region│
  │  (N. VA)   │  │ (Frankfurt)│  │ (Singapore)│
  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘
        │               │                │
  ┌─────▼──────────────────────────────────────────┐
  │         Global Cassandra Cluster                │
  │     (Multi-region replication built-in)         │
  └─────────────────────────────────────────────────┘
```

- Each region has local Redis cache and Cassandra replicas.
- Writes go to the primary region (e.g., US) and replicate to others asynchronously.
- Reads are served locally → ultra-low latency globally.

---

### 8.3 Handling Expired URLs

URLs with a TTL need to be cleaned up.

**Option A: Lazy Deletion**
- On every redirect, check `expires_at`.
- If expired, return 404 and optionally delete the record.
- Simple, but expired records linger in DB.

**Option B: Active Cleanup (Cron Job)**
- Run a background batch job daily to `DELETE WHERE expires_at < NOW()`.
- Keeps DB lean but adds write load.

**Option C: TTL in Cassandra**
- Cassandra natively supports TTL on rows: `INSERT ... USING TTL 2592000` (30 days in seconds).
- Cassandra automatically expires and tombstones the record.
- ✅ No extra cleanup code needed

**Recommendation**: Use Cassandra's native TTL for expired URLs.

---

### 8.4 Custom Aliases

Users want `short.ly/my-product` instead of `short.ly/aB3kQ2m`.

**Validation Rules:**
- Length: 3–50 characters
- Allowed chars: `[a-zA-Z0-9-_]`
- Profanity/reserved word filter (e.g., `admin`, `api`, `help`, `about`)

**Storage:**
- Custom aliases bypass KGS.
- Check if alias already exists in DB before inserting.
- Store in same `urls` table with `short_code = custom_alias`.

```sql
INSERT INTO urls (short_code, long_url, is_custom, user_id)
VALUES ('my-product', 'https://...', TRUE, 'user_123')
ON CONFLICT (short_code) DO NOTHING  -- return error if taken
RETURNING *;
```

---

### 8.5 Rate Limiting (Abuse Prevention)

Without rate limiting, a bot can create millions of junk URLs.

**Strategy: Sliding Window Rate Limiter backed by Redis**

```
For anonymous users:   10 URL creations per minute per IP
For authenticated users: 1,000 URL creations per day per user
```

Redis implementation using sorted sets:
```
Key: rate_limit:{user_id}
Value: sorted set of request timestamps
Algorithm:
  1. Remove timestamps older than 1 minute: ZREMRANGEBYSCORE key 0 (now-60s)
  2. Count remaining: ZCARD key
  3. If count >= limit → reject with 429 Too Many Requests
  4. Add current timestamp: ZADD key now now
  5. Set expiry: EXPIRE key 60
```

---

## 9. Edge Cases & Failure Scenarios

| Scenario | Handling |
|---|---|
| **Invalid URL format** | Regex validate URL; return 400 Bad Request |
| **Malicious / phishing URL** | Integrate with Google Safe Browsing API; block on match |
| **Duplicate long URL submission** | Return existing short code or create new (configurable) |
| **Custom alias already taken** | Return 409 Conflict with helpful error message |
| **Short code not found** | Return 404 with branded error page |
| **URL has expired** | Return 410 Gone (not 404, to distinguish expired vs. never existed) |
| **KGS goes down** | Secondary KGS takes over; fail-fast with circuit breaker |
| **Redis goes down** | Fall back to Cassandra directly; system slower but functional |
| **Cassandra node failure** | Cassandra replication factor 3 → tolerate N/2 node failures |
| **Hot short code (thundering herd)** | Redis cache absorbs traffic; use lock coalescing for cache stampede |
| **Very long URLs** | Set max URL length (e.g., 2048 chars); reject with 400 |
| **Cyclic redirect** | Detect: if long_url is in our own domain, warn or reject |

---

## 10. Monitoring & Observability

### Key Metrics to Track

| Category | Metric | Alert Threshold |
|---|---|---|
| **Latency** | p50/p99/p999 redirect latency | p99 > 50ms → alert |
| **Latency** | p99 URL creation latency | p99 > 200ms → alert |
| **Throughput** | Redirects per second | — (for capacity planning) |
| **Cache** | Redis cache hit ratio | < 80% → investigate |
| **Errors** | 4xx rate (bad requests) | > 5% → alert |
| **Errors** | 5xx rate (server errors) | > 0.1% → page on-call |
| **Database** | Cassandra read/write latency | > 20ms → alert |
| **KGS** | Available key count | < 1M → trigger key generation |
| **Storage** | Database disk usage | > 80% → expand capacity |

### Tools

- **Metrics**: Prometheus + Grafana dashboards
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana) or Loki
- **Tracing**: Jaeger or Zipkin (distributed tracing across services)
- **Alerting**: PagerDuty or OpsGenie for on-call alerts

---

## 11. Trade-offs Summary

| Decision | Option A | Option B | Choice |
|---|---|---|---|
| **URL Generation** | MD5 Hash (collision risk) | KGS Pre-gen | ✅ KGS |
| **Primary DB** | SQL (PostgreSQL) | NoSQL (Cassandra) | ✅ Cassandra for scale |
| **Redirect Type** | 301 Permanent | 302 Temporary | ✅ 302 (analytics) |
| **Cache Strategy** | Cache-Aside | Write-Through | ✅ Cache-Aside |
| **Analytics** | Sync (blocks redirect) | Async via Kafka | ✅ Async Kafka |
| **Expiry Cleanup** | Cron Job | DB-native TTL | ✅ Cassandra TTL |
| **Deduplication** | Always deduplicate | Always create new | Configurable |
| **Sharding** | Range-based | Consistent Hashing | ✅ Consistent Hashing |

---

## 12. Interview Tips

### ⏱️ Suggested Interview Timeline (45 minutes)

| Phase | Time | Activities |
|---|---|---|
| Requirements Clarification | 5 min | Ask about scale, analytics, custom aliases, expiry |
| Capacity Estimation | 5 min | QPS, storage, bandwidth calculations |
| High-Level Design | 10 min | Draw the block diagram, define APIs |
| Deep Dive | 20 min | URL generation strategy, DB, caching, scaling |
| Wrap-Up | 5 min | Bottlenecks, monitoring, what would you do differently |

### 💡 Key Points to Highlight

1. **The read:write ratio (100:1)** is the most important insight — design aggressively for reads.
2. **KGS is elegant** — explain it clearly, most candidates don't know it.
3. **301 vs 302** is a classic trade-off question — always discuss both.
4. **Cache-aside with Redis** is the standard pattern — know the eviction policies.
5. **Cassandra** is ideal here — partition by `short_code`, simple key-value access pattern.
6. **Consistent hashing** for sharding shows you understand distributed systems.

### ⚠️ Common Mistakes to Avoid

- Jumping to a single server solution without discussing scaling
- Forgetting cache invalidation when a URL is deleted or expires
- Not discussing 301 vs 302 HTTP semantics
- Using SQL for the mapping table without justification (it can work but harder to scale)
- Ignoring abuse/spam prevention (rate limiting, URL validation)
- Not discussing what happens when KGS fails

### 🔑 Key Terms to Know

| Term | Definition |
|---|---|
| **Base62** | Encoding using 62 characters (a-z, A-Z, 0-9) |
| **KGS** | Key Generation Service — pre-generates unique keys |
| **Cache-Aside** | App looks up cache; on miss, fetches from DB and populates cache |
| **LRU Eviction** | Least Recently Used — evict the key not accessed for the longest time |
| **Consistent Hashing** | Hash ring-based sharding that minimizes redistribution |
| **301 Redirect** | Permanent — browser caches; server not revisited |
| **302 Redirect** | Temporary — browser always queries server; good for analytics |
| **Thundering Herd** | Many clients simultaneously hit a cache miss and overwhelm the DB |
| **Bloom Filter** | Probabilistic data structure to test set membership (no false negatives) |
| **Cassandra TTL** | Native row expiry in Cassandra using `USING TTL` clause |

---

## 📚 Further Reading

| Resource | Topic | Link |
|---|---|---|
| System Design Interview (Alex Xu) | Chapter 8: URL Shortener | Vol 1 |
| ByteByteGo | URL Shortener Design | youtube.com/ByteByteGo |
| bit.ly Engineering Blog | How bit.ly works at scale | engineering.bitly.com |
| Redis Docs | Data Structures & TTL | redis.io/docs |
| Cassandra Docs | TTL & Data Modeling | cassandra.apache.org |
| AWS DynamoDB | Key-Value at scale | aws.amazon.com/dynamodb |

---

*Document created: 2026-07-19 | System Design Series — Problem #1 of 15*  
*See [system_design_problems.md](./system_design_problems.md) for all 15 problems.*
