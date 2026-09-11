# Alviora Android Multi-Module Project

本仓库为 Android 多模块工程。全部模块的职责、依赖关系、启动和业务调用流程见 [项目文档目录](D:/ASWorkspace/YN/Alviora/Alviora/docs/README.md)，其中包含 a 与 face/ui 的功能说明和完整页面索引。

## 1. 项目概览

| 项目项 | 说明 |
|---|---|
| 项目类型 | Android App（Gradle 多模块） |
| 根项目名 | `Alviora` |
| 应用包名 | `com.alviora.generator` |
| 版本号 | `versionName=101`，`versionCode=101` |
| 最低/目标 SDK | `minSdk 24`，`targetSdk 37` |
| 编译 SDK | `compileSdk 37` |
| Java/Kotlin | Kotlin 2.2.10 + Java 21（source/target 兼容） |

## 2. 技术栈

| 层级 | 技术项 |
|---|---|
| Build 工具链 | Gradle Wrapper `9.7.1`，AGP `9.4.0` |
| Android 特性 | DataBinding、BuildConfig、`application` 模块打包 |
| 架构组件 | AndroidX、Lifecycle、ViewModel、Room、Navigation、Paging |
| 网络层 | Retrofit `3.0.0`、OkHttp `4.12.0`、Moshi、Gson |
| 图片/媒体 | Coil、Glide、Picasso、FFmpeg 相关 SDK、CameraX 1.4.1 |
| 存储与解析 | Room、MMKV（注释中未启用）、自定义工具库 |
| 推送/统计/分析 | Firebase（Analytics / Crashlytics / Messaging / Auth / NDK） |
| 广告与归因 | AppLovin（含多广告源 Adapter）、Singular、Install Referrer |
| 代码生成/注解 | Kotlin KAPT + KSP (`2.3.6`) |
| 混淆与打包安全 | AabResGuard、XmlClassGuard |

## 3. 项目结构

```text
Alviora
├─ app/               # 应用壳模块（聚合入口）
├─ a/                 # 业务组合模块
├─ key/               # 第三方 key/config 相关库模块
├─ module/
│  ├─ architecture/   # 基础架构能力层（Base/Net/UI/工具）
│  ├─ face/           # 核心业务模块（AI 人脸/相机/特效/分享等）
│  ├─ main/           # 启动与主流程模块
│  ├─ openzoomlib/    # 图片查看库适配封装
│  ├─ drawboard/      # 绘制/画布相关能力模块
│  └─ selector/       # 资源选择（含图片/媒体选择能力）模块
├─ .github/           # GitHub Actions 打包流水线
└─ gradle/, build.gradle, settings.gradle
```

### 3.1 模块级资源结构（高层）

| 模块 | 代码目录 | 资源目录（节选） |
|---|---|---|
| app | 无 `java` / `kotlin` 代码 | `src/main/res/values` |
| a | `a/src/main/java/com/a` 等 | `color / drawable / layout / menu / mipmap* / values` |
| key | `key/src/main/java/com/key` | `drawable-* / mipmap-* / raw / values` |
| module/architecture | `src/main/java/com/zzkj/structure` | `anim / drawable* / layout / values / xml` |
| module/face | `src/main/java/com/face`，包含 `ad/ adapter/ bean/ database/ ui/ net/ util/ view/ viewmodel` 等 | `anim / color / drawable / layout / menu / values*（多语言） / xml` |
| module/main | `src/main/java/com/face/main` | `layout / values` |
| module/openzoomlib | `src/main/java/com/flyjingfish` | `drawable / layout / mipmap* / values` |
| module/drawboard | `src/main/java/com/king/drawboard` | `values` |
| module/selector | `src/main/java/com/luck/picture/lib` | `anim / drawable* / layout / values* / xml` |

### 3.2 依赖关系图（当前配置）

```text
app
└─> module:main
    ├─> :a
    └─> module:face
        ├─> :key
        ├─> module:architecture
        ├─> module:selector
        ├─> module:openzoomlib
        └─> module:drawboard
a
└─> module:face
    ├─> module:architecture
    └─> module:drawboard
```

说明：`:app` 为聚合壳工程，仅直接依赖 `:module:main`，核心能力逐层向下分发到具体业务模块。

## 4. 架构与职责（当前可观测）

