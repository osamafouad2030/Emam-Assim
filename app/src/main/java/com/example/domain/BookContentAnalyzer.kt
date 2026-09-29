package com.example.domain

import com.example.data.ChapterEntity
import com.example.data.EducationLevel
import com.example.data.LessonEntity
import com.example.data.QuizQuestionEntity
import com.example.data.SkillCategory

data class AnalyzedBookResult(
    val detectedTitle: String,
    val detectedDomainAr: String,
    val detectedDomainEn: String,
    val chaptersCount: Int,
    val generatedLessons: List<LessonEntity>,
    val generatedQuestions: List<QuizQuestionEntity>,
    val beginnerCount: Int,
    val intermediateCount: Int,
    val advancedCount: Int
)

object BookContentAnalyzer {

    val sampleBookManuscriptText: String = """
# فصل: أصول الاستدلال العقلي ومراتب الأدلة في التراث العلمي
الموضوع: منهجية التفكير العلمي والبرهان المنطقي

## المبحث الأول: تعريف الدليل وأقسامه الأولية
الدليل في الاصطلاح هو ما يلزم من العلم به العلم بشيء آخر. وينقسم في المبادئ التأسيسية إلى دليل عقلي محض ودليل حسّي تجريبي. ومن أمثلته الواضحة: الاستدلال بوجود الأثر والنظام الدقيق على وجود المؤثر الحكيم.

## المبحث الثاني: شروط صحة القياس والربط التطبيقي بين المقدمات
لا ينتج القياس التطبيقي نتيجة يقينية إلا إذا سلمت مقدمتاه من التناقض، واتحد الحد الأوسط بين المقدمة الصغرى والكبرى. مثال تطبيقي: كل معدن يتمدد بالحرارة، والنحاس معدن، إذن النحاس يتمدد بالحرارة.

## المبحث الثالث: نقد الشبهات وتحرير محل النزاع في القضايا المركبة
في المسائل المتقدمة التي تتجاذبها الأنظار الفلسفية والعلمية، يجب على الباحث تفكيك الحجة المركبة، وفصل الدعاوى المبرهنة عن الفرضيات الظنية، وتطبيق ميزان النقد المقارن لكشف مغالطة التعميم المتسرع.
    """.trimIndent()

