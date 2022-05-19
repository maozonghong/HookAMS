package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.jetbrains.annotations.TestOnly
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.thread

/**
Created by maozonghong

on 5/6/22
 **/
//@RunWith(AndroidJUnit4::class)
class TestInline {

    inline fun assWeCan(block: () -> Unit) {
        // codes
    }

    fun boyNextDoor(block: () -> Unit) {
        // codes
    }

    inline fun test(noinline f: () -> Unit) {
        thread(block = f)
    }

    inline fun test1(crossinline f: () -> Unit) {
//        thread({f})
    }

    @Test
    fun foo() {
        boyNextDoor {
            // 这种写法是不能通过编译的，因为你不能从一个函数跳出另一个函数。你必须使用 return@assWeCan 或者 return@foo 这种写法。
            return@boyNextDoor
        }
        assWeCan {
            // 这种写法可以通过编译，因为你inline了之后就是直接从外部退出了。
            return
        }
    }


    @Test
    fun main() {
        println("start execution:")
        sayHello {
            println("in lambda")
            return
        }
        println("end execution")
    }

    private inline fun sayHello(block: () -> Unit) {
        println("in sayHello")
        block()
    }

}