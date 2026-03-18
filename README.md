# meteo-data-process-server

信创技术下气象数据分析预测系统的服务端项目，采用 Spring Boot + Spring Cloud Alibaba 的微服务架构，围绕气象数据的采集、存储、查询、同步与预测接入构建统一后端能力。

项目当前由 5 个 Maven 模块组成：

| 模块 | 作用 | 默认端口 |
| --- | --- | --- |
| `meteo-gateway` | API 统一入口、跨域处理、令牌鉴权、路由转发 | `9094` |
| `user-client-service` | 用户注册、登录、退出、访问令牌管理 | `9194` |
| `meteo-process-resource` | 气象数据查询、统计、同步编排 | `9394` |
| `meteo-obtain-resource` | UDP 数据采集、响应处理、Redis/MySQL 落库 | `9494` |
| `meteo-common` | 公共响应模型、异常处理、配置属性、常量与基础组件 | 无独立端口 |

外部预测服务通过网关的 `/anapredict/**` 路由接入，默认地址为 `http://localhost:9594`，服务端代码不在本仓库内。

预测服务仓库地址：<https://github.com/organwalk/meteo-anapredict-server>

## 系统架构

项目采用“网关统一入口 + 用户服务认证 + 采集服务落库 + 处理服务查询编排 + 公共模块复用”的结构。

1. 调用方统一经由 `meteo-gateway` 进入系统。
2. `/user/**` 请求直接路由到用户服务，完成登录、注册与令牌管理。
3. `/qx/**` 请求默认经过网关令牌校验后转发到气象处理服务。
4. `meteo-process-resource` 负责对 MySQL/Redis 中的气象数据进行查询、统计与同步编排。
5. 当处理服务发起同步请求时，会调用 `meteo-obtain-resource`，由采集服务向 UDP 数据源发送请求。
6. UDP 响应由 Netty 客户端接收，经 `UdpResponseProcessor` 解析后写入 Redis 与 MySQL，再由同步编排返回最终结果。

## 模块职责

### `meteo-gateway`

- 基于 Spring Cloud Gateway 提供统一入口。
- 对 `/qx/**` 与 `/anapredict/**` 路由应用 `TokenAuthenticationGatewayFilterFactory`。
- 通过 `UserClient` 远程访问用户服务，校验请求头中的 `name` 与 `access_token`。
- 使用 `GatewayAuthProperties` 管理鉴权开关与跨域白名单。

### `user-client-service`

- 提供用户注册、登录、退出与令牌查询接口。
- 登录时使用 `BCryptPasswordEncoder` 校验密码。
- 成功登录后生成 URL Safe Token 并写入 Redis。
- 支持基于 `app.security.token-ttl` 的令牌生存周期控制。

### `meteo-process-resource`

- 提供站点信息、采集日期、按小时/按天/按区间/复杂条件查询等接口。
- 通过 `ObtainClient` 调用采集服务发起数据同步。
- 使用 `StationTableNameResolver` 将站点编码映射为动态气象表名。
- 对按小时、按天、按时间范围的查询结果进行 Redis 缓存。

### `meteo-obtain-resource`

- 通过 Netty UDP 客户端与外部数据源通信。
- 将 UDP 响应统一交由 `UdpResponseProcessor` 处理。
- 站点信息写入 `station` 表，日期范围写入 `station_date` 表，原始气象数据先进入 Redis 有序集合，再批量落入 MySQL。
- 通过 `SyncTaskCoordinator` 将异步 UDP 响应转换为同步 HTTP 结果。

### `meteo-common`

- 提供 `DataResult`、`MeteorologyResult`、`StationResult` 等统一响应结构。
- 提供 `GlobalExceptionHandler`，统一处理校验异常、参数异常和通用异常。
- 提供 `RedisTemplateConfiguration`、`CacheProperties`、`ServiceClientProperties` 等共享配置。
- 提供 `HeaderNames`、`RedisKeys`、`UdpCodes` 等公共常量。

## 技术栈

| 类别 | 技术 |
| --- | --- |
| 语言与运行时 | Java 17 |
| 构建工具 | Maven Wrapper |
| 基础框架 | Spring Boot 3.0.5 |
| 微服务组件 | Spring Cloud 2022.0.1、Spring Cloud Alibaba 2022.0.0.0-RC1 |
| 网关 | Spring Cloud Gateway、WebFlux |
| 数据访问 | MyBatis-Plus、MySQL、Druid |
| 缓存与会话 | Redis |
| 网络通信 | Netty UDP |
| 注册与配置中心 | Nacos |
| 测试 | JUnit 5、Mockito、Spring Boot Test |

## 目录结构

