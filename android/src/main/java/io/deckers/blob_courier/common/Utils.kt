/**
 * Copyright (c) Ely Deckers.
 *
 * This source code is licensed under the MPL-2.0 license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.deckers.blob_courier.common

import android.app.DownloadManager
import android.content.Context
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import okhttp3.Headers

fun notifyBridgeOfProgress(
  context: ReactApplicationContext,
  taskId: String,
  totalNumberOfBytesRead: Long,
  totalLength: Long
) {
  if (!context.hasActiveCatalystInstance()) {
    return
  }

  context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
    .emit(
      DEVICE_EVENT_PROGRESS,
      Arguments.createMap().apply {
        putString("taskId", taskId)
        putString("written", totalNumberOfBytesRead.toString())
        putString("total", totalLength.toString())
      }
    )
}

fun createDownloadManager(context: Context) =
  context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

fun Map<*, *>.toReactMap(): WritableMap {
  val thisMap = this

  return Arguments.createMap().apply {
    thisMap.forEach { (k, v) ->
      when {
        (v is String) ->
          putString(k.toString(), v)
        (v is Map<*, *>) -> {
          putMap(k.toString(), v.toReactMap())
        }
        else ->
          putString(k.toString(), v.toString())
      }
    }
  }
}

fun mapHeadersToMap(headers: Headers): Map<String, String> =
  headers
    .toMultimap()
    .map { entry -> Pair(entry.key, entry.value.joinToString()) }
    .toMap()

fun filterHeaders(unfilteredHeaders: Map<String, Any>): Map<String, String> =
  unfilteredHeaders
    .mapValues { (_, v) -> v as? String }
    .filter { true }
    .mapNotNull { (k, v) -> v?.let { k to it } }
    .toMap()