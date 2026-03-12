# 权限管理系统

基于 Vue 3 + Spring Boot 的权限管理系统，支持用户管理、角色管理、资源管理和接口权限控制。

## 功能特性

1. **用户管理** - 用户的增删改查
2. **角色管理** - 角色的增删改查，角色与资源、用户绑定
3. **资源管理** - 菜单、页面、按钮、API 的增删改查，启用/停用
4. **接口权限** - 访问后端接口时判断当前用户是否拥有该权限

## 技术栈

### 后端
- Spring Boot 3.2.0
- Java 17
- Maven

### 前端
- Vue 3.3.4
- Element Plus 2.3.14
- Pinia
- Vue Router 4
- Axios

## 项目结构

```
permission-system/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/example/permission/
│   │       ├── common/      # 通用类（Result, PageResult）
│   │       ├── config/      # 配置类
│   │       ├── controller/  # 控制器
│   │       ├── entity/      # 实体类
│   │       ├── interceptor/ # 拦截器
│   │       └── service/     # 服务层
│   └── pom.xml
├── frontend/                # Vue 前端
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── directives/      # 自定义指令
│   │   ├── router/          # 路由配置
│   │   ├── store/           # Pinia Store
│   │   └── views/           # 页面组件
│   └── package.json
└── README.md
```

## 快速开始

### 后端启动

```bash
cd permission-system/backend
mvn spring-boot:run
```

后端服务将在 12000-14000 范围内的随机端口启动。

### 前端启动

```bash
cd permission-system/frontend
npm install
npm run serve
```

前端服务将在 http://localhost:8080 启动。

## 默认账号

- 用户名: `admin`
- 密码: `admin123`

## 数据存储

当前版本使用内存存储数据，重启服务后数据会重置。所有 Service 层方法都设计为可替换为数据库实现，只需修改对应的 Service 实现类即可。

## API 文档

### 认证相关
- `POST /api/auth/login` - 用户登录

### 用户管理
- `GET /api/user/list` - 用户列表（分页）
- `GET /api/user/all` - 所有用户
- `GET /api/user/{id}` - 用户详情
- `POST /api/user` - 创建用户
- `PUT /api/user` - 更新用户
- `DELETE /api/user/{id}` - 删除用户
- `POST /api/user/{id}/roles` - 绑定角色
- `GET /api/user/{id}/roles` - 获取用户角色

### 角色管理
- `GET /api/role/list` - 角色列表（分页）
- `GET /api/role/all` - 所有角色
- `GET /api/role/{id}` - 角色详情
- `POST /api/role` - 创建角色
- `PUT /api/role` - 更新角色
- `DELETE /api/role/{id}` - 删除角色
- `POST /api/role/{id}/resources` - 绑定资源
- `GET /api/role/{id}/resources` - 获取角色资源

### 资源管理
- `GET /api/resource/list` - 资源列表（分页）
- `GET /api/resource/tree` - 资源树
- `GET /api/resource/all` - 所有资源
- `GET /api/resource/{id}` - 资源详情
- `POST /api/resource` - 创建资源
- `PUT /api/resource` - 更新资源
- `DELETE /api/resource/{id}` - 删除资源
- `PUT /api/resource/{id}/enable` - 启用资源
- `PUT /api/resource/{id}/disable` - 停用资源
- `GET /api/resource/user/menus` - 获取用户菜单
- `GET /api/resource/user/permissions` - 获取用户权限

## 权限控制说明

### 资源类型
- `1` - 菜单
- `2` - 页面
- `3` - 按钮
- `4` - 接口（API）

### 权限检查流程
1. 用户登录后获取 userId
2. 请求头中携带 `X-User-Id` 访问后端接口
3. 后端拦截器检查用户状态
4. 根据用户的角色获取资源权限
5. 判断用户是否有权访问该接口
6. 无权限返回 403 错误

### 前端权限控制
- 动态菜单：根据用户权限动态生成侧边栏菜单
- 动态路由：登录后根据菜单数据动态注册路由
- 按钮权限：使用 `v-permission` 指令控制按钮显示
