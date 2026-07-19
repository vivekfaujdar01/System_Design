# 🏗️ System Design Interview Problems

> A curated list of classic and advanced system design interview questions.
> Each problem includes key requirements, core components to think about, and topics to explore.

---

## 📋 Table of Contents

1. [URL Shortener](#1-url-shortener)
2. [Notification System](#2-notification-system)
3. [Rate Limiter](#3-rate-limiter)
4. [Design Twitter / X Feed](#4-design-twitter--x-feed)
5. [Design WhatsApp / Chat System](#5-design-whatsapp--chat-system)
6. [Design YouTube / Video Streaming](#6-design-youtube--video-streaming)
7. [Design Google Drive / Dropbox](#7-design-google-drive--dropbox)
8. [Design a Web Crawler](#8-design-a-web-crawler)
9. [Design Uber / Ride-Sharing](#9-design-uber--ride-sharing)
10. [Design a Search Autocomplete System](#10-design-a-search-autocomplete-system)
11. [Design a Distributed Cache](#11-design-a-distributed-cache)
12. [Design a Ticket Booking System](#12-design-a-ticket-booking-system)
13. [Design an E-Commerce Platform](#13-design-an-e-commerce-platform)
14. [Design a News Feed System](#14-design-a-news-feed-system)
15. [Design an API Gateway](#15-design-an-api-gateway)

---

## 1. URL Shortener

> **Example**: TinyURL, bit.ly

### 📌 Problem Statement
Design a service that takes a long URL and returns a shortened version. When someone visits the short URL, they should be redirected to the original URL.

### ✅ Functional Requirements
- Given a long URL, generate a unique short URL
- Redirect short URL to original URL
- Optional: Custom aliases (e.g., `short.ly/myname`)
- Optional: Link expiry after a TTL
- Optional: Analytics (click count, geo, device)

### ❌ Non-Functional Requirements
- High availability (system must always redirect)
- Low latency for redirects (~10ms)
- Scale: 100M URLs created/day, 10B redirects/day

### 🔑 Key Design Points
- **Encoding**: Base62 (a-z, A-Z, 0-9) → 6-char code = 56 billion combinations
- **Hashing approach**: MD5/SHA → take first 6 chars (handle collisions)
- **Counter-based approach**: Global auto-increment → encode to Base62
- **KGS (Key Generation Service)**: Pre-generate keys and store in DB
- **Database**: Key-value store (Redis) for fast lookups; Cassandra/DynamoDB for persistence
- **Cache**: Cache hot URLs (80/20 rule — 20% URLs get 80% traffic)
- **Load Balancer**: Distribute requests across servers

### 🧩 Components
```
Client → Load Balancer → API Servers → Cache (Redis) → Database (Cassandra)
                                    ↑
                              Key Generation Service
```

### 📚 Topics to Study
- Consistent hashing
- Cache eviction policies (LRU)
- Base62 encoding
- NoSQL vs SQL trade-offs

---

## 2. Notification System

> **Example**: Push notifications (mobile), Email, SMS alerts

### 📌 Problem Statement
Design a system that sends notifications to millions of users across different channels (push, email, SMS, in-app).

### ✅ Functional Requirements
- Send push notifications (iOS/Android), emails, SMS
- Support scheduled notifications
- Support user preference/opt-out
- Retry on failure

### ❌ Non-Functional Requirements
- High throughput: 10M+ notifications/day
- Low latency for critical alerts
- At-least-once delivery guarantee

### 🔑 Key Design Points
- **Event-driven**: Use a Message Queue (Kafka/RabbitMQ) to decouple producers and consumers
- **Worker Services**: Separate workers per channel (push worker, email worker, SMS worker)
- **Third-party integrations**: FCM (Firebase) for Android, APNs for iOS, Twilio for SMS, SendGrid for email
- **Priority queues**: High priority (OTP, alerts) vs low priority (newsletters)
- **Rate limiting**: Avoid overwhelming third-party services
- **Idempotency**: Prevent duplicate notifications using deduplication keys

### 🧩 Components
```
API Server → Kafka Queue → Notification Workers → 3rd Party Services (FCM, APNs, Twilio)
                 ↓
           Notification DB (user preferences, delivery logs)
```

### 📚 Topics to Study
- Message queues (Kafka vs RabbitMQ)
- Idempotency
- Retry mechanisms with exponential backoff
- Database for user preferences

---

## 3. Rate Limiter

> **Example**: API throttling, brute-force protection

### 📌 Problem Statement
Design a rate limiter that limits the number of requests a client can make to an API within a time window.

### ✅ Functional Requirements
- Allow/deny requests based on rate limit rules
- Support different limits per user/IP/API key
- Support multiple time windows (per second, per minute, per day)

### ❌ Non-Functional Requirements
- Very low latency (should add <1ms overhead)
- Distributed — works across multiple servers
- Accurate under high concurrency

### 🔑 Key Design Points
- **Algorithms**:
  - Token Bucket: Allows bursts; tokens refill at a constant rate
  - Leaky Bucket: Smooths out traffic; requests drain at a fixed rate
  - Fixed Window Counter: Simple but allows 2x traffic at window boundaries
  - Sliding Window Log: Accurate but memory-intensive
  - Sliding Window Counter: Hybrid, balances accuracy and memory
- **Storage**: Use Redis with atomic operations (INCR + EXPIRE) for counters
- **Headers**: Return `X-RateLimit-Remaining`, `X-RateLimit-Reset` headers

### 🧩 Components
```
Client → API Gateway (Rate Limiter Middleware) → Backend Services
                         ↓
                   Redis (counters per user/IP)
```

### 📚 Topics to Study
- Token bucket vs leaky bucket algorithm
- Redis atomic operations (INCR, EXPIRE, Lua scripts)
- Sliding window algorithms

---

## 4. Design Twitter / X Feed

> **Example**: Twitter, Instagram Feed

### 📌 Problem Statement
Design a social media platform where users can post tweets, follow other users, and see a timeline (feed) of tweets from people they follow.

### ✅ Functional Requirements
- Post a tweet (text, images, videos)
- Follow/unfollow users
- Home timeline: tweets from followed users
- User timeline: all tweets by a user
- Like, retweet, reply

### ❌ Non-Functional Requirements
- 300M DAU, 500M tweets/day
- Timeline load < 200ms
- High read throughput (reads >> writes, ratio ~100:1)

### 🔑 Key Design Points
- **Fan-out on Write (Push model)**: When a user tweets, push to followers' feed cache → fast reads, slow writes
- **Fan-out on Read (Pull model)**: On load, pull tweets from all followed users → slow reads, fast writes
- **Hybrid**: Use push for regular users, pull for celebrities (huge follower count)
- **Timeline Cache**: Store pre-built timelines in Redis (sorted set by timestamp)
- **Media Storage**: Object storage (S3) + CDN for images/videos
- **Search**: Elasticsearch for tweet search

### 🧩 Components
```
Client → API Gateway → Tweet Service → Fanout Service → Feed Cache (Redis)
                                  ↓                          ↓
                             Tweet DB              Timeline served to users
                          (Cassandra)
```

### 📚 Topics to Study
- Fan-out strategies
- Sorted sets in Redis
- CDN and object storage
- Denormalization for read performance

---

## 5. Design WhatsApp / Chat System

> **Example**: WhatsApp, Slack, Facebook Messenger

### 📌 Problem Statement
Design a real-time messaging system where users can send messages to individuals or groups.

### ✅ Functional Requirements
- 1-on-1 messaging
- Group messaging (up to 500 members)
- Online/offline status
- Read receipts (sent ✓, delivered ✓✓, read 🔵)
- Media sharing (images, videos, documents)
- Message history

### ❌ Non-Functional Requirements
- 2B users, 100B messages/day
- Low latency delivery (<100ms)
- Messages never lost

### 🔑 Key Design Points
- **WebSocket**: Persistent bidirectional connection for real-time messaging
- **Chat Service**: Routes messages, stores in DB, pushes to recipient
- **Message Queue (Kafka)**: For async delivery and retries
- **Message Storage**: Cassandra (append-only, time-series friendly)
- **Presence Service**: Track online/offline using heartbeat pings
- **Sync for offline users**: Store undelivered messages; deliver on reconnect
- **End-to-End Encryption**: Signal protocol

### 🧩 Components
```
Client A ←WebSocket→ Chat Server ←WebSocket→ Client B
                          ↓
                   Message Queue (Kafka)
                          ↓
                   Message DB (Cassandra)
```

### 📚 Topics to Study
- WebSocket vs long polling vs SSE
- Cassandra data modeling (partition by conversation_id)
- Message ordering and consistency
- Group message fan-out

---

## 6. Design YouTube / Video Streaming

> **Example**: YouTube, Netflix, TikTok

### 📌 Problem Statement
Design a video streaming platform where users can upload, view, and interact with videos.

### ✅ Functional Requirements
- Upload videos
- Stream videos smoothly
- Search videos
- Like, comment, subscribe
- Recommendation feed

### ❌ Non-Functional Requirements
- 2B users, 500 hours of video uploaded per minute
- Smooth playback at various resolutions/bandwidths
- Videos available within minutes of upload

### 🔑 Key Design Points
- **Video Upload Pipeline**: Upload → Object Storage → Transcoding Workers → Multiple formats (360p, 720p, 1080p, 4K) → CDN
- **Adaptive Bitrate Streaming (ABR)**: Serve appropriate quality based on bandwidth (HLS/DASH protocol)
- **CDN**: Cache videos close to users globally
- **Metadata DB**: SQL DB for video metadata (title, description, uploader)
- **Search**: Elasticsearch for full-text search
- **Recommendation**: ML pipeline (collaborative filtering, watch history)

### 🧩 Components
```
Upload → API Server → S3 (raw video) → Transcoding Service → CDN
                           ↓
                     Metadata DB (MySQL)

Viewer → CDN → Adaptive Bitrate Video Chunks
```

### 📚 Topics to Study
- HLS / DASH streaming protocol
- FFmpeg transcoding
- CDN (CloudFront, Akamai)
- Video chunking and segmentation

---

## 7. Design Google Drive / Dropbox

> **Example**: Google Drive, Dropbox, OneDrive

### 📌 Problem Statement
Design a cloud storage service where users can upload, store, and sync files across devices.

### ✅ Functional Requirements
- Upload and download files
- File sync across multiple devices
- File sharing (public/private links, permission levels)
- Version history / rollback
- Folder structure support

### ❌ Non-Functional Requirements
- 1B+ users
- File uploads up to 10GB
- Sync changes within seconds

### 🔑 Key Design Points
- **Chunking**: Split large files into chunks (~4MB); only re-upload changed chunks (delta sync)
- **Deduplication**: Use file hashes to detect identical chunks and save storage
- **Object Storage**: S3 for file chunks
- **Metadata DB**: Store file tree, version history, chunk IDs (SQL DB)
- **Sync Client**: Watches file system changes and uploads diffs
- **Conflict Resolution**: Last-write-wins or merge strategies

### 🧩 Components
```
Desktop Client → API Server → Metadata DB (MySQL)
                     ↓
               Block Storage (S3) ← Chunked file blocks
                     ↓
               Notification Service → Other devices (sync trigger)
```

### 📚 Topics to Study
- File chunking and deduplication
- Block-level sync
- Conflict resolution strategies
- WebSocket/SSE for real-time sync notifications

---

## 8. Design a Web Crawler

> **Example**: Googlebot, Common Crawl

### 📌 Problem Statement
Design a system that crawls the web, visits billions of web pages, and stores their content for indexing.

### ✅ Functional Requirements
- Start from seed URLs and discover new URLs
- Download and store page content
- Re-crawl pages periodically
- Respect `robots.txt`

### ❌ Non-Functional Requirements
- Crawl 1B pages in 1 month
- Politeness: don't overwhelm any single server
- Handle dynamic pages, redirects, and duplicate content

### 🔑 Key Design Points
- **URL Frontier**: Priority queue of URLs to crawl; prioritize by importance (PageRank)
- **DNS Resolver**: Cache DNS lookups to reduce latency
- **Fetcher**: Download HTML content with timeout/retry
- **Parser**: Extract new URLs, parse content
- **Deduplication**: Bloom filter or hash set to avoid re-crawling same URL
- **Content Dedup**: Simhash to detect near-duplicate content
- **Politeness**: Per-domain rate limiting; respect crawl-delay in robots.txt

### 🧩 Components
```
URL Frontier → Fetcher → Parser → Content Store (S3/HDFS)
     ↑               ↓
  URL Filter ← URL Extractor
  (Bloom Filter)
```

### 📚 Topics to Study
- Bloom filters
- Priority queues and BFS/DFS traversal
- robots.txt protocol
- Simhash for near-duplicate detection

---

## 9. Design Uber / Ride-Sharing

> **Example**: Uber, Lyft, OLA

### 📌 Problem Statement
Design a ride-sharing platform that matches riders with nearby drivers in real-time.

### ✅ Functional Requirements
- Rider requests a ride
- Match with nearest available driver
- Real-time location tracking during ride
- Trip fare calculation
- Ride history, payments

### ❌ Non-Functional Requirements
- Low latency matching (<1s)
- Accurate real-time location updates
- Handle surge pricing

### 🔑 Key Design Points
- **Geospatial Indexing**: Use Geohash or QuadTree to find nearby drivers efficiently
- **Driver Location Updates**: Drivers send GPS updates every 4 seconds via WebSocket
- **Matching Service**: Find available drivers in a radius, rank by distance/rating
- **Trip Service**: Manages the lifecycle of a trip (requested → accepted → in-progress → completed)
- **ETA Calculation**: Road network graph + routing algorithm (Dijkstra/A*)
- **Surge Pricing**: Supply/demand ratio in each geo-zone

### 🧩 Components
```
Rider App → API Gateway → Matching Service → Driver Location Store (Redis + Geospatial index)
                               ↓
                         Trip Service → DB (MySQL)
                               ↓
                         Notification Service → Driver App
```

### 📚 Topics to Study
- Geohash and QuadTree
- WebSocket for real-time tracking
- Consistent hashing for sharding
- Surge/dynamic pricing algorithms

---

## 10. Design a Search Autocomplete System

> **Example**: Google search bar suggestions, Amazon search

### 📌 Problem Statement
Design a system that provides real-time search query suggestions as the user types.

### ✅ Functional Requirements
- Return top-N (e.g., 5-10) suggestions per partial query
- Suggestions ranked by frequency/relevance
- Suggestions update in near-real time as new trends emerge

### ❌ Non-Functional Requirements
- Response in <100ms
- Handle billions of daily queries
- Scale globally

### 🔑 Key Design Points
- **Trie Data Structure**: Efficient prefix matching
- **Top-K storage in Trie nodes**: Store top-K suggestions at each node (avoid full tree traversal)
- **Precomputation**: Batch jobs compute top suggestions periodically and cache in Redis
- **Data aggregation**: Log all queries → MapReduce/Spark to compute query frequencies
- **Personalization**: Mix global trends with user history

### 🧩 Components
```
User types → API Gateway → Autocomplete Service → Cache (Redis Trie or precomputed suggestions)
                                    ↓
                             Data Pipeline (daily batch) → Trie update
                             Query Logs → Spark → Frequency counts
```

### 📚 Topics to Study
- Trie data structure
- Top-K frequency counting (Count-Min Sketch)
- MapReduce / Spark batch processing
- Cache invalidation strategies

---

## 11. Design a Distributed Cache

> **Example**: Redis, Memcached

### 📌 Problem Statement
Design a distributed caching system that stores key-value pairs with fast reads and writes across multiple nodes.

### ✅ Functional Requirements
- `get(key)` and `set(key, value, ttl)`
- Eviction policies (LRU, LFU)
- Cache expiry (TTL)
- Support for high throughput

### ❌ Non-Functional Requirements
- Sub-millisecond latency
- High availability
- Scale horizontally

### 🔑 Key Design Points
- **Consistent Hashing**: Distribute keys across nodes; minimize reshuffling when nodes join/leave
- **Virtual Nodes**: Improve load balancing with consistent hashing
- **Replication**: Each key replicated on N nodes for fault tolerance
- **Eviction**: LRU eviction when memory is full
- **Write-through vs Write-behind vs Cache-aside**: Different consistency strategies
- **Gossip Protocol**: Nodes communicate state to each other

### 📚 Topics to Study
- Consistent hashing
- CAP theorem
- LRU cache implementation (HashMap + Doubly Linked List)
- Write-through vs write-back caching

---

## 12. Design a Ticket Booking System

> **Example**: BookMyShow, Ticketmaster, IRCTC

### 📌 Problem Statement
Design a system where users can search for events and book tickets, ensuring no double-booking even under high concurrency.

### ✅ Functional Requirements
- Browse events and available seats
- Select and book seats
- Payment processing
- Booking confirmation and e-ticket generation

### ❌ Non-Functional Requirements
- No double-booking (strong consistency for seat selection)
- Handle flash sales (millions of users booking simultaneously)
- High availability

### 🔑 Key Design Points
- **Seat Locking**: Temporarily lock a seat for N minutes during checkout (optimistic/pessimistic locking)
- **Distributed Locks**: Redis-based locks (Redlock) to prevent race conditions
- **Queue for Flash Sales**: Virtual waiting queue during peak demand
- **Database**: SQL (MySQL) for ACID transactions on bookings
- **CQRS**: Separate read and write models; read replicas for browsing

### 🧩 Components
```
User → API Gateway → Seat Availability Service (read replicas)
                          ↓ (on select)
                     Locking Service (Redis Redlock)
                          ↓ (on payment)
                     Booking Service → DB (MySQL ACID)
                          ↓
                     Notification Service → Email/SMS
```

### 📚 Topics to Study
- Distributed locking (Redlock)
- Database transactions (ACID)
- Optimistic vs pessimistic locking
- CQRS pattern

---

## 13. Design an E-Commerce Platform

> **Example**: Amazon, Flipkart, Shopify

### 📌 Problem Statement
Design a large-scale e-commerce platform supporting product listing, search, cart, orders, and payments.

### ✅ Functional Requirements
- Product catalog with search and filters
- Shopping cart
- Order placement and tracking
- Payment processing
- Inventory management

### ❌ Non-Functional Requirements
- 500M products, millions of concurrent users
- Inventory must be accurate (no overselling)
- High availability during sale events

### 🔑 Key Design Points
- **Product Service**: Product catalog stored in a NoSQL DB (MongoDB) with Elasticsearch for search
- **Inventory Service**: Stock count per product; use Redis atomic DECR to prevent overselling
- **Order Service**: SQL DB for ACID compliance on orders; saga pattern for distributed transactions
- **Cart Service**: Session-based or Redis-backed cart
- **Payment Service**: Integration with payment gateways; idempotent payment processing

### 📚 Topics to Study
- Saga pattern for distributed transactions
- Elasticsearch for product search
- Inventory reservation patterns
- CDN for static assets and product images

---

## 14. Design a News Feed System

> **Example**: Facebook Feed, LinkedIn Feed

### 📌 Problem Statement
Design a personalized news feed that aggregates posts from friends, pages, and groups a user follows.

### ✅ Functional Requirements
- Create a post (text, photo, video)
- View personalized feed
- Like, comment, share
- Feed ranked by relevance (not just chronological)

### ❌ Non-Functional Requirements
- 1B users, 100M posts/day
- Feed load in <500ms
- Feed ranking updated near real-time

### 🔑 Key Design Points
- **Fan-out on Write**: Pre-generate feed for each user on post creation
- **Feed Ranking**: ML model scores posts by engagement signals (likes, recency, relationship strength)
- **Post Storage**: Cassandra for posts (write-heavy, time-series)
- **Feed Cache**: Redis stores pre-ranked feed per user (list of post IDs)
- **Celebrity Problem**: Use fan-out on read for accounts with millions of followers

### 📚 Topics to Study
- Fan-out on write vs read
- Feed ranking algorithms
- EdgeRank / ML-based ranking
- Cassandra for time-series data

---

## 15. Design an API Gateway

> **Example**: AWS API Gateway, Kong, Nginx

### 📌 Problem Statement
Design an API Gateway that acts as a single entry point for all client requests, routing them to appropriate microservices.

### ✅ Functional Requirements
- Request routing to backend services
- Authentication & authorization
- Rate limiting
- Load balancing
- Request/response transformation
- API versioning

### ❌ Non-Functional Requirements
- High throughput (millions of requests/sec)
- Very low added latency (<5ms)
- 99.99% availability

### 🔑 Key Design Points
- **Reverse Proxy**: Routes requests to correct microservice based on URL path
- **Auth**: JWT / OAuth 2.0 token validation at the gateway layer
- **Rate Limiter**: Token bucket per API key (backed by Redis)
- **Circuit Breaker**: Stops routing to unhealthy services (Hystrix pattern)
- **Service Discovery**: Dynamic service registry (Consul, Eureka) to locate services
- **Logging & Monitoring**: Centralized logging, distributed tracing (Jaeger, Zipkin)

### 🧩 Components
```
Client → API Gateway → Auth Service
              ↓              
        Rate Limiter (Redis)
              ↓
        Service Discovery → Microservices
              ↓
        Circuit Breaker
```

### 📚 Topics to Study
- Reverse proxy (Nginx, Envoy)
- JWT and OAuth 2.0
- Circuit breaker pattern
- Service mesh (Istio)

---

## 🧠 General Framework for Answering System Design Questions

Use this structured approach in any interview:

### Step 1: Clarify Requirements (5 min)
- Functional: What features?
- Non-Functional: Scale, latency, availability?
- Constraints: Read-heavy or write-heavy? Consistency or availability?

### Step 2: Estimate Scale (3 min)
- Daily Active Users (DAU)
- Read/Write QPS
- Storage requirements (daily, 5-year)
- Bandwidth

### Step 3: High-Level Design (10 min)
- Draw a simple block diagram
- Identify major components: API, DB, Cache, Queue, CDN

### Step 4: Deep Dive (15 min)
- Focus on the hardest or most interesting parts
- Discuss trade-offs for each decision

### Step 5: Wrap Up (5 min)
- Discuss bottlenecks
- How would you scale further?
- Monitoring & alerting?

---

## 📖 Recommended Resources

| Resource | Type | Link |
|---|---|---|
| Designing Data-Intensive Applications (DDIA) | Book | Martin Kleppmann |
| System Design Interview – Alex Xu | Book | Vol 1 & Vol 2 |
| ByteByteGo | YouTube / Newsletter | Alex Xu's channel |
| Grokking the System Design Interview | Course | Educative.io |
| High Scalability Blog | Blog | highscalability.com |
| AWS Architecture Center | Docs | aws.amazon.com/architecture |

---

*Happy Learning! 🚀 Consistent practice and understanding trade-offs is the key to acing system design interviews.*