```text
meteo-data-process-server
├─ meteo-common
├─ meteo-gateway
├─ meteo-obtain-resource
├─ meteo-process-resource
├─ user-client-service
├─ database
│  └─ compatibility
├─ .mvn
├─ mvnw
├─ mvnw.cmd
└─ pom.xml
```

## 运行依赖

启动本项目至少需要准备以下基础设施：

| 依赖项 | 用途 | 说明 |
| --- | --- | --- |
| MySQL | 用户数据、站点信息、站点日期、气象数据持久化 | 处理服务和采集服务均依赖 |
| Redis | Token、日期范围、采集阶段数据、查询缓存 | 用户服务、处理服务、采集服务均依赖 |
| Nacos | 服务注册发现、共享配置加载 | 默认通过 `NACOS_SERVER_ADDR` 指定 |
| 外部 UDP 数据源 | 返回站点、日期范围与气象明细 | 由采集服务访问 |
| 预测服务（可选） | `/anapredict/**` 路由目标 | 非本仓库内容 |

## 配置说明

### 配置加载策略

各服务的 `application.yml` 均采用“本地环境变量优先，Nacos 配置兜底”的方式加载：

```yaml
spring:
  config:
    import:
      - optional:nacos:common.yaml
      - optional:nacos:${spring.application.name}.yaml
      - optional:nacos:${spring.application.name}-${spring.profiles.active}.yaml
```

默认环境为 `local`，可通过 `SPRING_PROFILES_ACTIVE` 切换。

### 关键配置前缀

| 前缀 | 作用 |
| --- | --- |
| `app.clients.*` | 服务间 HTTP 调用地址与超时 |
| `app.cache.*` | 查询缓存前缀和 TTL |
| `app.gateway.auth.*` | 网关鉴权开关和跨域白名单 |
| `app.security.*` | 用户服务密码强度、Token TTL、Token 字节长度 |
| `app.udp.*` | UDP 服务端地址、认证账号、等待超时 |

### 默认端口与核心环境变量

| 服务 | 默认端口 | 关键环境变量 |
| --- | --- | --- |
| `meteo-gateway` | `9094` | `SERVER_PORT`、`NACOS_SERVER_ADDR`、`USER_SERVICE_BASE_URL`、`ANAPREDICT_SERVICE_BASE_URL`、`GATEWAY_AUTH_ENABLED` |
| `meteo-process-resource` | `9394` | `METEO_PROCESS_DB_*`、`METEO_PROCESS_REDIS_*`、`OBTAIN_SERVICE_BASE_URL`、`METEO_CACHE_*` |
| `meteo-obtain-resource` | `9494` | `METEO_OBTAIN_DB_*`、`METEO_OBTAIN_REDIS_*`、`UDP_REMOTE_HOST`、`UDP_REMOTE_PORT`、`UDP_AUTH_*` |
| `user-client-service` | `9194` | `USER_CLIENT_DB_*`、`USER_CLIENT_REDIS_*`、`USER_TOKEN_TTL`、`USER_PASSWORD_STRENGTH` |

### 请求头约定

网关鉴权开启时，调用 `/qx/**` 与 `/anapredict/**` 时需要携带以下请求头：

| Header | 说明 |
| --- | --- |
| `name` | 当前登录用户名 |
| `access_token` | 登录接口返回的访问令牌 |

## 启动步骤

### 1. 准备基础设施

1. 启动 MySQL、Redis、Nacos。
2. 准备基础业务库和基础表结构。
3. 若需要预测接口路由，额外启动预测服务。

### 2. 执行数据库兼容脚本

仓库提供了索引与唯一约束加固脚本：

```text
database/compatibility/20260318_schema_hardening.sql
```

该脚本会为 `station`、`station_date` 和现有的 `*_meteo_data` 表补齐必要索引。它是兼容性增强脚本，不等价于完整建库脚本。

### 3. 配置本地环境变量或 Nacos 配置

至少需要保证以下能力可用：

- MySQL 连接信息正确。
- Redis 连接信息正确。
- Nacos 地址可访问。
- 采集服务能访问 UDP 数据源。
- 网关与处理服务能访问下游 HTTP 服务。

### 4. 执行测试

```bash
./mvnw test
```

### 5. 启动服务

推荐启动顺序如下：

```bash
./mvnw -pl user-client-service spring-boot:run
./mvnw -pl meteo-obtain-resource spring-boot:run
./mvnw -pl meteo-process-resource spring-boot:run
./mvnw -pl meteo-gateway spring-boot:run
```

如需先打包再部署：

```bash
./mvnw clean package -DskipTests
```

## 接口概览

### 统一响应结构

| 类型 | 说明 |
| --- | --- |
| `DataResult` | `{ success, data }`，通用业务结果 |
| `MeteorologyResult` | `{ success, station, total, data }`，气象查询结果 |
| `StationResult` | `{ success, station }`，站点信息结果 |

