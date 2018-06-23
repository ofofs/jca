# 建议

author: houbinbin
time: 2018-06-23 07:45:37

## 测试

测试不够全面，且不够专业。
因为是生成后的 `.class` 文件，大部分应该只是通过眼睛确认。
我准备尝试写一个框架，专门校验生成后的 class 文件信息。

## 新注解

后续可以根据需要调增

- `alpha`

- `beta`

- `stable`

## 逐步增强

一口吃成 180 斤的大壮是不可能的，路要一步步走，很多功能点不完善，在使用中，逐步添加。
我们就是 jcTree 封装者兼使用者，我们用起来很舒服。(挑剔的看)
说明 jcTree 封装有进步。

日拱一卒，功不唐捐。

## 设计缺陷

### JcaUtil

JcaUtil 职责过大，所有的功能基本都在这一个类中，后期维护+拓展将会变得困难。

初步感觉可以进行如下拆分：

```
JcaClassUtil
JcaMethodUtil
JcaFieldUtil
JcaParamUtil
JcaObjUtil
JcaCommonUtil
...
```

### model 

model 中的所有对象，缺乏共有的父类。
所有的 jctree 封装**全部使用工具类写，会导致项目变得单薄，后期必定难以为继**。
建议类似于 JcTree，所有的实体是有共有父类的，便于以后统一拓展。
model 的完善，随着开发的进行，慢慢加。

### 项目发布后的规范化

后续如果发布：

添加 CI+测试覆盖率+maven🎖

- ChangeLog.md (必须)

- wiki (必须)

## 发展的愿景

至少希望发展成为以下四个分支：

- jcTree-core

jcTree 的封装，让开发人员可以简单的参与到 jca 开发中。

- jca

类似于 lombok。
甚至可以细分为：依赖 idea 插件的，不依赖 idea 插件的。

- jca-plugin-idea

idea 相关插件。
是 jca 发展壮大的得力助手

- CTest

Class Test 模块，专门用于辅助生成后的 class 文件测试校验，不局限与本项目




