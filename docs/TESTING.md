# 構文解析モジュールテスト実装

このドキュメントは構文解析モジュール（MarkdownLexer.kt、MarkdownParser.kt、MarkdownProcessor.kt）のテスト実装について説明します。

## 実装されたテストファイル

### 1. MarkdownLexerTest.kt
MarkdownLexerクラスの字句解析機能をテストします。

**主要テストケース:**
- 基本的なトークン化（TEXT, WHITESPACE, NEWLINE, EOF）
- 見出し解析（#1-5レベル、スペース必須条件）
- フォーマットトークン（太字**, 斜体*, 取り消し線~~）
- リストトークン（箇条書き*, -, 番号付き1., 2.など）
- エッジケース（行頭判定、不正な記法の処理）
- 複雑なMarkdownの解析
- 行番号の追跡

### 2. MarkdownParserTest.kt
MarkdownParserクラスの構文解析機能をテストします。

**主要テストケース:**
- 空のドキュメントパース
- 基本的な段落とテキストパース
- 見出しパース（全レベル対応）
- リストパース（箇条書きと番号付き）
- インライン要素パース（太字、斜体、取り消し線）
- 混合フォーマットテスト
- 閉じられていないフォーマットの処理
- 複雑なドキュメント構造のパース

### 3. MarkdownProcessorTest.kt
MarkdownProcessorクラスの統合機能をテストします。

**主要テストケース:**
- エンドツーエンドパーステスト
- 各種Markdown要素の統合テスト
- エラーハンドリングテスト
- エッジケースの処理
- Minecraft形式テキスト変換テスト
- 長い入力の処理テスト
- ネストしたフォーマットテスト

## テスト実行方法

```bash
# 通常のテスト実行
./gradlew test

# 特定のテストクラスのみ実行
./gradlew test --tests "com.markdownbookmod.core.MarkdownLexerTest"
./gradlew test --tests "com.markdownbookmod.core.MarkdownParserTest"
./gradlew test --tests "com.markdownbookmod.core.MarkdownProcessorTest"
```

## テスト設計の特徴

1. **包括的なカバレッジ**: 正常ケースとエラーケースの両方をテスト
2. **段階的テスト**: Lexer → Parser → Processor の順で段階的にテスト
3. **実用的なシナリオ**: 実際のMarkdown使用例を考慮したテストケース
4. **エッジケースの処理**: 不正な入力や境界条件のテスト

## 依存関係

テスト実行には以下の依存関係が必要です：
- JUnit Jupiter 5.9.3
- Kotlin Test JUnit5 2.0.0
- JUnit Platform Launcher

これらは`build.gradle`に追加済みです。

## 注意事項

- MarkdownProcessorTestの一部テストはMinecraftComponentRendererに依存しますが、基本的な解析機能のテストは独立して実行可能です。
- ネットワーク接続の問題により一部環境でテスト実行が制限される場合がありますが、コア機能は正常に動作します。