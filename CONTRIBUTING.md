# Contributing to Comic Translator

Terima kasih telah ingin berkontribusi! Panduan ini akan membantu Anda memulai.

## 📋 Proses Kontribusi

### 1. Fork Repository
```bash
# Fork di GitHub: Click "Fork" button
# Clone fork Anda
git clone https://github.com/YOUR_USERNAME/comic-translator.git
cd comic-translator
```

### 2. Create Branch
```bash
# Update main
git checkout main
git pull origin main

# Create feature branch
git checkout -b feature/your-feature-name
```

### 3. Make Changes
- Edit file sesuai kebutuhan
- Follow code style
- Add comments untuk complex logic
- Update tests jika perlu

### 4. Commit Changes
```bash
# Lihat perubahan
git status
git diff

# Commit dengan pesan yang jelas
git commit -m "feat: Add your feature description"
```

### 5. Push & Create PR
```bash
# Push ke fork Anda
git push origin feature/your-feature-name

# Create Pull Request di GitHub
# - Go to original repo
# - Click "Pull requests" → "New Pull Request"
# - Select your branch
# - Add description & submit
```

### 6. Code Review
- GitHub Actions akan otomatis test
- Maintainer akan review code
- Make changes jika diperlukan
- Merge ketika approved

## 🎯 Types of Contributions

### Bug Fixes
1. Create issue describing bug
2. Create branch `fix/bug-name`
3. Fix bug & add tests
4. Create PR dengan detail bug & fix

### New Features
1. Discuss feature di issues first
2. Create branch `feature/feature-name`
3. Implement feature
4. Add tests & documentation
5. Create PR dengan description

### Documentation
1. Fix typos atau improve clarity
2. Add examples
3. Update guides
4. Create PR dengan changes

### Performance
1. Profile code untuk find bottleneck
2. Create branch `perf/optimization-name`
3. Implement optimization
4. Benchmark & document improvement
5. Create PR dengan results

## 📝 Code Style

### Kotlin Style
```kotlin
// Class names: PascalCase
class MainActivity : AppCompatActivity() {
    
    // Function names: camelCase
    fun myFunction(parameter: String) {
        // Local variables: camelCase
        val myVariable = "value"
        var mutableVariable = 0
    }
    
    // Constants: SCREAMING_SNAKE_CASE
    companion object {
        private const val MY_CONSTANT = "value"
    }
}
```

### Naming Conventions
- Classes: `MainActivity`, `OCREngine`
- Functions: `recognizeText()`, `translateText()`
- Variables: `textBlocks`, `translationResult`
- Constants: `FRAME_INTERVAL`, `API_TIMEOUT`

### Comments & Documentation
```kotlin
/**
 * Recognize text dalam Bitmap menggunakan ML Kit
 * 
 * @param bitmap Input image untuk processing
 * @return List of TextBlock dengan detected text
 * 
 * @throws Exception jika ML Kit initialization gagal
 */
suspend fun recognizeText(bitmap: Bitmap): List<TextBlock> {
    // Implementation
}
```

## 🧪 Testing

### Run Tests
```bash
# Run semua tests
./gradlew test

# Run specific test
./gradlew test -x :app:testDebug

# Run dengan coverage
./gradlew test --coverage
```

### Write Tests
```kotlin
@Test
fun testTextRecognition() {
    // Arrange
    val testBitmap = createTestBitmap()
    
    // Act
    val result = OCREngine.recognizeText(testBitmap)
    
    // Assert
    assertTrue(result.isNotEmpty())
    assertEquals("expected text", result[0].text)
}
```

## 📊 Code Review Guidelines

### What Reviewers Look For
- ✅ Code follows style guidelines
- ✅ New features have tests
- ✅ Documentation updated
- ✅ No breaking changes
- ✅ Performance impact considered
- ✅ Security best practices followed

### How to Request Review
1. Create PR dengan clear description
2. Link related issues
3. Add screenshots/videos jika UI change
4. List changes & improvements
5. Mention reviewers jika needed

## 🚀 Workflow

### GitHub Actions Automatically
1. **Build & Test** (android-build.yml)
   - Checks code compiles
   - Runs unit tests
   - Builds APK

2. **Code Quality** (code-quality.yml)
   - Runs Android Lint
   - Checks code quality

3. **PR Checks**
   - Must pass all checks
   - Requires review
   - Can merge only if approved

## 📚 Development Setup

### Prerequisites
```bash
# Java 11+
java -version

# Android Studio or
# gradle
./gradlew --version

# Git
git --version
```

### Setup Environment
```bash
# Clone repository
git clone https://github.com/USERNAME/comic-translator.git

# Open in Android Studio
# File → Open → Select folder

# Build
./gradlew build

# Run tests
./gradlew test
```

## 🔍 Code Review Checklist

Before submitting PR, check:

- [ ] Code compiles without errors
- [ ] All tests pass locally
- [ ] New features have tests
- [ ] Documentation updated
- [ ] No unused imports
- [ ] No TODOs without issue reference
- [ ] Follows code style
- [ ] No sensitive data committed
- [ ] Commit messages are clear
- [ ] Branch is up-to-date dengan main

## 🐛 Bug Report Template

```markdown
## Bug Description
Clear description of what's wrong

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- OS: [e.g., Ubuntu 20.04]
- Java Version: [e.g., 11.0.11]
- Android Version: [e.g., 13]
- Device: [e.g., Pixel 6]

## Screenshots/Logs
Attach relevant logs or screenshots

## Additional Context
Any other info
```

## 💡 Feature Request Template

```markdown
## Description
Clear description of feature

## Use Case
Why do you need this?

## Proposed Solution
How should it work?

## Alternative Approaches
Any other ways to solve this?

## Additional Context
Any other relevant info
```

## 📞 Communication

### Discord/Chat
- Ask questions in discussions
- Share ideas
- Help others

### Issues
- Report bugs
- Request features
- Ask questions

### Pull Requests
- Discuss implementation
- Code review
- Feedback

## 🎓 Resources

### Learning
- [Kotlin Guide](https://kotlinlang.org/)
- [Android Developer Docs](https://developer.android.com/)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Git Guide](https://git-scm.com/)

### Project Docs
- [README.md](README.md) - Overview
- [TECHNICAL_DOCS.md](TECHNICAL_DOCS.md) - Architecture
- [BUILD_GUIDE.md](BUILD_GUIDE.md) - Build process

## ✨ Recognition

Contributors akan di-recognize di:
- README.md contributors section
- Release notes
- Project website

## 📄 License

Dengan berkontribusi, Anda setuju bahwa kontribusi Anda akan di-license di bawah MIT License yang sama dengan project ini.

---

**Contributing Guide Version**: 1.0  
**Last Updated**: December 2025  
**Status**: Active & Welcoming Contributions