其中 `success=1` 表示成功，`success=0` 表示失败。

### 用户服务接口 `/user/**`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/user/login` | 登录并生成访问令牌 |
| `POST` | `/user/register` | 注册用户 |
| `POST` | `/user/logout` | 注销并清理令牌 |
| `GET` | `/user/token` | 根据用户名获取当前令牌 |

### 处理服务接口 `/qx/**`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/qx/stations` | 查询全部站点或指定站点日期列表 |
| `GET` | `/qx/collection_year` | 查询站点采集年份 |
| `GET` | `/qx/collection_month` | 查询站点在某年的采集月份 |
| `GET` | `/qx/collection_day` | 查询站点在某年某月的采集日期 |
| `GET` | `/qx/data_sum` | 查询站点某月数据量统计 |
| `POST` | `/qx/stat_hour` | 小时维度统计查询 |
| `POST` | `/qx/stat_day` | 单日统计或图表查询 |
| `POST` | `/qx/stat_day_range` | 时间范围查询 |
| `POST` | `/qx/query` | 复杂多条件查询 |
| `GET` | `/qx/obtain/connect` | 申请采集通道令牌 |
| `GET` | `/qx/obtain/sync/station` | 同步站点编码 |
| `GET` | `/qx/obtain/sync/date_range` | 批量同步站点日期范围 |
| `GET` | `/qx/obtain/sync/latest_date` | 查询站点最新落库日期 |
| `GET` | `/qx/obtain/sync/exist` | 判断站点是否已有落库数据 |
| `POST` | `/qx/obtain/sync/meteo_data` | 同步指定站点指定时间段气象数据 |
| `GET` | `/qx/obtain/close` | 关闭采集通道并注销令牌 |

### 采集服务接口 `/api/obtain/**`

