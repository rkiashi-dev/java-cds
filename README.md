# java-cds

## JReleaser

JReleaser is configured in `jreleaser.yml` and can be tried from GitHub Actions:

1. Open **Actions → JReleaser → Run workflow**.
2. Keep **dry_run** enabled for the first run.
3. Disable **dry_run** only when you want to create a draft prerelease.

The workflow builds the application with Maven and packages the JAR as a ZIP
asset. The release version is currently `0.0.1`; update both the Maven project
version and `jreleaser.yml` before preparing a real release.
