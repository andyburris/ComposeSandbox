package com.andb.apps.composesandbox.util

import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import de.markusressel.kodehighlighter.core.LanguageRuleBook
import de.markusressel.kodehighlighter.core.StyleFactory
import de.markusressel.kodehighlighter.core.colorscheme.ColorScheme
import de.markusressel.kodehighlighter.core.rule.LanguageRule
import de.markusressel.kodehighlighter.core.rule.RuleHelper
import de.markusressel.kodehighlighter.language.kotlin.rule.*

fun CharacterStyle.toSpanStyle(): SpanStyle {
    return when(this) {
        is ForegroundColorSpan -> SpanStyle(color = Color(this.foregroundColor))
        else -> SpanStyle()
    }
}

fun StyleFactory.toSpanStyle() = this.invoke().toSpanStyle()

open class AnnotatedStringHighlighter(
    private val languageRuleBook: LanguageRuleBook,
    private val colorScheme: ColorScheme = languageRuleBook.defaultColorScheme
) : LanguageRuleBook by languageRuleBook {

    /**
     * Highlight the given text
     *
     * @param code the [String] to apply highlighting to
     */
    open suspend fun highlight(code: String): AnnotatedString {
        val ruleMatches = languageRuleBook.createHighlighting(code)
        return buildAnnotatedString {
            append(code)
            ruleMatches.map { ruleMatches ->
                val styleFactories = colorScheme.getStyles(ruleMatches.rule)
                ruleMatches.matches.forEach { match ->
                    styleFactories.forEach { styleFactory ->
                        this.addStyle(styleFactory.toSpanStyle(), match.startIndex, match.endIndex)
                    }
                }
            }
        }
    }
}

open class PatternRule(val pattern: Regex) : LanguageRule {
    override fun findMatches(text: CharSequence) = RuleHelper.findRegexMatches(text, pattern)
}

class BetterNumberRule : PatternRule("[^a-zA-Z](-)?\\d+(\\.\\d+)?(L)?".toRegex())
class ParameterRule : PatternRule("(?![\\(,])[\\n\\s]*\\w* *=".toRegex())

class ComposeRuleBook : LanguageRuleBook {

    override val defaultColorScheme: ColorScheme = LightBackgroundColorScheme()

    override fun getRules(): Set<LanguageRule> {
        return setOf(
            AnnotationRule(),
            ClassKeywordRule(),
            CommentRule(),
            ImportKeywordRule(),
            PackageKeywordRule(),
            ReturnKeywordRule(),
            FunctionKeywordRule(),
            VarKeywordRule(),
            BetterNumberRule(),
            ParameterRule(),
        )
    }
}

class LightBackgroundColorScheme : ColorScheme {

    override fun getStyles(type: LanguageRule): Set<StyleFactory> {
        return when (type) {
            is ImportKeywordRule,
            is PackageKeywordRule,
            is ClassKeywordRule,
            is OpenKeywordRule,
            is ReturnKeywordRule,
            is FunctionKeywordRule,
            is VisibilityKeywordRule,
            is VarKeywordRule -> {
                setOf { ForegroundColorSpan(android.graphics.Color.parseColor("#FF6D00")) }
            }
            is AnnotationRule -> {
                setOf { ForegroundColorSpan(android.graphics.Color.parseColor("#F2C94C")) }
            }
            is CommentRule -> {
                setOf { ForegroundColorSpan(android.graphics.Color.parseColor("#33691E")) }
            }
            is BetterNumberRule -> {
                setOf { ForegroundColorSpan(android.graphics.Color.parseColor("#01579B")) }
            }
            is ParameterRule -> {
                setOf { ForegroundColorSpan(android.graphics.Color.parseColor("#2F80ED")) }
            }
            else -> emptySet()
        }
    }

}