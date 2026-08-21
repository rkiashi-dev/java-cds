# Java CDS (Class Data Sharing) 検証ガイド

このドキュメントでは、Spring Boot CLI アプリケーションで Java Class Data Sharing (CDS) を
有効化・検証する方法を説明します。

---

## CDS とは

Java CDS (Class Data Sharing) は、JVM の起動時間とメモリ使用量を削減するための機能です。
クラスのロード情報をアーカイブファイルに事前に書き出し、次回起動時に再利用します。

### 主なメリット
- **起動時間の短縮**: クラスロードのオーバーヘッドを削減 (10–40% 改善の事例あり)
- **メモリ使用量の削減**: クラスデータをプロセス間で共有可能
- **コールドスタートの改善**: コンテナ/サーバーレス環境で特に効果的

---

## ローカルでの実行

### 1. ビルド

```bash
mvn -B package -DskipTests
```

### 2. CDS アーカイブの生成

```bash
JAR=$(ls target/java-cds-*.jar)

java -XX:ArchiveClassesAtExit=application.jsa \
     -Dspring.context.exit=onRefresh \
     -jar "$JAR"
```

> `application.jsa` が生成されます。

### 3. CDS 無効で起動（ベースライン）

```bash
time java -jar "$JAR"
```

### 4. CDS 有効で起動

```bash
time java -XX:SharedArchiveFile=application.jsa -jar "$JAR"
```

---

## Docker での実行

### イメージビルド

```bash
# CDS なしイメージ
docker build --target no-cds -t java-cds:no-cds .

# CDS ありイメージ（ビルドステージでアーカイブ生成）
docker build --target with-cds -t java-cds:with-cds .
```

### コンテナ実行と起動時間計測

```bash
# CDS なし
time docker run --rm java-cds:no-cds

# CDS あり
time docker run --rm java-cds:with-cds
```

---

## パフォーマンス期待値

| 環境                  | 典型的な改善率 |
|-----------------------|----------------|
| ローカル JVM          | 10–30%         |
| Docker コンテナ       | 15–40%         |
| Kubernetes Pod        | 20–40%         |

> 実際の改善はアプリの規模・JVM バージョン・ハードウェアによって異なります。

---

## CDS 有効状態の確認

アプリ起動時に以下のログが出力されます:

```
[CDS] CDS is ENABLED - shared archive is in use
[CDS] JVM arg: -XX:SharedArchiveFile=application.jsa
```

CDS が無効の場合:

```
[CDS] CDS is DISABLED - running without shared archive
```

---

## トラブルシューティング

### `application.jsa` が生成されない

- JDK (JRE ではなく) が必要な場合があります。`eclipse-temurin:17-jdk` を使用してください。
- JVM が `-XX:ArchiveClassesAtExit` をサポートしているか確認: `java -version`

### 起動時に `Could not open/create shared archive file` エラー

- アーカイブファイルのパスが正しいか確認してください。
- アーカイブを生成した JVM バージョンと実行時の JVM バージョンが一致しているか確認してください。

### アーカイブが使われていない

`-Xlog:class+load=info` を追加して、クラスがアーカイブからロードされているか確認:

```bash
java -XX:SharedArchiveFile=application.jsa \
     -Xlog:class+load=info \
     -jar app.jar 2>&1 | grep "shared objects file"
```

---

## 参考リンク

- [JEP 310 - Application Class-Data Sharing](https://openjdk.org/jeps/310)
- [JEP 350 - Dynamic CDS Archives](https://openjdk.org/jeps/350)
- [Spring Boot CDS Support](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.efficient.cds)
