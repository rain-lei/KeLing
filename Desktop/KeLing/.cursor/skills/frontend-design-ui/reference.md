# 古风·画卷 UI 参考

## 本项目中已采用的约定

- **配色**：见 `app/src/main/java/.../ui/theme/Color.kt`（墨/绢/朱砂/金/石青/石绿/赭石）
- **排版**：见 `Type.kt`，标题/显示用 `KelingSerifFamily`（Serif），正文用 `KelingFontFamily`（Default），行高略松
- **留白**：`KelingSpacing`（horizontalPage 20.dp, verticalPage 16.dp），用于列表 contentPadding 与区块
- **主题**：深色底 + 朱砂主色 + 金强调，见 `Theme.kt`

## 扩展建议

- 若需更强「宣纸」质感：可在根布局加极淡渐变或 drawable 纹理
- 若需竖排或书法字体：可引入 Noto Serif CJK 或自定义字体资源后，在 Type.kt 中切换
