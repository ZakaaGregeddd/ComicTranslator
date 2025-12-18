# GitHub Setup Instructions

Panduan lengkap untuk setup Comic Translator di GitHub dengan GitHub Actions CI/CD.

## 📋 Prerequisites

- GitHub account (free tier ok)
- Project files ready (sudah ada)
- Git installed locally

## 🚀 Step-by-Step Setup

### Step 1: Create GitHub Repository

1. **Go to GitHub**: https://github.com/new
2. **Repository Details**:
   - Repository name: `comic-translator`
   - Description: "Real-time screen translation app for comics"
   - Visibility: **Public** (untuk GitHub Actions free tier)
   - Initialize: Do NOT initialize (akan push existing files)
3. **Create Repository**

### Step 2: Prepare Local Repository

```bash
# Navigate ke project folder
cd d:\User\Documents\2025\SEM5\TEST\Translate\ComicTranslator

# Initialize git (jika belum)
git init

# Check git status
git status
```

### Step 3: Add Remote & Push

```bash
# Add GitHub remote
git remote add origin https://github.com/YOUR_USERNAME/comic-translator.git

# Create main branch & commit all files
git branch -M main
git add .
git commit -m "Initial commit: Comic Translator app with GitHub Actions setup"

# Push ke GitHub (first push, set upstream)
git push -u origin main
```

**Expected Output**:
```
[main (root-commit) xxxxx] Initial commit: Comic Translator app
 40 files changed, 10000 insertions(+)
 ...
Enumerating objects: 100, done.
```

### Step 4: Verify Repository

1. Go to: `https://github.com/YOUR_USERNAME/comic-translator`
2. Verify files uploaded
3. Check `.github/workflows/` folder exists

### Step 5: Enable GitHub Actions

1. Repository → **Settings** tab
2. Left sidebar → **Actions** → **General**
3. Under "Actions permissions":
   - Select: **Allow all actions and reusable workflows**
4. Click **Save**

### Step 6: Configure Branch Protection (Optional but Recommended)

1. Repository → **Settings** → **Branches**
2. Click **Add rule** under "Branch protection rules"
3. **Branch name pattern**: `main`
4. ✅ Check: **Require status checks to pass before merging**
5. ✅ Check: **Require code reviews before merging** (set to 1)
6. ✅ Check: **Dismiss stale pull request approvals when new commits are pushed**
7. Click **Create**

### Step 7: First GitHub Actions Run

1. Repository → **Actions** tab
2. Should see **"Android CI Build"** workflow
3. Wait for workflow to complete (~3-5 minutes)

**Expected Result**:
- ✅ Checkout code
- ✅ Setup Java 11
- ✅ Build with Gradle
- ✅ Run tests
- ✅ Build APKs
- ✅ Upload artifacts

## ✅ Verification Checklist

After setup, verify:

- [ ] Repository created on GitHub
- [ ] All files pushed
- [ ] `.github/workflows/` directory exists
- [ ] GitHub Actions enabled
- [ ] First workflow run successful
- [ ] APK artifacts generated
- [ ] Branch protection rules set
- [ ] Badges working (if added to README)

## 📊 File Structure on GitHub

```
comic-translator/
├── .github/
│   ├── workflows/
│   │   ├── android-build.yml       ← Auto build on push
│   │   ├── release.yml              ← Auto release on tag
│   │   └── code-quality.yml         ← Lint checks
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md
│   │   ├── feature_request.md
│   │   └── question.md
│   └── pull_request_template.md
├── app/
│   └── src/main/
│       └── java/com/example/comictranslator/
├── README.md
├── GITHUB_ACTIONS.md                ← This guide
├── CONTRIBUTING.md
├── BUILD_GUIDE.md
├── TECHNICAL_DOCS.md
└── ... (other docs)
```

## 🔄 GitHub Actions Workflows

### Workflow 1: Android CI Build
**Trigger**: Push to `main`/`develop` or Pull Request

**What it does**:
- ✅ Build project
- ✅ Run tests
- ✅ Generate APKs
- ✅ Upload artifacts (30 days retention)

**Artifacts Available**:
- `app-debug.apk` - For testing
- `app-release-unsigned.apk` - For release
- Build reports

### Workflow 2: Release Build
**Trigger**: Push tag (e.g., `v1.0.0`)

