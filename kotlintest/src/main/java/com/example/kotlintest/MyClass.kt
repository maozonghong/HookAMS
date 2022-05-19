package com.example.kotlintest

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.system.measureTimeMillis

class MyClass {

    companion object{

        fun testcoroutinebuild()=runBlocking {

            var job1=launch {
                delay(200)
                println("job1 finished")
            }

            var job2=async {
                delay(200)
                println("job2 finished")
                "job2 result"
            }

            launch {
                delay(300)
                println("job3 finished")
            }

//            println(job2.await())
        }


        fun testStartMode()= runBlocking {
            var job=launch(start = CoroutineStart.DEFAULT){
                delay(1000)
                println("job finished")
            }

            delay(100)
            job.cancel()
            println("finish")
        }

        /**
         * 第一个挂起函数之前
         */
        fun testStartMode2()= runBlocking {
            var job=launch(start = CoroutineStart.ATOMIC){
                println("第一个挂起函数之前")
                delay(1000)
                println("job finished")
            }

            delay(100)
            job.cancel()
            println("finish")
        }


        fun testStartMode3()= runBlocking {
            var job=launch(start = CoroutineStart.LAZY){
                println("第一个挂起函数之前")
                delay(1000)
                println("job finished")
            }

            delay(100)
            job.start()
//            job.cancel()
            println("finish")
        }

        fun testStartModeUnDispatched()= runBlocking {
           async(context =Dispatchers.IO,start = CoroutineStart.UNDISPATCHED){
               delay(1000)
               println("Thread ${Thread.currentThread().name}")
           }
        }


        fun testCancel()= runBlocking {
            val scope= CoroutineScope(Dispatchers.Default)

            scope.launch {
                delay(1000)
                println("job finished")
            }

            scope.launch {
                delay(1000)
                println("job2 finished")
            }

            delay(100)
            scope.cancel()

            delay(3000)
        }

        /**
         * 被取消的子协程 不影响其他兄弟协程
         */
        fun  testCancel2()= runBlocking {
            val scope= CoroutineScope(Dispatchers.Default)
            var job1=scope.launch {
                delay(1000)
                println("job1 finished")
            }

            var job2=scope.launch {
                delay(1000)
                println("job2 finished")
            }

            delay(100)
            job1.cancel()

            delay(3000)
        }

        fun testCoroutineScopeException()= runBlocking {
            var job1=launch {
                delay(2000)
                println("job1 finished")
            }

            var handler= CoroutineExceptionHandler{_,throwable->
                throwable.printStackTrace()
            }

            var job2= CoroutineScope(Dispatchers.Default).async(handler) {
                delay(3000)
                println("job2 finished")
                throw IllegalArgumentException()
            }
            try {
                job2.await()
            }catch (e:Exception){
                e.printStackTrace()
            }
            println("finished")
        }


        fun flowCollect(){
            runBlocking {
                 measureTimeMillis {
                    flow {
                        for (i in 1..3) {
                            delay(1000)
                            emit(i)
                        }
                    }.collect {
                        delay(3000)
                        println("$it")
                    }
                }.apply (::println)
            }
        }


        fun flatMap(){
            val list = listOf("jack", "rose", "danny")
            val list2 = listOf("jack1", "rose2", "danny3")
            val bigList = listOf(list, list2)

            val flat = bigList.flatMap {
                it
            }.apply(::println)
        }

        fun zip(){
            val list = listOf("jack", "rose", "danny")
            val list2 = listOf(14, 18, 23)

            list.zip(list2).apply(::println)

        }


        fun TestFunction(){
            val funcp: (Int,Int) -> String = {a,b->
                "我是一个函数类型变量:${a+b}"
            }

            println(funcp)
            println(funcp(2,3))
        }

        fun main3() = runBlocking {
            launch {        //launch①
                delay(1000)                 //挂起launch①
                println("test2")
            }
            println("test1")
            coroutineScope {                //第一次挂起runBlocking,直至内部逻辑完成
                launch {    //launch②
                    delay(2000)             //挂起launch②
                    println("test3")
                }
                delay(5000)     //delay①    //第二次挂起runBlocking
                println("test4")
            }
            println("test5")
        }


        suspend fun showSomeData() = coroutineScope {
            cancel()
            val data1 = async { //子任务1
                delay(2000)
                100
            }
            val data2 = async {         //子任务2
                delay(3000)
                20
            }

            withContext(Dispatchers.Default) {
                //合并结果并返回
                delay(3000)
                val random = Random(10)
                data1.await() + data2.await() + random.nextInt(100)
            }
        }


        @JvmStatic
        fun main(args: Array<String>) {
//            testcoroutinebuild()
//            testStartMode3()
//            testStartModeUnDispatched()
//            testCancel()
//            testCancel2()
//            testCoroutineScopeException()

//            flowCollect()
//            flatMap()
//            zip()
//            TestFunction()
//            main3()

            runBlocking {
                showSomeData().apply(::println)
            }
        }
    }


}