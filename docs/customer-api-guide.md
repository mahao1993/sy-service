# 招投标数据查询API

## 接口说明

该接口用于根据 `id` 查询文章数据。

返回内容包括：

- 文章 ID
- 标题
- 正文
- 发布时间

## 接口信息

- 请求方式：`GET`
- 请求路径：`/api/articles/{id}`
- 返回格式：`application/json; charset=UTF-8`

基础地址示例：

```text
http://106.54.36.135
```

完整请求示例：

```text
http://106.54.36.135/api/articles/123
```

## 认证方式

该接口使用 API Key 认证。

请在请求头中传入：

```text
X-API-Key: f9b9-4558-954b-895ad5d3cc80
```

如果 API Key 缺失或错误，服务端将返回 `401 Unauthorized`。

## 路径参数

| 参数名 | 类型 | 是否必填 | 说明 |
| --- | --- | --- | --- |
| `id` | integer | 是 | 文章 ID，必须大于 0 |

## 请求示例

```bash
curl -X GET "http://106.54.36.135/api/articles/123" \
  -H "X-API-Key: f9b9-4558-954b-895ad5d3cc80"
```

## 成功返回

HTTP 状态码：

```text
200 OK
```

返回示例：

```json
{
  "id": 123,
  "title": "示例标题",
  "content": "示例正文",
  "publishTime": "2026-03-19 10:30:00"
}
```

## 返回字段说明

| 字段名 | 类型 | 说明 |
| --- | --- | --- |
| `id` | integer | 文章 ID |
| `title` | string | 文章标题 |
| `content` | string | 文章正文 |
| `publishTime` | string 或 null | 原始发布时间 |

说明：

- `publishTime` 来源于上游数据字段 `website_publish_time`
- `publishTime` 可能为空，即返回 `null`
- `publishTime` 保留源数据格式，可能不是统一的标准时间格式

## 错误返回

### 400 Bad Request

当 `id` 参数不合法时返回。

```json
{
  "timestamp": "2026-03-19T14:10:00+08:00",
  "status": 400,
  "error": "Bad Request",
  "message": "id must be greater than 0"
}
```

### 401 Unauthorized

当 API Key 缺失或错误时返回。

```json
{
  "timestamp": "2026-03-19T14:10:00+08:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid API key."
}
```

### 404 Not Found

当指定 `id` 对应的数据不存在时返回。

```json
{
  "timestamp": "2026-03-19T14:10:00+08:00",
  "status": 404,
  "error": "Not Found",
  "message": "Article not found for id: 123"
}
```

### 500 Internal Server Error

当服务端出现未预期异常时返回。

```json
{
  "timestamp": "2026-03-19T14:10:00+08:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Internal server error"
}
```

## 接入建议

- 请使用 UTF-8 编码处理响应内容
- 请妥善保管 API Key，避免泄露
- 如调用失败，请优先检查请求地址、API Key 和网络连通性
