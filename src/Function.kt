import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

abstract class Function {
    abstract fun res(x: Double): Double
    fun res(x: Int) = res(x.toDouble())
    abstract fun diff(): Function
    fun derivative() = diff().simplify()
    open fun simplify() = this

    //convenience operators
    operator fun plus(f: Function) = Add(this, f)
    operator fun times(f: Function) = Mult(this, f)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = Mult(Number(-1), this)
    operator fun minus(f: Function) = Add(this, -f)
    fun reciprocal() = Pow(this, Number(-1))
    operator fun div(f: Function) = Mult(this, f.reciprocal())
    fun pow(f: Function) = Pow(this, f)
    fun exp() = Pow(n(Math.E), this)
}

data class Number(val n: Double) : Function() {
    constructor(n: Int) : this(n.toDouble())
    override fun res(x: Double) = n
    override fun diff() = n(0)
    private fun isCloseToInt(n: Double): Boolean = (abs(n.roundToInt() - n) < .0001)
    override fun toString() =
            when {
                n == Math.PI -> "pi"
                n == Math.E -> "e"
                isCloseToInt(n) -> n.roundToInt().toString()
                else -> n.toString()
            }
}

fun n(n: Double) = Number(n)
fun n(n: Int) = Number(n)

data class Add(val a: Function, val b: Function) : Function() {
    override fun res(x: Double) = a.res(x) + b.res(x)
    override fun diff() = Add(a.diff(), b.diff())
    override fun toString() = "($a + $b)"
    override fun simplify(): Function {
        val theA = a.simplify()
        val theB = b.simplify()
        return when {
            theA == n(0) -> theB
            theB == n(0) -> theA
            theA is Number && theB is Number && theA.n != Math.PI && theB.n != Math.PI -> n(res(0))
            else -> Add(theA, theB)
        }
    }
}

data class Mult(val a: Function, val b: Function) : Function() {
    override fun res(x: Double) = a.res(x) * b.res(x)
    override fun diff() = Add(Mult(a, b.diff()), Mult(b, a.diff()))
    override fun toString() = "($a * $b)"
    override fun simplify(): Function {
        val theA = a.simplify()
        val theB = b.simplify()
        return when {
            theA == n(0) || theB == n(0) -> n(0)
            theA == n(1) -> theB
            theB == n(1) -> theA
            theA is Number && theB is Number && theA.n != Math.PI && theB.n != Math.PI -> n(res(0))
            else -> Mult(theA, theB)
        }
    }
}

object X : Function() {
    override fun res(x: Double) = x
    override fun diff() = Number(1)
    override fun toString() = "x"
}

data class Pow(val a: Function, val b: Function) : Function() {
    override fun res(x: Double) = a.res(x).pow(b.res(x))
    override fun diff() = b * a.pow(b - Number(1)) * a.diff() + a.pow(b) * Ln(a) * b.diff()
    override fun toString() = "($a^$b)"
    override fun simplify(): Function {
        val theA = a.simplify()
        val theB = b.simplify()
        return when {
            theA == n(0) -> n(0)
            theB == n(0) -> n(1)
            theB == n(1) -> theA
            theA is Number && theB is Number -> n(res(0))
            else -> Pow(theA, theB)
        }
    }
}

data class Ln(val a: Function): Function() {
    override fun res(x: Double) = kotlin.math.ln(a.res(x))
    override fun diff() = a.reciprocal() * a.diff()
    override fun toString() = "ln($a)"
    override fun simplify(): Function {
        val theA = a.simplify()
        return when (theA) {
            n(1) -> n(0)
            n(Math.E) -> n(1)
            else -> Ln(theA)
        }
    }
}

data class Sin(val a: Function): Function() {
    override fun res(x: Double) = kotlin.math.sin(a.res(x))
    override fun diff() = cos(a) * a.diff()
    override fun toString() = "sin($a)"
    override fun simplify() = sin(a.simplify())
}

fun sin(x: Function) = Sin(x)
fun cos(x: Function) = sin(n(Math.PI) / n(2) - x)
fun tan(x: Function) = sin(x)/cos(x)
fun csc(x: Function) = sin(x).reciprocal()
fun sec(x: Function) = cos(x).reciprocal()
fun cot(x: Function) = tan(x).reciprocal()