| 层级 | 模块 | 观察到的职责 |
|---|---|---|
| 应用壳 | app | 应用签名与发布参数、Gradle 插件与资源混淆流程、签名配置 |
| 启动与主流程 | module/main | 提供 `WelcomeActivity` 与应用启动入口（`MANIFEST` 中的 `MAIN/LAUNCHER`） |
| 业务聚合 | a | 业务壳/业务入口模块，聚合并复用 `face` |
| 核心业务 | module/face | 主要 UI/业务逻辑模块，包含多个 `ui`、`adapter`、`viewmodel` 与网络子包 |
| 基础设施 | module/architecture | base/lifecycle/net/ui/util 等基础设施和公共能力 |
| 公共库 | module/openzoomlib / selector / drawboard / key | 图片查看、选择、画布、广告/统计 key 相关能力 |

## 5. 构建与打包（已配置）

以下命令在项目根目录的 Windows PowerShell 中执行，使用项目自带的 Gradle Wrapper。当前没有 `GooglePlay` 产品变体，常用构建命令为：

```powershell
# Debug APK
.\gradlew.bat :app:assembleDebug

# Release AAB（源码已完成 XmlClassGuard 时，重新打包使用此命令）
.\gradlew.bat :app:bundleRelease
```

### 5.1 XmlClassGuard 的作用与现有配置

XmlClassGuard 会直接重命名工作区中的类、源码文件和目录，并更新 Manifest、布局 XML 及代码中的相关引用。它会影响应用依赖的业务模块，修改会出现在 `git diff` 中。R8 负责后续字节码压缩和混淆，AabResGuard 负责 AAB 内的资源混淆，三者的映射文件需要分别保存。

根目录 [build.gradle](D:/ASWorkspace/YN/Alviora/Alviora/build.gradle) 已声明插件依赖：

```groovy
buildscript {
    dependencies {
        classpath("com.bytedance.android:aabresguard-plugin:2.0")
        classpath("com.github.liujingxing:XmlClassGuard:2.0")
    }
}
```

[settings.gradle](D:/ASWorkspace/YN/Alviora/Alviora/settings.gradle) 的 `pluginManagement.repositories` 和 `dependencyResolutionManagement.repositories` 已配置本项目使用的插件仓库：

```groovy
maven { setUrl("https://raw.githubusercontent.com/lmk26/AabResGuard/mvn-repo") }
maven { setUrl("https://raw.githubusercontent.com/lmk26/XmlClassGuard/mvn-repo") }
```

[app/build.gradle](D:/ASWorkspace/YN/Alviora/Alviora/app/build.gradle) 中的相关配置如下；这些配置已存在，无需重复添加：

```groovy
plugins {
    // 省略其他插件；此处顺序必须保留
    id "com.bytedance.android.aabResGuard"
    id "xml-class-guard"
}

xmlClassGuard {
    findAabConstraintReferencedIds = true
    // mappingFile = file("xml-class-mapping.txt")
    // packageChange = ["com.ljx.example": "ab.cd"]
    // moveDir = ["com.ljx.example": "ef.gh"]
}
```

| 配置 | 当前状态与用途 |
|---|---|
| `findAabConstraintReferencedIds` | 已开启：查找 ConstraintLayout 的 `constraint_referenced_ids`，加入 AabResGuard 白名单；要求先应用 AabResGuard 插件。 |
| `mappingFile` | 未开启：需要增量混淆、复用既有映射时再配置。执行前先归档与源码基线对应的旧映射，避免把上次映射覆盖后丢失。 |
| `packageChange` | 未开启：用于修改 Manifest 的 `package`；注释中的包名仅为插件示例。 |
| `moveDir` | 未开启：用于指定目录迁移关系；注释中的包名仅为插件示例。 |

### 5.2 执行顺序

**截至 2026-09-11，当前工作区源码已保留上次 XmlClassGuard 的混淆结果。普通重新构建直接执行 `:app:bundleRelease`，不要把再次运行 XmlClassGuard 当作每次打包的必需步骤。**

需要从混淆前源码重新生成完整发布包时：

1. 确认源码基线，保存当前改动，并备份涉及的源码、XML、构建配置及旧映射。建议在独立发布分支或工作目录操作。
2. 执行 `xmlClassGuardRelease`，确认成功后检查类名、目录和 XML 引用的改动。
3. 执行 `:app:bundleRelease`，由现有配置继续完成 R8、AabResGuard 和 Release 签名。
4. 归档最终 AAB、三份映射文件、源码版本及构建日志，并验证生成包。

