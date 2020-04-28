fun main(args: Array<String>) {
    val f = X.pow(n(3))
    println(f)
    val fPrime = f.derivative()
    println(fPrime)
    println(fPrime.res(2))

    println(X.pow(X).derivative())
    println(X.exp().derivative())
    println(X.pow(n(2)).exp())
    println(X.pow(n(2)).exp().derivative().res(3))

    println(cot(X).derivative())
}
