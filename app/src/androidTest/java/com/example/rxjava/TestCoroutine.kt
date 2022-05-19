package com.example.rxjava

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
Created by maozonghong

on 5/16/22
 **/
class TestCoroutine {

    @Test
    fun testcoroutinebuild()=runBlocking {
        var job1=launch {
            delay(200)
            println("job1 finished")
        }

        var job2=async {
            delay(200)
            println("job2 finished")
            "jon2 result"
        }

        println(job2.await())
    }



}