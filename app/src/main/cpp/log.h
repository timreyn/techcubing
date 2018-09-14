#ifndef APP_LOG_H
#define APP_LOG_H

#include <android/log.h>

#define MODULE_NAME "TCNativeTimerImpl"

#define LOGI(...) \
__android_log_print(ANDROID_LOG_INFO, MODULE_NAME, __VA_ARGS__)

#endif //APP_LOG_H
