# AI Writing Detector

A Kotlin Multiplatform (KMP) application that analyzes text to determine the likelihood it was written by AI rather than a human.

![Platforms](https://img.shields.io/badge/platforms-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-purple)
![Compose](https://img.shields.io/badge/Compose%20Multiplatform-1.7.3-green)

## Features

- **Multidimensional Analysis**: Evaluates vocabulary, sentence structure, rhetorical patterns, and statistical properties
- **Real-time Stats**: Live character and word count as you type
- **Detailed Reports**: Comprehensive breakdown of detection signals with evidence
- **Cross-platform**: Runs on Android, iOS, Desktop (macOS/Windows/Linux), and Web (WASM)
- **Elegant UI**: Editorial-inspired design with warm color palette

## Detection Signals

The analyzer looks for patterns commonly found in AI-generated text:

| Signal | Description |
|--------|-------------|
| Sentence Uniformity | AI tends to produce uniform sentence lengths |
| Hedging Words | Excessive qualifiers like "generally", "typically" |
| Transition Phrases | Overuse of formal connectors like "Furthermore" |
| Vague Attributions | Citations without sources like "Studies show" |
| Passive Voice | Higher passive voice ratio than typical human writing |
| Vocabulary Diversity | Low unique word ratio suggests repetitive vocabulary |
| Formulaic Openings | Clichéd intros like "In today's world" |
| Conclusion Telegraphing | Explicit markers like "In conclusion" |

## Getting Started

### Prerequisites

- JDK 17+
- Android Studio Hedgehog+ (for Android)
- Xcode 15+ (for iOS)

### Run Android

```bash
./gradlew :composeApp:assembleDebug
```

Or open in Android Studio and run.

### Run Desktop

```bash
./gradlew :composeApp:run
```

### Run Web (WASM)

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

### Run iOS

Open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.

## Architecture

```
├── domain/
│   ├── model/          # Data models (AnalysisResult, TextStats, etc.)
│   ├── analyzer/       # Core analysis logic & pattern dictionaries
│   └── usecase/        # Business logic (AnalyzeTextUseCase)
├── presentation/
│   ├── screen/         # Compose screens
│   ├── viewmodel/      # MVI ViewModels
│   ├── components/     # Reusable UI components
│   └── theme/          # Colors, typography, theming
└── di/                 # Koin dependency injection modules
```

### Tech Stack

- **UI**: Compose Multiplatform
- **Architecture**: MVI + Clean Architecture
- **DI**: Koin 4.0
- **Navigation**: Voyager
- **Async**: Kotlin Coroutines

## Scoring System

The detector calculates a weighted score from 0.0 (definitely human) to 1.0 (definitely AI):

```
Final Score = Σ (weight × signal_score) / Σ weights
```

Each signal produces a score based on thresholds derived from linguistic research.

### Verdict Categories

| Score Range | Verdict |
|-------------|---------|
| 0.00 - 0.25 | Likely Human |
| 0.25 - 0.40 | Possibly Human |
| 0.40 - 0.60 | Inconclusive |
| 0.60 - 0.75 | Possibly AI |
| 0.75 - 1.00 | Likely AI |

## Limitations

This is an educational project demonstrating text analysis techniques. Important caveats:

1. **No detector is perfect**: AI writing detection is fundamentally challenging
2. **False positives**: Human academic/formal writing may trigger AI signals
3. **Model evolution**: As LLMs improve, their writing becomes less detectable
4. **Context matters**: Technical documentation naturally has different patterns than creative writing

Try testing the detector on famous texts that pre-date LLMs (before 2017) to see how challenging detection truly is!

## License

MIT License - see [LICENSE](LICENSE) for details.

## Contributing

Contributions welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) first.

---

Built with ❤️ using Kotlin Multiplatform
