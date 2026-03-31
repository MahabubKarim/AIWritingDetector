package com.mmk.aiwritingdetector.domain.analyzer

/**
 * Comprehensive pattern dictionaries for detecting AI writing characteristics.
 * 
 * Based on:
 * - Pearson PTE AuDITR (Automated Detection of Inauthentic Templated Responses) research
 * - GPTZero, Turnitin, Copyleaks, Originality.ai detection methods
 * - Pangram Labs linguistic research
 * - Academic studies on perplexity/burstiness metrics
 * - Wikipedia AI writing signs documentation
 * 
 * Detection approaches covered:
 * 1. Perplexity - word-level predictability
 * 2. Burstiness - sentence length/complexity variation
 * 3. Template/memorized content detection
 * 4. Structural regularity analysis
 * 5. Token probability distribution
 * 6. Vocabulary patterns (era-specific AI words)
 */
object PatternDictionaries {

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 1: AI VOCABULARY (Era-specific overused words)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * GPT-4 era vocabulary (2023-mid 2024) — the infamous "delve" generation.
     * These words appeared at statistically anomalous rates in ChatGPT output.
     */
    val gpt4EraWords = listOf(
        "delve", "delves", "delving", "delved",
        "tapestry", "tapestries",
        "landscape", "landscapes",
        "intricate", "intricacies",
        "meticulous", "meticulously",
        "pivotal", "testament",
        "underscore", "underscores", "underscoring",
        "vibrant", "enduring",
        "bolstered", "bolster", "bolstering",
        "garner", "garnered", "garnering",
        "interplay", "nuanced", "nuance"
    )

    /**
     * GPT-4o/Claude era vocabulary (mid 2024-2026) — evolved patterns.
     * Detection systems track these as current AI signatures.
     */
    val modernAiWords = listOf(
        "fostering", "showcasing", "highlighting",
        "emphasizing", "enhance", "enhances", "enhanced",
        "align with", "aligns with", "aligned with",
        "crucial", "robust", "comprehensive",
        "leverage", "leveraging", "leveraged",
        "utilize", "utilizing", "utilized",
        "facilitate", "facilitating", "facilitated",
        "paradigm", "synergy", "synergies",
        "holistic", "holistically",
        "streamline", "streamlining", "streamlined",
        "innovative", "cutting-edge", "state-of-the-art",
        "groundbreaking", "transformative", "game-changing"
    )

    /**
     * Highly overused AI nouns — appear far more in AI than human text.
     * From Pangram Labs comprehensive vocabulary analysis.
     */
    val aiOverusedNouns = listOf(
        // Abstract concepts AI loves
        "realm", "realms", "facet", "facets",
        "journey", "quest", "beacon", "catalyst",
        "cornerstone", "linchpin", "nexus", "crux",
        "spectrum", "myriad", "plethora", "gamut",
        "array", "slew", "wealth", "trove",
        "depths", "heights", "fabric",
        // Metaphorical favorites
        "mosaic", "kaleidoscope", "symphony", "dance",
        "interplay", "dynamics", "nuance", "nuances",
        "complexities", "intricacies",
        "implications", "ramifications",
        "underpinnings", "bedrock", "foundation",
        "roadmap", "blueprint", "toolkit", "arsenal",
        // Overused in conclusions
        "testament", "testament to", "reminder"
    )

    /**
     * Highly overused AI verbs — signal patterns.
     */
    val aiOverusedVerbs = listOf(
        "delve", "delving", "delved",
        "navigate", "navigating", "navigated",
        "unravel", "unraveling", "unraveled",
        "illuminate", "illuminating", "illuminated",
        "underscore", "underscoring", "underscored",
        "showcase", "showcasing", "showcased",
        "foster", "fostering", "fostered",
        "harness", "harnessing", "harnessed",
        "leverage", "leveraging", "leveraged",
        "streamline", "streamlining", "streamlined",
        "bolster", "bolstering", "bolstered",
        "empower", "empowering", "empowered",
        "transcend", "transcending", "transcended",
        "embark", "embarking", "embarked",
        "grapple", "grappling", "grappled",
        "resonate", "resonating", "resonated",
        "reverberate", "reverberating",
        "culminate", "culminating", "culminated",
        "epitomize", "epitomizing", "epitomized",
        "exemplify", "exemplifying", "exemplified",
        "encapsulate", "encapsulating"
    )