    fun analyzeAndClassifyText(
        rawText: String,
        targetChapterId: Int,
        startingOrderIndex: Int
    ): AnalyzedBookResult {
        val cleanText = rawText.trim().ifEmpty { sampleBookManuscriptText }
        val lines = cleanText.lines().map { it.trim() }.filter { it.isNotEmpty() }

        val titleLine = lines.firstOrNull { it.startsWith("#") }?.removePrefix("#")?.trim()
            ?: lines.firstOrNull()?.take(60)
            ?: "فصل مستورد من الكتاب | Imported Book Chapter"

        // Detect domain
        val isLiteraryOrRhetoric = cleanText.contains("بلاغة") || cleanText.contains("بيان") || cleanText.contains("rhetoric", ignoreCase = true)
        val domainAr = if (isLiteraryOrRhetoric) "أدبي وبلاغي" else "علمي ومنهجي (أصول الفكر)"
        val domainEn = if (isLiteraryOrRhetoric) "Literary & Rhetorical" else "Scientific & Methodological"

        // Split into sections or paragraphs
        val rawSections = cleanText.split(Regex("(?=## )|(?=\n\n)"))
            .map { it.trim() }
            .filter { it.length > 35 }
            .ifEmpty { listOf(cleanText) }

        val generatedLessons = mutableListOf<LessonEntity>()
        val generatedQuestions = mutableListOf<QuizQuestionEntity>()

        rawSections.take(6).forEachIndexed { index, sectionText ->
            val sectionLines = sectionText.lines().map { it.trim() }.filter { it.isNotEmpty() }
            val heading = sectionLines.firstOrNull()?.removePrefix("##")?.removePrefix("#")?.trim()
                ?: "مبحث تحليلي رقم ${index + 1}"
            val body = if (sectionLines.size > 1) sectionLines.drop(1).joinToString("\n") else sectionText

            // 3-Level Pedagogical Classification Criteria:
            // Beginner: Definitions, foundational concepts, introductory terms
            // Intermediate: Rules, cause-effect, practical application, examples
            // Advanced: Critique, comparative deduction, complex synthesis, fallacies
            val level = when {
                sectionText.contains("نقد") || sectionText.contains("متقدم") || sectionText.contains("مغالط") ||
                    sectionText.contains("تعارض") || sectionText.contains("critique", ignoreCase = true) || index >= 2 ->
                    EducationLevel.ADVANCED
                sectionText.contains("تطبيق") || sectionText.contains("شروط") || sectionText.contains("قياس") ||
                    sectionText.contains("علاق") || sectionText.contains("application", ignoreCase = true) || index == 1 ->
                    EducationLevel.INTERMEDIATE
                else -> EducationLevel.BEGINNER
            }

            val levelLabelAr = when (level) {
                EducationLevel.BEGINNER -> "تأسيسي"
                EducationLevel.INTERMEDIATE -> "تطبيقي"
                EducationLevel.ADVANCED -> "استنباطي متقدم"
            }

            val lesson = LessonEntity(
                id = 0,
                chapterId = targetChapterId,
                level = level.code,
                orderIndex = startingOrderIndex + index,
                titleAr = "$heading ($levelLabelAr)",
                titleEn = "$heading (${level.code.lowercase().replaceFirstChar { it.uppercase() }})",
                estimatedMinutes = when (level) {
                    EducationLevel.BEGINNER -> 15
                    EducationLevel.INTERMEDIATE -> 22
                    EducationLevel.ADVANCED -> 30
                },
                objectivesAr = "• استيعاب المرتكزات في: $heading\n• تطبيق المعايير المنهجية للمستوى $levelLabelAr",
                objectivesEn = "• Master key principles in: $heading\n• Apply ${level.code} analytical criteria",
                keyConceptsAr = "المفهوم المحوري: $heading — تم استخلاصه وتصنيفه آلياً وفق معايير الكثافة المفهومية.",
                keyConceptsEn = "Core Concept: $heading — Extracted and classified via structural complexity rubric.",
                contentAr = body,
                contentEn = body,
                exampleTitleAr = "تطبيق مستخرج من سياق النص",
                exampleTitleEn = "Contextual Application from Text",
                exampleBodyAr = body.take(180) + if (body.length > 180) "..." else "",
                exampleBodyEn = body.take(180) + if (body.length > 180) "..." else "",
                exercisePromptAr = "لخّص القضية الأساسية في ($heading) وبيّن كيف ترتبط بالمستوى $levelLabelAr.",
                exercisePromptEn = "Summarize the central proposition in ($heading) and explain its analytical tier."
            )
            generatedLessons.add(lesson)

            val skill = when (level) {
                EducationLevel.BEGINNER -> SkillCategory.COMPREHENSION
                EducationLevel.INTERMEDIATE -> SkillCategory.APPLICATION
                EducationLevel.ADVANCED -> SkillCategory.CRITICAL_THINKING
            }

            generatedQuestions.add(
                QuizQuestionEntity(
                    id = 0,
                    lessonId = 0, // Linked when inserted
                    level = level.code,
                    skillTag = skill.code,
                    questionAr = "ما هي الفكرة المحورية التي يعالجها مبحث ($heading)؟",
                    questionEn = "What is the central thesis addressed in ($heading)?",
                    option1Ar = body.take(65),
                    option2Ar = "إلغاء القواعد المنهجية والاكتفاء بالانطباع",
                    option3Ar = "الفصل التام بين النظرية والتطبيق",
                    option4Ar = "الاعتماد على مقدمات غير مبرهنة",
                    option1En = body.take(65),
                    option2En = "Abandoning methodological rules for guesswork",
                    option3En = "Separating theory entirely from practice",
                    option4En = "Relying on unverified assumptions",
                    correctOptionIndex = 0,
                    explanationAr = "النص يؤصل لهذه القاعدة المنهجية ويربطها بأمثلتها التطبيقية.",
                    explanationEn = "The text establishes this methodological principle and links it to applied evidence."
                )
            )
        }

        return AnalyzedBookResult(
            detectedTitle = titleLine,
            detectedDomainAr = domainAr,
            detectedDomainEn = domainEn,
            chaptersCount = 1,
            generatedLessons = generatedLessons,
            generatedQuestions = generatedQuestions,
            beginnerCount = generatedLessons.count { it.level == EducationLevel.BEGINNER.code },
            intermediateCount = generatedLessons.count { it.level == EducationLevel.INTERMEDIATE.code },
            advancedCount = generatedLessons.count { it.level == EducationLevel.ADVANCED.code }
        )
    }
}
