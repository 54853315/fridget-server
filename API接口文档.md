# FridgetServer API 接口文档

## 项目概述

FridgetServer 是一个基于 Spring Boot 的后端服务，提供用户管理、食材管理和食谱推荐功能。

## 基础信息

- **技术栈**: Spring Boot 2.7.11 + Java 11
- **数据库**: MongoDB
- **认证方式**: Spring Security + JWT
- **响应格式**: 统一的 JSON 格式

## 通用响应格式

大部分接口使用统一的 CommonResponse 格式：

```json
{
  "status": "HttpStatus对象",
  "message": "响应消息",
  "result": "具体数据对象"
}
```

## 数据模型定义

### User (用户实体)
```json
{
  "id": "用户唯一标识",
  "name": "用户姓名",
  "userId": "用户登录ID",
  "userPassword": "用户密码",
  "role": "用户角色(USER/ADMIN)",
  "vegan": "素食偏好",
  "meatConsumption": "肉类消费偏好(数值)",
  "fishConsumption": "鱼类消费偏好(数值)",
  "vegeConsumption": "蔬菜消费偏好(数值)",
  "spiciness": "辣度偏好(数值)",
  "allergies": ["过敏信息数组"],
  "userIngredients": ["用户食材列表"]
}
```

### UserIngredient (用户食材)
```json
{
  "id": "食材唯一标识",
  "name": "食材名称",
  "category": "食材分类"
}
```

### UserCookingPreference (用户烹饪偏好)
```json
{
  "id": "偏好设置唯一标识",
  "userId": "关联用户ID",
  "cookingEquipment": ["擅长的厨具列表"],
  "cookingMethods": ["擅长的料理方式列表"],
  "servingSize": "用餐人数",
  "additionalInfo": "其他信息或备注"
}
```

### Recipe (食谱)
```json
{
  "name": "食谱名称",
  "description": "食谱描述",
  "nutrition": {
    "calories": "卡路里",
    "protein": "蛋白质",
    "carbs": "碳水化合物",
    "fat": "脂肪",
    "fiber": "纤维",
    "sugar": "糖分",
    "sodium": "钠含量"
  },
  "ingredients": [
    {
      "name": "食材名称",
      "quantity": "用量"
    }
  ],
  "steps": ["制作步骤数组"],
  "imageUrl": "食谱图片URL",
  "reference": "参考来源",
  "missingIngredients": ["缺少的食材列表"],
  "spiceLevel": "辣度等级(数值)"
}
```

## API 接口概览

| 功能模块 | 接口路径 | 请求方式 | 权限要求 | 描述 |
|---------|---------|---------|---------|-----|
| 用户管理 | `/users` | GET | ADMIN | 获取所有用户 |
| 用户管理 | `/user/create` | POST | 无 | 用户注册 |
| 用户管理 | `/doLogin` | POST | 无 | 用户登录 |
| 用户管理 | `/doLogout` | POST | 登录 | 用户登出 |
| 食材管理 | `/user/ingredients` | GET | 登录 | 获取我的食材 |
| 食材管理 | `/user/ingredients/create` | POST | 登录 | 添加食材 |
| 食材管理 | `/user/ingredients/delete` | DELETE | 登录 | 删除食材 |
| 烹饪偏好 | `/user/cooking-preference` | GET | 登录 | 获取烹饪偏好 |
| 烹饪偏好 | `/user/cooking-preference` | POST | 登录 | 保存烹饪偏好 |
| 烹饪偏好 | `/user/cooking-preference` | DELETE | 登录 | 删除烹饪偏好 |
| 食谱推荐 | `/recipe/recommend` | GET | 登录 | 获取推荐食谱 |

## API 接口详情

### 1. 用户管理接口 (UserController)

#### 1.1 获取所有用户

- **请求方式**: `GET`
- **接口路径**: `/users`
- **权限要求**: 需要 ADMIN 角色
- **请求参数**: 无
- **返回值**: `List<User>` 用户列表

**前端请求示例**:
```javascript
fetch('/users', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
})
```

#### 1.2 用户注册

- **请求方式**: `POST`
- **接口路径**: `/user/create`
- **请求参数**: JSON 格式的 RequestBody

