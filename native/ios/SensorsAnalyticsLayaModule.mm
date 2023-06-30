//
//  SensorsAnalyticsLayaModule.mm
//  SensorsAnalyticsLayaModule
//
//  Created by  储强盛 on 2023/5/23.
//  Copyright © 2023 SensorsAnalyticsLayaModule. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

#if ! __has_feature(objc_arc)
#error This file must be compiled with ARC. Either turn on ARC for the project or use -fobjc-arc flag on this file.
#endif

#import "SensorsAnalyticsLayaModule.h"
#if __has_include(<SensorsAnalyticsSDK/SensorsAnalyticsSDK.h>)
#import <SensorsAnalyticsSDK/SensorsAnalyticsSDK.h>
#else
#import "SensorsAnalyticsSDK.h"
#endif
#import <conchRuntime.h>

@implementation SensorsAnalyticsLayaModule

/*
 server_url:'',
 show_log:true,
 mini:{
   appShow:true,
   appHide:true,
 },
 H5:{},
 app:{
    //通用的
    appStart:true,
    appEnd:true
  },
 super_properties:{},
}
 */

/// 通过配置参数，开启神策 SDK
///
/// @param config 初始化配置
+ (void)initSDK:(NSDictionary *)config {

    if (![config isKindOfClass:[NSDictionary class]]) {
        return;
    }

    NSString *serverURL = config[@"server_url"];
    if (![serverURL isKindOfClass:[NSString class]]) {
        return;
    }

    // 初始化配置
    SAConfigOptions *configOptions = [[SAConfigOptions alloc] initWithServerURL:serverURL launchOptions:nil];

    NSNumber *enableLog = config[@"show_log"];
    if ([enableLog isKindOfClass:[NSNumber class]]) {
        configOptions.enableLog = [enableLog boolValue];
    }

    // 解析公共属性
    NSDictionary *superProperties = config[@"super_properties"];

    /********  App 特有配置解析  ********/
    NSDictionary *appConfig = config[@"app"];
    if (![appConfig isKindOfClass:[NSDictionary class]]) {
        [SensorsAnalyticsSDK startWithConfigOptions:configOptions];

        if ([superProperties isKindOfClass:[NSDictionary class]] && superProperties.count > 0) {
            [SensorsAnalyticsSDK.sharedInstance registerSuperProperties:superProperties];
        }
        return;
    }

    // 全埋点配置解析
    SensorsAnalyticsAutoTrackEventType autoTrackEventType = 0;
    NSNumber *appStart = appConfig[@"app_start"];
    if ([appStart isKindOfClass:[NSNumber class]] && [appStart boolValue]) {
        autoTrackEventType = autoTrackEventType | SensorsAnalyticsEventTypeAppStart;
    }

    NSNumber *appEnd = appConfig[@"app_end"];
    if ([appEnd isKindOfClass:[NSNumber class]] && [appEnd boolValue]) {
        autoTrackEventType = autoTrackEventType | SensorsAnalyticsEventTypeAppEnd;
    }
    configOptions.autoTrackEventType = autoTrackEventType;


    // 开启 SDK
    [SensorsAnalyticsSDK startWithConfigOptions:configOptions];

    // 注册公共属性
    if ([superProperties isKindOfClass:[NSDictionary class]] && superProperties.count > 0) {
        [SensorsAnalyticsSDK.sharedInstance registerSuperProperties:superProperties];
    }
}

/// 调用 track 接口，追踪一个带有属性的 event
///
/// @param eventName 事件名称
///
/// @param properties 属性信息
+ (void)track:(NSString *)eventName properties:(NSDictionary *)properties {
    [SensorsAnalyticsSDK.sharedInstance track:eventName withProperties:properties];
}

/// 设置当前用户的 loginId
///
/// @param eventName 当前用户的 loginId
+ (void)login:(NSString *)loginId {
    [SensorsAnalyticsSDK.sharedInstance login:loginId];
}

/// 注销，清空当前用户的 loginId
+ (void)logout {
    [SensorsAnalyticsSDK.sharedInstance logout];
}

/// 重置当前用户的匿名 ID
///
/// @param anonymousId 当前用户的 anonymousId
+ (void)identify:(NSString *)anonymousId {
    [SensorsAnalyticsSDK.sharedInstance identify:anonymousId];
}

/// 注册公共属性
///
/// 和小程序接口命名一致
+ (void)registerApp:(NSDictionary *)properties {
    [SensorsAnalyticsSDK.sharedInstance registerSuperProperties:properties];
}

/// 注销公共属性
///
/// 和小程序接口命名一致
+ (void)clearAppRegister:(NSString *)property {
    [SensorsAnalyticsSDK.sharedInstance unregisterSuperProperty:property];
}

/// 设置用户的一个或者几个 Profiles
+ (void)setProfile:(NSDictionary *)profileDict {
    [SensorsAnalyticsSDK.sharedInstance set:profileDict];
}

/// 首次设置用户的一个或者几个 Profiles
+ (void)setOnceProfile:(NSDictionary *)profileDict {
    [SensorsAnalyticsSDK.sharedInstance setOnce:profileDict];
}

/// 用于在 App 首次启动时追踪渠道来源，SDK 会将渠道值填入事件属性 $utm_ 开头的一系列属性中
///
/// 和其他端接口命名一致
///
/// @param properties 激活事件的属性
+ (void)trackAppInstall:(NSDictionary *)properties {
    [SensorsAnalyticsSDK.sharedInstance trackAppInstallWithProperties:properties];
}

/// 把数据上报到对应的 SensorsAnalytics 服务器上
+ (void)flush {
    [SensorsAnalyticsSDK.sharedInstance flush];
}

/// 获取预置属性
///
/// 无返回值，需要使用 callback 将内容设置到当前的 js 环境
+ (void)getPresetProperties {
    NSDictionary *properties = [SensorsAnalyticsSDK.sharedInstance getPresetProperties];
    [[conchRuntime GetIOSConchRuntime] callbackToJSWithClass:self.class methodName:@"getPresetProperties" ret:properties];
}

/// 删除本地缓存的全部事件
///
/// 一旦调用该接口，将会删除本地缓存的全部事件，请慎用！
+ (void)deleteAll {
    [SensorsAnalyticsSDK.sharedInstance deleteAll];
}


@end