**What it does**:
- ✅ Build release APK & AAB
- ✅ Create GitHub Release
- ✅ Upload files to release
- ✅ Available for download

**Usage**:
```bash
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

### Workflow 3: Code Quality
**Trigger**: Push to `main`/`develop` or Pull Request

**What it does**:
- ✅ Run Android Lint
- ✅ Generate quality report
- ✅ Upload lint results

## 📈 Add Status Badge

Add to README.md:

```markdown
# Comic Translator

[![Build Status](https://github.com/YOUR_USERNAME/comic-translator/workflows/Android%20CI%20Build/badge.svg)](https://github.com/YOUR_USERNAME/comic-translator/actions)
[![Quality Gate](https://github.com/YOUR_USERNAME/comic-translator/workflows/Android%20Code%20Quality/badge.svg)](https://github.com/YOUR_USERNAME/comic-translator/actions)
```

## 🔐 Optional: Setup Secrets

For future signing capability, add secrets:

1. Settings → **Secrets and variables** → **Actions**
2. Click **New repository secret**
3. Add:
   - `KEYSTORE_FILE` - Your keystore (base64 encoded)
   - `KEYSTORE_PASSWORD` - Keystore password
   - `KEY_ALIAS` - Alias untuk key
   - `KEY_PASSWORD` - Key password

## 🎯 Common Tasks

### View Build Status
1. Repository → **Actions** tab
2. See all workflow runs
3. Click run untuk details

### Download APK from Build
1. Actions → Click workflow run
2. Scroll ke "Artifacts"
3. Click download

### Create Release
```bash
# Tag release
git tag -a v1.0.0 -m "First release"

# Push tag (triggers release.yml)
git push origin v1.0.0

# Check release di GitHub
# Settings → Releases
```

### Debug Failed Build
1. Actions → Click failed run
2. Expand job steps
3. Look untuk red ❌ steps
4. Click step untuk see logs
5. Read error message

## 🐛 Troubleshooting

### Workflow Not Running?

**Check 1**: Is GitHub Actions enabled?
- Settings → Actions → Permissions = Allow all actions

**Check 2**: File in correct location?
- Must be: `.github/workflows/filename.yml`

**Check 3**: Branch name matches trigger?
- Workflow triggers: `main`, `develop`
- Check branch name di push command

### Build Failed?

**Common Fixes**:
1. Ensure `.github/workflows/*.yml` files valid
2. Check Java version (should be 11)
3. Run locally: `./gradlew build`
4. Check logcat output di GitHub Actions

### Artifacts Not Generated?

**Check**:
- Workflow completed successfully (no red X)
- Scroll down ke "Artifacts" section
- Retention might be expired (30 days default)

## 📞 Additional Resources

### GitHub Documentation
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [Android Gradle Plugin](https://developer.android.com/studio/build)

### Project Documentation
- [README.md](./README.md) - Overview
- [GITHUB_ACTIONS.md](./GITHUB_ACTIONS.md) - Actions guide
- [CONTRIBUTING.md](./CONTRIBUTING.md) - Contributing guide
- [BUILD_GUIDE.md](./BUILD_GUIDE.md) - Build process

## ✨ What You Get

✅ **Automatic Build on Every Push**
- No manual building needed
- Instant feedback
- Build artifacts ready

✅ **Automatic Testing**
- Run tests with every push
- Catch issues early
- Report in PR

✅ **Release Management**
- Create release dengan tag
- Auto-upload to GitHub Release
- Ready for distribution

✅ **Code Quality Checks**
- Lint checks automatic
- Quality reports generated
- Issues identified early

✅ **Status Badges**
- Show build status in README
- Prove quality to users
- Track history

## 🎓 Next Steps

1. ✅ Setup GitHub repository
2. ✅ Enable GitHub Actions
3. ✅ First build runs automatically
4. 👉 Create features & make PRs
5. 👉 Tag releases untuk publish
6. 👉 Monitor build status
7. 👉 Gather community contributions

## 📚 Learn More

- Read [GITHUB_ACTIONS.md](./GITHUB_ACTIONS.md) untuk detailed workflows
- Read [CONTRIBUTING.md](./CONTRIBUTING.md) untuk contribution guidelines
- Read [BUILD_GUIDE.md](./BUILD_GUIDE.md) untuk build details

---

**GitHub Setup Guide Version**: 1.0  
**Last Updated**: December 2025  
**Status**: Ready for Setup
