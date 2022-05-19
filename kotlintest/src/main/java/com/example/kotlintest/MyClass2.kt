package com.example.kotlintest

/**
Created by maozonghong

on 5/18/22
 **/
class MyClass2:()->Unit {


    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
           var class2= MyClass2()
            class2()
        }
    }

//    override fun invoke(p1: Int) {
//        println("invoke class2:${p1}")
//    }

    override fun invoke() {
        println("invoke class2:")
    }
}