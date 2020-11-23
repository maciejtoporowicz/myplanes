package it.toporowicz

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("it.toporowicz")
		.start()
}

