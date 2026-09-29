package com.example.data

object SeedData {

    suspend fun populateIfEmpty(dao: MishkahDao) {
        if (dao.getUsersCount() > 0) return

        val salt = SecurityUtils.generateSalt()
        val defaultHash = SecurityUtils.hashPassword("Mishkah2026!", salt)

        // 1. Seed Users (Student, Second Student for comparison, Instructor, Admin)
        val student1Id = dao.insertUser(
            UserEntity(
                id = 1,
                fullName = "أحمد المنصور | Ahmad Al-Mansour",
                email = "student@mishkah.edu",
                passwordHash = defaultHash,
                passwordSalt = salt,
                role = UserRole.STUDENT.code,
                isEmailVerified = true,
                preferredLang = "ar"
            )
        ).toInt()

        val student2Id = dao.insertUser(
            UserEntity(
                id = 2,
                fullName = "ليلى الهاشمي | Layla Al-Hashimi",
                email = "layla@mishkah.edu",
                passwordHash = defaultHash,
                passwordSalt = salt,
                role = UserRole.STUDENT.code,
                isEmailVerified = true,
                preferredLang = "ar"
            )
        ).toInt()

        dao.insertUser(
            UserEntity(
                id = 3,
                fullName = "د. طارق الرازي | Dr. Tariq Al-Razi",
                email = "instructor@mishkah.edu",
                passwordHash = defaultHash,
                passwordSalt = salt,
                role = UserRole.INSTRUCTOR.code,
                isEmailVerified = true,
                preferredLang = "ar"
            )
        )

        dao.insertUser(
            UserEntity(
                id = 4,
                fullName = "م. سارة العلي | Eng. Sara Al-Ali",
                email = "admin@mishkah.edu",
                passwordHash = defaultHash,
                passwordSalt = salt,
                role = UserRole.ADMIN.code,
                isEmailVerified = true,
                preferredLang = "ar"
            )
        )

        // 2. Seed Chapters (Book Structure)
        dao.insertChapter(
            ChapterEntity(
                id = 1,
                orderIndex = 1,
                titleAr = "الفصل الأول: أصول المعرفة وبناء التصورات",
                titleEn = "Chapter 1: Foundations of Knowledge & Conceptualization",
                categoryAr = "المنطق وأصول الفكر",
                categoryEn = "Logic & Epistemology",
                summaryAr = "يتناول هذا الفصل مراتب الإدراك، الفرق بين التصور والتصديق، وضوابط التعريف الدقيق للمفاهيم العلمية.",
                summaryEn = "Explores cognitive ranks, the distinction between conception and judgment, and rigorous definition rules."
            )
        )

        dao.insertChapter(
            ChapterEntity(
                id = 2,
                orderIndex = 2,
                titleAr = "الفصل الثاني: هندسة البيان وبناء الحجة",
                titleEn = "Chapter 2: Architecture of Rhetoric & Argumentation",
                categoryAr = "البلاغة والبيان",
                categoryEn = "Rhetoric & Eloquence",
                summaryAr = "يدرس مطابقة الكلام لمقتضى الحال، دلالات الألفاظ، وطرق صياغة الحجج البرهانية وكشف المغالطات.",
                summaryEn = "Examines contextual eloquence, semantic precision, demonstrative argumentation, and fallacy detection."
            )
        )

        dao.insertChapter(
            ChapterEntity(
                id = 3,
                orderIndex = 3,
                titleAr = "الفصل الثالث: منهجية البحث وتحليل النصوص",
                titleEn = "Chapter 3: Research Methodology & Textual Synthesis",
                categoryAr = "المناهج والنقد",
                categoryEn = "Methodology & Criticism",
                summaryAr = "يركز على مهارات تفكيك النصوص المركبة، المقارنة النقدية بين المدارس الفكرية، والاستنباط المنهجي.",
                summaryEn = "Focuses on deconstructing complex manuscripts, comparative critical synthesis, and systematic deduction."
            )
        )

        // 3. Seed Lessons across 3 Levels (Beginner, Intermediate, Advanced)
        val lessons = listOf(
            // BEGINNER LEVEL (المستوى المبتدئ)
            LessonEntity(
                id = 1,
                chapterId = 1,
                level = EducationLevel.BEGINNER.code,
                orderIndex = 1,
                titleAr = "مراتب الإدراك: التصور والتصديق",
                titleEn = "Cognitive Ranks: Conception & Judgment",
                estimatedMinutes = 15,
                objectivesAr = "• التمييز بين التصور الساذج والتصديق الجازم\n• تحديد أركان القضية الخبرية في النص العلمي",
                objectivesEn = "• Distinguish between simple conception and affirmative judgment\n• Identify components of a propositional statement",
                keyConceptsAr = "التصور (Conception): إدراك المفرد من غير حكم عليه بنفي أو إثبات.\nالتصديق (Judgment): إدراك النسبة بين طرفين مع الحكم بوقوعها أو لا وقوعها.",
                keyConceptsEn = "Conception: Grasping a single concept without affirming or negating.\nJudgment: Affirming or negating a relationship between subject and predicate.",
                contentAr = "يقسم العلماء العلم الحادث إلى قسمين رئيسيين هما: التصور والتصديق. فإذا سمعت كلمة (مشكاة) أو (علم) وفهمت معناها المفرد دون أن تحكم عليها بشيء، فهذا يسمى تصوراً. أما إذا قلت: (العلمُ نورٌ يهدي البصيرة)، فقد ربطت بين المحكوم عليه (العلم) والمحكوم به (نور) بنسبة ثبوتية، وهذا هو التصديق.",
                contentEn = "Scholars divide acquired knowledge into two foundational pillars: Conception (Tasawwur) and Judgment (Tasdiq). Comprehending a standalone term such as 'Lamp' or 'Knowledge' without making a claim is Conception. Asserting 'Knowledge illuminates the intellect' binds a subject and predicate with an affirmative judgment.",
                exampleTitleAr = "تحليل أمثلة من التراث العلمي",
                exampleTitleEn = "Analyzing Classical Text Examples",
                exampleBodyAr = "١. عبارة (الذهب معدن نفيس): تصديق لأنها تتضمن حكماً بالإثبات.\n٢. عبارة (ما هي البلاغة؟): طلب تصور لأنها تستفسر عن ماهية المفرد.",
                exampleBodyEn = "1. 'Gold is a precious metal': Judgment, as it affirms a proposition.\n2. 'What is eloquence?': Inquiry for Conception, seeking the essence of a single concept.",
                exercisePromptAr = "صنّف العبارات الآتية في دفترك الذهني: (العدل أساس العمران) و(مفهوم الجاذبية).",
                exercisePromptEn = "Classify: 'Justice is the foundation of civilization' vs. 'The concept of gravity'."
            ),
            LessonEntity(
                id = 2,
                chapterId = 1,
                level = EducationLevel.BEGINNER.code,
                orderIndex = 2,
                titleAr = "شروط التعريف المنطقي (الحد والرسم)",
                titleEn = "Rules of Logical Definition",
                estimatedMinutes = 18,
                objectivesAr = "• معرفة شرطي الجمع والمنع في التعريفات\n• تجنب الدور والتعريف بالأخفى",
                objectivesEn = "• Master inclusiveness and exclusiveness in definitions\n• Avoid circularity and obscure terms",
                keyConceptsAr = "التعريف الجامع المانع: ما يدخل فيه جميع أفراد المعرَّف ويخرج منه جميع الأغيار.\nالدور المنطقي: تعريف الشيء بنفسه.",
                keyConceptsEn = "Inclusive & Exclusive Definition: Encompasses all instances of the defined term while excluding non-instances.\nCircular Definition: Defining a term using itself.",
                contentAr = "لا يستقيم البحث العلمي إلا بتحديد المصطلحات. ويشترط في التعريف الصحيح أن يكون جامعاً لأفراده مانعاً من دخول غيرها فيه، وأن يكون أوضح من المعرَّف، خالياً من الألفاظ المشتركة أو المجازية غير القرينة.",
                contentEn = "Rigorous scholarship begins with precise terminology. A valid definition must be inclusive of all members of the class, exclusive of outsiders, clearer than the term defined, and free of circular references.",
                exampleTitleAr = "نقد التعريفات الناقصة",
                exampleTitleEn = "Critiquing Flawed Definitions",
                exampleBodyAr = "تعريف (الشمس بأنها كوكب يطلع نهاراً) ثم تعريف (النهار بأنه وقت طلوع الشمس) يقع في خطأ الدور المنطقي.",
                exampleBodyEn = "Defining 'Daytime' as 'when the sun rises' while defining 'Sun' as 'the star that rises in Daytime' commits a circular fallacy.",
                exercisePromptAr = "استخرج الخطأ في تعريف (العلم هو معرفة المعلوم).",
                exercisePromptEn = "Identify the logical flaw in defining 'Knowledge' as 'knowing what is known'."
            ),
            LessonEntity(
                id = 3,
                chapterId = 2,
                level = EducationLevel.BEGINNER.code,
                orderIndex = 3,
                titleAr = "الفصاحة والبلاغة ومطابقة مقتضى الحال",
                titleEn = "Fluency, Eloquence & Contextual Fit",
                estimatedMinutes = 20,
                objectivesAr = "• التفريق بين فصاحة الكلمة وبلاغة الكلام\n• فهم ركيزة (لكل مقام مقال)",
                objectivesEn = "• Differentiate word fluency from discourse eloquence\n• Understand contextual appropriateness",
                keyConceptsAr = "الفصاحة: خلوص اللفظ من تنافر الحروف والغرابة ومخالفة القياس.\nالبلاغة: تأدية المعنى الجليل واضحاً بعبارة صحيحة فصيحة ملائمة للمقام.",
                keyConceptsEn = "Fluency (Fasahah): Clarity of words free from phonetic dissonance or grammatical anomaly.\nEloquence (Balaghah): Conveying noble meaning in a manner tailored to the audience's state.",
                contentAr = "الفصاحة وصفٌ للكلمة والكلام والمتكلم، وتتحقق بسلامة اللفظ من التنافر الصوتي والغرابة. أما البلاغة فمدارها على مطابقة الكلام لمقتضى الحال مع فصاحته؛ فخطاب المبتدئ يقتضي الإيضاح والتفصيل، وخطاب الخبير يقتضي الإيجاز والإشارة.",
                contentEn = "While fluency concerns phonetic harmony and lexical clarity, true eloquence requires matching discourse to the audience's cognitive state: elaboration for novices, concise allusion for masters.",
                exampleTitleAr = "موازنة بين الإيجاز والإطناب",
                exampleTitleEn = "Balancing Conciseness and Elaboration",
                exampleBodyAr = "في مقام التحذير السريع نقول: (الحذرَ الحذرَ!) بالإيجاز، وفي مقام التعليم نشرح الأسباب والنتائج بالتفصيل.",
                exampleBodyEn = "In urgent warning, brevity ('Beware!') is eloquent; in foundational instruction, structured elaboration is required.",
                exercisePromptAr = "متى يكون الإطناب (التفصيل) أبلغ من الإيجاز؟",
                exercisePromptEn = "When is detailed elaboration more eloquent than brevity?"
            ),

            // INTERMEDIATE LEVEL (المستوى المتوسط)
            LessonEntity(
                id = 4,
                chapterId = 2,
                level = EducationLevel.INTERMEDIATE.code,
                orderIndex = 4,
                titleAr = "دلالات الألفاظ: المطابقة والتضمن والالتزام",
                titleEn = "Semantic Signification: Direct, Partial & Implied",
                estimatedMinutes = 22,
                objectivesAr = "• تحليل مستويات الدلالة الثلاثة في النصوص\n• توظيف دلالة الالتزام في الاستنباط",
                objectivesEn = "• Analyze the three tiers of semantic signification\n• Apply implicature in textual analysis",
                keyConceptsAr = "دلالة المطابقة: دلالة اللفظ على تمام معناه الموضوع له.\nدلالة التضمن: دلالته على جزء معناه.\nدلالة الالتزام: دلالته على أمر خارجي لازم له ذهناً.",
                keyConceptsEn = "Complete Signification (Mutabaqah): Word denotes its full referent.\nPartial (Tadammun): Denotes an internal part.\nImplied (Iltizam): Denotes a necessary mental concomitant.",
                contentAr = "تتفاوت دلالة الألفاظ على المعاني في دقتها؛ فلفظ (المدرسة) يدل بالمطابقة على المبنى التعليمي كاملاً، ويدل بالتضمن على الفصول الدراسية وحدها لأنها جزء منه، ويدل بالالتزام على وجود معلمين وطلاب وإن لم يُذكروا صراحةً للزوم ذلك عقلاً وعرفاً.",
                contentEn = "Words signify meaning across three analytical layers: 'University' signifies the entire institution by direct match, the lecture halls alone by partial inclusion, and the presence of scholars by mental implication.",
                exampleTitleAr = "تحليل دلالي لنص بلاغي",
                exampleTitleEn = "Semantic Analysis of Literary Discourse",
                exampleBodyAr = "قولهم (فلانٌ كثيرُ الرماد): يدل بالالتزام العقلي والعرفي على شجاعة الكرم وكثرة الضيافة.",
                exampleBodyEn = "Saying 'His hearth has abundant ashes' implies generosity and frequent hospitality through necessary cultural implication.",
                exercisePromptAr = "حلل دلالات لفظ (المكتبة) وفق المستويات الثلاثة.",
                exercisePromptEn = "Deconstruct the term 'Library' across all three semantic layers."
            ),
            LessonEntity(
                id = 5,
                chapterId = 1,
                level = EducationLevel.INTERMEDIATE.code,
                orderIndex = 5,
                titleAr = "القياس المنطقي والاستقراء التجريبي",
                titleEn = "Syllogistic Deduction & Empirical Induction",
                estimatedMinutes = 25,
                objectivesAr = "• بناء القياس البرهاني من مقدمتين صحيحتين\n• التمييز بين الاستقراء التام والاستقراء الناقص",
                objectivesEn = "• Construct valid syllogisms from sound premises\n• Distinguish complete from incomplete induction",
                keyConceptsAr = "القياس (Deduction): الانتقال من الكلي إلى الجزئي.\nالاستقراء (Induction): تتبع الجزئيات للوصول إلى قاعدة كلية عامة.",
                keyConceptsEn = "Deduction: Moving from universal principles to specific instances.\nInduction: Observing particulars to establish a general law.",
                contentAr = "يعتمد العقل العلمي على جناحين: القياس الذي ينطلق من القواعد الكلية لتطبيقها على المفردات، والاستقراء الذي يفحص الظواهر الجزئية بالتجربة والملاحظة ليستخلص منها قانوناً عاماً. وتتكامل الطريقتان في بناء العلوم التطبيقية والإنسانية.",
                contentEn = "Scientific inquiry flies on two wings: Deduction applies universal axioms to specific cases, whereas Induction observes empirical particulars to formulate universal laws.",
                exampleTitleAr = "تكامل القياس والاستقراء في المختبر",
                exampleTitleEn = "Integrating Deduction and Induction",
                exampleBodyAr = "فحص مئات العينات من المعادن وملاحظة تمددها بالحرارة هو (استقراء)، ثم الحكم بأن قطعة حديد جديدة ستتمدد بالحرارة هو (قياس).",
                exampleBodyEn = "Testing hundreds of metals expanding under heat is Induction; predicting a new iron rod will expand is Deduction.",
                exercisePromptAr = "صغ قياساً منطقياً يثبت أن (تنظيم الوقت يرفع جودة التحصيل).",
                exercisePromptEn = "Formulate a syllogism proving time management improves mastery."
            ),
            LessonEntity(
                id = 6,
                chapterId = 3,
                level = EducationLevel.INTERMEDIATE.code,
                orderIndex = 6,
                titleAr = "تفكيك النص العلمي وبناء الخريطة المفاهيمية",
                titleEn = "Textual Deconstruction & Concept Mapping",
                estimatedMinutes = 24,
                objectivesAr = "• استخراج القضية المركزية والأدلة المساندة\n• تحويل الفصول المطولة إلى شبكات مفاهيمية",
                objectivesEn = "• Extract central theses and supporting evidence\n• Convert dense chapters into structured concept maps",
                keyConceptsAr = "القضية المركزية (Central Thesis): الفكرة المحورية التي يدور حولها الفصل.\nالروابط المنطقية: علاقات السببية، الشرط، والتفريع.",
                keyConceptsEn = "Central Thesis: The core proposition anchoring a chapter.\nLogical Connectors: Causal, conditional, and hierarchical links.",
                contentAr = "قراءة الكتب العلمية بفاعلية تتطلب تجاوز السرد السطحي إلى تفكيك البنية العميقة للنص: ما هي الدعوى الأساسية التي يطرحها المؤلف؟ وما هي الأدلة النقلية أو العقلية أو التجريبية التي أسندها بها؟ وكيف ترتبط الأقسام الفرعية بالأصل الكلي؟",
                contentEn = "Effective scholarly reading requires deconstructing a text's deep architecture: identifying the author's core claim, evaluating empirical or rational evidence, and mapping hierarchical sub-clauses.",
                exampleTitleAr = "تفكيك مقدمة ابن خلدون",
                exampleTitleEn = "Deconstructing Ibn Khaldun's Muqaddimah",
                exampleBodyAr = "الدعوى: العمران البشري ضروري. الدليل: الإنسان مدني بالطبع يحتاج للتعاون في تحصيل الغذاء والأمن. النتيجة: قيام الاجتماع والسلطان.",
                exampleBodyEn = "Claim: Human civilization is necessary. Evidence: Humans require cooperative division of labor for survival and defense. Conclusion: Societal organization.",
                exercisePromptAr = "حدد الدعوى والدليل والنتيجة في أي فقرة علمية تختارها.",
                exercisePromptEn = "Identify the claim, evidence, and conclusion in a scholarly paragraph."
            ),

            // ADVANCED LEVEL (المستوى المتقدم)
            LessonEntity(
                id = 7,
                chapterId = 2,
                level = EducationLevel.ADVANCED.code,
                orderIndex = 7,
                titleAr = "كشف المغالطات المنطقية ونقد الحجج المركبة",
                titleEn = "Detecting Logical Fallacies & Complex Critique",
                estimatedMinutes = 30,
                objectivesAr = "• تشخيص مغالطة المصادرة على المطلوب والاحتكام للمجهول\n• تفنيد الإيهام البلاغي بالحجة البرهانية",
                objectivesEn = "• Diagnose begging the question and false dilemma fallacies\n• Refute rhetorical sophistry with demonstrative proof",
                keyConceptsAr = "المصادرة على المطلوب (Begging the Question): جعل النتيجة نفسها جزءاً من المقدمات.\nمغالطة رجل القش (Straw Man): تشويه حجة الخصم ليسهل نقضها.",
                keyConceptsEn = "Begging the Question: Assuming the conclusion within the premises.\nStraw Man Fallacy: Distorting an opponent's argument to refute a weaker version.",
                contentAr = "في المستوى المتقدم من التحليل، لا يكتفي الباحث بفهم ظاهر الحجة، بل يختبر سلامة انتقالها المنطقي. كثيراً ما تُكسى المغالطات ثوباً بلاغياً جذاباً؛ كأن يُفترض المطلوب إثباته ضمن المقدمات، أو تُحصر الخيارات في ثنائية زائفة لا ثالث لها.",
                contentEn = "Advanced analytical mastery demands testing logical validity beneath persuasive rhetoric—exposing circular premises, false dichotomies, and non-sequitur leaps.",
                exampleTitleAr = "تشريح مغالطة الثنائية الزائفة",
                exampleTitleEn = "Dissecting a False Dichotomy",
                exampleBodyAr = "القول: (إما أن تحفظ الكتاب كاملاً عن ظهر قلب، أو أنك لم تستفد منه شيئاً) يتجاهل مراتب الفهم والتحليل والتطبيق.",
                exampleBodyEn = "Claiming 'Either you memorize the entire book verbatim or you learned nothing' ignores comprehension, synthesis, and application.",
                exercisePromptAr = "حلل نصاً جدلياً واستخرج منه مغالطتين مع تصويبهما المنهجي.",
                exercisePromptEn = "Analyze a polemical text, isolate two fallacies, and reconstruct valid reasoning."
            ),
            LessonEntity(
                id = 8,
                chapterId = 3,
                level = EducationLevel.ADVANCED.code,
                orderIndex = 8,
                titleAr = "الاستنباط المقارن والجمع بين النصوص المتعارضة ظاهراً",
                titleEn = "Comparative Deduction & Reconciling Apparent Contradictions",
                estimatedMinutes = 32,
                objectivesAr = "• إعمال قواعد الجمع والتخصيص والتقييد\n• تحرير محل النزاع بدقة منهجية",
                objectivesEn = "• Apply contextual harmonization and specification rules\n• Isolate the exact locus of dispute (Tahrir Mahall al-Niza')",
                keyConceptsAr = "تحرير محل النزاع: فصل مواضع الاتفاق عن موضع الاختلاف الحقيقي.\nإعمال النصين أولى من إهمال أحدهما: الجمع بين القواعد باختلاف السياق أو الشرط.",
                keyConceptsEn = "Isolating the Locus of Dispute: Separating agreed premises from the exact point of contention.\nContextual Harmonization: Reconciling two principles by distinguishing their domains.",
                contentAr = "من أرقى المهارات البحثية ما يسمى (تحرير محل النزاع) عند دراسة رأيين علميين يبدوان متعارضين. فكثيراً ما يكون الخلاف لفظياً أو ناتجاً عن اختلاف زاوية النظر؛ فإذا حُدِّد السياق والشرط زال التعارض الظاهري وتكاملت المعرفة.",
                contentEn = "One of the highest scholarly competencies is isolating the true locus of dispute when two theories appear contradictory. Often, apparent conflict dissolves once scope, definitions, and boundary conditions are specified.",
                exampleTitleAr = "تحرير محل النزاع في مناهج التعليم",
                exampleTitleEn = "Harmonizing Pedagogical Theories",
                exampleBodyAr = "الجمع بين (أهمية التلقين التأسيسي) و(التعلم بالاكتشاف): الأول ضروري في المبادئ والاصطلاحات، والثاني ضروري في التطبيقات والمسائل.",
                exampleBodyEn = "Reconciling direct instruction with discovery learning: foundational axioms require structured transmission, while problem-solving thrives on guided discovery.",
                exercisePromptAr = "طبّق قاعدة (تحرير محل النزاع) على مسألة علمية خلافية في تخصصك.",
                exercisePromptEn = "Apply locus-of-dispute analysis to a debated scientific question."
            ),
            LessonEntity(
                id = 9,
                chapterId = 3,
                level = EducationLevel.ADVANCED.code,
                orderIndex = 9,
                titleAr = "التأليف المنهجي وصناعة النظريات التعليمية",
                titleEn = "Systematic Authorship & Theory Construction",
                estimatedMinutes = 35,
                objectivesAr = "• تصميم معمارية معرفية متدرجة من المبادئ إلى الغايات\n• صياغة المعايير التقييمية للمستويات الثلاثة",
                objectivesEn = "• Architect progressive knowledge systems from axioms to synthesis\n• Formulate multi-tier evaluation rubrics",
                keyConceptsAr = "التدرج المعرفي (Scaffolding): الانتقال المنظم من البسيط المحسوس إلى المركب المعقول.\nالأصالة المنهجية: إضافة بناء تحليلي جديد فوق التراث السابق.",
                keyConceptsEn = "Cognitive Scaffolding: Structured progression from concrete fundamentals to abstract synthesis.\nMethodological Originality: Extending foundational heritage with rigorous new frameworks.",
                contentAr = "يبلغ الباحث ذروة الإتقان حين ينتقل من استهلاك المعرفة إلى هندستها وتأليفها؛ فيرتب المقاصد، ويؤسس المبادئ، ويقسم المطالب تقسيماً عقلياً حاصراً يراعي التدرج من المبتدئ إلى المتوسط فالمنتهي.",
                contentEn = "Mastery culminates when the scholar transitions from knowledge consumer to knowledge architect—structuring axioms, chapters, and evaluative rubrics across progressive mastery tiers.",
                exampleTitleAr = "هندسة كتاب مِشْكَاة التفاعلي",
                exampleTitleEn = "Architecting the Mishkah Curriculum",
                exampleBodyAr = "تقسيم كل باب علمي إلى: ١. ضبط المفاهيم (مبتدئ)، ٢. تحليل العلاقات والأمثلة (متوسط)، ٣. النقد والاستنباط (متقدم).",
                exampleBodyEn = "Structuring every discipline into: 1. Conceptual Precision (Beginner), 2. Relational Analysis (Intermediate), 3. Critical Synthesis (Advanced).",
                exercisePromptAr = "صمّم مخططاً من ٣ مستويات لتدريس مهارة جديدة من اختيارك.",
                exercisePromptEn = "Design a 3-tier curriculum blueprint for a discipline of your choice."
            )
        )
        lessons.forEach { dao.insertLesson(it) }

        // 4. Seed Interactive Quiz Questions (2 questions per lesson across all 9 lessons = 18 rich questions)
        seedQuestions(dao)

        // 5. Seed Realistic Student Progress, Attempts & Smart Alerts for Analytics Dashboard
        seedAnalyticsAndAlerts(dao, student1Id, student2Id)
    }