这些接口主要供处理服务内部调用，不建议前端直接访问。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/obtain/token/user` | 获取采集令牌 |
| `POST` | `/api/obtain/token` | 注销采集令牌 |
| `GET` | `/api/obtain/meteo/station` | 获取站点编码 |
| `GET` | `/api/obtain/meteo/date_range` | 获取站点日期范围 |
| `GET` | `/api/obtain/meteo/data` | 获取气象明细数据 |

### 预测服务路由 `/anapredict/**`

- 由网关统一转发到外部预测服务。
- 默认同样受网关令牌过滤器保护。
- 本仓库不包含预测服务实现代码。

## 测试方式

当前仓库已包含关键单元测试：

| 测试类 | 验证内容 |
| --- | --- |
| `StationTableNameResolverTest` | 站点表名解析与非法输入保护 |
| `TokenAuthenticationGatewayFilterFactoryTest` | 网关 Token 鉴权通过/拒绝逻辑 |
| `SyncTaskCoordinatorTest` | 异步任务注册、完成与等待 |
| `MeteorologySyncServiceImplTest` | 无站点场景下的同步失败逻辑 |
| `UserServiceImplTest` | 登录保存 Token、重复注册拦截 |

执行命令：

```bash
./mvnw test
```

## 数据库与缓存说明

### MySQL

- 用户服务使用独立用户库，负责账号与密码持久化。
- 处理服务与采集服务依赖气象库，主要表包括：
  - `station`
  - `station_date`
  - `<station>_meteo_data`
- `SaveToMySQLMapper` 会在同步站点后自动执行 `CREATE TABLE IF NOT EXISTS`，为每个站点创建独立的气象明细表。
- 动态气象表包含 `date`、`datetime`、`time`、`temperature`、`humidity`、`speed`、`direction`、`rain`、`sunlight`、`pm25`、`pm10` 等字段。

### Redis

- 用户服务存储访问令牌。
- 采集服务缓存 UDP Token、站点日期范围、待落库气象明细。
- 处理服务缓存热点查询结果。
- 典型键前缀：
  - `meteo:user:token:*`
  - `meteo:obtain:token:*`
  - `meteo:station:date-range:*`
  - `meteo:station:data:*`
  - `meteo:cache:*`

## 常见问题

### 1. 为什么访问 `/qx/**` 返回 401

- 请求未携带 `name` 或 `access_token`。
- Token 与 Redis 中保存的值不一致。
- 网关访问用户服务失败，过滤器会按未授权处理。

### 2. 为什么同步接口返回失败

- UDP 数据源未连通。
- 采集服务没有可用 Token。
- `SyncTaskCoordinator` 在 `app.udp.await-timeout` 时间内没有收到响应。

### 3. 为什么查询结果为空

- 对应站点尚未完成日期范围或明细同步。
- 查询时间段超出已采集日期。
- 复杂查询条件过严，没有命中记录。

### 4. 为什么本地配置没有生效

- 环境变量与 Nacos 同名配置存在覆盖关系。
- `SPRING_PROFILES_ACTIVE` 未按预期设置。
- 修改的是 `application-local.yml` 但当前 profile 不是 `local`。

## 维护建议

1. 对新引入的模块继续复用 `meteo-common` 中的公共响应、异常处理和配置绑定方式。
2. 新增下游服务时优先通过 `app.clients.*` 扩展统一的 HTTP 客户端配置。
3. 任何涉及动态表名的 SQL 都应继续通过 `StationTableNameResolver` 做白名单校验。
4. 若同步吞吐继续提升，优先改造日期范围与明细同步的批量/并发策略，而不是直接放大超时时间。
5. 生产环境建议为 Redis、MySQL、Nacos 和网关补充监控与告警。

## 功能亮点

### 1. 网关统一鉴权与请求收口

**设计目标**  
将用户认证、跨域和服务路由统一收敛到一个入口层，避免业务服务重复编写认证逻辑，并确保 `/qx/**` 与 `/anapredict/**` 访问具备一致的安全边界。

**实现方式**  
`meteo-gateway` 使用 Spring Cloud Gateway 统一暴露入口，通过 `TokenAuthenticationGatewayFilterFactory` 读取 `name` 与 `access_token` 请求头，并利用 `UserClient` 调用用户服务的 `/user/token` 校验 Redis 中的最新令牌；同时 `CorsConfig` 基于 `app.gateway.auth.*` 配置实现跨域白名单和鉴权开关，既能在联调环境快速放行，也能在生产场景集中控制。

### 2. UDP 异步采集转同步业务编排

**设计目标**  
外部数据源以 UDP 方式异步返回结果，而上层 HTTP 接口需要给出明确的同步成功/失败语义，因此必须在不暴露底层通信细节的前提下，完成异步响应与同步接口之间的桥接。

**实现方式**  
采集服务通过 `UDPClient` 发送请求，`UDPClientHandler` 负责接收报文，`UdpResponseProcessor` 根据 `UdpCodes` 分发处理不同类型响应；`SyncTaskCoordinator` 使用 `ConcurrentHashMap<String, CompletableFuture<Boolean>>` 为每个同步任务登记等待句柄，在站点同步、日期范围同步和气象明细同步的响应到达后完成对应 Future，从而把 UDP 的异步回包转成处理服务可直接消费的同步结果。

### 3. 站点维度动态建表与安全表名解析

**设计目标**  
气象明细数据天然以站点为核心维度，若全部落在单表中会放大冷热数据混合、索引膨胀与查询范围控制问题，因此系统采用“按站点分表”的方式控制查询粒度和持久化边界。

**实现方式**  
`SaveToMySQLMapper` 在同步站点时自动执行 `createMeteoDataTableIfNotExists`，为每个站点创建 `<station>_meteo_data` 表，并自动补齐 `datetime` 唯一索引、`date` 索引和 `station` 索引；所有查询和写入前都通过 `StationTableNameResolver` 校验站点编码只允许字母和数字，从源头阻断动态表名带来的 SQL 注入风险。

### 4. Redis + MySQL 双层存储与查询缓存

**设计目标**  
采集链路需要处理 UDP 返回的分段数据与短时间高频查询两类不同场景，因此单纯依赖 MySQL 会放大写入窗口和查询延迟，单纯依赖 Redis 又缺乏稳定持久化能力。

**实现方式**  
采集服务先将气象明细写入 Redis 有序集合，利用时间戳作为 score 保证顺序，待接收到末段报文后再批量落入 MySQL；处理服务对按小时、按天、按区间的查询结果按组合条件构建缓存键，并使用 `app.cache.meteorology-ttl` 控制过期时间。这样既提升了采集阶段的缓冲能力，也降低了热点统计查询对数据库的持续压力。

### 5. 统一公共组件与配置化改造

**设计目标**  
多模块项目在快速演进时最容易出现配置散落、返回结构不一致、重复定义 HTTP 客户端和 Redis 配置的问题，因此需要一个被所有服务共享的公共层来稳定接口约定和运行时行为。

**实现方式**  
`meteo-common` 提供统一响应模型、全局异常处理、RedisTemplate 配置、服务客户端配置属性和常量定义；各服务通过 `@ConfigurationPropertiesScan` 直接绑定 `app.clients.*`、`app.cache.*`、`app.security.*`、`app.udp.*` 等配置前缀，网关和处理服务则通过声明式 `HttpServiceProxyFactory` + `WebClient` 封装下游调用，减少样板代码并让超时、基础地址、缓存 TTL 等策略都能通过配置集中调整。