以下为本机已验证的命令参数。JDK 和缓存目录需按实际环境调整；`--offline` 适用于依赖已缓存的情况，首次构建或缓存不完整时移除此参数：

```powershell
# 仅在准备好混淆前源码及备份后执行
& .\gradlew.bat '-Dorg.gradle.java.home=D:\Android\Android Studio\jbr' '-g' 'D:\WeiDong\.gradle' xmlClassGuardRelease --offline --console=plain
if ($LASTEXITCODE -ne 0) {
    throw 'XmlClassGuard 失败：先检查日志和源码状态，恢复完整基线后再重试。'
}

& .\gradlew.bat '-Dorg.gradle.java.home=D:\Android\Android Studio\jbr' '-g' 'D:\WeiDong\.gradle' ':app:bundleRelease' --offline --console=plain
if ($LASTEXITCODE -ne 0) {
    throw 'Release AAB 构建失败，请检查构建日志。'
}
```

`xmlClassGuardRelease` 仅完成 XML 类名混淆，不会直接产出 AAB。`:app:bundleRelease` 已接入 AabResGuard；当前最终文件名由 `obfuscatedBundleFileName = "duplicated-app.aab"` 指定。

### 5.3 产物与映射文件

下表路径均相对于项目根目录：

| 路径 | 用途 |
|---|---|
| `app/build/outputs/bundle/release/duplicated-app.aab` | 最终经过资源混淆并签名的 Release AAB，交付使用此文件。 |
| `app/build/outputs/bundle/release/app-release.aab` | AabResGuard 处理前的中间 AAB。 |
| `app/xml-class-mapping.txt` | XmlClassGuard 输出的类名、目录映射，追踪源码重命名时使用。 |
| `app/build/outputs/bundle/release/resources-mapping.txt` | AabResGuard 的资源映射。 |
| `app/build/outputs/mapping/release/mapping.txt` | R8 映射，解析混淆堆栈时使用。 |

每次发布应将最终 AAB 与对应的三份映射一起另存，记录版本号、源码提交、校验值及日志。当前 AabResGuard 会过滤 AAB 内的 `BUNDLE-METADATA/**/proguard.map`，因此必须保留外部 R8 映射。

2026-09-10 已完成上述两步并验证最终 AAB，随后由该 AAB 转换 APK 并安装成功。本机详细记录见 [Release 构建记录](D:/ASWorkspace/YN/Alviora/Alviora/build/release-20260910-184040/README.md)；此记录位于本地 `build/` 产物目录，其他检出目录不一定包含。

### 5.4 解析失败与重试

当前 XmlClassGuard 2.0 的 Kotlin 源码解析器会把部分 MIME 字符串中的字符误判为注释。上次构建已使用以下等价写法处理，运行时字符串值不变：

```kotlin
// 原值为 "*/*"
var mime = "*" + "/" + "*"

// 原值为 arrayOf("image/*", "video/*")
pickPictureLauncher.launch(arrayOf("image/" + "*", "video/" + "*"))
```

当日志报 Kotlin 文件解析失败时，也需检查该文件中包含未配对括号、JSON 或转义文本的旧注释。上次还移除了一条被注释掉的旧归因示例，保留了运行逻辑；这是插件解析兼容问题。

**失败任务可能已重命名部分文件。** 应保留失败日志，确认并恢复本次执行前的完整源码基线（包括清理本次生成的重命名副本），应用兼容修改后再重试。`gradlew clean` 只清理构建产物，不能还原这些源码修改；恢复时需保留执行后新增的有效工作，避免覆盖其他改动。

GitHub Actions 的 `.github/workflows/main.yml` 定义了 `pack` 分支上的打包与 Telegram 通知流程（包括 `bundleRelease` + bundle 转 APK）。

## 6. 备注

- 项目资源中出现了 `storePassword` 与私钥路径相关配置（`app/build.gradle` 中的签名信息），实际提交/迁移时建议转移到 CI/环境变量或本地安全配置中管理，避免敏感信息明文化。  
- `app` 模块未直接放置业务代码，代码主要落在 `:module/*` 与 `:a`、`:`key 模块中，便于能力拆分与复用。  

后续若需要，我可以再按“运行-部署-问题清单”方向补充：
1. 各模块职责的更细颗粒说明文档（按 UI/网络/数据/服务分层）  
2. 运行/发布手册（常用 `gradlew` 命令 + 常见失败排查）  
3. 依赖版本清单（可转 CSV 或 BOM 对齐建议）