    /**
     * Highly overused AI adjectives.
     */
    val aiOverusedAdjectives = listOf(
        "multifaceted", "nuanced", "intricate", "complex",
        "comprehensive", "robust", "pivotal", "crucial",
        "paramount", "imperative", "profound", "invaluable",
        "indispensable", "instrumental", "seminal",
        "groundbreaking", "transformative", "revolutionary",
        "unprecedented", "unparalleled", "remarkable",
        "extraordinary", "compelling", "captivating",
        "thought-provoking", "insightful", "holistic",
        "seamless", "dynamic", "vibrant", "resilient",
        "sustainable", "authentic", "genuine",
        "meticulous", "rigorous", "cutting-edge"
    )

    /**
     * AI-typical adverbs — overuse of intensifiers and formal modifiers.
     */
    val aiOverusedAdverbs = listOf(
        "meticulously", "seamlessly", "profoundly",
        "fundamentally", "inherently", "intrinsically",
        "undeniably", "unequivocally", "remarkably",
        "extraordinarily", "increasingly", "significantly",
        "particularly", "especially", "notably",
        "importantly", "crucially", "essentially",
        "ultimately", "effectively", "efficiently",
        "strategically", "holistically", "comprehensively"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 2: PHRASE PATTERNS (Core detection signals)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Hedging words — AI overuses qualifiers to avoid definitive statements.
     * High density = AI signature.
     */
    val hedgingWords = listOf(
        "generally", "typically", "often", "usually", "commonly",
        "tends to", "might", "may", "could", "perhaps",
        "possibly", "probably", "likely", "potentially", "seemingly",
        "somewhat", "relatively", "fairly", "rather", "quite",
        "approximately", "roughly", "essentially", "basically", "virtually",
        "largely", "mostly", "primarily", "mainly", "predominantly",
        "arguably", "conceivably", "presumably", "ostensibly"
    )

    /**
     * Transition phrases — AI overuses formal connectors.
     * Turnitin and Copyleaks specifically track these.
     */
    val transitionPhrases = listOf(
        "furthermore", "moreover", "additionally", "in addition",
        "consequently", "therefore", "thus", "hence", "accordingly",
        "nevertheless", "nonetheless", "however", "on the other hand",
        "in contrast", "conversely", "similarly", "likewise",
        "for instance", "for example", "specifically", "in particular",
        "as a result", "due to this", "because of this",
        "it is worth noting", "it should be noted", "it is important to note",
        "that being said", "with that in mind", "given this",
        "in light of", "taking into account", "considering this",
        "by the same token", "along these lines", "in a similar vein"
    )

    /**
     * Vague attributions — AI cites without actual citations.
     * Major red flag in academic contexts (PTE, IELTS, university essays).
     */
    val vagueAttributions = listOf(
        "studies have shown", "research suggests", "experts believe",
        "many experts", "some researchers", "scientists say",
        "according to experts", "evidence suggests", "data indicates",
        "statistics show", "surveys indicate", "reports suggest",
        "many believe", "some argue", "critics contend",
        "proponents suggest", "advocates claim", "observers note",
        "analysts predict", "commentators suggest", "sources indicate",
        "findings suggest", "scholarship indicates", "literature suggests",
        "consensus holds", "conventional wisdom suggests",
        "it has been argued", "it is widely believed"
    )

    /**
     * Filler phrases — padding that adds no substantive value.
     * PTE specifically penalizes these in Write Essay and Summarize tasks.
     */
    val fillerPhrases = listOf(
        "it is important to", "it is worth mentioning",
        "it goes without saying", "needless to say",
        "it is interesting to note", "it is crucial to understand",
        "in today's world", "in modern society", "in this day and age",
        "at the end of the day", "when all is said and done",
        "the fact of the matter is", "the reality is that",
        "it is widely known", "it is commonly understood",
        "as we all know", "as is well known",
        "in order to", "for the purpose of", "with the aim of",
        "it cannot be denied", "it must be acknowledged",
        "one cannot overstate", "it bears mentioning",
        "it is no secret that", "there is no doubt that"
    )

    /**
     * Formulaic openings — AI loves these clichéd intros.
     * PTE's AuDITR system specifically flags these patterns.
     */
    val formulaicOpenings = listOf(
        "in today's rapidly", "in an era of", "in the modern world",
        "throughout history", "since the dawn of", "from time immemorial",
        "it is no secret that", "there is no doubt that",
        "one cannot deny that", "it is undeniable that",
        "when it comes to", "when we think about",
        "the question of", "the issue of", "the topic of",
        "in recent years", "over the past decade",
        "as technology advances", "as society evolves",
        "in the realm of", "in the world of", "in the field of",
        "in a world where", "in a world of",
        "picture this", "imagine a world",
        "have you ever wondered", "what if i told you",
        // PTE-specific template starters
        "the given image depicts", "the picture illustrates",
        "i have a beautiful picture in front of me",
        "the lecture discusses", "the speaker talks about"
    )

    /**
     * Conclusion telegraphing — AI announces endings explicitly.
     * PTE penalizes "in conclusion" overuse heavily.
     */
    val conclusionTelegraphs = listOf(
        "in conclusion", "to conclude", "in summary", "to summarize",
        "to sum up", "in closing", "finally", "ultimately",
        "all in all", "overall", "on the whole",
        "in the final analysis", "when all is considered",
        "taking everything into account", "to wrap up",
        "as we have seen", "as demonstrated above",
        "as this analysis shows", "as we have discussed",
        "in essence", "at its core", "fundamentally",
        "to recapitulate", "in a nutshell"
    )

    /**
     * Balanced argument markers — AI's formulaic "fairness" pattern.
     * Creates artificially balanced arguments even when inappropriate.
     */
    val balancedArgumentMarkers = listOf(
        "on one hand", "on the other hand",
        "while some argue", "others contend",
        "proponents believe", "opponents argue",
        "supporters claim", "critics counter",
        "advocates suggest", "skeptics maintain",
        "both sides", "from both perspectives",
        "there are advantages and disadvantages",
        "there are pros and cons",
        "it's not about", "not only but also",
        "while it is true that", "it is equally true that"
    )

    /**
     * Dramatic/Emphatic phrases — AI overuses for artificial effect.
     */
    val dramaticPhrases = listOf(
        "stands as a testament", "serves as a reminder",
        "is a testament to", "speaks volumes about",
        "cannot be overstated", "cannot be understated",
        "leaves no stone unturned", "goes above and beyond",
        "pushes the boundaries", "breaks new ground",
        "raises the bar", "sets the standard",
        "paves the way", "opens the door",
        "sheds light on", "brings to light",
        "paints a picture", "tells a story",
        "strikes a chord", "hits home",
        "leaves an indelible mark", "etched in history"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 3: STRUCTURAL PATTERNS (Burstiness/Regularity)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Passive voice indicators.
     * AI overuses passive voice for formal tone.
     */
    val passiveIndicators = listOf(
        "is being", "are being", "was being", "were being",
        "has been", "have been", "had been",
        "is considered", "are considered", "was considered",
        "is believed", "are believed", "was believed",
        "is known", "are known", "was known",
        "is thought", "are thought", "was thought",
        "is said", "are said", "was said",
        "is seen", "are seen", "was seen",
        "is expected", "are expected", "was expected",
        "is required", "are required", "was required",
        "can be", "could be", "should be", "would be", "must be",
        "is deemed", "is regarded", "is viewed",
        "is suggested", "is recommended", "is proposed"
    )

    /**
     * Common AI sentence starters — repetitive patterns.
     * Low variety = low burstiness = AI signature.
     */
    val aiSentenceStarters = listOf(
        "it is", "there is", "there are", "this is",
        "these are", "that is", "one of the",
        "the first", "the second", "the third",
        "another", "additionally", "furthermore",
        "moreover", "however", "therefore",
        "while", "although", "despite",
        "given that", "considering that",
        "in terms of", "with regard to",
        "when it comes to", "as for"
    )

    /**
     * List introduction phrases — AI loves structured lists.
     */
    val listIntroductions = listOf(
        "here are some", "here are a few",
        "the following", "as follows",
        "consider the following", "let's explore",
        "let's examine", "let's look at",
        "there are several", "there are many",
        "some of these include", "these include",
        "key factors include", "important aspects include",
        "the main points are", "to list a few"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 4: TONE & STYLE INDICATORS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Overly positive/enthusiastic language.
     * AI is trained to be helpful and positive, leading to unnatural enthusiasm.
     */
    val overlyPositiveLanguage = listOf(
        "exciting", "amazing", "incredible", "fantastic",
        "wonderful", "excellent", "outstanding", "remarkable",
        "exceptional", "impressive", "invaluable", "indispensable",
        "game-changing", "life-changing", "world-class",
        "top-notch", "first-rate", "cutting-edge",
        "state-of-the-art", "best-in-class", "unparalleled"
    )

    /**
     * Earnest/helpful tone markers — AI being "assistive".
     */
    val earnestToneMarkers = listOf(
        "i hope this helps", "hope this helps",
        "let me know if", "feel free to",
        "don't hesitate to", "i'd be happy to",
        "i'm here to help", "happy to assist",
        "if you have any questions", "should you need",
        "please don't hesitate", "i encourage you to"
    )

    /**
     * Formal/stiff constructions — lack of natural flow.
     * Academic writing AI tends toward excessive formality.
     */
    val formalConstructions = listOf(
        "it is imperative that", "it is essential that",
        "it is necessary to", "it is advisable to",
        "one must", "one should", "one might",
        "it behooves", "it is incumbent upon",
        "in accordance with", "pursuant to",
        "with respect to", "in relation to",
        "pertaining to", "concerning", "regarding"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 5: SPECIFICITY & CREATIVITY INDICATORS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Vague quantifiers — AI avoids specific numbers.
     * Contributes to higher predictability (lower perplexity).
     */
    val vagueQuantifiers = listOf(
        "various", "numerous", "several", "many",
        "countless", "a number of", "a variety of",
        "a range of", "a host of", "a multitude of",
        "a plethora of", "an array of", "a wealth of",
        "some", "few", "multiple", "diverse",
        "a great deal of", "a significant amount of"
    )

    /**
     * Generic time references — AI avoids specific dates.
     */
    val genericTimeReferences = listOf(
        "in recent years", "over time", "throughout history",
        "in the past", "in the future", "moving forward",
        "going forward", "as time goes on", "in due time",
        "over the years", "through the ages", "for centuries",
        "for generations", "in modern times", "in today's world",
        "in the coming years", "in the near future"
    )

    /**
     * AI-typical names — statistically overused in AI-generated fiction.
     * ChatGPT/Claude default to these 60-70% of the time.
     */
    val aiTypicalNames = listOf(
        "emily", "sarah", "michael", "david",
        "alex", "sam", "chris", "jordan",
        "taylor", "morgan", "casey", "jamie",
        "maya", "liam", "emma", "noah",
        "olivia", "ethan", "sophia", "aiden"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 6: CONTRACTION & GRAMMAR PATTERNS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Common contractions — AI tends to AVOID these in formal writing.
     * Lack of contractions = AI signature in conversational contexts.
     */
    val contractionWords = listOf(
        "don't", "doesn't", "didn't", "won't", "wouldn't",
        "can't", "couldn't", "shouldn't", "isn't", "aren't",
        "wasn't", "weren't", "haven't", "hasn't", "hadn't",
        "i'm", "you're", "we're", "they're", "it's",
        "i've", "you've", "we've", "they've",
        "i'll", "you'll", "we'll", "they'll",
        "i'd", "you'd", "we'd", "they'd",
        "that's", "there's", "here's", "what's",
        "who's", "let's", "ain't"
    )

    /**
     * Grammar "perfection" indicators.
     * AI rarely makes typos or uses fragments — suspiciously clean.
     */
    val humanErrorIndicators = listOf(
        // Informal fragments humans use
        "gonna", "wanna", "gotta", "kinda", "sorta",
        // Informal expressions
        "yeah", "yep", "nope", "ok", "okay",
        // Conversational fillers
        "um", "uh", "well", "so", "like",
        // Casual starts (violate "formal grammar")
        "but", "and", "so", "because" // at sentence start
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 7: METACOGNITIVE & REFLECTION PATTERNS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Personal experience markers — AI can't have experiences.
     * Presence of these suggests human authorship.
     */
    val personalExperienceMarkers = listOf(
        "i remember", "i recall", "i once",
        "when i was", "in my experience",
        "personally", "from my perspective",
        "i felt", "i thought", "i realized",
        "it struck me", "it occurred to me",
        "i couldn't help but", "i found myself",
        "my own", "my personal"
    )

    /**
     * AI self-reference patterns — when AI accidentally reveals itself.
     */
    val aiSelfReferencePatterns = listOf(
        "as an ai", "as a language model",
        "i don't have personal", "i cannot",
        "i'm not able to", "i was trained",
        "my training data", "i should note",
        "i should mention", "i want to clarify",
        "as an assistant", "i'm designed to"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 8: PTE-SPECIFIC TEMPLATE PATTERNS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * PTE Describe Image templates — Pearson's AuDITR system flags these.
     * From Pearson's "Test Integrity" research paper.
     */
    val pteDescribeImageTemplates = listOf(
        "i have a beautiful picture in front of me",
        "upon having a closer look",
        "let me have a closer look",
        "i can see colors, shapes, and numbers",
        "overall, the picture is",
        "the given image depicts",
        "the picture shows",
        "looking at the image, i can see",
        "the image illustrates",
        "the graph shows that",
        "according to the graph",
        "as we can see from the image"
    )

    /**
     * PTE Retell Lecture templates — flagged for human review.
     */
    val pteRetellLectureTemplates = listOf(
        "the lecture was about",
        "the speaker talked about",
        "the speaker discussed",
        "the main point of the lecture was",
        "according to the speaker",
        "the lecturer mentioned that",
        "in the lecture, we learned that",
        "the speaker explained that"
    )

    /**
     * PTE Essay templates — highly penalized.
     * From template databases and Pearson warnings.
     */
    val pteEssayTemplates = listOf(
        "in this modern era",
        "in today's competitive world",
        "it is an irrefutable fact that",
        "i strongly believe that",
        "there are myriad reasons for this",
        "to begin with", "first and foremost",
        "last but not least",
        "to conclude, i would like to reiterate",
        "taking everything into consideration"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION 9: COMBINED WEIGHTED PATTERNS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * All AI vocabulary combined for comprehensive checking.
     */
    val allAiVocabulary: List<String> by lazy {
        (gpt4EraWords + modernAiWords + aiOverusedNouns +
         aiOverusedVerbs + aiOverusedAdjectives + aiOverusedAdverbs)
            .distinct()
    }

    /**
     * High-confidence AI indicators — if multiple appear, very likely AI.
     * These are the strongest signals across all detection systems.
     */
    val highConfidenceIndicators = listOf(
        // Vocabulary
        "delve", "tapestry", "landscape", "multifaceted",
        "realm", "plethora", "myriad", "nuanced",
        // Phrases
        "it is important to note", "in today's rapidly",
        "stands as a testament", "cannot be overstated",
        "in conclusion", "furthermore", "moreover",
        "a myriad of", "a plethora of", "the realm of",
        // Structural
        "on one hand", "on the other hand",
        "while some argue", "others contend"
    )

    /**
     * Human writing indicators — presence strongly suggests human authorship.
     * These patterns increase perplexity and burstiness.
     */
    val humanWritingIndicators = listOf(
        // Personal voice
        "i think", "i believe", "i feel", "i guess",
        // Informal hedging
        "honestly", "frankly", "actually", "really",
        // Casual approximators
        "kind of", "sort of", "pretty much", "more or less",
        // Conversational fillers
        "you know", "i mean", "like", "basically",
        // Informal discourse markers
        "anyway", "anyways", "btw", "tbh",
        // Emotional expressions
        "lol", "haha", "omg", "wow",
        // Mild expletives (humans use these, AI avoids)
        "damn", "hell", "crap", "geez"
    )

    /**
     * Sentence variety indicators — humans vary sentence openings more.
     * Starting sentences with conjunctions increases burstiness.
     */
    val humanSentenceVariety = listOf(
        "but", "and", "so", "yet", "or",
        "because", "since", "if", "when", "while",
        "after", "before", "although", "though",
        "suddenly", "finally", "eventually",
        "honestly", "frankly", "actually",
        "look", "listen", "see", "okay"
    )

    /**
     * All templates combined for comprehensive template detection.
     */
    val allTemplates: List<String> by lazy {
        (pteDescribeImageTemplates + pteRetellLectureTemplates + 
         pteEssayTemplates + formulaicOpenings + conclusionTelegraphs)
            .distinct()
    }
}
