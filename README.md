# productcatalogservice (ShopNest): the project at the END of Class 16

This is the catalog service after Class 16 (UUIDs and Flyway). It starts from the project at the end of Class 15 (resilience). Spring Boot 4.1.1, Java 17 (also runs on 21), MySQL 8 or newer.

## What Class 16 changed

| File | Change |
|---|---|
| `models/BaseModel` | The id is now `UUID`, made by Hibernate as a **version 7** UUID: `@GeneratedValue` and `@UuidGenerator(style = UuidGenerator.Style.VERSION_7)`. MySQL stores it as `BINARY(16)`. |
| `repositories`, `services`, `dtos`, `controllers/ProductController` | `Long` became `UUID` for the id of a product and of a category (10 files; in class the agent does this). The three `id < 1` checks are gone, because Spring checks the format of a UUID. |
| `services/DummyJsonProductService` | The supplier keeps its own numbers (`DummyJsonProductDto.id` is still `Long`). `getProductById(UUID)` returns `null`, because dummyjson cannot look a product up by our id. `from(dto)` no longer sets an id. |
| `pom.xml` | Added `spring-boot-starter-flyway` and `flyway-mysql` |
| `application.properties` | `ddl-auto=validate`, `spring.flyway.baseline-on-migrate=true`, `spring.flyway.baseline-version=1` |
| `db/migration/V1__baseline.sql` (new) | The schema at the end of Class 15, the raw output of `mysqldump --no-data --compact --skip-add-drop-table` (it keeps 6 harmless `/*!` lines). A baseline: it is not run on a database that already exists. |
| `db/migration/V2__ids_to_uuid.sql` (new) | Moves `category.id`, `product.id` and `product.category_id` from `BIGINT` to `BINARY(16)` in three steps, and keeps every link |
| `homework/V3__category_name_unique.sql` | **Not** in the migration folder. It is homework item 3: a unique rule on `category.name` (the rule that `ddl-auto=update` never added in Class 8). Students write it themselves; this is the answer. |
| `seed/seed-products.sql` | The seed now gives every row a UUID (`UUID_TO_BIN(UUID())`). Run it after the first start. |
| `test/.../ProductMapperTest` | Fixed UUIDs instead of `1L`, `7L`, `999L` |

## Run it

1. MySQL 8.4 or newer. **Two ways to start:**
   - **Your database from Class 15** (tables with numbers): take a backup first (`mysqldump -u root -p productcatalogservice > backup-before-uuid.sql`). The first start baselines it as version 1 and runs `V2`.
   - **A new empty database:** the first start runs `V1` and `V2`. Then load `seed/seed-products.sql` (194 products, 24 categories).
2. Keep your own `DB_PASSWORD` and `GROQ_API_KEY`. For a `POST`, start the pretend friend (`java Friend.java`) and set `AUTH_SERVICE_URL=http://localhost:9000`.

```bash
export AUTH_SERVICE_URL=http://localhost:9000
mvn spring-boot:run
```

3. Things to call:

```bash
curl -s "localhost:8080/products?size=2"
curl -s localhost:8080/products/PASTE-AN-ID-HERE
curl -s -i localhost:8080/products/1                  # 400: the id must be a UUID now
curl -s -X POST localhost:8080/products -H "Authorization: Bearer x" -H "Content-Type: application/json" -d '{"name":"Test Mug","description":"A mug","price":9.99,"category":{"name":"kitchen-accessories"}}'
```

## What was tested (29 Oct 2026, JDK 21, Boot 4.1.1, Hibernate 7.4.5, Flyway 12.4.0, MySQL 8.4.11)

- 17 tests pass (`mvn test`, with the environment variables set; the Spring context test also runs Flyway).
- On a database made by the Class 15 project (194 products, 24 categories): Flyway baselines it as version 1, and runs `V2` (63 ms). The pairs "product name and category name" are **identical** before and after. All 194 products still know their category.
- On a **new empty** database: `V1` and `V2` run in order, then the new seed loads (194 products, all with a category).
- New products get version 7 UUIDs (the third group of the id starts with `7`). `GET /products/1` answers 400. A `PATCH` and a soft `DELETE` work with UUIDs.
- With `ddl-auto=update` and no Flyway (UUID code on the old table), the shop **starts**, and Hibernate changes `product.id` to `binary(16)` with the old numbers as text, while `product.category_id` stays `bigint`. The backup restores it.
- Editing `V2` after it was applied stops the shop (`Migration checksum mismatch for migration version 2`). A field without a column stops it too (`Schema validation: missing column [...]`). A new enum value is **not** noticed by `validate`.
- The agent step (`opencode run`, `deepseek-v4.1-flash-free`) took 53 seconds, changed 10 files, and left the supplier call that sends our UUID to dummyjson (it compiles, and it can never work).

## Not tested

JDK 17 at run time, Windows, PostgreSQL, a migration while the shop is running, and the timing of the key test on other laptops.
