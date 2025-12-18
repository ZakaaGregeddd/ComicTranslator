# GitHub Actions Guide for Comic Translator

Panduan lengkap untuk setup dan menggunakan GitHub Actions untuk build CI/CD.

## 📋 Apa yang Sudah Disiapkan

### 1. **android-build.yml** - Build & Test Otomatis
**Trigger**: Push ke `main` atau `develop` branch, Pull Request
**Actions**:
- ✅ Checkout code
- ✅ Setup Java 11
- ✅ Build project dengan Gradle
- ✅ Run unit tests
- ✅ Build Debug APK
- ✅ Build Release APK (unsigned)
- ✅ Upload artifacts (APK, reports)
- ✅ Generate build reports

**Output**: 
- `app-debug.apk` - Debug APK
- `app-release-unsigned.apk` - Release APK
- Build reports

### 2. **release.yml** - Release Otomatis
**Trigger**: Push tag ke repository (e.g., `v1.0.0`)
**Actions**:
- ✅ Build Release APK (unsigned)
- ✅ Build AAB (Android App Bundle)
- ✅ Create GitHub Release
- ✅ Upload APK ke release
- ✅ Upload AAB ke release

**Output**:
- GitHub Release dengan APK & AAB
- Ready untuk di-distribute

### 3. **code-quality.yml** - Code Quality Check
**Trigger**: Push ke branch, Pull Request
**Actions**:
- ✅ Run Android Lint
- ✅ Generate quality reports
- ✅ Upload lint results

**Output**:
- Lint report HTML
- Code quality metrics

## 🚀 Quick Setup

### Step 1: Upload ke GitHub

```bash
# Initialize git (jika belum)
cd ComicTranslator
git init

# Add remote repository
git remote add origin https://github.com/USERNAME/comic-translator.git

# Create & checkout main branch
git checkout -b main

# Commit semua files
git add .
git commit -m "Initial commit: Comic Translator app"

# Push ke GitHub
git push -u origin main
```

### Step 2: Enable GitHub Actions

1. Di repository GitHub:
   - Settings → Actions → General
   - Pastikan "Actions permissions" = "Allow all actions and reusable workflows"

2. Repository harus public atau anda harus have Actions enabled

### Step 3: View Build Status

1. Di repository: Click "Actions" tab
2. Lihat workflow runs
3. Klik run untuk detail

## 📊 Workflow Details

### Android Build Workflow (`android-build.yml`)

```yaml
# Triggers
- Push ke main/develop
- Pull Request ke main/develop

# Jobs:
1. Checkout code
2. Setup Java 11
3. Cache Gradle dependencies
4. Make gradlew executable
5. Build dengan Gradle
6. Run tests
7. Build Debug APK
8. Upload Debug APK artifact
9. Build Release APK (unsigned)
10. Upload Release APK artifact
11. Generate reports
12. Upload reports

# Artifacts generated:
- app-debug.apk (30-50 minutes retention)
- app-release-unsigned.apk (30 days)
- build-reports/ (30 days)
```

### Release Workflow (`release.yml`)

```yaml
# Triggers
- Push dengan tag (v1.0.0, v2.0.0, etc)
- Manual workflow_dispatch

# Jobs:
1. Checkout code
2. Setup Java 11
3. Build Release APK (unsigned)
4. Build AAB (Android App Bundle)
5. Extract version dari tag
6. Create GitHub Release
7. Upload APK ke release
8. Upload AAB ke release

# Output:
- GitHub Release page dengan files
- APK dan AAB ready untuk distribute
```

### Code Quality Workflow (`code-quality.yml`)

```yaml
# Triggers
- Push ke main/develop
- Pull Request ke main/develop

# Jobs:
1. Checkout code
2. Setup Java 11
3. Run Android Lint
4. Upload lint results

# Output:
- Lint HTML report
- Code quality metrics
```

## 🔄 Usage Examples

### Example 1: Automatic CI Build

```bash
# 1. Make changes
echo "// New feature" >> app/src/main/java/com/example/comictranslator/MainActivity.kt

# 2. Commit & Push
git add .
git commit -m "Add new feature"
git push origin main

# 3. GitHub Actions automatically:
#    ✅ Triggers android-build.yml
#    ✅ Builds APK
#    ✅ Runs tests
#    ✅ Uploads artifacts
#    ✅ You get notified when done
```

### Example 2: Release Build

```bash
# 1. Tag release
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# 2. GitHub Actions automatically:
#    ✅ Triggers release.yml
#    ✅ Builds Release APK & AAB
#    ✅ Creates GitHub Release
#    ✅ Uploads files to release
#    ✅ Release is available for download
```

### Example 3: Pull Request

```bash
# 1. Create feature branch
git checkout -b feature/new-translation-language

# 2. Make changes & push
git add .
git commit -m "Add Thai language support"
git push origin feature/new-translation-language

# 3. Create PR on GitHub

# 4. GitHub Actions automatically:
#    ✅ Triggers android-build.yml
#    ✅ Verifies build doesn't break
#    ✅ Shows status in PR
#    ✅ PR can only merge if build passes
```

## 📦 Artifacts Management

### Download Artifacts

1. Go to repository → Actions
2. Click workflow run
3. Scroll to "Artifacts" section
4. Download APK/Reports

