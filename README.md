# Grid07 Guardrail

**Stack:** Java 21 · Spring Boot 3.5 · PostgreSQL 15 · Redis 7 · Docker

---

## How to Run

```bash
# Start Postgres and Redis
docker-compose up -d

# Run the app
./mvnw spring-boot:run
```

App runs on `http://localhost:9090`

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/posts` | Create a post |
| POST | `/api/posts/{id}/comments` | Add a comment |
| POST | `/api/posts/{id}/like` | Like a post |


---

# Project Structure 

```
grid07-backend/
├── src/main/java/com/grid07/
│   ├── Grid07Application.java          
│   ├── config/
│   │   └── RedisConfig.java
│   ├── controller/
│   │   └── PostController.java
│   ├── dto/
│   │   ├── CreatePostRequest.java
│   │   ├── AddCommentRequest.java
│   │   └── LikeRequest.java
│   ├── entity/
│   │   ├── AuthorType.java            
│   │   ├── User.java
│   │   ├── Bot.java
│   │   ├── Post.java
│   │   └── Comment.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── BotRepository.java
│   │   ├── PostRepository.java
│   │   └── CommentRepository.java
│   ├── service/
│   │   ├── PostService.java
│   │   ├── ViralityService.java
│   │   ├── GuardrailService.java
│   │   └── NotificationService.java
│   └── scheduler/
│       └── NotificationSweeper.java
├── src/main/resources/
│   └── application.yml
├── docker-compose.yml
├── postman_collection.json
└── README.md
```

## Thread Safety — How Atomic Locks Work in Phase 2

There are three guardrails for bot comments. Each  has a concurrency problem and I solved each of it differently.

### Horizontal Cap (max 100 bot comments per post)

First read the count check if under 100, then increment it has a race condition. Two threads can both read 99, both pass the check and both write 100. Now it become 101 bots.

I used **Lua script** executed in Redis:

```lua
local count = redis.call('INCR', KEYS[1])
if count > tonumber(ARGV[1]) then
  redis.call('DECR', KEYS[1])
  return 0
else
  return count
end
```

Redis runs Lua scripts as a single atomic unit where nothing else can execute between the "INCR" and the check. If the count exceeds 100 it rolls back with "DECR" and rejects the request.

If the horizontal cap passes but a later guardrail rejects the request, `decrementBotCount()` is called to undo the increment otherwise rejected requests would silently fail the count over time.

### Cooldown Cap (bot can't re-reply to same human within 10 min)

This uses Redis "SET NX" (set-if-not-exists):

```java
redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofSeconds(600));
```

"SET NX" is a single atomic command. Either the key gets created (bot allowed, cooldown starts) or it already exists (bot rejected). There is no separate check then set step that could race between threads.

### Vertical Cap (max 20 depth levels)
No Redis needed. The `depthLevel` comes in the request. A plain integer check (`depthLevel <= 20`) runs first before any Redis or DB call, rejecting deep threads immediately.

### Why Not `synchronized`?

`synchronized` only works within one JVM. If two instances of this service run behind a load balancer then each has its own lock they don't know about each other. Redis is the shared external state, so atomic operations will work correctly across all instances.

