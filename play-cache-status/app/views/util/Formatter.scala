package views.util

object Formatter {
  private val formatter = java.text.NumberFormat.getIntegerInstance

  def pretty(value: Int): String = formatter.format(value)
  def pretty(value: Long): String = formatter.format(value)
  def pretty(value: Float): String = formatter.format(value)
  def pretty(value: Double): String = formatter.format(value)
}