    private suspend fun seedQuestions(dao: MishkahDao) {
        val questions = listOf(
            // Lesson 1 (Beginner)
            QuizQuestionEntity(
                lessonId = 1, level = EducationLevel.BEGINNER.code, skillTag = SkillCategory.COMPREHENSION.code,
                questionAr = "ما هو المصطلح العلمي لإدراك المعنى المفرد من غير حكم عليه بنفي أو إثبات؟",
                questionEn = "What is the term for grasping a single concept without affirming or negating a claim?",
                option1Ar = "التصور (Conception)", option2Ar = "التصديق (Judgment)", option3Ar = "الاستقراء (Induction)", option4Ar = "القياس (Syllogism)",
                option1En = "Conception (Tasawwur)", option2En = "Judgment (Tasdiq)", option3En = "Induction", option4En = "Syllogism",
                correctOptionIndex = 0,
                explanationAr = "التصور هو إدراك المفرد فقط دون إصدار حكم، بينما التصديق يتضمن حكماً بنسبة ثبوتية أو سلبية.",
                explanationEn = "Conception grasps a standalone concept without asserting a proposition, whereas Judgment affirms or negates."
            ),
            QuizQuestionEntity(
                lessonId = 1, level = EducationLevel.BEGINNER.code, skillTag = SkillCategory.APPLICATION.code,
                questionAr = "أي العبارات الآتية تُعدّ مثالاً على (التصديق)؟",
                questionEn = "Which of the following expressions represents a Judgment (Tasdiq)?",
                option1Ar = "فهم معنى كلمة (البلاغة)", option2Ar = "العلمُ يرفعُ شأنَ صاحبه", option3Ar = "مفهوم المثلث الهندسي", option4Ar = "ما هو المنطق؟",
                option1En = "Understanding the word 'Eloquence'", option2En = "Knowledge elevates its seeker", option3En = "The concept of a triangle", option4En = "What is logic?",
                correctOptionIndex = 1,
                explanationAr = "جملة (العلم يرفع شأن صاحبه) جملة خبرية تتضمن حكماً بثبوت الرفعة للعلم، فهي تصديق.",
                explanationEn = "'Knowledge elevates its seeker' asserts an affirmative relationship between subject and predicate."
            ),
            // Lesson 2 (Beginner)
            QuizQuestionEntity(
                lessonId = 2, level = EducationLevel.BEGINNER.code, skillTag = SkillCategory.COMPREHENSION.code,
                questionAr = "ما المقصود بكون التعريف (جامعاً مانعاً)؟",
                questionEn = "What does it mean for a logical definition to be 'inclusive and exclusive'?",
                option1Ar = "أن يكون طويلاً ومفصلاً", option2Ar = "أن يشمل جميع أفراد المعرَّف ويمنع دخول غيرها", option3Ar = "أن يستخدم ألفاظاً مجازية", option4Ar = "أن يعرّف الشيء بنفسه",
                option1En = "It is lengthy and poetic", option2En = "It includes all instances of the concept and excludes non-instances", option3En = "It uses metaphorical language", option4En = "It defines the term using itself",
                correctOptionIndex = 1,
                explanationAr = "الجامع هو ما يجمع كل أفراد المفهوم، والمانع هو ما يمنع دخول الأغيار فيه.",
                explanationEn = "Inclusiveness covers all valid members of the concept; exclusiveness bars unrelated items."
            ),
            QuizQuestionEntity(
                lessonId = 2, level = EducationLevel.BEGINNER.code, skillTag = SkillCategory.ANALYSIS.code,
                questionAr = "تعريف (الحركة بأنها عدم السكون) و(السكون بأنه عدم الحركة) يقع في خطأ:",
                questionEn = "Defining 'Motion as lack of rest' while defining 'Rest as lack of motion' commits:",
                option1Ar = "الدور المنطقي (Circularity)", option2Ar = "الاستقراء التام", option3Ar = "دلالة التضمن", option4Ar = "الإيجاز البلاغي",
                option1En = "Circular Definition (Dawr)", option2En = "Complete Induction", option3En = "Partial Signification", option4En = "Rhetorical Brevity",
                correctOptionIndex = 0,
                explanationAr = "توقف معرفة الأول على الثاني وتوقف الثاني على الأول هو الدور المنطقي المخل بالتعريف.",
                explanationEn = "Making Concept A depend on Concept B while B depends on A creates a circular definition."
            ),
            // Lesson 3 (Beginner)
            QuizQuestionEntity(
                lessonId = 3, level = EducationLevel.BEGINNER.code, skillTag = SkillCategory.COMPREHENSION.code,
                questionAr = "ما هو الضابط الجوهري للبلاغة عند علماء البيان؟",
                questionEn = "What is the foundational criterion of Eloquence (Balaghah)?",
                option1Ar = "استخدام الألفاظ الغريبة النادرة", option2Ar = "مطابقة الكلام لمقتضى الحال مع فصاحته", option3Ar = "تطويل الجمل دائماً", option4Ar = "الاقتصار على الإيجاز في كل مقام",
                option1En = "Using archaic rare vocabulary", option2En = "Matching fluent discourse to the context and audience state", option3En = "Always writing long sentences", option4En = "Using brevity in every situation",
                correctOptionIndex = 1,
                explanationAr = "البلاغة هي مطابقة الكلام الفصيح لمقتضى حال المخاطَب والمقام.",
                explanationEn = "Eloquence requires adapting fluent speech to the specific context and audience readiness."
            ),
            // Lesson 4 (Intermediate)
            QuizQuestionEntity(
                lessonId = 4, level = EducationLevel.INTERMEDIATE.code, skillTag = SkillCategory.ANALYSIS.code,
                questionAr = "دلالة لفظ (الكتاب) على (الأوراق والغلاف معاً) هي دلالة:",
                questionEn = "The signification of the word 'Book' referring to both pages and cover together is:",
                option1Ar = "مطابقة (Complete Match)", option2Ar = "تضمن (Partial Inclusion)", option3Ar = "التزام (Mental Implication)", option4Ar = "مجاز مرسل",
                option1En = "Complete Signification (Mutabaqah)", option2En = "Partial Inclusion (Tadammun)", option3En = "Mental Implication (Iltizam)", option4En = "Metonymy",
                correctOptionIndex = 0,
                explanationAr = "لأن اللفظ دلّ على تمام المعنى الموضوع له وهو مجموع الأوراق والغلاف.",
                explanationEn = "It refers to the complete referent for which the term was coined."
            ),
            QuizQuestionEntity(
                lessonId = 4, level = EducationLevel.INTERMEDIATE.code, skillTag = SkillCategory.APPLICATION.code,
                questionAr = "دلالة لفظ (السقف) على وجود (الجدران الحاملة له) تُصنَّف ضمن:",
                questionEn = "The word 'Roof' signifying the existence of load-bearing 'Walls' is an example of:",
                option1Ar = "دلالة المطابقة", option2Ar = "دلالة التضمن", option3Ar = "دلالة الالتزام العقلي", option4Ar = "المشترك اللفظي",
                option1En = "Complete Signification", option2En = "Partial Inclusion", option3En = "Implied Signification (Iltizam)", option4En = "Homonymy",
                correctOptionIndex = 2,
                explanationAr = "الجدران خارجة عن ماهية السقف لكنها لازمة له عقلاً وعادةً، فهي دلالة التزام.",
                explanationEn = "Walls are external to the definition of a roof but mentally necessary for it to stand."
            ),
            // Lesson 5 (Intermediate)
            QuizQuestionEntity(
                lessonId = 5, level = EducationLevel.INTERMEDIATE.code, skillTag = SkillCategory.DEDUCTION.code,
                questionAr = "الانتقال من فحص جزئيات متعددة لاستخلاص قانون علمي عام يسمى:",
                questionEn = "Moving from examining multiple specific instances to formulate a general law is:",
                option1Ar = "القياس الصوري", option2Ar = "الاستقراء (Induction)", option3Ar = "التصور الساذج", option4Ar = "التمثيل البلاغي",
                option1En = "Formal Syllogism", option2En = "Induction (Istiqra')", option3En = "Simple Conception", option4En = "Rhetorical Analogy",
                correctOptionIndex = 1,
                explanationAr = "الاستقراء هو تتبع الجزئيات للوصول إلى حكم كلي عام.",
                explanationEn = "Induction tracks empirical particulars to establish a universal rule."
            ),
            // Lesson 6 (Intermediate)
            QuizQuestionEntity(
                lessonId = 6, level = EducationLevel.INTERMEDIATE.code, skillTag = SkillCategory.ANALYSIS.code,
                questionAr = "عند تفكيك نص علمي، ما هي الوظيفة الأساسية للخريطة المفاهيمية؟",
                questionEn = "When deconstructing a scholarly text, what is the primary purpose of a concept map?",
                option1Ar = "نسخ النص حرفياً", option2Ar = "إبراز العلاقات الهرمية والسببية بين القضية المركزية والأدلة", option3Ar = "حذف الأمثلة التطبيقية", option4Ar = "التركيز على زخرفة الألفاظ",
                option1En = "Copying the text verbatim", option2En = "Visualizing hierarchical and causal links between thesis and evidence", option3En = "Omitting all practical examples", option4En = "Focusing only on decorative phrasing",
                correctOptionIndex = 1,
                explanationAr = "الخريطة المفاهيمية تكشف البنية المنطقية والروابط السببية بين الأفكار الرئيسية والفرعية.",
                explanationEn = "Concept maps expose the underlying relational and causal architecture of arguments."
            ),
            // Lesson 7 (Advanced)
            QuizQuestionEntity(
                lessonId = 7, level = EducationLevel.ADVANCED.code, skillTag = SkillCategory.CRITICAL_THINKING.code,
                questionAr = "تشويه حجة الخصم وتبسيطها بشكل مخلّ ليسهل الرد عليها يُعرف بمغالطة:",
                questionEn = "Distorting and oversimplifying an opponent's thesis to easily refute it is known as:",
                option1Ar = "المصادرة على المطلوب", option2Ar = "مغالطة رجل القش (Straw Man)", option3Ar = "الاستقراء الناقص", option4Ar = "تحرير محل النزاع",
                option1En = "Begging the Question", option2En = "Straw Man Fallacy", option3En = "Incomplete Induction", option4En = "Isolating the Dispute",
                correctOptionIndex = 1,
                explanationAr = "مغالطة رجل القش تقوم على استبدال حجة الخصم القوية بنسخة مشوهة يسهل نقضها.",
                explanationEn = "The Straw Man fallacy replaces a nuanced proposition with a caricature to attack it."
            ),
            QuizQuestionEntity(
                lessonId = 7, level = EducationLevel.ADVANCED.code, skillTag = SkillCategory.DEDUCTION.code,
                questionAr = "جعل النتيجة المطلوب إثباتها مقدمةً ضمنية في الدليل نفسه هو:",
                questionEn = "Embedding the very conclusion to be proven as an assumed premise is:",
                option1Ar = "البرهان اللمي", option2Ar = "المصادرة على المطلوب (Begging the Question)", option3Ar = "القياس الشرطي", option4Ar = "السبر والتقسيم",
                option1En = "Causal Demonstration", option2En = "Begging the Question (Musadarah)", option3En = "Conditional Syllogism", option4En = "Eliminative Division",
                correctOptionIndex = 1,
                explanationAr = "المصادرة على المطلوب تفترض صحة النتيجة مسبقاً داخل المقدمات دون برهان مستقل.",
                explanationEn = "Begging the question assumes the truth of the conclusion inside the premise itself."
            ),
            // Lesson 8 (Advanced)
            QuizQuestionEntity(
                lessonId = 8, level = EducationLevel.ADVANCED.code, skillTag = SkillCategory.CRITICAL_THINKING.code,
                questionAr = "ما هي الخطوة الأولى منهجياً عند دراسة قولين علميين يبدوان متعارضين؟",
                questionEn = "What is the first methodological step when analyzing two seemingly contradictory scholarly claims?",
                option1Ar = "ردّ القولين معاً", option2Ar = "تحرير محل النزاع وفصل مواضع الاتفاق عن موضع الاختلاف", option3Ar = "الاعتماد على الشهرة فقط", option4Ar = "تجاهل السياق التاريخي واللغوي",
                option1En = "Rejecting both claims immediately", option2En = "Isolating the exact locus of dispute and separating points of agreement", option3En = "Relying solely on popularity", option4En = "Ignoring linguistic context",
                correctOptionIndex = 1,
                explanationAr = "تحرير محل النزاع يكشف هل الخلاف حقيقي أم لفظي ناتج عن اختلاف السياق والشرط.",
                explanationEn = "Isolating the locus of dispute clarifies whether disagreement is substantive or merely contextual."
            ),
            // Lesson 9 (Advanced)
            QuizQuestionEntity(
                lessonId = 9, level = EducationLevel.ADVANCED.code, skillTag = SkillCategory.DEDUCTION.code,
                questionAr = "ما هو الأساس المنهجي لتقسيم المحتوى التعليمي إلى (مبتدئ، متوسط، متقدم)؟",
                questionEn = "What is the pedagogical rationale for structuring content into Beginner, Intermediate, and Advanced tiers?",
                option1Ar = "التدرج المعرفي من ضبط التصورات إلى تحليل العلاقات ثم الاستنباط والنقد", option2Ar = "تقسيم الصفحات بالتساوي عددياً فقط", option3Ar = "تقديم المسائل الخلافية المعقدة قبل المفاهيم الأساسية", option4Ar = "فصل الأمثلة عن القواعد نهائياً",
                option1En = "Cognitive scaffolding from concept mastery to relational analysis and critical deduction", option2En = "Dividing page counts strictly into equal thirds", option3En = "Presenting complex disputes before basic definitions", option4En = "Removing examples from rules",
                correctOptionIndex = 0,
                explanationAr = "البناء العلمي الرصين يتدرج من التصورات الأساسية إلى التطبيق والتحليل ثم الملكة النقدية والاستنباطية.",
                explanationEn = "Sound curriculum architecture scaffolds learners from foundational concepts to analysis and finally synthesis."
            )
        )
        questions.forEach { dao.insertQuestion(it) }
    }