### Retention Policy

| Artifact | Retention |
|----------|-----------|
| app-debug.apk | 30 days |
| app-release-unsigned.apk | 30 days |
| build-reports | 30 days |
| lint-results | 30 days |

Change in workflow file:
```yaml
retention-days: 90  # Change this value
```

## 🔐 Secrets & Permissions

### For Release Signing (Optional Future Feature)

Add to repository Secrets (Settings → Secrets and variables → Actions):

```
KEYSTORE_FILE: base64-encoded keystore
KEYSTORE_PASSWORD: your-password
KEY_ALIAS: your-alias
KEY_PASSWORD: your-key-password
```

Then use in workflow:
```yaml
- name: Decode Keystore
  run: |
    echo "${{ secrets.KEYSTORE_FILE }}" | base64 -d > keystore.jks
    
- name: Sign Release APK
  run: |
    jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
      -keystore keystore.jks \
      -storepass ${{ secrets.KEYSTORE_PASSWORD }} \
      -keypass ${{ secrets.KEY_PASSWORD }} \
      app/build/outputs/apk/release/app-release-unsigned.apk \
      ${{ secrets.KEY_ALIAS }}
```

## 🐛 Troubleshooting

### Build Failed

**Common Issues**:

1. **"Gradle command not found"**
   ```yaml
   - run: chmod +x gradlew  # Add this step
   ```

2. **"JDK not set"**
   ```yaml
   - uses: actions/setup-java@v3
     with:
       java-version: '11'
   ```

3. **"Dependency download failed"**
   ```yaml
   # Clear cache & rebuild
   - run: ./gradlew clean build
   ```

### View Logs

1. GitHub → Repository → Actions
2. Click failed workflow run
3. Expand job steps untuk lihat logs
4. Search untuk error message

### Debugging

Add debugging output:
```yaml
- name: Debug Info
  run: |
    java -version
    ./gradlew --version
    pwd
    ls -la
```

## 📈 Monitoring & Notifications

### Enable Status Badge

Add to README.md:
```markdown
![Build Status](https://github.com/USERNAME/comic-translator/workflows/Android%20CI%20Build/badge.svg)
```

### Email Notifications

1. GitHub → Settings → Notifications
2. Enable "Email notifications for workflows"

### Slack Integration (Optional)

Add Slack webhook:
```yaml
- name: Notify Slack on Failure
  if: failure()
  uses: slackapi/slack-github-action@v1
  with:
    payload: |
      {
        "text": "❌ Comic Translator build failed!",
        "blocks": [
          {
            "type": "section",
            "text": {
              "type": "mrkdwn",
              "text": "Build Failed: ${{ github.run_id }}"
            }
          }
        ]
      }
  env:
    SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK }}
```

## 🎯 Best Practices

### 1. Branch Protection

Settings → Branches → Add rule:
- Require status checks to pass before merge
- Require builds to succeed (android-build.yml)
- Dismiss stale PRs

### 2. Commit Messages

```
format: type: description

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- test: Test addition
- perf: Performance improvement
- refactor: Code refactoring
- chore: Maintenance

Example:
feat: Add Thai language support
fix: Fix OCR rotation detection
docs: Update README with new features
```

### 3. Pull Request Template

Create `.github/pull_request_template.md`:
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
How was this tested?

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex areas
- [ ] Documentation updated
- [ ] No new warnings generated
```

### 4. Issue Template

Create `.github/ISSUE_TEMPLATE/bug_report.md`:
```markdown
## Bug Description
Clear description of the bug

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- Android Version:
- Device:
- App Version:
```

## 📊 Performance Tips

### Speed Up Builds

1. **Enable Gradle Daemon** (default in GitHub Actions)
2. **Use Gradle cache**:
```yaml
- uses: gradle/gradle-build-action@v2
```

3. **Parallel builds**:
```yaml
- run: ./gradlew build --parallel -x test
```

4. **Skip tests in CI**:
```yaml
- run: ./gradlew build -x test  # For speed, remove -x test for full testing
```

### Reduce APK Size

Edit `app/build.gradle`:
```gradle
android {
    ...
    bundle {
        language.enableSplit = true
        density.enableSplit = true
        abi.enableSplit = true
    }
}
```

## 📚 Advanced Configuration

### Matrix Builds (Test Multiple Versions)

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java-version: [11, 17]
        gradle-version: [7.x, 8.x]
    
    steps:
    - uses: actions/setup-java@v3
      with:
        java-version: ${{ matrix.java-version }}
```

### Scheduled Builds

```yaml
on:
  schedule:
    # Run every day at 2 AM
    - cron: '0 2 * * *'
```

### Build Report HTML

Workflow automatically generates HTML reports di:
- `app/build/reports/lint-results-debug.html`
- Bisa di-upload dan di-view

## 🎓 Learning Resources

- GitHub Actions docs: https://docs.github.com/en/actions
- Android Gradle docs: https://developer.android.com/build
- Workflow syntax: https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions

## 📞 Support

Need help?
1. Check workflow logs (Actions tab)
2. Read error message carefully
3. Check this guide troubleshooting section
4. Google the error message
5. Ask on GitHub Discussions

---

**GitHub Actions Guide Version**: 1.0
**Last Updated**: December 2025
**Status**: Ready for Use
