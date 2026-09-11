# AApi 参数类型与可空性同步记录

已按 Api.kt 同步 38 处差异：2 处基础类型、36 处可空性，涉及 28 个方法。当前 90 个对应方法声明（含两处分页重载）、130 个对应参数的类型和可空性全部一致。

参数优先按请求字段名对应，避免 Kotlin 变量名或参数位置不同造成误配。对应参数转为非空时，原有 `null` 默认值改为 Api.kt 的默认值；Api.kt 没有默认值时移除默认值。其他参数默认值保留原配置。

## 已同步参数

| 方法 | 请求字段 | 同步前 AApi.kt | 同步后 AApi.kt | Api.kt 对照 |
| --- | --- | --- | --- | --- |
| [searchAi](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:103) | `type` | `type: String?` | `type: String` | [type: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:83) |
| [searchAi](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:103) | `key` | `key: String?` | `key: String` | [keywords: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:83) |
| [getTopic](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:140) | `page` | `page: Int?` | `page: Int` | [page: Int](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:134) |
| [getMediaByTag](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:173) | `isHot` | `isHot: String?` | `isHot: String` | [email: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:168) |
| [getMediaByTag](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:173) | `type` | `type: String?` | `type: String` | [content: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:168) |
| [getDateNewMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:186) | `date` | `date: String` | `date: String?` | [date: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:182) |
| [getUserAiMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:225) | `param` | `param: String?` | `param: String` | [param: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:217) |
| [addUserTFLOPS](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:286) | `content` | `content: String?` | `content: String` | [type: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:277) |
| [getUserSetting](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:323) | `type` | `type: String?` | `type: String` | [type: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:304) |
| [payUserMediaNum](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:362) | `num` | `num: Int` | `num: Int?` | [num: Int?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:339) |
| [payUserMediaNum](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:362) | `type` | `type: String` | `type: String?` | [tag: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:339) |
| [getShortMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:372) | `page` | `page: Int?` | `page: Int` | [id: Int](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:350) |
| [getShortMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:372) | `limit` | `limit: Int?` | `limit: Int` | [limit: Int](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:350) |
| [mediaBlack](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:405) | `media_id` | `mediaId: String` | `mediaId: String?` | [mediaId: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:384) |
| [mediaBlack](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:405) | `content` | `content: String` | `content: String?` | [content: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:384) |
| [sendAiTask](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:419) | `type` | `type: String` | `type: String?` | [type: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:395) |
| [saveUserAiMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:448) | `type` | `type: String` | `type: String?` | [type: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:418) |
| [saveUserAiMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:448) | `url` | `url: String` | `url: String?` | [url: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:418) |
| [saveUserAiMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:448) | `tags` | `tags: String` | `tags: String?` | [tags: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:418) |
| [saveUserAiMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:448) | `h_w` | `hW: String` | `hW: String?` | [hw: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:418) |
| [queryAiTask](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:462) | `task_id` | `taskId: String` | `taskId: String?` | [taskId: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:433) |
| [refundAiGroupTask](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:472) | `task_group` | `taskGroup: String` | `taskGroup: String?` | [taskGroup: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:443) |
| [queryAiGroupTask](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:481) | `task_group` | `taskGroup: String` | `taskGroup: String?` | [taskGroup: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:453) |
| [resetAiTask](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:490) | `task_id` | `taskId: String` | `taskId: String?` | [taskId: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:462) |
| [cancelTask](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:499) | `task_id` | `taskId: String` | `taskId: String?` | [taskId: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:472) |
| [getUserAiTask](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:518) | `type` | `type: String` | `type: String?` | [type: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:491) |
| [setHeaderType](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:611) | `pic` | `pic: String?` | `pic: String` | [pic: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:576) |
| [saveCollect](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:630) | `num` | `num: Int?` | `num: Int` | [num: Int](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:595) |
| [removeUserCollect](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:642) | `id` | `id: Int` | `id: String` | [id: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:607) |
| [getUserCollectUrl](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:651) | `url` | `url: String` | `url: String?` | [url: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:617) |
| [getUserCollectMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:660) | `media_id` | `mediaId: String` | `mediaId: String?` | [mediaId: String?](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:627) |
| [getSysConfig](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:760) | `type` | `type: String?` | `type: String` | [type: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:734) |
| [googleLogin](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:806) | `authToken` | `authToken: String?` | `authToken: String` | [token: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:791) |
| [createNoPaymentOrder](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:890) | `query_id` | `queryId: Int` | `queryId: String` | [schemeId: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:885) |
| [shareHotMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:924) | `video_type` | `videoType: String?` | `videoType: String` | [video_type: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:975) |
| [shareHotMedia](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:924) | `media_type` | `mediaType: String?` | `mediaType: String` | [media_type: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:975) |
| [getShareZoneList](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:939) | `media_type` | `mediaType: String?` | `mediaType: String` | [media_type: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:987) |
| [getShareZoneList](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:939) | `video_type` | `videoType: String?` | `videoType: String` | [video_type: String](D:/ASWorkspace/YN/Alviora/Alviora/module/face/src/main/java/com/face/net/Api.kt:987) |

## 已补齐分页重载

- `getBanners(page: Int = 0, limit: Int = 20)`
- `getHots(page: Int = 0, limit: Int = 20)`

两处重载按 Api.kt 的方法顺序插入，使用相同的 POST 表单注解；AApi 继续使用直接接口路径。

## 保留的文档新增参数

7 个方法的 9 个新增参数继续保留；这些参数在 Api.kt 中没有对应项。

| 方法 | 参数 | 请求字段 |
| --- | --- | --- |
| [getUserSetting](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:323) | `key: String?` | `key` |
| [getUserRecordPage](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:564) | `page: Int?` | `page` |
| [getUserRecordPage](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:564) | `limit: Int?` | `limit` |
| [getSysConfig](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:760) | `key: String?` | `key` |
| [accountRegister](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:831) | `deviceInfo: String?` | `device_info` |
| [createNoPaymentOrder](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:890) | `num: Int?` | `num` |
| [createNoPaymentOrder](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:890) | `devicecampaign: String?` | `devicecampaign` |
| [saveMediaShare](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:952) | `imgUrl: String?` | `img_url` |
| [getUserItemLog](D:/ASWorkspace/YN/Alviora/Alviora/a/src/main/java/com/a/net/AApi.kt:971) | `item: String` | `item` |

## 调用与验证

- ARepository.googleLogin 的命名参数同步为 `thirdId`、`authToken`。
- 源码比对：130 个对应参数的类型和可空性无差异，方法顺序一致。
- AApi 独立编译通过；116 个 Retrofit 声明、67 个表单请求（138 个字段）、5 个 JSON 请求体通过本地验证，没有发送网络请求。
- 完整 a 模块编译仍被 ARepository 的缺失接口、参数名称/顺序以及返回类型不匹配错误阻断。