    private suspend fun seedAnalyticsAndAlerts(dao: MishkahDao, student1Id: Int, student2Id: Int) {
        val now = System.currentTimeMillis()
        val dayMs = 86_400_000L

        // Student 1 (Ahmad): Strong in Beginner (92%), Good in Intermediate (78%), Struggling slightly in Advanced (58% with multiple attempts)
        val student1Progress = listOf(
            LessonProgressEntity("${student1Id}_1", student1Id, 1, EducationLevel.BEGINNER.code, 100, 920, 95, 1, true, now - 6 * dayMs),
            LessonProgressEntity("${student1Id}_2", student1Id, 2, EducationLevel.BEGINNER.code, 100, 1100, 90, 2, true, now - 5 * dayMs),
            LessonProgressEntity("${student1Id}_3", student1Id, 3, EducationLevel.BEGINNER.code, 100, 1050, 92, 1, true, now - 4 * dayMs),
            LessonProgressEntity("${student1Id}_4", student1Id, 4, EducationLevel.INTERMEDIATE.code, 100, 1450, 82, 2, true, now - 3 * dayMs),
            LessonProgressEntity("${student1Id}_5", student1Id, 5, EducationLevel.INTERMEDIATE.code, 100, 1620, 74, 3, true, now - 2 * dayMs),
            LessonProgressEntity("${student1Id}_6", student1Id, 6, EducationLevel.INTERMEDIATE.code, 75, 1180, 78, 1, false, now - 2 * dayMs),
            LessonProgressEntity("${student1Id}_7", student1Id, 7, EducationLevel.ADVANCED.code, 100, 2100, 62, 3, true, now - 1 * dayMs),
            LessonProgressEntity("${student1Id}_8", student1Id, 8, EducationLevel.ADVANCED.code, 55, 1750, 54, 3, false, now - 3600_000L)
        )
        student1Progress.forEach { dao.upsertProgress(it) }

        val student1Attempts = listOf(
            StudentAttemptEntity(userId = student1Id, lessonId = 1, level = EducationLevel.BEGINNER.code, scorePercent = 95, correctCount = 2, totalQuestions = 2, timeSpentSeconds = 420, attemptNumber = 1, missedSkillTagsCsv = "", masteredSkillTagsCsv = "COMPREHENSION,APPLICATION", completedAt = now - 6 * dayMs),
            StudentAttemptEntity(userId = student1Id, lessonId = 2, level = EducationLevel.BEGINNER.code, scorePercent = 75, correctCount = 1, totalQuestions = 2, timeSpentSeconds = 510, attemptNumber = 1, missedSkillTagsCsv = "ANALYSIS", masteredSkillTagsCsv = "COMPREHENSION", completedAt = now - 5 * dayMs),
            StudentAttemptEntity(userId = student1Id, lessonId = 2, level = EducationLevel.BEGINNER.code, scorePercent = 90, correctCount = 2, totalQuestions = 2, timeSpentSeconds = 380, attemptNumber = 2, missedSkillTagsCsv = "", masteredSkillTagsCsv = "COMPREHENSION,ANALYSIS", completedAt = now - 5 * dayMs + 3600_000L),
            StudentAttemptEntity(userId = student1Id, lessonId = 4, level = EducationLevel.INTERMEDIATE.code, scorePercent = 82, correctCount = 2, totalQuestions = 2, timeSpentSeconds = 640, attemptNumber = 1, missedSkillTagsCsv = "", masteredSkillTagsCsv = "ANALYSIS,APPLICATION", completedAt = now - 3 * dayMs),
            StudentAttemptEntity(userId = student1Id, lessonId = 5, level = EducationLevel.INTERMEDIATE.code, scorePercent = 74, correctCount = 1, totalQuestions = 2, timeSpentSeconds = 790, attemptNumber = 2, missedSkillTagsCsv = "DEDUCTION", masteredSkillTagsCsv = "APPLICATION", completedAt = now - 2 * dayMs),
            StudentAttemptEntity(userId = student1Id, lessonId = 7, level = EducationLevel.ADVANCED.code, scorePercent = 50, correctCount = 1, totalQuestions = 2, timeSpentSeconds = 880, attemptNumber = 1, missedSkillTagsCsv = "CRITICAL_THINKING,DEDUCTION", masteredSkillTagsCsv = "", completedAt = now - 1 * dayMs),
            StudentAttemptEntity(userId = student1Id, lessonId = 7, level = EducationLevel.ADVANCED.code, scorePercent = 62, correctCount = 1, totalQuestions = 2, timeSpentSeconds = 720, attemptNumber = 2, missedSkillTagsCsv = "CRITICAL_THINKING", masteredSkillTagsCsv = "DEDUCTION", completedAt = now - 18 * 3600_000L),
            StudentAttemptEntity(userId = student1Id, lessonId = 8, level = EducationLevel.ADVANCED.code, scorePercent = 54, correctCount = 1, totalQuestions = 2, timeSpentSeconds = 950, attemptNumber = 3, missedSkillTagsCsv = "CRITICAL_THINKING,DEDUCTION", masteredSkillTagsCsv = "", completedAt = now - 3600_000L)
        )
        student1Attempts.forEach { dao.insertAttempt(it) }

        // Smart Alerts for Student 1
        dao.insertAlert(
            SmartAlertEntity(
                userId = student1Id,
                level = EducationLevel.ADVANCED.code,
                severity = "HIGH",
                titleAr = "تنبيه تعثر في المستوى المتقدم (٣ محاولات متتالية)",
                titleEn = "Bottleneck Detected in Advanced Level (3 Consecutive Attempts)",
                messageAr = "لوحظ تراجع في درجات درس (الاستنباط المقارن وتحرير محل النزاع) إلى 54% مع تكرار الخطأ في مهارتي النقد والاستنباط.",
                messageEn = "Score dropped to 54% in 'Comparative Deduction' across 3 attempts with recurring errors in Critical Thinking & Deduction.",
                recommendationAr = "يُنصح بمراجعة درس (دلالات الألفاظ) في المستوى المتوسط قبل إعادة اختبار تحرير محل النزاع.",
                recommendationEn = "Recommended Action: Review 'Semantic Signification' in the Intermediate Level before retrying the Advanced assessment."
            )
        )
        dao.insertAlert(
            SmartAlertEntity(
                userId = student1Id,
                level = EducationLevel.INTERMEDIATE.code,
                severity = "MEDIUM",
                titleAr = "فرصة تحسين سرعة الاستنتاج في المستوى المتوسط",
                titleEn = "Pacing Optimization in Intermediate Level",
                messageAr = "استغرق درس (القياس المنطقي والاستقراء) ٢٧ دقيقة مع محاولتين؛ دقة الفهم ممتازة (92%) لكن مهارة الاستنباط تحتاج تثبيتاً.",
                messageEn = "Spent 27 minutes on 'Syllogistic Deduction'; Comprehension is strong (92%) but Deduction speed needs reinforcement.",
                recommendationAr = "طبّق تمرين صياغة المقدمات الكلية والجزئية على مثالين إضافيين.",
                recommendationEn = "Practice constructing universal and particular premises with two additional examples."
            )
        )

        // Student 2 (Layla): High achiever across all levels for instructor comparison
        val student2Progress = listOf(
            LessonProgressEntity("${student2Id}_1", student2Id, 1, EducationLevel.BEGINNER.code, 100, 800, 100, 1, true, now - 5 * dayMs),
            LessonProgressEntity("${student2Id}_2", student2Id, 2, EducationLevel.BEGINNER.code, 100, 850, 95, 1, true, now - 4 * dayMs),
            LessonProgressEntity("${student2Id}_4", student2Id, 4, EducationLevel.INTERMEDIATE.code, 100, 1100, 92, 1, true, now - 3 * dayMs),
            LessonProgressEntity("${student2Id}_7", student2Id, 7, EducationLevel.ADVANCED.code, 100, 1350, 88, 1, true, now - 1 * dayMs)
        )
        student2Progress.forEach { dao.upsertProgress(it) }

        val student2Attempts = listOf(
            StudentAttemptEntity(userId = student2Id, lessonId = 1, level = EducationLevel.BEGINNER.code, scorePercent = 100, correctCount = 2, totalQuestions = 2, timeSpentSeconds = 400, attemptNumber = 1, missedSkillTagsCsv = "", masteredSkillTagsCsv = "COMPREHENSION,APPLICATION", completedAt = now - 5 * dayMs),
            StudentAttemptEntity(userId = student2Id, lessonId = 4, level = EducationLevel.INTERMEDIATE.code, scorePercent = 92, correctCount = 2, totalQuestions = 2, timeSpentSeconds = 520, attemptNumber = 1, missedSkillTagsCsv = "", masteredSkillTagsCsv = "ANALYSIS,APPLICATION", completedAt = now - 3 * dayMs),
            StudentAttemptEntity(userId = student2Id, lessonId = 7, level = EducationLevel.ADVANCED.code, scorePercent = 88, correctCount = 2, totalQuestions = 2, timeSpentSeconds = 610, attemptNumber = 1, missedSkillTagsCsv = "", masteredSkillTagsCsv = "CRITICAL_THINKING,DEDUCTION", completedAt = now - 1 * dayMs)
        )
        student2Attempts.forEach { dao.insertAttempt(it) }
    }
}
