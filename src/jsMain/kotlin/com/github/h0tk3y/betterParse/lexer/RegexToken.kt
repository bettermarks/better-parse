package com.github.h0tk3y.betterParse.lexer

public actual class RegexToken : Token {
  private val pattern: String
  private val regex: Regex

  public actual constructor(
      name: String?,
      patternString: String,
      ignored: Boolean
  ) : super(name, ignored) {
    pattern = patternString
    regex = pattern.toRegex()
  }

  public actual constructor(name: String?, regex: Regex, ignored: Boolean) : super(name, ignored) {
    pattern = regex.pattern
    this.regex = regex
  }

  override actual fun match(input: CharSequence, fromIndex: Int): Int =
      regex.find(input, fromIndex)?.range?.let {
        if (it.first == fromIndex) {
          val length = it.last - it.first + 1
          length
        } else 0
      } ?: 0

  override fun toString(): String = "${name ?: ""} [$pattern]" + if (ignored) " [ignorable]" else ""
}