**请求参数结构**:
```json
{
  "name": "用户姓名",
  "userId": "用户ID",
  "userPassword": "用户密码",
  "role": "用户角色枚举值(USER/ADMIN)",
  "vegan": "素食偏好",
  "meatConsumption": 0,
  "fishConsumption": 0,
  "vegeConsumption": 0,
  "spiciness": 0,
  "allergies": ["过敏信息数组"]
}
```

**返回值**:
```json
{
  "status": "CREATED",
  "message": "Sign Up Success!",
  "result": "User对象"
}
```

**前端请求示例**:
```javascript
fetch('/user/create', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    name: "张三",
    userId: "zhangsan",
    userPassword: "password123",
    role: "USER",
    vegan: "no",
    meatConsumption: 3,
    fishConsumption: 2,
    vegeConsumption: 5,
    spiciness: 2,
    allergies: ["花生"]
  })
})
```

#### 1.3 用户登录

- **请求方式**: `POST`
- **接口路径**: `/doLogin`
- **请求参数**: JSON 格式的 RequestBody

**请求参数结构**:
```json
{
  "userId": "用户ID",
  "userPassword": "用户密码"
}
```

**返回值**:
```json
{
  "status": "OK",
  "message": "Login Success!",
  "result": {
    "userId": "用户ID",
    "token": "访问令牌",
    "refreshToken": "刷新令牌"
  }
}
```

**前端请求示例**:
```javascript
fetch('/doLogin', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    userId: "zhangsan",
    userPassword: "password123"
  })
})
```

#### 1.4 用户登出

- **请求方式**: `POST`
- **接口路径**: `/doLogout`
- **请求参数**: JSON 格式的 RequestBody

**请求参数结构**:
```json
{
  "token": "访问令牌"
}
```

**返回值**:
```json
{
  "status": "OK",
  "message": "Logout Success",
  "result": null
}
```

**前端请求示例**:
```javascript
fetch('/doLogout', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    token: accessToken
  })
})
```

### 2. 食材管理接口 (IngredientController)

#### 2.1 获取我的食材

- **请求方式**: `GET`
- **接口路径**: `/user/ingredients`
- **请求参数**: 无
- **返回值**: `List<UserIngredient>` 用户食材列表

**前端请求示例**:
```javascript
fetch('/user/ingredients', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
})
```

#### 2.2 添加食材

- **请求方式**: `POST`
- **接口路径**: `/user/ingredients/create`
- **请求参数**: JSON 数组格式的 RequestBody

**请求参数结构**:
```json
[
  {
    "name": "食材名称",
    "category": "食材分类"
  }
]
```

**返回值**:
```json
{
  "status": "OK",
  "message": "Ingredients added!",
  "result": "List<UserIngredient>"
}
```

**前端请求示例**:
```javascript
fetch('/user/ingredients/create', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify([
    {
      name: "西红柿",
      category: "蔬菜"
    },
    {
      name: "鸡蛋",
      category: "蛋类"
    }
  ])
})
```

#### 2.3 删除食材

- **请求方式**: `DELETE`
- **接口路径**: `/user/ingredients/delete`
- **请求参数**: JSON 数组格式的 RequestBody

**请求参数结构**:
```json
[
  {
    "name": "食材名称",
    "category": "食材分类"
  }
]
```

**返回值**:
```json
{
  "status": "OK",
  "message": "Ingredients removed!",
  "result": "List<UserIngredient>"
}
```

**前端请求示例**:
```javascript
fetch('/user/ingredients/delete', {
  method: 'DELETE',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify([
    {
      name: "西红柿",
      category: "蔬菜"
    }
  ])
})
```

### 3. 用户烹饪偏好接口 (IngredientController)

#### 3.1 获取用户烹饪偏好

- **请求方式**: `GET`
- **接口路径**: `/user/cooking-preference`
- **请求参数**: 无
- **返回值**: `UserCookingPreference` 用户烹饪偏好信息

**返回值结构**:
```json
{
  "status": "OK",
  "message": "Cooking preference retrieved!",
  "result": {
    "id": "偏好设置ID",
    "userId": "用户ID",
    "cookingEquipment": ["炒锅", "烤箱", "蒸锅"],
    "cookingMethods": ["炒", "煮", "烤"],
    "servingSize": "4人",
    "additionalInfo": "其他备注信息"
  }
}
```

**前端请求示例**:
```javascript
fetch('/user/cooking-preference', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
})
```

#### 3.2 创建或更新用户烹饪偏好

