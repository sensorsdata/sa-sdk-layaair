<!--
 * @Date: 2023-06-01 15:35:11
 * @File: 
-->
## SDK 接入指南 - LAYA 引擎

sensorsDataAPI SDK 实现了对 LAYA 引擎开发的小游戏的埋点接口。您可以通过集成 sensorsDataAPI SDK 向 神策上报数据。

如果您需要详细的使用说明，请参考我们的[官方使用手册](https://manual.sensorsdata.cn/sa/latest/page-120881866.html)。

### 一、概述

sensorsDataAPI SDK Laya3.0 与 Laya2.0 的文件目录结构有所不同请注意存放。

### 二、TypeScript 集成方法

如果您的项目是 TypeScript 工程，接入步骤如下：
1. 修改 tsconfig.json文件，增加 "allowJs": true，允许编译 js 文件。
2. Laya3.0 把 SDK 文件放入 assets 目录,在源码中引用。
3. Laya2.0 把 SDK 放入 src 目录下,在源码中引用。

```js
//tsconfig.json 文件
{
  "compilerOptions": {
  "module": "es6",
  "target": "es6",
  "baseUrl": "../libs",
  "outDir": "../build/src",
  "allowJs": true,
}

//3.0 引入方式
import sensorsData from "../assets/js/sensors-laya.min.esm.js";
 
//2.0 引入方式
import sensorsData from "./sensors-laya.min.esm.js";
```

### 三、JavaScript 集成方法

如果您的项目是 JavaScript 工程，您可以直接将 `sensors-laya.min.esm.js` 放入您的工程中，在源码中引用:
```js
import sensorsData from "./sensors-laya.min.esm.js";
```

### 四、初始化 SDK

```js
// TA SDK 配置对象
var config = {
    server_url:'',
    show_log:true,
    super_properties:{superKey:"value"},
    mini:{
        app_show:true,
        app_hide:true
    },
    app:{
        app_start:true,
        app_end:true,
    }
}

// 创建 TA 实例
sensorsData.init(config);

// 上报一个简单事件, 事件名为 test_event
sensorsData.track('test');
```
