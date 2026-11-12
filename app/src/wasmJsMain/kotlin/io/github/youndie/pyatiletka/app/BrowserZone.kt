package io.github.youndie.pyatiletka.app

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone

/**
 * The viewer's offset as the browser knows it. kotlinx-datetime on wasm has no zone rules without a
 * zone database in the page, so the page shows the snapshot's moment at today's offset, which is what
 * a moment of the last hours needs.
 */
fun browserZone(): TimeZone = UtcOffset(minutes = -timezoneOffsetMinutes()).asTimeZone()

@OptIn(ExperimentalWasmJsInterop::class)
private fun timezoneOffsetMinutes(): Int = js("new Date().getTimezoneOffset()")
