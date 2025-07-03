# CONTRIBUTING.md

## ブランチ運用ルール

MarkdownBookModプロジェクトでは、以下のブランチ運用ルールに従って開発を行います。

### ブランチの作成

- ブランチを切る場合は `release` ブランチから切る
- ブランチ名は `develop/[作業者名]/issue-[issue番号]` とする

### Pull Request の作成

- Pull Requestは `release` ブランチに向けて作成する
- Pull Requestのタイトルは関連するIssueタイトルにすること

### 開発フロー

1. `release` ブランチから新しいブランチを作成
   ```bash
   git checkout release
   git pull origin release
   git checkout -b develop/[作業者名]/issue-[issue番号]
   ```

2. 機能開発・バグ修正を実施

3. コミット・プッシュ
   ```bash
   git add .
   git commit -m "適切なコミットメッセージ"
   git push origin develop/[作業者名]/issue-[issue番号]
   ```

4. `release` ブランチに向けてPull Requestを作成

5. レビュー・マージ

### コミットメッセージ

- 日本語または英語で記載
- 変更内容が分かりやすいように記載
- 関連するIssue番号があれば記載（例：`Fix #123: バグ修正`）

### Issue とPull Request

- Issue作成時は提供されたテンプレートを使用
- Pull Request作成時は提供されたテンプレートを使用
- 適切なラベルの設定
- 関連するIssueとの紐付け

### コードレビュー

- 全てのPull Requestはレビューが必要
- 適切なテストの実施
- コードの品質確保

## Copilot Agent 使用時の注意事項

Copilot Agentを使用してコーディングを依頼する際は、以下のルールを必ず守ってください：

1. ブランチ名は必ず `develop/[作業者名]/issue-[issue番号]` の形式で作成
2. Pull Requestは必ず `release` ブランチに向けて作成
3. Issue・PR作成時は必ずテンプレートを使用
4. 変更内容は最小限に留める
5. 既存のコードスタイルに従う

このルールに従わない場合、Pull Requestは受け入れられない可能性があります。