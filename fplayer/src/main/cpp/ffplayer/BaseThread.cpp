//
// Created by fanqiang on 2019-10-09.
//

#include "BaseThread.h"
#include <thread>

void BaseThread::start() {
    std::thread threadTemp;
    threadTemp.detach();
}

void BaseThread::stop() {

}

void BaseThread::threadMain() {

}