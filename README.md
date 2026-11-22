# 苍穹外卖（Sky Take-Out）

基于 **Spring Boot 3 + JDK 17** 实现的餐饮外卖平台后端服务，涵盖 **商家管理后台（Admin）** 与 **微信小程序用户端（User）** 完整业务链路，支持来单实时语音提醒、运营数据统计、Excel 报表导出以及订单并发安全控制。

---

## 技术栈

| 分类 | 技术 / 组件 | 版本 / 说明 |
|---|---|---|
| 基础框架 | Spring Boot | `3.0.2`（JDK `17`） |
| 持久层 | MyBatis + Druid + PageHelper | `3.0.3` / `1.2.23` / `5.3.2` |
| 数据库与缓存 | MySQL + Spring Data Redis | 关系型数据存储与菜品/店铺状态缓存 |
| 实时通信 | Spring WebSocket（Jakarta） | 来单提醒与客户催单实时推送 |
| 定时任务 | Spring Task | 支付超时自动取消、派送中订单定时完成 |
| 认证鉴权 | JJWT（双端独立拦截器） | `0.11.5`，管理端与用户端独立 JWT 校验 |
| 文件与报表 | Aliyun OSS + Apache POI | `3.17.4` / `5.2.3`，图片上传与运营数据 Excel 导出 |
| 接口文档 | Knife4j（OpenAPI 3） | `4.3.0`，启动后访问 `/doc.html` |

---

## 工程结构

```text
sky-take-out
├── sky-common          # 公共模块：统一返回 Result、自定义异常、JWT/OSS/微信工具类、常量与属性配置
├── sky-pojo            # 实体模型：Entity、DTO（请求参数）、VO（响应视图）
└── sky-server          # 核心服务：Controller、Service、Mapper、AOP 公共字段填充、WebSocket、定时任务
```

---

## 核心功能模块

### 1. 商家管理端（`/admin/**`）
- **员工与店铺管理**：员工登录（JWT）、分页查询、启用/禁用、信息编辑；店铺营业状态切换（Redis 存储）。
- **分类、菜品与套餐管理**：分类管理；菜品/套餐增删改查、起售/停售、口味与菜品关联维护、OSS 图片上传与 Redis 缓存清理。
- **订单管理**：动态条件分页搜索、各状态数量统计、订单详情查询；接单、拒单、取消、派送、完成全流程状态流转。
- **数据统计与导出**：营业额统计、用户统计、订单统计（总数/有效数/完成率）、销量 Top10 排行、最近 30 日运营数据 Excel 导出。
- **工作台概览**：今日营业数据概览、订单管理总览、菜品与套餐启停售总览。

### 2. 小程序用户端（`/user/**`）
- **微信登录与地址簿**：小程序授权登录、个人收货地址管理与默认地址设置。
- **浏览与购物车**：按分类浏览启售菜品与套餐、规格口味选择、购物车增删改查。
- **下单与支付**：提交订单、模拟免密支付流转（兼容真实微信回调通知接口 `/notify/paySuccess`）、历史订单分页查询、再来一单、取消订单与客户催单。

---

## 工程亮点与设计细节

- **订单状态并发安全（CAS 条件更新）**：接单、拒单、取消、派送、完成以及用户端取消订单均采用 `WHERE id = #{id} AND status = #{expectedStatus}` 原子更新，防止并发请求（如同时接单与拒单/用户取消）导致状态覆盖或重复退款。
- **WebSocket 实时提醒**：支付成功或发起催单后通过 `WebSocketServer` 群发消息至在线管理端，实现新订单语音播报与催单弹窗。
- **自动化运维任务**：`OrderTask` 定时扫描并关闭超时未支付订单，自动完成长期处于派送中的订单。
- **公共字段自动填充**：基于自定义注解 `@AutoFill` + Spring AOP 切面统一注入 `createTime`、`updateTime`、`createUser`、`updateUser`。

---

## 快速启动

### 1. 环境准备
- JDK `17`
- Maven `3.8+`
- MySQL `8.0+`（创建数据库 `sky_take_out` 并导入对应表结构）
- Redis（默认连接 `localhost:6379`）

### 2. 配置说明
在 `sky-server/src/main/resources/application-dev.yml` 中配置本地数据源、微信 `appid`/`secret` 及 OSS 参数；其中阿里云 OSS 密钥通过系统环境变量注入，避免明文提交到仓库：

```bash
OSS_ACCESS_KEY_ID=your-access-key-id
OSS_ACCESS_KEY_SECRET=your-access-key-secret
```

### 3. 编译与运行
```bash
# 编译整个工程
mvn clean compile

# 运行 sky-server 中的 SkyApplication 启动服务（默认端口 8080）
```

启动成功后，可访问接口文档地址：`http://localhost:8080/doc.html`
