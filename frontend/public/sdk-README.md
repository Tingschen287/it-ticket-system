# IT Ticket System JS SDK

## 简介

IT Ticket System JS SDK 是一个轻量级的 JavaScript SDK，允许第三方系统轻松集成工单提交功能。

## 快速开始

### 1. 引入 SDK

```html
<script src="https://your-domain.com/it-ticket-sdk.js"></script>
```

### 2. 初始化

```javascript
ITTicketSDK.init({
  apiUrl: 'https://your-domain.com/api',
  token: 'your-jwt-token'
});
```

### 3. 使用方式

#### 打开工单表单

```javascript
ITTicketSDK.openTicketForm({
  title: '报告问题',
  onSubmit: function(error, ticket) {
    if (error) {
      console.error('提交失败:', error);
    } else {
      console.log('提交成功:', ticket);
    }
  }
});
```

#### 编程式创建工单

```javascript
ITTicketSDK.createTicket({
  title: 'Bug Report',
  description: 'Description here',
  type: 'BUG',
  priority: 'HIGH',
  departmentId: 1
})
.then(function(ticket) {
  console.log('Ticket created:', ticket);
})
.catch(function(error) {
  console.error('Failed to create ticket:', error);
});
```

#### 查询工单

```javascript
// 获取单个工单
ITTicketSDK.getTicket(123)
  .then(function(ticket) {
    console.log(ticket);
  });

// 获取我的工单列表
ITTicketSDK.getMyTickets({ page: 0, size: 10 })
  .then(function(result) {
    console.log(result.content);
  });
```

#### 创建嵌入式组件

```html
<div id="ticket-widget"></div>

<script>
ITTicketSDK.createWidget('ticket-widget', {
  buttonText: '提交工单'
});
</script>
```

## API 参考

### 配置选项

| 选项 | 类型 | 必填 | 说明 |
|------|------|------|------|
| apiUrl | string | 是 | API 基础 URL |
| token | string | 是 | JWT 认证令牌 |
| theme | string | 否 | 主题 ('light' 或 'dark') |
| language | string | 否 | 语言代码 |

### 方法

#### init(options)
初始化 SDK。

#### setToken(token)
设置认证令牌。

#### openTicketForm(options)
打开工单提交表单模态框。

#### closeTicketForm()
关闭工单提交表单。

#### createTicket(ticket)
编程式创建工单。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| title | string | 是 | 工单标题 |
| description | string | 否 | 工单描述 |
| type | string | 否 | 类型: BUG, FEATURE, TASK, SUPPORT, OTHER |
| priority | string | 否 | 优先级: LOW, MEDIUM, HIGH, CRITICAL |
| departmentId | number | 否 | 部门 ID |
| assigneeId | number | 否 | 指派人 ID |

#### getTicket(ticketId)
获取工单详情。

#### getMyTickets(params)
获取当前用户的工单列表。

#### createWidget(containerId, options)
在指定容器中创建嵌入式组件。

## 主题定制

SDK 支持通过 CSS 变量自定义主题：

```css
:root {
  --it-ticket-primary: #3b82f6;
  --it-ticket-primary-hover: #2563eb;
  --it-ticket-border: #d1d5db;
  --it-ticket-text: #374151;
}
```

## 版本历史

- 1.0.0 - 初始版本