- **请求方式**: `POST`
- **接口路径**: `/user/cooking-preference`
- **请求参数**: `CookingPreferenceReqDto`
- **返回值**: `UserCookingPreference` 保存后的烹饪偏好信息

**请求参数结构**:
```json
{
  "cookingEquipment": ["炒锅", "烤箱", "蒸锅", "微波炉"],
  "cookingMethods": ["炒", "煮", "烤", "蒸", "炸"],
  "servingSize": "4人",
  "additionalInfo": "喜欢清淡口味，不吃辣"
}
```

**返回值**:
```json
{
  "status": "OK",
  "message": "Cooking preference saved!",
  "result": "UserCookingPreference对象"
}
```

**前端请求示例**:
```javascript
fetch('/user/cooking-preference', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    cookingEquipment: ["炒锅", "烤箱"],
    cookingMethods: ["炒", "烤"],
    servingSize: "2人",
    additionalInfo: "偏爱简单快手菜"
  })
})
```

#### 3.3 删除用户烹饪偏好

- **请求方式**: `DELETE`
- **接口路径**: `/user/cooking-preference`
- **请求参数**: 无
- **返回值**: 删除成功确认信息

**返回值**:
```json
{
  "status": "OK",
  "message": "Cooking preference deleted!",
  "result": null
}
```

**前端请求示例**:
```javascript
fetch('/user/cooking-preference', {
  method: 'DELETE',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
})
```

### 4. 食谱推荐接口 (RecipeController)

#### 4.1 获取推荐食谱

- **请求方式**: `GET`
- **接口路径**: `/recipe/recommend`
- **权限要求**: 需要登录认证
- **请求参数**: 无
- **返回值**: `List<Recipe>` 推荐食谱列表

**返回值结构**:
```json
[
  {
    "name": "西红柿炒鸡蛋",
    "description": "经典家常菜，营养丰富",
    "nutrition": {
      "calories": "180kcal",
      "protein": "12g",
      "carbs": "8g",
      "fat": "10g",
      "fiber": "2g",
      "sugar": "6g",
      "sodium": "400mg"
    },
    "ingredients": [
      {
        "name": "西红柿",
        "quantity": "2个"
      },
      {
        "name": "鸡蛋",
        "quantity": "3个"
      }
    ],
    "steps": [
      "将西红柿切块",
      "鸡蛋打散",
      "热锅下油，炒鸡蛋",
      "下西红柿翻炒",
      "调味出锅"
    ],
    "imageUrl": "https://example.com/recipe-image.jpg",
    "reference": "AI生成食谱",
    "missingIngredients": [],
    "spiceLevel": 1
  }
]
```

**前端请求示例**:
```javascript
fetch('/recipe/recommend', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
})
```

## 认证说明

### JWT Token 使用

1. **获取 Token**: 通过登录接口获取 `accessToken` 和 `refreshToken`
2. **使用 Token**: 在需要认证的接口请求头中添加：
   ```
   Authorization: Bearer {accessToken}
   ```
3. **Token 刷新**: 当 `accessToken` 过期时，使用 `refreshToken` 获取新的令牌

### 权限控制

- **公开接口**: 用户注册、用户登录
- **需要登录**: 大部分用户相关接口
- **需要 ADMIN 权限**: `/users` 接口

## 错误处理

### 常见错误码

- **400 Bad Request**: 请求参数错误
- **401 Unauthorized**: 未认证或 Token 无效
- **403 Forbidden**: 权限不足
- **404 Not Found**: 资源不存在
- **500 Internal Server Error**: 服务器内部错误

### 错误响应格式

```json
{
  "status": "错误状态码",
  "message": "错误描述信息",
  "result": null
}
```

## 开发注意事项

1. **Content-Type**: 所有 POST/DELETE 请求必须设置 `Content-Type: application/json`
2. **参数验证**: 标注了 `@Valid` 的接口会进行参数验证
3. **Token 管理**: 前端需要妥善保存和管理 JWT Token
4. **错误处理**: 前端需要根据响应状态码进行相应的错误处理
5. **CORS**: 如果前后端分离部署，需要配置 CORS 策略

## 测试建议

1. 使用 Postman 或类似工具进行 API 测试
2. 先测试用户注册和登录流程
3. 获取 Token 后测试其他需要认证的接口
4. 测试各种错误场景和边界条件
